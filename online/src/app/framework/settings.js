
import { createSignal } from 'solid-js';

/*
  User settings, persisted in localStorage and reactive.

  createSetting( key, values, defaultValue ) returns { value, set }: value() is a signal
  read, and set( v ) stores v and updates every reader at once, so a settings dialog and the
  code that uses the setting stay in step.  Only the keys of `values` are accepted; anything
  else in storage, or storage that can't be read at all, means defaultValue.

  Create each setting once, at module level, and share it: two createSetting calls for the
  same key would not see each other's changes.

  Every localStorage access is guarded: it can throw outright in sandboxed or partitioned
  iframes (see savedcolors.js).  A setting that can't be stored still works for the session.
*/
export const createSetting = ( key, values, defaultValue ) =>
{
  const isValid = v => Object .hasOwn( values, v );

  let initial = defaultValue;
  try {
    const stored = globalThis.localStorage ?.getItem( key );
    if ( isValid( stored ) )
      initial = stored;
  } catch ( e ) {
    // use the default
  }
  const [ value, setValue ] = createSignal( initial );

  const set = v =>
  {
    if ( ! isValid( v ) )
      return;
    setValue( v );
    try {
      if ( v === defaultValue )
        globalThis.localStorage ?.removeItem( key );
      else
        globalThis.localStorage ?.setItem( key, v );
    } catch ( e ) {
      // not persisted, but still in effect for this session
    }
  }

  return { value, set };
}

// A setting that is on or off, for a checkbox: value() is a boolean, and set( v ) takes one.
export const createBooleanSetting = ( key, defaultValue ) =>
{
  const setting = createSetting( key, { true: true, false: false }, String( defaultValue ) );
  return {
    value: () => setting .value() === 'true',
    set: v => setting .set( String( !! v ) ),
  };
}
