
import { Switch, Match, createSignal } from 'solid-js';

import TouchAppIcon from '@suid/icons-material/TouchApp';
import BuildIcon from '@suid/icons-material/Build';
import ToggleButton from "@suid/material/ToggleButton";
import ToggleButtonGroup from "@suid/material/ToggleButtonGroup";

import { SceneCanvas } from '../../../viewer/solid/index.jsx';
import { useWorkerClient } from '../../../workerClient/index.js';
import { InteractionToolProvider } from '../tools/interaction.jsx';
import { SelectionTool } from '../tools/selection.jsx';
import { StrutDragTool } from '../tools/strutdrag.jsx';

export const SceneEditor = ( props ) =>
{
  const { getScene } = useWorkerClient();
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
        <SceneCanvas height="100%" width="100%" scene={getScene()} >
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
          <TouchAppIcon fontSize="large" />
        </ToggleButton>
        <ToggleButton value="strutDrag" aria-label="strut drag">
          <BuildIcon    fontSize="large" />
        </ToggleButton>
      </ToggleButtonGroup>
    </div>
  );
}