
import { fetchGists, GHRequestError } from "./gists";

const isRateLimited = error => error instanceof GHRequestError && error.limits?.remaining === '0';

// Resolves to { designs, rateLimited, resetAt } rather than throwing, so a rate-limit hit
// is just data to check, not an exception that createResource would need to rethrow on every read.
export const fetchGitHubShares = async githubUser =>
{
  const BASE_URL = `https://raw.githubusercontent.com/${githubUser}/vzome-sharing/main/`;

  if ( ! githubUser ) {
    return { designs: [], rateLimited: false };
  }

  const gistsPromise =
    fetchGists( githubUser )
      .then( ({ gists }) => {
        const designs = [];
        for (const { files, created_at, id } of gists) {
          const date = new Date( created_at );
          const details = created_at .replace( 'T', ' ' ) .replace( 'Z', '' ) + ' GIST';
          const path = `https://gist.github.com/${githubUser}/${id}`;
          for ( const [key, value] of Object.entries(files) ) {
            const { filename, raw_url } = value;
            if ( filename .endsWith( '.vZome' ) ) {
              const title = filename .substring( 0, filename .indexOf( '.vZome' ) );
              designs.push( { kind: 'gist', title, details, path, url: raw_url, date } );
            }
          }
        };
        return designs;
      } )
      .catch( error => {
        if ( isRateLimited( error ) )
          throw error;
        return [];
      } );

  const repoPromise =
    fetch( `https://api.github.com/repos/${githubUser}/vzome-sharing/git/trees/main?recursive=1` )
      .then( async response => {
        if ( ! response.ok && response.headers.get( 'x-ratelimit-remaining' ) === '0' ) {
          const reset = new Date( response.headers.get( 'x-ratelimit-reset' ) * 1000 );
          throw new GHRequestError( { remaining: '0', reset }, response.statusText || response.status );
        }
        return response.json();
      } )
      .then( json => {
        const designs = ( json.tree || [] ) .filter( entry => entry.type==="blob" && entry.path.endsWith( '.vZome' ) )
          .map( ({ path }) => {
            const tokens = path .split( '/' );
            const url = BASE_URL + tokens .map( encodeURIComponent ) .join( '/' );
            const lastToken = tokens[ tokens.length-1 ];
            const title = lastToken .substring( 0, lastToken .indexOf( '.vZome' ) );
            // dated paths look like YYYY/MM/DD/HH-MM-SS[-mmmZ]-title-slug/filename.vZome
            const timeMatch = tokens.length === 5 && /^\d{4}$/.test( tokens[0] ) && /^\d{2}$/.test( tokens[1] ) && /^\d{2}$/.test( tokens[2] )
              && tokens[3] .match( /^(\d{2})-(\d{2})-(\d{2})(?:[-.]\d{3}Z?)?(?:-.*)?$/ );
            if ( timeMatch ) {
              const [ year, month, day ] = tokens;
              const [ , hour, minute, second ] = timeMatch;
              const date = new Date( year, month-1, day, hour, minute, second );
              const details = `${year}-${month}-${day}     ${hour}:${minute}:${second}`;
              return { kind: 'dated', title, details, path, url, date };
            }
            return { kind: 'other', title, details: 'no timestamp', path, url, tokens };
          } );
        console.log( 'Repo has', designs.length, 'entries.' );
        return designs;
      } )
      .catch( error => {
        if ( isRateLimited( error ) )
          throw error;
        return [];
      } );

  const [ repoResult, gistResult ] = await Promise.allSettled( [ repoPromise, gistsPromise ] );

  const rateLimitError = [ repoResult, gistResult ] .find( r => r.status === 'rejected' ) ?.reason;
  if ( rateLimitError )
    return { designs: [], rateLimited: true, resetAt: rateLimitError.limits?.reset };

  return {
    designs: [ ...gistResult.value, ...repoResult.value ],
    rateLimited: false,
  };
}

// Checks that a GitHub account exists and has a public vzome-sharing repository.
// Resolves to { valid, rateLimited, resetAt }, never throws.
export const verifyGithubUser = async githubUser =>
{
  try {
    const response = await fetch( `https://api.github.com/repos/${githubUser}/vzome-sharing` );
    if ( ! response.ok && response.headers.get( 'x-ratelimit-remaining' ) === '0' ) {
      const resetAt = new Date( response.headers.get( 'x-ratelimit-reset' ) * 1000 );
      return { valid: false, rateLimited: true, resetAt };
    }
    return { valid: response.ok, rateLimited: false };
  }
  catch {
    return { valid: false, rateLimited: false };
  }
}

export const getAssetUrl = ( githubUser, path ) =>
{
  const repoUrl = `https://github.com/${githubUser}/vzome-sharing/tree/main/`;
  const tokens = path .split( '/' ) .slice( 0, 4 );
  return repoUrl + tokens .map( encodeURIComponent ) .join( '/' );
}

export const getEmbeddingHtml = ( githubUser, path, url ) =>
{
  if ( path .startsWith( 'https://gist.github.com' ) ) {
    return `<vzome-viewer style="width: 100%; height: 60vh"
  src="${url}">
</vzome-viewer>
`;
  }

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

