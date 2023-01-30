
import React, { useEffect } from 'react'

export const useGitHubShares = githubUser =>
{
  const BASE_URL = `https://raw.githubusercontent.com/${githubUser}/vzome-sharing/main/`;
  const [ designs, setDesigns ] = React.useState( [] );

  useEffect( () => {
    localStorage.setItem( 'vzome-github-user', githubUser );
    fetch( `https://api.github.com/repos/${githubUser}/vzome-sharing/git/trees/main?recursive=1` )
      .then( response => response.json() )
      .then( json => {
        const designs = json.tree.filter( entry => entry.type==="blob" && entry.path.endsWith( '.vZome' ) )
          .filter( entry => entry.path > "2021/07/31" )
          .map( ({ path }) => {
            const tokens = path .split( '/' );
            const title = tokens[4] .substring( 0, tokens[4] .indexOf( '.vZome' ) );
            const url = BASE_URL + tokens .map( encodeURIComponent ) .join( '/' );
            const date = `${ tokens[0] }-${ tokens[1] }-${ tokens[2] }`;
            const timeTokens = tokens[3] .split( '-' );
            const details = `${date}     ${ timeTokens[0] }:${ timeTokens[1] }:${ timeTokens[2] }`;
            return { title, details, path, url };
          } );
        console.log( 'Repo has', designs.length, 'entries.' );
        setDesigns( designs );
      } );
  }, [githubUser] );

  return designs;
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
  return `<figure style="width: 87%; margin: 5%">
  <vzome-viewer style="width: 100%; height: 60vh"
      src="${designUrl}" >
    <img  style="width: 100%"
      src="${imageUrl}" >
  </vzome-viewer>
  <figcaption style="text-align: center; font-style: italic;">
    REPLACE this caption!
  </figcaption>
</figure>
`;
}

