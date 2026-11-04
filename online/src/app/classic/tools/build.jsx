
import { createEffect } from 'solid-js';

import { useInteractionTool, useCameraControl } from '../../../viewer/context/interaction.jsx';
import { useEditor } from '../../framework/context/editor.jsx';
import { useSymmetry } from '../context/symmetry.jsx';
import { useMarquee } from './marquee.jsx';
import { useSelectClicks } from './selection.jsx';
import { useStrutDrag } from './strutdrag.jsx';
import { createSetting } from '../../framework/settings.js';

// Build mode: drag from a ball to build a strut, with the wheel adjusting its length while
// dragging.  What the other gestures do is composed from two settings (see the Settings dialog,
// classic/settings.jsx, and developer-docs/mouse-tools.md):
//
//   buildClick   what a click does:                'none' or 'select'
//   buildDrag    what a left-drag that doesn't     'none', 'rotate' (snapping at the end, as in
//                start on a ball does:             desktop vZome), or 'marquee'
//
// Desktop vZome's behavior is 'select' with 'rotate'.  Each value has a label for the Settings
// dialog and the hint lines describing it; BuildTool composes the matching behavior below.
export const BUILD_CLICKS = {
  none:   { label: 'Nothing', lines: [] },
  select: { label: 'Select or deselect, as in select mode',
            lines: [ 'Click: select or deselect', 'Click empty space: deselect all' ] },
};
export const BUILD_DRAGS = {
  none:    { label: 'Nothing', lines: [] },
  rotate:  { label: 'Rotate the view, as in camera mode',
             lines: [ 'Drag elsewhere: rotate' ] },
  marquee: { label: 'Select everything inside a box, as in select mode',
             lines: [ 'Drag elsewhere: add everything inside a box' ] },
};

export const buildClick = createSetting( 'vzome.buildClick', BUILD_CLICKS, 'none' );
export const buildDrag = createSetting( 'vzome.buildDrag', BUILD_DRAGS, 'none' );

// What build mode does with the current settings, beyond the general lines in the hint, e.g.
// for a tooltip.  Reactive: it reads the settings.
export const buildSummary = () =>
  [ 'Drag from a ball: build a strut', ...BUILD_CLICKS[ buildClick.value() ].lines, ...BUILD_DRAGS[ buildDrag.value() ].lines ];

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
    // Re-run whenever a setting changes, so the Settings dialog takes effect at once.
    const click = buildClick.value();
    const drag = drags[ buildDrag.value() ];
    const [ ballDrag, ...others ] = buildSummary();
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
