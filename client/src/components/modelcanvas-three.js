
import React, { useRef, useMemo } from 'react'
import { connect } from 'react-redux'
import { Canvas, useThree, extend, useFrame } from 'react-three-fiber'
import * as THREE from 'three'
import { PerspectiveCamera } from 'drei'
import { TrackballControls } from 'three/examples/jsm/controls/TrackballControls'
import { selectionToggled, commandTriggered, previewChanged } from '../bundles/mesh'
import { doMoveWorkingPlane, doToggleStrutMode } from '../bundles/planes'
import BuildPlane from './buildplane'

extend({ TrackballControls })
const Controls = props => {
  const { gl, camera } = useThree()
  const controls = useRef()
  useFrame(() => controls.current.update())
  return <trackballControls ref={controls} args={[camera, gl.domElement]} {...props} />
}

const computeNormal = ( vertices, face ) => {
  const v0 = vertices[ face.vertices[0] ]
  const v1 = vertices[ face.vertices[1] ]
  const v2 = vertices[ face.vertices[2] ]
  const e1 = new THREE.Vector3().subVectors( v1, v0 )
  const e2 = new THREE.Vector3().subVectors( v2, v0 )
  return new THREE.Vector3().crossVectors( e1, e2 )
}

const createGeometry = ( { vertices, faces } ) =>
{
  var geometry = new THREE.Geometry();
  geometry.vertices = vertices.map( v => new THREE.Vector3( v.x, v.y, v.z ) )
  geometry.faces = faces.map( f => new THREE.Face3( f.vertices[0], f.vertices[1], f.vertices[2], computeNormal( vertices, f ) ) )
  geometry.computeBoundingSphere();
  return geometry
}

const Instance = ( { id, position, rotation, geometry, color, selected, atFocus, onClick } ) =>
{
  const handleClick = ( e ) =>
  {
    if ( onClick ) { // may be undefined when the model is not editable, or when the object is not clickable in the current mode
      e.stopPropagation()
      onClick( id, selected )
    }
  }
  return (
    <group position={ position } quaternion={ rotation }>
      <mesh geometry={geometry} onClick={handleClick}>
        <meshLambertMaterial attach="material" color={color} emissive={(selected || atFocus(id))? "#888888" : "black"} />
      </mesh>
    </group>
  )
}

const InstancedShape = ( { instances, shape, onClick, atFocus } ) =>
{
  const geometry = useMemo( () => createGeometry( shape ), [ shape ] )
  return (
    <>
      { instances.map( instance => 
        <Instance key={instance.id} {...instance} geometry={geometry} atFocus={atFocus} onClick={onClick} /> ) }
    </>
  )
}

// Found this trick for getting the DL.target into the scene here:
//   https://spectrum.chat/react-three-fiber/general/how-to-set-spotlight-target~823340ea-433e-426a-a0dc-b9a333fc3f94
const DirLight = ( { direction, color } ) =>
{
  const position = direction.map( x => -x )
  const light = useMemo(() => new THREE.DirectionalLight(), [])
  return (
    <>
      <primitive object={light} position={position} color={color} />
      <primitive object={light.target} position={[0,0,0]}  />
    </>
  )
}

const Lighting = ( { backgroundColor, ambientColor, directionalLights } ) => {
  const color = useMemo(() => new THREE.Color( backgroundColor ), [backgroundColor])
  useFrame( ({scene}) => { scene.background = color } )
  return (
    <>
      <ambientLight color={ambientColor} />
      { directionalLights.map( ( config ) => <DirLight {...config} /> ) }
    </>
  )
}

/*
TODO:
  - lighting component extract (Redux context tunneling)
*/

// Thanks to Paul Henschel for this, to fix the camera.lookAt by adjusting the Controls target
//   https://github.com/react-spring/react-three-fiber/discussions/609

const ModelCanvas = ( { lighting, shapes, camera, clickable, selectionToggler, doEdit, movePlane, clearFocus, workingPlane, changePreview } ) => {
  const { fov, position, up, lookAt } = camera
  const mouseSelectMode = ! workingPlane
  const focus = workingPlane && workingPlane.position
  const atFocus = id => workingPlane && workingPlane.buildingStruts && ( id === JSON.stringify(focus) )
  const buildNodeOrEdge = ( start, end, move, preview ) =>
  {
    start = workingPlane && workingPlane.buildingStruts? start : undefined
    doEdit( 'buildstrut', { start, end, preview } )
    move && movePlane( end )
  }
  const eraseNodeOrEdge = () =>
  {
    changePreview( new Map() )
  }
  const handleClick = ( id, selected ) =>
  {
    const position = JSON.parse( id )
    if ( mouseSelectMode )
      selectionToggler( id, selected )
    else if ( ! focus )
      movePlane( position )
    else if ( atFocus( id ) )
      clearFocus()
    else {
      changePreview( new Map() )
      buildNodeOrEdge( focus, position, true )
    }
  }
  return(
    <>
      <Canvas gl={{ antialias: true, alpha: false }} >
        <PerspectiveCamera makeDefault {...{fov, position, up}}>
          <Lighting {...lighting} />
        </PerspectiveCamera>
        <Controls staticMoving='true' rotateSpeed={6} zoomSpeed={3} panSpeed={1} target={lookAt} />
        { shapes.map( ( { shape, instances } ) =>
          <InstancedShape key={shape.id} shape={shape} instances={instances} atFocus={atFocus} onClick={clickable && handleClick} />
        ) }
        {workingPlane && workingPlane.enabled &&
        <BuildPlane config={workingPlane} buildNodeOrEdge={buildNodeOrEdge} eraseNodeOrEdge={eraseNodeOrEdge} />}
      </Canvas>
    </>
  )
}

const select = ( state ) =>
{
  const { camera, lighting, model, workingPlane } = state
  return {
    workingPlane,
    camera,
    lighting,
    shapes: model.sortedShapes( state ),
    clickable: model.supportsEdits
  }
}

const boundEventActions = {
  doEdit : commandTriggered,
  movePlane : doMoveWorkingPlane,
  selectionToggler : selectionToggled,
  clearFocus: doToggleStrutMode,
  changePreview: previewChanged,
}

export default connect( select, boundEventActions )( ModelCanvas )
