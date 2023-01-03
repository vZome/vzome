
import { REVISION } from '../revision.js';

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

const format = ( element ) =>
{
  const indented = asIndentedText( element, "" );
  const parser = new DOMParser();
  return parser .parseFromString( indented, "text/xml" ) .documentElement;
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

const serializeCamera = ( camera, doc ) =>
{
  const { near, far, width, distance, perspective, lookAt, lookDir, up } = camera;
  const viewing = doc .createElement( "Viewing" );
  const viewmodel = doc .createElement( "ViewModel" );
  viewing .appendChild( viewmodel );
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
  return viewing;
}

export const serializeVZomeXml = ( xmlStr, lighting, camera, originalCamera ) =>
{
  const parser = new DOMParser();
  const doc = parser .parseFromString( xmlStr, "application/xml" );

  const rootElement = doc .documentElement;
  const field = rootElement .getAttribute( 'field' );

  const children = rootElement .childNodes;

  const newRoot = document .createElementNS( 'http://xml.vzome.com/vZome/4.0.0/', 'vzome:vZome' );
  newRoot .setAttribute( 'field', field );
  newRoot .setAttribute( 'edition', 'online' );
  newRoot .setAttribute( 'version', '1.0' );
  newRoot .setAttribute( 'buildNumber', REVISION );
  newRoot .replaceChildren( ...children );

  const sceneModel = serializeLighting( lighting, doc );

  const frustumRatio = originalCamera.far / originalCamera.distance;
  camera .far = camera .distance * frustumRatio;
  const viewing = serializeCamera( camera, doc );

  const justToFormat = doc .createElement( "justToFormat" );
  justToFormat .appendChild( sceneModel );
  justToFormat .appendChild( viewing );

  const newKids = format( justToFormat ) .childNodes;
  newRoot .append( ...newKids );

  rootElement .replaceWith( newRoot );

  const serializer = new XMLSerializer();
  return '<?xml version="1.0" encoding="UTF-8" standalone="no"?>\n' + serializer .serializeToString( doc );
}

// from https://www.bitdegree.org/learn/javascript-download
export const download = ( name, text, type ) =>
{
  const blob = new Blob( [ text ], { type } );
  const element = document.createElement( 'a' )
  const blobURI = URL.createObjectURL( blob )
  element.setAttribute( 'href', blobURI )
  element.setAttribute( 'download', `${name}` )
  element.style.display = 'none'
  document.body.appendChild( element )
  element.click()
  document.body.removeChild( element )
}
