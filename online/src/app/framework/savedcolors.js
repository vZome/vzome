
/*
  The user's saved colors for the color picker, persisted in localStorage.

  An entry is { hex, name }, where hex is "#rrggbb" and name is a possibly-empty string
  (naming is optional).  The list defaults to empty.

  Every localStorage access is guarded: it can throw outright in sandboxed or partitioned
  iframes, which is exactly where the vzome-viewer web component runs.  See the same
  precaution in viewer/renderer-support.js.  A failure to read yields an empty list, and a
  failure to write is non-fatal -- the colors simply do not outlive the session.
*/

const STORAGE_KEY = 'vzome-saved-colors';

const isHex = value => /^#[0-9a-fA-F]{6}$/ .test( value || '' );

export const readSavedColors = () =>
{
  let stored;
  try {
    stored = globalThis.localStorage ?.getItem( STORAGE_KEY );
  }
  catch {
    return []; // storage unavailable (sandboxed iframe)
  }
  if ( ! stored )
    return [];
  try {
    const parsed = JSON .parse( stored );
    if ( ! Array .isArray( parsed ) )
      return [];
    // Validate on the way in.  This is user-editable storage that may also have been
    //  written by an older version, so a bad entry is dropped rather than trusted.
    return parsed
      .filter( entry => entry && isHex( entry.hex ) )
      .map( ( { hex, name } ) => ( { hex, name: typeof name === 'string'? name : '' } ) );
  }
  catch ( e ) {
    console.error( "Error parsing saved colors from localStorage:", e );
    return [];
  }
}

export const writeSavedColors = colors =>
{
  try {
    globalThis.localStorage ?.setItem( STORAGE_KEY, JSON .stringify( colors ) );
  }
  catch {
    /* non-fatal: the colors just will not persist beyond this session */
  }
}
