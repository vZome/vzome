
import React, { useRef } from 'react';
import { TrackballControls } from '@react-three/drei';
import { Vector3 } from 'three';

import { SceneCanvas } from '../../ui/viewer/scenecanvas.jsx';

// These are pure function React components, with no state, to better
//  integrate with the SolidJS-based context.

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

export const SceneEditor = ( { scene, strutting, toolActions, syncCamera } ) =>
{
  const exporterRef = useRef(); // TODO hook this up for export

  return (
    // not using DesignViewer because it has its own UI, not corresponding to classic desktop vZome
    <SceneCanvas scene={scene} toolActions={toolActions} trackball={!strutting} syncCamera={syncCamera} ref={exporterRef}
      style={{ position: 'relative', height: '100%' }}
      children3d={ strutting && <StrutDrag state={{}} /> }
    />
  );
}