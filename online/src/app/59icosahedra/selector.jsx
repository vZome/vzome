
import { createEffect } from "solid-js";

import { useWorkerClient } from "../../workerClient/index.js";
import { useInteractionTool } from "../../viewer/solid/interaction.jsx";
import { controllerAction, subController } from "../../workerClient/controllers-solid.js";

export const CellSelectorTool = props =>
{
  const { rootController } = useWorkerClient();
  const pickingController  = () => subController( rootController(), 'picking' );

  const handlers = {

    allowTrackball: true,

    onClick: ( id, position, type, selected ) => {
      if ( !type ) { // a panel
        controllerAction( pickingController(), 'SelectManifestation', { id } )
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
