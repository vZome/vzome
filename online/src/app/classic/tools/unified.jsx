
import { createEffect, createSignal } from 'solid-js';
import { Vector3 } from 'three';
import { useThree } from "solid-three";

import { useInteractionTool } from '../../../viewer/context/interaction.jsx';
import { ObjectTrackball } from './trackball.jsx';
import { VectorArrow } from './arrow.jsx';
import { subController, useEditor } from '../../framework/context/editor.jsx';

/*
This combines the behaviors of StrutDragTool and SelectionTool, to better
emulate the behavior of desktop vZome.

I'd like to reuse their code, but that seems too complicated for the benefit.
*/

const UnifiedTool = props =>
{
  const eye = useThree(({ camera }) => camera.position);
  const { startPreviewStrut, endPreviewStrut, movePreviewStrut, scalePreviewStrut,
          rootController, setState, controllerAction } = useEditor();
  const pickingController  = () => subController( rootController(), 'picking' );

  const [ line, setLine ] = createSignal( [ 0, 0, 1 ] );
  const [ operating, setOperating ] = createSignal( null );
  const [ position, setPosition ] = createSignal( [0,0,0] );

  let totalY = 0;
  const MOUSE_WHEEL_TICKS_PER_SCALE = 25;

  const handlers = {

    allowTrackball: false,

    onContextMenu: ( id, position, type, selected, label ) => {
      setState( 'picked', { id, position, type, selected, label } );
    },

    onClick: ( id, position, type, selected ) => {
      controllerAction( pickingController(), 'SelectManifestation', { id } )
    },
    
    bkgdClick: () =>
    {
      controllerAction( rootController(), 'DeselectAll' );
    },
    
    onWheel: deltaY => {
      if ( operating() ) {
        // Logic copied from the desktop implementation, so we are not so sensitive.
        //    (See LengthCanvasTool.java)
        totalY += deltaY;
        let increment = 0;
        if ( totalY > MOUSE_WHEEL_TICKS_PER_SCALE )
          increment = +1;
        else if ( totalY < -MOUSE_WHEEL_TICKS_PER_SCALE )
          increment = -1;
        if ( increment ) {
          scalePreviewStrut( -increment ); // sense must be reversed
          totalY = 0;
        }
      }
    },

    onDragStart: ( evt, id, position, type, selected ) => {
      if ( type !== 'ball' )
        return;
      totalY = 0;
      setPosition( position );
      const { x, y, z } = new Vector3() .copy( eye() ) .sub( new Vector3( ...position ) ) .normalize();
      setLine( [ x, y, z ] );
      startPreviewStrut( id, [ x, y, z ] );
      setOperating( evt ); // so we can pass it to the ObjectTrackball
    },
    onDrag: () => {},
    onDragEnd: () => {
      if ( operating() ) {
        setOperating( null );
        endPreviewStrut();
      }
    }
  };

  createEffect( () => {
    if ( operating() ) {
      movePreviewStrut( line() );
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

export { UnifiedTool };