import React, { useRef } from 'react'
import './App.css';
import { Canvas, useThree, useRender, extend } from 'react-three-fiber';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'
import { connect } from 'redux-bundler-react'

import Plane from './components/plane'
import Scene from './components/scene'

extend({ TrackballControls })

function Controls( props ) {
  const { gl, camera } = useThree()
  const ref = useRef()
  useRender(() => ref.current.update())
  return <trackballControls ref={ref} args={[camera, gl.domElement]} {...props} />
}

function App( {points, workingPlaneEnabled, doToggleWorkingPlane} ) {
  return (
    <Canvas camera={{ fov: 50 }}>
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
      <Scene points={points} ballClick={doToggleWorkingPlane}/>
      {workingPlaneEnabled && <Plane/>}
    </Canvas>
  )
}

export default connect( 'doToggleWorkingPlane', 'selectWorkingPlaneEnabled', 'selectPoints', App );
