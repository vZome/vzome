
import { createEffect } from "solid-js";

import { useInteractionTool, useCameraControl } from "../../../viewer/context/interaction.jsx";
import { subController, useEditor } from '../../framework/context/editor.jsx';

// Select mode: only changes the selection.  Click an object to select or deselect it; click empty
// space to deselect everything.  (Marquee selection by dragging is not implemented yet.)
const SelectionTool = props =>
{
  const { rootController, setState, controllerAction } = useEditor();
  const pickingController  = () => subController( rootController(), 'picking' );
  const camera = useCameraControl();

  const handlers = {

    picks: { click: true, drag: false, hover: false },

    hint: {
      title: 'Select mode (S)',
      lines: [
        'Click: select or deselect',
        'Click empty space: deselect all',
        'Wheel: zoom',
        'Right-click: menu',
        'Hold Alt: camera mode',
      ],
    },

    onClick: ( e, hit ) => {
      controllerAction( pickingController(), 'SelectManifestation', { id: hit.id } )
    },
    bkgdClick: () =>
    {
      controllerAction( rootController(), 'DeselectAll' );
    },
    onWheel: camera.onWheel,
    onContextMenu: ( e, { id, position, type, selected, label } ) => {
      setState( 'picked', { id, position, type, selected, label } );
    }
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return null;
}

export { SelectionTool };
