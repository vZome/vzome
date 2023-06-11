
import { createEffect } from "solid-js";

import { useWorkerClient } from "../../../workerClient/index.js";
import { useInteractionTool } from "./interaction.jsx";
import { controllerAction, subController } from "../../../workerClient/controllers-solid.js";

const SelectionTool = props =>
{
  const { rootController } = useWorkerClient();
  const pickingController  = () => subController( rootController(), 'picking' );

  const handlers = {

    allowTrackball: true,

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
    }
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return null;
}

export { SelectionTool };