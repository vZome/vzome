
import { createEffect } from "solid-js";

import { useInteractionTool, useCameraControl } from "../../../viewer/context/interaction.jsx";
import { subController, useEditor } from '../../framework/context/editor.jsx';
import { useMarquee } from './marquee.jsx';

// Selecting by clicks, as a behavior for tools: { onClick, bkgdClick } handlers that select or
// deselect the object clicked, or deselect everything on a click in empty space.
export const useSelectClicks = () =>
{
  const { rootController, controllerAction } = useEditor();
  const pickingController  = () => subController( rootController(), 'picking' );

  return {
    onClick: ( e, hit ) => {
      controllerAction( pickingController(), 'SelectManifestation', { id: hit.id } )
    },
    bkgdClick: () =>
    {
      controllerAction( rootController(), 'DeselectAll' );
    },
  };
}

// Select mode: only changes the selection.  Click an object to select or deselect it; click empty
// space to deselect everything; drag anywhere, even from an object, to add everything inside a box.
const SelectionTool = props =>
{
  const { setState } = useEditor();
  const camera = useCameraControl();
  const clicks = useSelectClicks();
  const marquee = useMarquee();

  const handlers = {

    picks: { click: true, drag: false, hover: false },

    hint: {
      title: 'Select mode (S)',
      lines: [
        'Click: select or deselect',
        'Click empty space: deselect all',
        'Drag: add everything inside a box',
        'Wheel: zoom',
        'Right-click: menu',
        'Hold Alt: camera mode',
      ],
    },

    ...clicks,
    onDragStart: e => ( e.button === 0 )? marquee.startDrag( e ) : null,
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
