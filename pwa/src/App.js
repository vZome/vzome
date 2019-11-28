import React, { useRef } from 'react'
import './App.css';
import { Canvas, useThree, useRender, extend } from 'react-three-fiber';
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'

import Plane from './components/plane'
import Scene from './components/scene'

extend({ TrackballControls })

function Controls( props ) {
  const { gl, camera } = useThree()
  const ref = useRef()
  useRender(() => ref.current.update())
  return <trackballControls ref={ref} args={[camera, gl.domElement]} {...props} />
}

function App() {
  return (
    <Canvas camera={{ fov: 50 }}>
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
      
      <Scene points={[[0,0,0]]}/>
      <Plane/>
    </Canvas>
  );
}

export default App;
