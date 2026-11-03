
import { createEffect } from 'solid-js';

import { useInteractionTool, useCameraControl } from '../../../viewer/context/interaction.jsx';
import { useEditor } from '../../framework/context/editor.jsx';
import { useSymmetry } from '../context/symmetry.jsx';
import { useMarquee } from './marquee.jsx';
import { useSelectClicks } from './selection.jsx';
import { useStrutDrag } from './strutdrag.jsx';

// Build mode: drag from a ball to build a strut, with the wheel adjusting its length while
// dragging.  What the other gestures do is composed from two options, each a user preference
// (see readBuildOptions below, and developer-docs/mouse-tools.md):
//
//   click   what a click does:                'none' or 'select'
//   drag    what a drag that doesn't start    'none', 'rotate' (snapping at the end, as in
//           on a ball does:                   desktop vZome), or 'marquee'
//
// Each option value lists the hint lines describing it; BuildTool composes its behavior below.
export const BUILD_CLICKS = {
  none:   [],
  select: [ 'Click: select or deselect', 'Click empty space: deselect all' ],
};
export const BUILD_DRAGS = {
  none:    [],
  rotate:  [ 'Drag elsewhere: rotate' ],
  marquee: [ 'Drag elsewhere: add everything inside a box' ],
};

const BUILD_CLICK_KEY = 'vzome.buildClick';
const BUILD_DRAG_KEY = 'vzome.buildDrag';

const readOption = ( key, values ) =>
{
  try {
    const value = globalThis.localStorage ?.getItem( key );
    if ( value in values )
      return value;
  } catch ( e ) {
    // localStorage can throw in sandboxed or partitioned iframes; use the default
  }
  return 'none';
}

// The user's build-mode options, { click, drag }, from localStorage.  Desktop vZome's behavior
// is click 'select' with drag 'rotate'.
export const readBuildOptions = () => ( {
  click: readOption( BUILD_CLICK_KEY, BUILD_CLICKS ),
  drag: readOption( BUILD_DRAG_KEY, BUILD_DRAGS ),
} );

// What build mode does with these options, beyond the general lines in the hint, e.g. for a tooltip.
export const buildSummary = ( { click = 'none', drag = 'none' } = {} ) =>
  [ 'Drag from a ball: build a strut', ...BUILD_CLICKS[ click ], ...BUILD_DRAGS[ drag ] ];

export const BuildTool = props =>
{
  const { setState } = useEditor();
  const { snapCamera } = useSymmetry();
  const camera = useCameraControl();
  const strut = useStrutDrag();
  const clicks = { none: {}, select: useSelectClicks() };
  const marquee = useMarquee();
  const drags = {
    none:    () => null,
    rotate:  e => camera.rotate( e, { onEnd: snapCamera } ),
    marquee: e => marquee.startDrag( e ),
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => {
    const click = props.click || 'none';
    const drag = drags[ props.drag ] || drags.none;
    const [ ballDrag, ...others ] = buildSummary( props );
    setTool( {

      cursor: 'cell',

      picks: { click: click !== 'none', drag: true, hover: false },

      hint: {
        title: 'Build mode (B)',
        lines: [
          ballDrag,
          'Wheel while dragging: strut length',
          ...others,
          'Wheel: zoom',
          'Right-click: menu',
          'Hold Alt: camera mode',
        ],
      },

      ...clicks[ click ],

      onDragStart: ( e, hit ) => strut.startDrag( e, hit ) || ( ( e.button === 0 )? drag( e ) : null ),

      onWheel: camera.onWheel,

      onContextMenu: ( e, { id, position, type, selected, label } ) => {
        setState( 'picked', { id, position, type, selected, label } );
      },
    } );
  } );

  return <strut.Preview debug={props.debug} />;
}
