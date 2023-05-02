
import { createEffect, createSignal } from 'solid-js';
import { Vector3 } from 'three';
import { SceneCanvas } from './components/scenecanvas.jsx';

const pseudoCamera = {
  position: new Vector3(),
  up: new Vector3(),
  lookAt: () => {},
  isPerspectiveCamera: () => {
    console.log( 'pseudoCamera isPerspectiveCamera' );
    return true
  },
}

const StrutDrag = ( { state } ) =>
{
  return (
    <group position={ [0,0,-5] }>
      <TrackballControls camera={pseudoCamera} staticMoving='true' rotateSpeed={4.5} zoomSpeed={3} panSpeed={1} />}
      {/* <mesh>
        <meshLambertMaterial attach="material" color={"pink"} />
        <sphereBufferGeometry attach="geometry" args={[ 2 ]} />
      </mesh> */}
    </group>
  );
}

export const SceneEditor = ( props ) =>
{
  return (
    // not using DesignViewer because it has its own UI, not corresponding to classic desktop vZome
    <SceneCanvas height="880px" width="100%" scene={props.scene} toolActions={props.toolActions}
      trackball={true} syncCamera={props.syncCamera}
      // trackball={!props.strutting} syncCamera={props.syncCamera}
      // children3d={ props.strutting && <StrutDrag state={{}} /> }
    />
  );
}