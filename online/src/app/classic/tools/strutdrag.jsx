
import { createEffect, createSignal } from 'solid-js';
import { Vector3 } from 'three';
import { useThree } from "solid-three";

import { useInteractionTool } from '../../../viewer/solid/interaction.jsx';
import { useWorkerClient } from '../../../workerClient/context.jsx';
import { endPreviewStrut, movePreviewStrut, startPreviewStrut } from '../../../workerClient/actions.js';
import { ObjectTrackball } from './trackball.jsx';
import { VectorArrow } from './arrow.jsx';

/*

After digging through three.js TrackballControls.js

   https://github.com/mrdoob/three.js/blob/master/examples/jsm/controls/TrackballControls.js

I thought I could use it to manipulate a "camera" as long as that object had the same
"up", "position", "lookAt", and "isPerspectiveCamera" fields as an actual camera.
(Yay, duck typing!)

However, I failed miserably, and I found it simpler to just copy the event handling bits
of TrackballControls.js and implement the transformations myself.  See ObjectTrackball.

*/

const StrutDragTool = props =>
{
  const { postMessage } = useWorkerClient();
  const eye = useThree(({ camera }) => camera.position);

  const [ line, setLine ] = createSignal( [ 0, 0, 1 ] );
  const [ operating, setOperating ] = createSignal( null );
  const [ position, setPosition ] = createSignal( [0,0,0] );

  const handlers = {

    allowTrackball: false,

    cursor: 'cell',

    onClick: () => {},
    bkgdClick: () => {},
    onDrag: evt => {},

    onDragStart: ( id, position, type, starting, evt ) => {
      if ( type !== 'ball' )
        return;
      setPosition( position );
      const { x, y, z } = new Vector3() .copy( eye() ) .sub( new Vector3( ...position ) ) .normalize();
      setLine( [ x, y, z ] );
      postMessage( startPreviewStrut( id, [ x, y, z ] ) );
      setOperating( evt ); // so we can pass it to the ObjectTrackball
    },
    onDragEnd: evt => {
      if ( operating() ) {
        setOperating( null );
        postMessage( endPreviewStrut() );
      }
    }
  };

  createEffect( () => {
    if ( operating() ) {
      postMessage( movePreviewStrut( line() ) );
    }
  });

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return (
    <Show when={operating()}>
      <group position={position()}>
        <ObjectTrackball startEvent={operating()} line={line()} setLine={setLine} rotateSpeed={0.9} debug={props.debug} />
        <Show when={props.debug}>
          <VectorArrow vector={line()} />
        </Show>
      </group>
    </Show>
  );
}

export { StrutDragTool };