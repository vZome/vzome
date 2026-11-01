
import { Switch, Match, Show, createSignal, onCleanup } from 'solid-js';

import { createT } from 'solid-three';
import { Group } from "three";
const T = createT({ Group });

import SvgIcon from '@suid/material/SvgIcon'
import ToggleButton from "@suid/material/ToggleButton";
import ToggleButtonGroup from "@suid/material/ToggleButtonGroup";

import { LabelDialog } from '../dialogs/label.jsx';
import { ColorPicker } from '../../framework/colorpicker.jsx';

import { useCamera } from '../../../viewer/context/camera.jsx';
import { InteractionToolProvider } from '../../../viewer/context/interaction.jsx';
import { useEditor } from '../../framework/context/editor.jsx';
import { resumeMenuKeyEvents, suspendMenuKeyEvents } from '../context/commands.jsx';
import { menuKeyEventsSuspended } from '../../framework/keyevents.js';
import { SceneCanvas } from '../../../viewer/index.jsx';
import { Spinner } from '../../../viewer/spinner.jsx';
import { useViewer } from '../../../viewer/context/viewer.jsx';

import { CameraTool } from '../tools/camera.jsx';
import { ContextualMenuArea } from '../../framework/menus.jsx';
import { ContextualMenu } from '../menus/contextmenu.jsx';
import { SelectionTool } from '../tools/selection.jsx';
import { StrutDragTool } from '../tools/strutdrag.jsx';
import { useScene } from '../../../viewer/context/scene.jsx';

