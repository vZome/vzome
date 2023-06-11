
import { Show, createEffect, createSignal, onCleanup } from "solid-js";
import { Matrix4, Quaternion, Vector3 } from 'three';
import { useFrame, useThree } from "solid-three";
import { DragHandler } from "./drag";
import { VectorArrow } from "./arrow";

const defaultDrag = {
  direction: [ 1, 0 ],
  angle: 0.1,
}

// modified copy from https://github.com/devinxi/vinxi/blob/1514f966d9cdcc2c19e2733a8c7bf03831f7ecf3/packages/solid-drei/src/OrbitControls.tsx

const ObjectTrackball = (props) =>
{
  const gl = useThree(({ gl }) => gl);
  const size = useThree(({ size }) => size);
  const camera = useThree(({ camera }) => camera);
  const [ drag, setDrag ] = createSignal( defaultDrag );

  const worldDragVector = new Vector3(); // avoid reactive allocations
  const viewToWorld = new Matrix4();
  // not reactive, camera can't change during drag
  viewToWorld .extractRotation( camera() .matrixWorld ); // I was certain this should be .matrixWorldInverse!
  const worldDrag = () =>
  {
    const { direction } = drag();
    const [ screenX, screenY ] = direction;
    worldDragVector.set( screenX, screenY, 0 ) .transformDirection( viewToWorld );

    const { x, y, z } = worldDragVector;
    return [ x, y, z ];
  }

  const rollAxisVector = new Vector3(); // avoid reactive allocations
  const rollAxis = () =>
  {
    const { direction } = drag();
    const [ screenX, screenY ] = direction;
    rollAxisVector.set( -screenY, screenX, 0 ) .transformDirection( viewToWorld );
    return rollAxisVector;
  }

  // target is a one-time copy; we don't want to react to props.line, since we'll be updating it!
  const target = new Vector3( ...props.line );
  const trackballRoll = new Quaternion(); // avoid reactive allocations
  createEffect( () =>
  {
    let { angle } = drag();
    if ( angle ) {
      angle *= props.rotateSpeed;
      trackballRoll .setFromAxisAngle( rollAxis(), angle )
      target .applyQuaternion( trackballRoll )
      const { x, y, z } = target
      props.setLine( [ x, y, z ] )
    }
  });

  const controls = new DragHandler( setDrag, gl().domElement, props.startEvent );  

  createEffect(() => {
    // SV: This effect is necessary so that we get correctly connected to the domElement
    //   *after* it has been connected to the document and assigned a valid size.
    if ( size().height < 0 ) // should never happen, just making a dependency
      console.log( 'height is', size().height ); // This is the change we care about.
      controls.connect(gl().domElement);
  });

  useFrame(() => {
    if (controls.enabled)
      controls.doDrag();
  });

  controls.connect(gl().domElement);

  onCleanup(() => {
    controls.dispose();
  });
  
  return (
    <Show when={ props.debug && drag() .angle }>
      <VectorArrow vector={worldDrag()} color={0xbb0000} />
      <VectorArrow vector={rollAxis()} color={0x00bb00} />
    </Show>
  );
};

export { ObjectTrackball }