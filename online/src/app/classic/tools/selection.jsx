
import { createEffect } from "solid-js";

import { useWorkerClient } from "../../../workerClient/index.js";
import { useInteractionTool } from "../../../viewer/solid/interaction.jsx";
import { controllerAction, subController } from "../../../workerClient/controllers-solid.js";

const SelectionTool = props =>
{
  const { rootController, setState } = useWorkerClient();
  const pickingController  = () => subController( rootController(), 'picking' );

  const handlers = {

    allowTrackball: false,

    onClick: ( id, position, type, selected ) => {
      // console.log( 'selectionTool clicked' );
      controllerAction( pickingController(), 'SelectManifestation', { id } )
    },
    bkgdClick: () =>
    {
      controllerAction( rootController(), 'DeselectAll' );
    },
    onDragStart: ( id, position, type, starting, evt ) => {
      // console.log( 'selectionTool onDragStart?????!!!!!' );
    },
    onDrag: evt => {
      // console.log( 'selectionTool onDrag?????!!!!!' );
    },
    onDragEnd: evt => {
      // console.log( 'selectionTool onDragEnd?????!!!!!' );
    },
    onContextMenu: ( id, position, type, selected ) => {
      setState( 'picked', { id, position, type, selected } );
    }
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return null;
}

export { SelectionTool };