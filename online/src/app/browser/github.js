
import React, { useEffect } from 'react'

export const useGitHubShares = githubUser =>
{
  const [ designs, setDesigns ] = React.useState( [] );

  useEffect( () => {
    fetch( `https://api.github.com/repos/${githubUser}/vzome-sharing/git/trees/main?recursive=1` )
      .then( response => response.json() )
      .then( json => {
        const allPaths = json.tree.filter( entry => entry.type==="blob" && entry.path.endsWith( '.vZome' ) )
          .filter( entry => entry.path > "2021/07/31" )
          .map( entry => entry.path );
        console.log( 'Repo has', allPaths.length, 'entries.' );
        setDesigns( allPaths );
      } );
  }, [] );

  const getDesignUrl = index =>
  {
    const BASE_URL = `https://raw.githubusercontent.com/${githubUser}/vzome-sharing/main/`;
    const path = designs[ index ] .split( '/' ) .map( encodeURIComponent ) .join( '/' );
    return BASE_URL + path;
  }

  return [ designs, getDesignUrl ];
}