export const SceneEditor = ( props ) =>
{
  const { setState } = useEditor();
  const { setLighting, state } = useCamera();
  const { scene } = useScene();
  // The mouse mode: 'camera', 'select' or 'build'.  See developer-docs/mouse-tools.md.
  const [ mode, setModeSignal ] = createSignal( 'build' );
  const viewing = () => mode() === 'camera';
  let lastEditingMode = mode(); // the most recent non-camera mode
  const setMode = newMode =>
  {
    if ( newMode !== 'camera' )
      lastEditingMode = newMode;
    setModeSignal( newMode );
  }

  // Holding Alt switches between camera mode and the last editing mode, until it is released.
  let modeBeforeAlt = null;
  const isAlt = evt => evt.code === "AltLeft" || evt.code === "AltRight";
  const MODE_KEYS = { KeyC: 'camera', KeyS: 'select', KeyB: 'build' };
  const isTyping = evt => evt.target ?.isContentEditable || [ 'INPUT', 'TEXTAREA', 'SELECT' ] .includes( evt.target ?.tagName );

  const onKeyDown = evt =>
  {
    if ( evt.repeat )
      return;
    if ( isAlt( evt ) ) {
      evt .preventDefault();
      modeBeforeAlt = mode();
      setMode( viewing()? lastEditingMode : 'camera' );
      return;
    }
    const keyMode = MODE_KEYS[ evt.code ];
    if ( keyMode && ! evt.metaKey && ! evt.ctrlKey && ! evt.altKey && ! evt.shiftKey
        && ! menuKeyEventsSuspended() && ! isTyping( evt ) ) {
      evt .preventDefault();
      modeBeforeAlt = null;
      setMode( keyMode );
    }
  }

  const onKeyUp = evt =>
  {
    if ( isAlt( evt ) ) {
      evt .preventDefault();
      if ( modeBeforeAlt )
        setMode( modeBeforeAlt );
      modeBeforeAlt = null;
    }
  }

  document .addEventListener( "keydown", onKeyDown );
  document .addEventListener( "keyup", onKeyUp );
  onCleanup( () => {
    document .removeEventListener( "keydown", onKeyDown );
    document .removeEventListener( "keyup", onKeyUp );
  } );

  const handleToolMode = ( evt, newValue ) =>
  {
    if ( newValue ) // null when the active button is clicked again
      setMode( newValue );
  }

  const resetPicked = opening =>
  {
    if ( !opening )
      // picked must be re-evaluated with each click
      setState( 'picked', undefined );
  }

  // The background picker previews live, as the old native input did on its "input" event.
  // Cancel therefore has to restore the color we started with, so we stash it on open.
  const [ pickingColor, setPickingColor ] = createSignal( false );
  const [ priorBackground, setPriorBackground ] = createSignal( null );
  const background = () => state.lighting.backgroundColor;
  const previewBackground = color => setLighting( { backgroundColor: color } );
  const closeColorPicker = hex =>
  {
    setPickingColor( false );
    if ( !hex ) // cancelled; undo the live preview
      setLighting( { backgroundColor: priorBackground() } );
  }
  const [ labeling, setLabeling ] = createSignal( null );
  const [ label, setLabel ] = createSignal( null );
  const showDialog = (key,...rest) =>
  {
    switch (key) {

      case 'color':
        setPriorBackground( background() );
        setPickingColor( true );
        break;
    
      case 'label':
        const [ id, label ] = rest;
        setLabel( label );
        suspendMenuKeyEvents();
        setLabeling( id );
        break;
    
      default:
        break;
    }
  }
  const hideLabelDialog = () =>
  {
    resumeMenuKeyEvents();
    setLabeling( null );
  }

  const { waiting } = useViewer();

  // not using DesignViewer because it has its own UI, not corresponding to classic desktop vZome
  return (
    <div class="relative-h100">
      <Show when={waiting()}>
        <Spinner/>
      </Show>
      <ColorPicker show={pickingColor()} close={closeColorPicker} title="Background Color"
          color={priorBackground()} onPreview={previewBackground} />
      <LabelDialog open={!!labeling()} close={hideLabelDialog} id={labeling()} label={label()} />
      <InteractionToolProvider>
        <ContextualMenuArea menu={<ContextualMenu showDialog={showDialog} />} class="absolute-0" disabled={viewing()} onOpenChange={resetPicked}>
          <SceneCanvas symmetryRenderer={true} height="100%" width="100%" scene={scene} rotationOnly={false} hints={true} >
            {/* The group is only necessary because of https://github.com/solidjs-community/solid-three/issues/11 */}
            <T.Group>
              <Switch>
                <Match when={ mode() === 'camera' }>
                  <CameraTool/>
                </Match>
                <Match when={ mode() === 'select' }>
                  <SelectionTool/>
                </Match>
                <Match when={ mode() === 'build' }>
                  <StrutDragTool/>
                </Match>
              </Switch>
            </T.Group>
          </SceneCanvas>
        </ContextualMenuArea>
      </InteractionToolProvider>
      <ToggleButtonGroup exclusive value={ mode() } aria-label="mouse tool mode"
          style={ { position: 'absolute', top: '1em', right: '1em' } }
          onChange={handleToolMode}
        >
        <ToggleButton value="camera" aria-label="camera" title={ "Camera (C)\nDrag: rotate · Right-drag: pan · Wheel: zoom" }>
          <SvgIcon fontSize="large" width="24" height="24" viewBox="0 0 24 24">
            <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><path d="M16.466 7.5C15.643 4.237 13.952 2 12 2C9.239 2 7 6.477 7 12s2.239 10 5 10c.342 0 .677-.069 1-.2m2.194-8.093l3.814 1.86l-1.86 3.814"/><path d="M19 15.57c-1.804.885-4.274 1.43-7 1.43c-5.523 0-10-2.239-10-5s4.477-5 10-5c4.838 0 8.873 1.718 9.8 4"/></g>
          </SvgIcon>
        </ToggleButton>
        <ToggleButton value="select" aria-label="select" title={ "Select (S)\nClick: select · Click empty space: deselect all" }>
          <SvgIcon fontSize="large" width="2452" height="2452" viewBox="-370 -180 1782 1972">
            <path fill="currentColor" d="M1133 1043q31 30 14 69q-17 40-59 40H706l201 476q10 25 0 49t-34 35l-177 75q-25 10-49 0t-35-34l-191-452l-312 312q-19 19-45 19q-12 0-24-5q-40-17-40-59V64Q0 22 40 5q12-5 24-5q27 0 45 19z"/>
          </SvgIcon>        
        </ToggleButton>
        <ToggleButton value="build" aria-label="build" title={ "Build (B)\nDrag from a ball: build a strut" }>
          <SvgIcon fontSize="large" width="24" height="24" viewBox="0 0 24 24">
            <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-width="2.2">
              <path d="M8.2 15.8L17 7"/>
              <circle cx="18.5" cy="5.5" r="2.2" stroke-width="1.6" stroke-dasharray="2 1.6"/>
            </g>
            <circle cx="6" cy="18" r="3.4" fill="currentColor"/>
          </SvgIcon>
        </ToggleButton>
      </ToggleButtonGroup>
    </div>
  );
}