
import React, { useEffect } from 'react'

export const useGitHubShares = githubUser =>
{
  const BASE_URL = `https://raw.githubusercontent.com/${githubUser}/vzome-sharing/main/`;
  const [ designs, setDesigns ] = React.useState( [] );

  useEffect( () => {
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
            return { title, details, url };
          } );
        console.log( 'Repo has', designs.length, 'entries.' );
        setDesigns( designs );
      } );
  }, [] );

  return designs;
}

