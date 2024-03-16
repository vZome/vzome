
import { createEffect } from "solid-js";

import { useInteractionTool } from "../../../viewer/context/interaction.jsx";
import { subController, useEditor } from "../../../viewer/context/editor.jsx";

const SelectionTool = props =>
{
  const { rootController, setState, controllerAction } = useEditor();
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
    onContextMenu: ( id, position, type, selected, label ) => {
      setState( 'picked', { id, position, type, selected, label } );
    }
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return null;
}

export { SelectionTool };