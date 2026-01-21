
export const fetchUrlText = async ( url ) =>
{
  let response;
  try {
    response = await fetch( url )
  } catch ( error ) {
    console.log( `Fetching ${url} failed with "${error}"; trying cors-anywhere` )
    // TODO: I should really deploy my own copy of this proxy on Heroku
    response = await fetch( 'https://cors-anywhere.herokuapp.com/' + url )
  }
  if ( !response.ok ) {
    throw new Error( `Failed to fetch "${url}": ${response.statusText}` )
  }
  return response.text()
}

export const fetchUrlJSON = async ( url ) =>
{
  const text = await fetchUrlText( url );
  return JSON .parse( text );
}

export const downloadText = ( text, filename, mimeType ) =>
{
  const blob = new Blob( [text], { type: mimeType } );
  const url = URL.createObjectURL( blob );
  const a = document.createElement( 'a' );
  a.href = url;
  a.download = filename;
  document.body.appendChild( a );
  a.click();
  document.body.removeChild( a );
  URL.revokeObjectURL( url );
}

export const downloadJSON = ( data, filename ) =>
{
  const jsonString = JSON.stringify( data, null, 2 );
  downloadText( jsonString, filename, 'application/json' );
}