
import { Switch, Match, createSignal, createEffect } from 'solid-js';

import SvgIcon from '@suid/material/SvgIcon'
import ToggleButton from "@suid/material/ToggleButton";
import ToggleButtonGroup from "@suid/material/ToggleButtonGroup";

import { LabelDialog } from '../dialogs/label.jsx';

import { useCamera } from '../../../viewer/context/camera.jsx';
import { useViewer } from '../../../viewer/context/viewer.jsx';
import { InteractionToolProvider } from '../../../viewer/context/interaction.jsx';
import { useEditor } from '../../framework/context/editor.jsx';
import { resumeMenuKeyEvents, suspendMenuKeyEvents } from '../context/commands.jsx';
import { SceneCanvas } from '../../../viewer/index.jsx';

import { SnapCameraTool } from '../tools/snapcamera.jsx';
import { SelectionTool } from '../tools/selection.jsx';
import { StrutDragTool } from '../tools/strutdrag.jsx';
import { ContextualMenuArea } from '../../framework/menus.jsx';
import { ContextualMenu } from '../menus/contextmenu.jsx';
import { UnifiedTool } from '../tools/unified.jsx';

export const SceneEditor = ( props ) =>
{
  const { setState } = useEditor();
  const { setLighting } = useCamera();
  const { scene } = useViewer();
  const [ viewing, setViewing ] = createSignal( false );
  const toolValue = () => viewing()? 'camera' : 'select';

  document .addEventListener( "keydown", evt => {
    if ( evt.repeat )
      return;
    if ( evt.code === "AltLeft" || evt.code === "AltRight" ) {
      evt .preventDefault();
      setViewing( v => !v );
    }
  } );

  document .addEventListener( "keyup", evt => {
    if ( evt.code === "AltLeft" || evt.code === "AltRight" ) {
      evt .preventDefault();
      setViewing( v => !v );
    }
  } );

  const handleToolMode = ( evt, newValue ) =>
  {
    if ( newValue === 'camera' ) {
      setViewing( true );
    } else {
      setViewing( false );
    }
  }

  const resetPicked = opening =>
  {
    if ( !opening )
      // picked must be re-evaluated with each click
      setState( 'picked', undefined );
  }

  let colorPicker;
  createEffect( () => {
    colorPicker .addEventListener( "input", e => {
      const color = e.target.value;
      console.log( 'new background is', color );
      setLighting( { backgroundColor: color } )
    }, false );
  });
  const [ labeling, setLabeling ] = createSignal( null );
  const [ label, setLabel ] = createSignal( null );
  const showDialog = (key,...rest) =>
  {
    switch (key) {

      case 'color':
        colorPicker .click();
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

  // not using DesignViewer because it has its own UI, not corresponding to classic desktop vZome
  return (
    <div class="relative-h100">
      <input ref={colorPicker} type="color" name="color-picker" class='hidden-color-input' />
      <LabelDialog open={!!labeling()} close={hideLabelDialog} id={labeling()} label={label()} />
      <InteractionToolProvider>
        <ContextualMenuArea menu={<ContextualMenu showDialog={showDialog} />} class="absolute-0" disabled={viewing()} onOpenChange={resetPicked}>
          <SceneCanvas height="100%" width="100%" scene={scene} rotationOnly={false} >
            {/* The group is only necessary because of https://github.com/solidjs-community/solid-three/issues/11 */}
            <group>
              <Switch fallback={
                  <UnifiedTool/>
                }>
                <Match when={ viewing() }>
                  <SnapCameraTool/>
                </Match>
              </Switch>
            </group>
          </SceneCanvas>
        </ContextualMenuArea>
      </InteractionToolProvider>
      <ToggleButtonGroup exclusive value={ toolValue() } aria-label="mouse tool mode"
          style={ { position: 'absolute', top: '1em', right: '1em' } }
          onChange={handleToolMode}
        >
        <ToggleButton value="camera" aria-label="camera">
          <SvgIcon fontSize="large" width="24" height="24" viewBox="0 0 24 24">
            <g fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"><path d="M16.466 7.5C15.643 4.237 13.952 2 12 2C9.239 2 7 6.477 7 12s2.239 10 5 10c.342 0 .677-.069 1-.2m2.194-8.093l3.814 1.86l-1.86 3.814"/><path d="M19 15.57c-1.804.885-4.274 1.43-7 1.43c-5.523 0-10-2.239-10-5s4.477-5 10-5c4.838 0 8.873 1.718 9.8 4"/></g>
          </SvgIcon>
        </ToggleButton>
        <ToggleButton value="select" aria-label="selection">
          <SvgIcon fontSize="large" width="2452" height="2452" viewBox="-370 -180 1782 1972">
            <path fill="currentColor" d="M1133 1043q31 30 14 69q-17 40-59 40H706l201 476q10 25 0 49t-34 35l-177 75q-25 10-49 0t-35-34l-191-452l-312 312q-19 19-45 19q-12 0-24-5q-40-17-40-59V64Q0 22 40 5q12-5 24-5q27 0 45 19z"/>
          </SvgIcon>        
        </ToggleButton>
      </ToggleButtonGroup>
    </div>
  );
}