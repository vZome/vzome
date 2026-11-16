
// The metadata filename is fixed, part of the contract; the "metadata" and "revision"
//  attributes of zometool-instructions only determine where it is fetched from.
//  (The zometool-products.json catalog alongside it is fetched by page code; see the zometool-model test case.)
export const PARTS_CATALOG = 'zometool-parts.json';

const DEFAULT_METADATA = 'https://zometool.github.io/vzome-sharing/metadata/';

const cache = new Map(); // so multiple elements on a page share a single fetch

export const metadataUrl = ( filename, metadata, revision ) =>
  new URL( ( metadata ?? DEFAULT_METADATA ) + ( revision ?? '2026/' ) + filename, window.location ) .toString();

export const fetchMetadata = url =>
{
  if ( ! cache .has( url ) )
    cache .set( url, fetch( url )
      .then( response => {
        if ( ! response.ok )
          throw new Error( `HTTP status ${response.status}` );
        return response.text();
      } )
      .then( text => JSON.parse( text ) )
      .catch( error => {
        console.error( `Failed to load Zometool metadata from ${url}: ${error.message}` );
        return {};
      } ) );
  return cache .get( url );
}
