import React, { useRef } from 'react'
import { Canvas, useThree, useRender, extend } from 'react-three-fiber';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'
import { connect } from 'redux-bundler-react'
import polyfill from '@juggle/resize-observer'

import './App.css';
import BuildPlane from './components/buildplane'
import Scene from './components/scene'

extend({ TrackballControls })

function Controls( props ) {
  const { gl, camera } = useThree()
  const ref = useRef()
  useRender(() => ref.current.update())
  return <trackballControls ref={ref} args={[camera, gl.domElement]} {...props} />
}

function App( {points, workingPlane, doMoveWorkingPlane, doAddPoint} ) {
  return (
    <Canvas camera={{ fov: 50 }} resize={{polyfill}}>
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
      <Scene points={points} ballClick={doMoveWorkingPlane}/>
      <BuildPlane config={workingPlane} buildFn={doAddPoint}/>
    </Canvas>
  )
}

export default connect( 'doAddPoint', 'doMoveWorkingPlane', 'selectWorkingPlane', 'selectPoints', App );
