// Sample code: a table of the parts required, built from the BOM that <zometool-instructions> reports.
//
// This relies only on the "zometool-instructions-loaded" event, whose detail is an array of
//  { partNum, count, color, shape }, in parts catalog order:
//  - partNum is the Zometool part ID, e.g. "PST-B1-BLU"
//  - shape is a shape code, e.g. "B1", "R00", or "BALL"
//  - color is the CSS color for the part, from the parts catalog
// The event may fire more than once, so the table is rebuilt each time.
//
// The part pictures are SVG files in ./parts/, named by lowercase shape code.  They are inlined
//  rather than used as <img> sources, so that each can be recolored with its part color.

const instructions = document .querySelector( 'zometool-instructions' );
const tableBody = document .querySelector( '.parts-table tbody' );

const svgTexts = new Map(); // shape code -> Promise of SVG text, or null if there is no picture

const fetchSvg = shape =>
{
  if ( ! svgTexts .has( shape ) )
    svgTexts .set( shape, fetch( `./parts/${shape .toLowerCase()}.svg` )
      .then( response => response.ok? response.text() : null )
      .catch( () => null ) );
  return svgTexts .get( shape );
}

const partPicture = async ( shape, color ) =>
{
  const svgText = await fetchSvg( shape );
  if ( ! svgText )
    return null;
  const svg = new DOMParser() .parseFromString( svgText, 'image/svg+xml' ) .documentElement;
  if ( color )
    svg .setAttribute( 'fill', color );
  return document .importNode( svg, true );
}

instructions .addEventListener( 'zometool-instructions-loaded', async ( { detail: bom } ) => {
  const rows = await Promise.all( bom .map( async ( { shape, count, color } ) => {
    const row = document .createElement( 'tr' );
    const picture = await partPicture( shape, color );
    row .insertCell() .append( ...( picture? [ picture ] : [] ) );
    row .insertCell() .textContent = ( shape === 'BALL' )? '' : shape;
    row .insertCell() .textContent = count;
    return row;
  } ) );
  tableBody .replaceChildren( ...rows );
} );
