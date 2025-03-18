// This script is manually deployed to the web server hosting downloads
//   for desktop vZome.  It is used on the downloads page to update
//   URLs to the latest builds.

const updateUrl = which => {
  const link = document.getElementById( which );
  if ( !link )
    return;
  fetch( `/download/latest/${which}.json` )
    .then( response => response.text() )
    .then( text => {
      const { url } = JSON.parse( text );
      link.href = url;
      console.log( `${which} latest is ${url}` );
    })
    .catch( error => {
      console.dir( error );
    })
}

updateUrl( 'mac-intel' );
updateUrl( 'mac-arm64' );
updateUrl( 'windows' );
updateUrl( 'linux' );