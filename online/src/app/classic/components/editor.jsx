
import { Switch, Match, createSignal } from 'solid-js';

import SvgIcon from '@suid/material/SvgIcon'
import ToggleButton from "@suid/material/ToggleButton";
import ToggleButtonGroup from "@suid/material/ToggleButtonGroup";

import { SceneCanvas } from '../../../viewer/solid/index.jsx';
import { useWorkerClient } from '../../../workerClient/index.js';
import { InteractionToolProvider } from '../../../viewer/solid/interaction.jsx';
import { SelectionTool } from '../tools/selection.jsx';
import { StrutDragTool } from '../tools/strutdrag.jsx';

export const SceneEditor = ( props ) =>
{
  const { state } = useWorkerClient();
  const [ strutting, setStrutting ] = createSignal( false );

  document .addEventListener( "keydown", evt => {
    if ( evt.repeat )
      return;
    if ( evt.code === "AltLeft" || evt.code === "AltRight" ) {
      evt .preventDefault();
      setStrutting( v => !v );
    }
  } );

  document .addEventListener( "keyup", evt => {
    if ( evt.code === "AltLeft" || evt.code === "AltRight" ) {
      evt .preventDefault();
      setStrutting( v => !v );
    }
  } );

  const handleToolMode = ( evt, newValue ) =>
  {
    if ( newValue ) {
      setStrutting( newValue === 'strutDrag' );
    }
  }

  // not using DesignViewer because it has its own UI, not corresponding to classic desktop vZome
  return (
    <div style={{ position: 'relative', display: 'flex', overflow: 'hidden', height: '100%' }}>
      <InteractionToolProvider>
        <SceneCanvas height="100%" width="100%" scene={state.scene} rotationOnly={false} >
          {/* The group is only necessary because of https://github.com/solidjs-community/solid-three/issues/11 */}
          <group>
            <Switch fallback={
                <SelectionTool/>
              }>
              <Match when={ strutting() }>
                <StrutDragTool/>
              </Match>
            </Switch>
          </group>
        </SceneCanvas>
      </InteractionToolProvider>
      <ToggleButtonGroup exclusive value={strutting()? 'strutDrag' : 'select' } aria-label="mouse tool mode"
          style={ { position: 'absolute', top: '1em', right: '1em' } }
          onChange={handleToolMode}
        >
        <ToggleButton value="select" aria-label="selection">
          <SvgIcon fontSize="large" width="2452" height="2452" viewBox="-370 -180 1782 1972">
            <path fill="currentColor" d="M1133 1043q31 30 14 69q-17 40-59 40H706l201 476q10 25 0 49t-34 35l-177 75q-25 10-49 0t-35-34l-191-452l-312 312q-19 19-45 19q-12 0-24-5q-40-17-40-59V64Q0 22 40 5q12-5 24-5q27 0 45 19z"/>
          </SvgIcon>        
        </ToggleButton>
        <ToggleButton value="strutDrag" aria-label="strut drag">
          <SvgIcon fontSize="large" width="24" height="24" viewBox="0 0 24 24">
            <path fill="currentColor" d="M4 9h5V4h6v5h5v6h-5v5H9v-5H4V9m7 4v5h2v-5h5v-2h-5V6h-2v5H6v2h5Z"/>
          </SvgIcon>
        </ToggleButton>
      </ToggleButtonGroup>
    </div>
  );
}