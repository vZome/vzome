
import { createEffect, createSignal, Show } from 'solid-js';
import { createT } from 'solid-three';
import { Group } from "three";
const T = createT({ Group });

import { ObjectTrackball } from './trackball.jsx';
import { VectorArrow } from './arrow.jsx';
import { useEditor } from '../../framework/context/editor.jsx';
import { useCamera } from '../../../viewer/context/camera.jsx';

/*

Strut dragging, as a behavior for tools: drag from a ball to build a strut, rolling its direction
like a trackball, with the wheel adjusting its length while dragging.  startDrag( e, hit ) returns
the drag object, or null if the drag didn't start on a ball.  The tool must render Preview, which
puts the ObjectTrackball in the scene while a strut is being dragged.

After digging through three.js TrackballControls.js

   https://github.com/mrdoob/three.js/blob/master/examples/jsm/controls/TrackballControls.js

I thought I could use it to manipulate a "camera" as long as that object had the same
"up", "position", "lookAt", and "isPerspectiveCamera" fields as an actual camera.
(Yay, duck typing!)

However, I failed miserably, and I found it simpler to just copy the event handling bits
of TrackballControls.js and implement the transformations myself.  See ObjectTrackball.

*/

export const useStrutDrag = () =>
{
  const { state } = useCamera();
  const { startPreviewStrut, endPreviewStrut, movePreviewStrut, scalePreviewStrut } = useEditor();

  const [ line, setLine ] = createSignal( [ 0, 0, 1 ] );
  const [ operating, setOperating ] = createSignal( null );
  const [ position, setPosition ] = createSignal( [0,0,0] );

  const MOUSE_WHEEL_TICKS_PER_SCALE = 25;

  const startDrag = ( evt, hit ) =>
  {
    if ( evt.button !== 0 || hit ?.source !== 'scene' || hit.type !== 'ball' )
      return null;
    let totalY = 0;
    setPosition( hit.position );
    const eye = state .camera .lookDir .map( x => -x ); // invert the lookDir to get eye direction in world coordinates
    setLine( eye );
    startPreviewStrut( hit.id, eye );
    setOperating( evt ); // so we can pass it to the ObjectTrackball
    return {
      onWheel: e => {
        // Logic copied from the desktop implementation, so we are not so sensitive.
        //    (See LengthCanvasTool.java)
        totalY += e.deltaY;
        let increment = 0;
        if ( totalY > MOUSE_WHEEL_TICKS_PER_SCALE )
          increment = +1;
        else if ( totalY < -MOUSE_WHEEL_TICKS_PER_SCALE )
          increment = -1;
        if ( increment ) {
          scalePreviewStrut( -increment ); // sense must be reversed
          totalY = 0;
        }
      },
      onDragEnd: () => {
        setOperating( null );
        endPreviewStrut();
      },
    };
  }

  createEffect( () => {
    if ( operating() ) {
      movePreviewStrut( line() );
    }
  });

  const Preview = props => (
    <Show when={operating()}>
      <T.Group position={position()}>
        <ObjectTrackball startEvent={operating()} line={line()} setLine={setLine} rotateSpeed={0.9} debug={props.debug} />
        <Show when={props.debug}>
          <VectorArrow vector={line()} />
        </Show>
      </T.Group>
    </Show>
  );

  return { startDrag, Preview };
}
