
import { ParsedEdit } from './edit.js';
import * as txml from 'txml/dist/txml.mjs';
import { JavaDomElement } from './dom.js';


const assignIds = ( txmlElement, id=':' ) =>
{
  if ( txmlElement.tagName === 'effects' )
    return txmlElement;
  txmlElement.id = id;
  txmlElement.children.map( (child,index) => {
    if ( child instanceof Object && child.tagName !== 'effects' ) {
      child.index = index;
      assignIds( child, `${id}${index}:` )
    }
  });
  return txmlElement
}

const parseVector = ( element, name ) =>
{
  const child = element.getChildElement( name )
  const x = parseFloat( child.getAttribute( "x" ) )
  const y = parseFloat( child.getAttribute( "y" ) )
  const z = parseFloat( child.getAttribute( "z" ) )
  return [ x, y, z ]
}

const parseColor = ( rgbCsv ) =>
{
  const [ r, g, b ] = rgbCsv.split( "," ).map( s => parseInt( s ) )
  const componentToHex = (c) =>
  {
    const hex = c.toString( 16 )
    return hex.length === 1 ? "0" + hex : hex
  }
  return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b)
}

const parseLighting = ( sceneElement ) =>
{
  const backgroundColor = parseColor( sceneElement.getAttribute( "background" ) )
  return {
    backgroundColor,
    // TODO: parse these too, in case they change someday
    ambientColor: '#292929',
    directionalLights: [ // These are the vZome defaults, for consistency
      { direction: [ 1, -1, -0.3 ], color: '#FDFDFD' },
      { direction: [ -1, 0, 0 ], color: '#808080' },
      { direction: [ 0, 0, -1 ], color: '#1E1E1E' },
    ]
  }
}

const parseViewXml = ( viewingElement ) =>
{
  const viewModel = viewingElement.getChildElement( "ViewModel" )
  const distance = parseFloat( viewModel.getAttribute( "distance" ) )
  const near = parseFloat( viewModel.getAttribute( "near" ) )
  const far = parseFloat( viewModel.getAttribute( "far" ) )
  const width = parseFloat( viewModel.getAttribute( "width" ) )
  const parallelAttr = viewModel.getAttribute( "parallel" );
  const orthographic = parallelAttr ? (parallelAttr.toLowerCase() == "true") : false
  const lookAt = parseVector( viewModel, "LookAtPoint" )
  const up = parseVector( viewModel, "UpDirection" )
  const lookDir = parseVector( viewModel, "LookDirection" )
  const camera = {
    near, far, width, distance,
    up, lookAt, lookDir,
    perspective: !orthographic
  }
  return camera;
}

const findSnapshotNodes = ( xmlTree ) =>
{
  const snapshotNodes = [];
  const findSnapshots = ( txmlElement ) =>
  {
    if ( txmlElement.tagName === "Snapshot" ) {
      const snapshotId = parseInt( txmlElement.attributes.id );
      snapshotNodes[ snapshotId ] = txmlElement.id;
    } else {
      txmlElement.children.map( child => {
        if ( child instanceof Object ) {
          findSnapshots( child, snapshotNodes )
        }
      });
    }
  }
  findSnapshots( xmlTree );
  return snapshotNodes;
}

const parseArticle = ( notesElement ) =>
{
  if ( !notesElement )
    return [];

  const parseArticlePage = ( pageElement ) =>
  {
    const { snapshot, title } = pageElement.attributes;
    const pageDom = new JavaDomElement( pageElement );
    const camera = parseViewXml( pageDom );
    const content = pageDom .getChildElement( 'content' ) ?.getTextContent() || "";
    return { title, snapshot, camera, content };
  }
  return notesElement.nativeElement.children.map( pageElement => parseArticlePage( pageElement ) )
          // Early vZome files always had a default article with one explanatory page
          .filter( snapshot => snapshot.title !== "How to save notes" );
}

export const createParser = ( documentFactory ) => ( xmlText ) =>
{
  const domDoc = txml.parse( xmlText /*, options */ );

  let vZomeRoot = new JavaDomElement( domDoc.filter( n => n.tagName === 'vzome:vZome' )[ 0 ] );

  const namespace = vZomeRoot.getAttribute( "xmlns:vzome" )
  const fieldName = vZomeRoot.getAttribute( "field" )

  const legacyDesign = documentFactory( fieldName, namespace, vZomeRoot )

  const viewing = vZomeRoot.getChildElement( "Viewing" )
  const camera = viewing && parseViewXml( viewing )

  const scene = vZomeRoot.getChildElement( "sceneModel" )
  const lighting = scene && parseLighting( scene )

  const historyElement = vZomeRoot.getChildElement( "EditHistory" ) || vZomeRoot.getChildElement( "editHistory" ) || vZomeRoot.getChildElement( "EditHistoryDetails" );
  const xmlTree = assignIds( historyElement.nativeElement );
  const edits = new ParsedEdit( xmlTree, null, legacyDesign.interpretEdit );
  const targetEdit = Number( edits.getAttribute( "editNumber" ) );
  const targetEditId = `:${targetEdit}:`
  const firstEdit = edits.firstChild()

  const snapshotNodes = [ ...findSnapshotNodes( xmlTree ), targetEditId ]; // The extra snapshot is discarded later
  const scenes = parseArticle( vZomeRoot.getChildElement( "notes" ) );

  return { ...legacyDesign, firstEdit, camera, lighting, xmlTree, scenes, snapshotNodes, targetEdit }
}
