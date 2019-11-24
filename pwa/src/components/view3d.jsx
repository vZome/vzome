import React, { useRef } from 'react'
import { connect } from 'redux-bundler-react'

import { Canvas, useThree, useRender, useResource, extend } from 'react-three-fiber';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'

import Ball from '../components/ball'
import Plane from '../components/plane'

extend({ TrackballControls })

function Controls( props ) {
  const { gl, camera } = useThree()
  const ref = useRef()
  useRender(() => ref.current.update())
  return <trackballControls ref={ref} args={[camera, gl.domElement]} {...props} />
}

function View3dPage( {points, workingPlaneEnabled, doToggleWorkingPlane} ) {
  const [geometryRef, geometry] = useResource()
  const [materialRef, material] = useResource()
  return (
    <Canvas camera={{ fov: 30 }}>
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
      {workingPlaneEnabled && <Plane/>}
      <dodecahedronBufferGeometry ref={geometryRef} args={[0.4, 0]} />
      <meshNormalMaterial ref={materialRef} />

      { points.map( point => (
        <Ball geom={geometry} material={material} position={point} onClick={doToggleWorkingPlane} />
      ))}
    </Canvas>
  );
};

export default connect(
  'selectPoints',
  'selectWorkingPlaneEnabled',
  'doToggleWorkingPlane',
  View3dPage
)
