
import { createSignal } from 'solid-js';

import { useInteractionSurface } from '../../../viewer/context/interaction.jsx';
import { subController, useEditor } from '../../framework/context/editor.jsx';

// Marquee selection, as a behavior for tools: startDrag( e ) returns the drag object for a drag
// that draws a box, and on release adds every object entirely inside it to the selection.  It
// only adds: objects already selected stay selected, matching clicks, which never replace the
// selection.  The worker records one SelectManifestation per object, which undo together.
export const useMarquee = () =>
{
  const surface = useInteractionSurface();
  const { rootController, controllerAction } = useEditor();

  const startDrag = down =>
  {
    if ( ! surface )
      return null;
    const canvasRect = down.target .getBoundingClientRect();
    const [ corner, setCorner ] = createSignal( { x: down.clientX, y: down.clientY } );
    const rect = () => {
      const { x, y } = corner();
      return {
        left: Math.min( down.clientX, x ), right: Math.max( down.clientX, x ),
        top: Math.min( down.clientY, y ), bottom: Math.max( down.clientY, y ),
      };
    };

    // Drawn in the canvas's overlay layer, so positioned relative to the canvas.
    const MarqueeBox = () => (
      <div style={{
        position: 'absolute', 'box-sizing': 'border-box',
        left: `${rect().left - canvasRect.left}px`, top: `${rect().top - canvasRect.top}px`,
        width: `${rect().right - rect().left}px`, height: `${rect().bottom - rect().top}px`,
        border: '1px dashed white', outline: '1px dashed rgba(0,0,0,0.5)',
        background: 'rgba(255,255,255,0.12)',
      }} />
    );
    surface .setOverlay( MarqueeBox );

    return {
      onDrag: e => setCorner( { x: e.clientX, y: e.clientY } ),
      onDragEnd: async () => {
        surface .setOverlay( null );
        const hits = await surface .hitsInRect( rect() );
        const ids = [ ...new Set( hits .filter( hit => hit.source === 'scene' && ! hit.selected ) .map( hit => hit.id ) ) ];
        if ( ids.length )
          controllerAction( subController( rootController(), 'picking' ), 'SelectManifestations', { ids } );
      },
    };
  }

  return { startDrag };
}
