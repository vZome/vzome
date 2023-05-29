
import { Switch, Match, createSignal } from 'solid-js';

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
    if ( evt.code === "AltLeft" || evt.code === "AltRight" ) {
      evt .preventDefault();
      setStrutting( true );
    }
  } );

  document .addEventListener( "keyup", evt => {
    if ( evt.code === "AltLeft" || evt.code === "AltRight" ) {
      evt .preventDefault();
      setStrutting( false );
    }
  } );

  return (
    // not using DesignViewer because it has its own UI, not corresponding to classic desktop vZome
    <InteractionToolProvider>
      <SceneCanvas height="880px" width="100%" scene={getScene()} >
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
  );
}