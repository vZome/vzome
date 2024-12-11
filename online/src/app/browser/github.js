
export const fetchGitHubShares = async githubUser =>
{
  const BASE_URL = `https://raw.githubusercontent.com/${githubUser}/vzome-sharing/main/`;

  if ( ! githubUser ) {
    return [];
  }

  return (
    fetch( `https://api.github.com/repos/${githubUser}/vzome-sharing/git/trees/main?recursive=1` )
      .then( response => response.json() )
      .then( json => {
        const designs = json.tree.filter( entry => entry.type==="blob" && entry.path.endsWith( '.vZome' ) )
          .map( ({ path }) => {
            const tokens = path .split( '/' );
            const url = BASE_URL + tokens .map( encodeURIComponent ) .join( '/' );
            const lastToken = tokens[ tokens.length-1 ];
            const title = lastToken .substring( 0, lastToken .indexOf( '.vZome' ) );
            let details = 'NONSTANDARD PATH';
            if ( tokens.length === 5 ) {
              const date = `${ tokens[0] }-${ tokens[1] }-${ tokens[2] }`;
              const timeTokens = tokens[3] .split( '-' );
              details = `${date}     ${ timeTokens[0] }:${ timeTokens[1] }:${ timeTokens[2] }`;
            }
            return { title, details, path, url };
          } );
        console.log( 'Repo has', designs.length, 'entries.' );
        return designs;
      } )
      .catch( () => {
        return [];
      })
  )
}

export const getAssetUrl = ( githubUser, path ) =>
{
  const repoUrl = `https://github.com/${githubUser}/vzome-sharing/tree/main/`;
  const tokens = path .split( '/' ) .slice( 0, 4 );
  return repoUrl + tokens .map( encodeURIComponent ) .join( '/' );
}

export const getEmbeddingHtml = ( githubUser, path ) =>
{
  const sharingUrl = `https://${githubUser}.github.io/vzome-sharing/`;
  const tokens = path .split( '/' );
  const designUrl = sharingUrl + tokens .map( encodeURIComponent ) .join( '/' );
  tokens[4] = tokens[4] .substring( 0, tokens[4] .indexOf( '.vZome' ) ) + '.png';
  const imageUrl = sharingUrl + tokens .map( encodeURIComponent ) .join( '/' );
  return `<vzome-viewer style="width: 100%; height: 60vh"
    src="${designUrl}" >
  <img style="width: 100%"
    src="${imageUrl}" >
</vzome-viewer>
`;
}

