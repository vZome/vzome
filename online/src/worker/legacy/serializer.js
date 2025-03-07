
import { REVISION } from '../../revision.js';
import { JavaDomDocument } from './dom.js';

const asIndentedText = ( element, indentation ) =>
{
  let result = indentation + "<" + element.localName;
  for ( const { name, value } of element.attributes ) {
    result += " " + name + "=\"" + value + "\"";
  }
  if ( element.childElementCount === 0 )
    return result + "/>\n";
  else
    result += ">\n";
  const text = element .textContent;
  if ( text ) {
    result += indentation + "  " + text + "\n";
  } else {
    for ( const child of element.children ) {
      result += asIndentedText( child, indentation+"  " );
    }
  }
  result += indentation + "</" + element.localName + ">\n";
  return result;
}

const serializeLighting = ( lighting, doc ) =>
{
  const getRGB = color =>{
    const [ r, g, b ] = [ parseInt( color.substr(1, 2), 16 ), parseInt( color.substr(3, 2), 16 ), parseInt( color.substr(5, 2), 16 ) ];
    return r + ',' + g + ',' + b;
  }
  const { ambientColor, backgroundColor, directionalLights } = lighting;
  const sceneModel = doc .createElement( "sceneModel" );
  sceneModel .setAttribute( "ambientLight", getRGB( ambientColor ) );
  sceneModel .setAttribute( "background", getRGB( backgroundColor ) );
  for (const dlight of directionalLights) {
    const { color, direction } = dlight;
    const child = doc .createElement( "directionalLight" );
    child .setAttribute( "color", getRGB( color ) );
    child .setAttribute( "x", String( direction[ 0 ] ) );
    child .setAttribute( "y", String( direction[ 1 ] ) );
    child .setAttribute( "z", String( direction[ 2 ] ) );
    sceneModel .appendChild( child );
  }
  return sceneModel;
}

const serializeViewing = ( camera, doc ) =>
{
  const viewing = doc .createElement( "Viewing" );
  const viewmodel = serializeCamera( camera, doc );
  viewing .appendChild( viewmodel );
  return viewing;
}

const serializeCamera = ( camera, doc ) =>
{
  const { near, far, width, distance, perspective, lookAt, lookDir, up } = camera;
  const viewmodel = doc .createElement( "ViewModel" );
  viewmodel .setAttribute( "distance", String( distance ) );
  viewmodel .setAttribute( "far", String( far ) );
  viewmodel .setAttribute( "near", String( near ) );
  viewmodel .setAttribute( "parallel", String( !perspective ) );
  viewmodel .setAttribute( "stereoAngle", "0.0" );
  viewmodel .setAttribute( "width", String( width ) );
  {
      const child = doc .createElement( "LookAtPoint" );
      child .setAttribute( "x", String( lookAt[ 0 ] ) );
      child .setAttribute( "y", String( lookAt[ 1 ] ) );
      child .setAttribute( "z", String( lookAt[ 2 ] ) );
      viewmodel .appendChild( child );
  }
  {
      const child = doc .createElement( "UpDirection" );
      child .setAttribute( "x", String( up[ 0 ] ) );
      child .setAttribute( "y", String( up[ 1 ] ) );
      child .setAttribute( "z", String( up[ 2 ] ) );
      viewmodel .appendChild( child );
  }
  {
      const child = doc .createElement( "LookDirection" );
      child .setAttribute( "x", String( lookDir[ 0 ] ) );
      child .setAttribute( "y", String( lookDir[ 1 ] ) );
      child .setAttribute( "z", String( lookDir[ 2 ] ) );
      viewmodel .appendChild( child );
  }
  return viewmodel;
}

const serializeScenes = ( scenes, doc ) =>
{
  const notes = doc .createElement( "notes" );
  for (const scene of scenes) {
    const { title, snapshot, camera, content } = scene;
    if ( snapshot < 0 )
      continue; // default scene does not go here
    const page = doc .createElement( "page" );
    page .setAttribute( 'title', title );
    page .setAttribute( 'snapshot', snapshot );
    const viewModel = serializeCamera( camera, doc );
    page .appendChild( viewModel );
    const contentElem = doc .createElement( 'content' );
    contentElem .setAttribute( 'xml:space', 'preserve' );
    if ( !! content ) {
      contentElem .setTextContent( content );
    }
    page .appendChild( contentElem );
    notes .appendChild( page );
  }
  return notes;
}

export const serializeVZomeXml = ( legacyDesign, lighting, camera, scenes ) =>
{
  const doc = new JavaDomDocument();

  const newRoot = doc .createElement( 'vzome:vZome' );
  newRoot .setAttribute( 'xmlns:vzome', 'http://xml.vzome.com/vZome/4.0.0/' );
  newRoot .setAttribute( 'edition', 'online' );
  newRoot .setAttribute( 'version', '1.0' );
  newRoot .setAttribute( 'buildNumber', REVISION );

  legacyDesign .serializeToDom( doc, newRoot );

  const sceneModel = serializeLighting( lighting, doc );

  const frustumRatio = camera.far / camera.distance;
  camera .far = camera .distance * frustumRatio;
  const viewing = serializeViewing( camera, doc );
  const notes = serializeScenes( scenes, doc );

  newRoot .appendChild( sceneModel );
  newRoot .appendChild( viewing );
  newRoot .appendChild( notes );

  return '<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n' + newRoot .toIndentedString( '' );
}
