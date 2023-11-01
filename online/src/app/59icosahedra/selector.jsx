
import { createEffect } from "solid-js";

import { useWorkerClient } from "../../workerClient/index.js";
import { useInteractionTool } from "../../viewer/solid/interaction.jsx";
import { controllerAction, subController } from "../../workerClient/controllers-solid.js";
import { c } from './macros.js';
import { doControllerMacro } from "../../workerClient/actions.js";

export const CellSelectorTool = props =>
{
  const { rootController } = useWorkerClient();
  const pickingController  = () => subController( rootController(), 'picking' );

  const handlers = {

    allowTrackball: true,

    onClick: ( id, position, type, selected, label ) => {
      if ( !!label ) { // a labeled panel
        controllerAction( pickingController(), 'SelectManifestation', { id } )
        console.log( selected? 'deselecting' : 'selecting', label );
        if ( selected && label === 'c' ) {

        }
      }
    },
    
    bkgdClick: () =>
    {
      controllerAction( rootController(), 'DeselectAll' );
    },

    onDragStart: ( id, position, type, starting, evt ) => {},
    onDrag: evt => {},
    onDragEnd: evt => {},
    onContextMenu: ( id, position, type, selected ) => {}
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return null;
}

export const MacroTriggerTool = props =>
{
  const { postMessage } = useWorkerClient();

  const handlers = {

    allowTrackball: true,

    onClick: ( id, position, type, selected, label ) => {},
    
    bkgdClick: () =>
    {
      postMessage( doControllerMacro( c ) );
    },

    onDragStart: ( id, position, type, starting, evt ) => {},
    onDrag: evt => {},
    onDragEnd: evt => {},
    onContextMenu: ( id, position, type, selected ) => {}
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return null;
}
