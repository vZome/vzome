
import { createContext, useContext } from "solid-js";
import { unwrap } from "solid-js/store";

import { controllerExportAction, subController, useEditor } from "../../framework/context/editor.jsx";
import { useCamera } from "../../../viewer/context/camera.jsx";
import { useSymmetry } from './symmetry.jsx';
import { saveTextFileAs, saveTextFile } from "../../../viewer/util/files.js";
import { useViewer } from "../../../viewer/context/viewer.jsx";

let menuKeyEventsSuspended = false;

export const suspendMenuKeyEvents = () => menuKeyEventsSuspended = true;
export const resumeMenuKeyEvents = () => menuKeyEventsSuspended = false;

const CommandsContext = createContext();

export const CommandsProvider = props =>
{
  const { rootController, controllerAction, state, setState } = useEditor();
  const { showPolytopesDialog, symmetryController } = useSymmetry();
  const { state: cameraState } = useCamera();
  const { scenes } = useViewer();
  const globalAction   = action => () => controllerAction( rootController(), action );
  const undoRedoAction = action => () => controllerAction( subController( rootController(), 'undoRedo' ), action );
  const symmetryAction = ( symm, action ) => () => controllerAction( subController( rootController(), `symmetry.${symm}` ), action );
  
  const doSave = ( chooseFile = false ) =>
  {
    let name;
    const { camera, lighting } = unwrap( cameraState );
    controllerExportAction( rootController(), 'vZome', { camera, lighting, scenes: unwrap( scenes ) } )
      .then( text => {
        name = state?.designName || 'untitled';
        const mimeType = 'application/xml';
        if ( state.fileHandle && !chooseFile )
          return saveTextFile( state.fileHandle, text, mimeType )
        else
          return saveTextFileAs( name + '.vZome', text, mimeType );
      })
      .then( result => {
        const { handle, success } = result;
        if ( success ) {
          if ( !!handle ) { // file system API supported
            setState( 'fileHandle', handle );
            name = handle.name;
            if ( name .toLowerCase() .endsWith( '.vZome' .toLowerCase() ) )
              name = name .substring( 0, name.length - 6 );
          }
          setState( 'designName', name ); // cooperatively managed by both worker and client
          controllerAction( rootController(), 'clearChanges' );
        }
      })
  }
  
  const doCut = () =>
  {
    controllerExportAction( rootController(), 'cmesh', { selection: true } )
      .then( text => {
        navigator.clipboard .writeText( text );
        controllerAction( rootController(), 'Delete' );
      });
  }
  const doCopy = () =>
  {
    controllerExportAction( rootController(), 'cmesh', { selection: true } )
      .then( text => {
        navigator.clipboard .writeText( text );
      });
  }
  const doPaste = () =>
  {
    if ( ! navigator.clipboard .readText ) {
      console.warn( 'Clipboard paste is not supported in this browser.' );
      setState( 'problem', 'Clipboard paste is not supported in this browser.' );
      return;
    }
    navigator.clipboard .readText()
      .then( text => {
        controllerAction( rootController(), "ImportColoredMeshJson/clipboard", { vef: text } )
      });
  }
      
  const commands = {};

  const listeners = new Set();
  const registerKeyListener = ( modifiers, deleteKey, key, handler ) =>
  {
    const listenerName = deleteKey? "⌫" : modifiers + key;
    if ( ! listeners .has( listenerName ) ) {
      console.log( `addEventListener ${listenerName}` );
      const targetCodes = deleteKey? [ 'Delete', 'Backspace' ] : [ "Key" + key.toUpperCase() ];
      const hasMeta = !! modifiers ?.includes( '⌘' );
      const hasControl = !! modifiers ?.includes( '⌃' );
      const hasShift = !! modifiers ?.includes( '⇧' );
      const hasOption = !! modifiers ?.includes( '⌥' );
      document.body .addEventListener( "keydown", evt => {
        if ( menuKeyEventsSuspended )
          return;
        if ( targetCodes .indexOf( evt.code ) < 0 )
          return;
        if ( hasMeta !== evt.metaKey )
          return;
        if ( hasControl !== evt.ctrlKey )
          return;
        if ( hasShift !== evt.shiftKey )
          return;
        if ( hasOption !== evt.altKey )
          return;
        evt .preventDefault(); // Why doesn't this work for ⌘N?
        handler();
      } );
      listeners .add( listenerName );
    }
  }

  const isMac = navigator.userAgentData?.platform === 'macOS' || navigator.userAgent .includes( 'Macintosh' );

  const createCommand = ( action, keyEquiv, handler=globalAction(action) ) =>
  {
    let keystroke = '';  
    if ( !! keyEquiv ) {
      let modifiers = keyEquiv.mods;
      let prefix = modifiers;
      if ( !isMac && modifiers ) {
        modifiers = modifiers .replace( '⌘', '⌃' );
        prefix = '';
        if ( modifiers .includes( '⌃' ) )
          prefix += 'Ctrl';
        if ( modifiers .includes( '⇧' ) )
          prefix += prefix? '+Shift' : 'Shift';
        if ( modifiers .includes( '⌥' ) )
          prefix += prefix? '+Alt' : 'Alt';
        if ( prefix )
          prefix += '-';
      }
      registerKeyListener( modifiers, keyEquiv.deleteKey, keyEquiv.key, handler );
      keystroke = (!keyEquiv)? '' : ( keyEquiv.deleteKey? "⌫" : prefix + keyEquiv.key );
    }
    commands[ action ] = { keystroke, handler };
    return commands[ action ];
  }
  const getCommand = action =>
  {
    let command = commands[ action ];
    if ( !command ) {
      command = createCommand( action );
    }
    return command;
  }
  
  createCommand( 'Save',              { mods:"⌘",  key:"S" }, doSave );
  createCommand( 'SaveAs',            { mods:"⌥⌘", key:"S" }, () => doSave( true ) );

  createCommand( 'undo',              { mods:"⌘",  key:"Z" }, undoRedoAction( 'undo' ) );
  createCommand( 'undoAll',           { mods:"⌥⌘", key:"Z" }, undoRedoAction( 'undoAll' ) );
  createCommand( 'redo',              { mods:"⌘",  key:"Y" }, undoRedoAction( 'redo' ) );
  createCommand( 'redoAll',           { mods:"⌥⌘", key:"Y" }, undoRedoAction( 'redoAll' ) );

  createCommand( 'Cut',               { mods:"⌘",  key:"X" }, doCut );
  createCommand( 'Copy',              { mods:"⌘",  key:"C" }, doCopy );
  createCommand( 'Paste',             { mods:"⌘",  key:"V" }, doPaste );
  createCommand( 'Delete',            { deleteKey: true } );

  createCommand( 'SelectAll',         { mods:"⌘",  key:"A" } );
  createCommand( 'SelectNeighbors',   { mods:"⌥⌘", key:"A" } );
  createCommand( 'InvertSelection' );

  createCommand( 'GroupSelection/group',   { mods:"⌘", key:"G" } );
  createCommand( 'GroupSelection/ungroup', { mods:"⌥⌘", key:"G" } );

  createCommand( 'hideball',               { mods:"⌃", key:"H" } );
  createCommand( 'ShowHidden',             { mods:"⌥⌃", key:"H" } );

  createCommand( 'JoinPoints/CLOSED_LOOP', { mods:"⌘",  key:"J" } );
  createCommand( 'JoinPoints/CHAIN_BALLS', { mods:"⌥⌘", key:"J" } );
  createCommand( 'panel',                  { mods:"⌘",  key:"P" } );
  createCommand( 'Parallelepiped',         { mods:"⇧⌘", key:"P" } );

  createCommand( 'icosasymm',          { mods:"⌘",  key:"I" }, symmetryAction( 'icosahedral', 'icosasymm' ) );
  createCommand( 'octasymm',           { mods:"⌥⌘", key:"C" }, symmetryAction( 'octahedral', 'octasymm' ) );
  createCommand( 'tetrasymm',          { mods:"⌥⌘", key:"T" }, symmetryAction( 'octahedral', 'tetrasymm' ) );
  createCommand( 'showPolytopeDialog', { mods:"⌥⌘", key:"P" }, showPolytopesDialog );

  return (
    <CommandsContext.Provider value={{ registerKeyListener, getCommand }}>
      {props.children}
    </CommandsContext.Provider>
  );
}

export const useCommands = () => useContext( CommandsContext );
