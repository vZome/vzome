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

function App( {structure, workingPlane, doMoveWorkingPlane, doToggleStrutMode, doAddSegment, doAddPoint} )
{
  function buildNodeOrEdge( start, end )
  {
    doAddPoint( end )
    start && doAddSegment( start, end )
    doMoveWorkingPlane( end )
  }
  return (
    <Canvas camera={{ fov: 50 }} resize={{polyfill}} dpr={window.devicePixelRatio}>
      <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} />
      <Scene points={structure.points} segments={structure.segments}
        buildEdge={buildNodeOrEdge}
        setFocus={doMoveWorkingPlane}
        clearFocus={doToggleStrutMode}
        focus={workingPlane.enabled && workingPlane.buildingStruts && workingPlane.position} />
      {workingPlane.enabled &&
      <BuildPlane config={workingPlane} buildNodeOrEdge={buildNodeOrEdge} />}
    </Canvas>
  )
}

export default connect(
  'doAddSegment', 'doAddPoint',
  'doMoveWorkingPlane', 'doToggleStrutMode',
  'selectWorkingPlane', 'selectStructure',
  App );
