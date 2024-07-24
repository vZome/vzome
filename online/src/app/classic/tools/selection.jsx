
import { createEffect } from "solid-js";

import { useInteractionTool } from "../../../viewer/context/interaction.jsx";
import { subController, useEditor } from '../../framework/context/editor.jsx';

const SelectionTool = props =>
{
  const { rootController, setState, controllerAction } = useEditor();
  const pickingController  = () => subController( rootController(), 'picking' );

  const handlers = {

    allowTrackball: false,

    onClick: ( id, position, type, selected, label ) => {
      // console.log( 'selectionTool clicked' );
      controllerAction( pickingController(), 'SelectManifestation', { id } )
    },
    bkgdClick: () =>
    {
      controllerAction( rootController(), 'DeselectAll' );
    },
    onDragStart: () => {
      // console.log( 'selectionTool DRAG START' );
    },
    onDrag: () => {
      // console.log( 'selectionTool    DRAG' );
    },
    onDragEnd: () => {
      // console.log( 'selectionTool DRAG END' );
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