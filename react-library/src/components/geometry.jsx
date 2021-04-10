import { BufferGeometry, Vector3, Float32BufferAttribute, Matrix4 } from 'three'
import React, { useMemo, useRef, useLayoutEffect } from 'react'

const createGeometry = ( { vertices, faces } ) =>
{
  const computeNormal = ( [ v0, v1, v2 ] ) => {
    const e1 = new Vector3().subVectors( v1, v0 )
    const e2 = new Vector3().subVectors( v2, v0 )
    return new Vector3().crossVectors( e1, e2 ).normalize()
  }
  let positions = []
  let normals = []
  faces.forEach( face => {
    const corners = face.vertices.map( i => vertices[ i ] )
    const { x:nx, y:ny, z:nz } = computeNormal( corners )
    corners.forEach( ( { x, y, z } ) => {
      positions.push( x, y, z )
      normals.push( nx, ny, nz )
    })
  } )
  const geometry = new BufferGeometry()
  geometry.setAttribute( 'position', new Float32BufferAttribute( positions, 3 ) )
  geometry.setAttribute( 'normal', new Float32BufferAttribute( normals, 3 ) )
  geometry.computeBoundingSphere()
  return geometry
}

const Instance = ( { id, vectors, position, rotation, geometry, color, selected, highlightBall=()=>{}, onClick, onHover } ) =>
{
  const ref = useRef()
  useLayoutEffect( () => {
    const m = new Matrix4()
    m.set( ...rotation )
    ref.current.applyMatrix4( m )
  }, [rotation] )
  const handleHoverIn = ( e ) =>
  {
    e.stopPropagation()
    onHover && onHover( vectors, true )
  }
  const handleHoverOut = ( e ) =>
  {
    e.stopPropagation()
    onHover && onHover( vectors, false )
  }
  const handleClick = ( e ) =>
  {
    if ( onClick ) { // may be undefined when the model is not editable, or when the object is not clickable in the current mode
      e.stopPropagation()
      onClick && onClick( id, vectors, selected )
    }
  }
  const emissive = selected? "#bbbbbb" : highlightBall( id )? "#884444" : "black"
  // TODO: cache materials
  return (
    <group position={ position } >
      <mesh matrixAutoUpdate={false} ref={ ref } geometry={geometry} onPointerOver={handleHoverIn} onPointerOut={handleHoverOut} onClick={handleClick}>
        <meshLambertMaterial attach="material" color={color} emissive={emissive} />
      </mesh>
    </group>
  )
}

const InstancedShape = ( { instances, shape, onClick, onHover, highlightBall } ) =>
{
  const geometry = useMemo( () => createGeometry( shape ), [ shape ] )
  return (
    <>
      { instances.map( instance => 
        <Instance key={instance.id} {...instance} geometry={geometry} highlightBall={highlightBall} onClick={onClick} onHover={onHover} /> ) }
    </>
  )
}

export const SortedGeometry = ( { sortedInstances, highlightBall, handleClick, onHover } ) =>
{
  return (
    <>
      { sortedInstances.map( ( { shape, instances } ) =>
        <InstancedShape key={shape.id} shape={shape} instances={instances} highlightBall={highlightBall} onClick={handleClick} onHover={onHover} />
      ) }
    </>
  )
}

export const ShapedGeometry = ( { shapes, instances, highlightBall, handleClick, onHover } ) =>
{
  const sortedInstances = useInstanceSorter( shapes, instances )
  return (
    <SortedGeometry {...{ sortedInstances, highlightBall, handleClick, onHover }} />
  )
}

const useInstanceSorter = ( shapes, instances ) =>
{
  const filterInstances = ( shape, instances ) => instances.filter( instance => instance.shapeId === shape.id )
  const sortByShape = () => Object.values( shapes ).map( shape => ( { shape, instances: filterInstances( shape, instances ) } ) )
  return useMemo( sortByShape, [ shapes, instances ] )
}
