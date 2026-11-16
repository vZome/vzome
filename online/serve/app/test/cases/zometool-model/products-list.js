// Sample code: a list of the Zometool products that include all the parts required.
//
// This relies only on the "zometool-instructions-loaded" event (see parts-list.js for its detail),
//  plus the products catalog, which this code fetches itself.  Each catalog entry has a label,
//  a url (relative to zometool.com), and a bom mapping part IDs to counts.

// The same location that <zometool-instructions> uses for zometool-parts.json by default;
//  if you set its "metadata" or "revision" attributes, change this to match.
const PRODUCTS_URL = 'https://zometool.github.io/vzome-sharing/metadata/2026/zometool-products.json';

const instructions = document .querySelector( 'zometool-instructions' );
const productsList = document .querySelector( '.products-list' );

const productsPromise = fetch( PRODUCTS_URL )
  .then( response => {
    if ( ! response.ok )
      throw new Error( `HTTP status ${response.status}` );
    return response.json();
  } )
  .catch( error => {
    console.error( `Failed to load Zometool products from ${PRODUCTS_URL}: ${error.message}` );
    return {};
  } );

const coversBom = ( product, bom ) =>
  bom .every( ( { partNum, count } ) => product.bom?.[ partNum ] >= count );

instructions .addEventListener( 'zometool-instructions-loaded', async ( { detail: bom } ) => {
  const products = await productsPromise;
  const items = Object.values( products )
    .filter( product => product.url && coversBom( product, bom ) ) // ignore products without a URL
    .map( ( { label, url } ) => {
      const link = document .createElement( 'a' );
      link.href = new URL( url, 'https://zometool.com' );
      link.target = '_blank';
      link.rel = 'noopener';
      link.textContent = label;
      const item = document .createElement( 'li' );
      item .append( link );
      return item;
    } );
  productsList .replaceChildren( ...items );
} );
