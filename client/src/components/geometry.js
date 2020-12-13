import * as THREE from 'three'
import React, { useMemo } from 'react'

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

const Instance = ( { id, vectors, position, rotation, geometry, color, selected, highlightBall, onClick, onHover } ) =>
{
  const handleHoverIn = ( e ) =>
  {
    e.stopPropagation()
    onHover( vectors, true )
  }
  const handleHoverOut = ( e ) =>
  {
    e.stopPropagation()
    onHover( vectors, false )
  }
  const handleClick = ( e ) =>
  {
    if ( onClick ) { // may be undefined when the model is not editable, or when the object is not clickable in the current mode
      e.stopPropagation()
      onClick( id, vectors, selected )
    }
  }
  const emissive = selected? "#707070" : highlightBall( id )? "#884444" : "black"
  // TODO: cache materials
  return (
    <group position={ position } quaternion={ rotation }>
      <mesh geometry={geometry} onPointerOver={handleHoverIn} onPointerOut={handleHoverOut} onClick={handleClick}>
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

const shapeInstance = ( instance, selected, field, shapedInstances, resolve ) =>
{
  // TODO: handle undefined result from resolve
  let shapedInstance = shapedInstances[ instance.id ]
  if ( shapedInstance )
    return { ...shapedInstance, selected }
  shapedInstance = resolve( instance )
  const position = field.embedv( instance.vectors[0] ) // TODO: does this work for struts and panels?
  // everything except selected state will go into shapedInstances
  shapedInstance = { ...shapedInstance, position, vectors: instance.vectors }
  shapedInstances[ instance.id ] = shapedInstance
  return { ...shapedInstance, selected }
}

const filterInstances = ( shape, instances ) =>
{
  return instances.filter( instance => instance.shapeId === shape.id )
}

export default ({ mesh, resolver, preResolved, highlightBall, handleClick, onHover }) =>
{
  const shapes = useMemo( () => ( preResolved && preResolved.shapes ) || {}, [ resolver, preResolved ] )
  const shapedInstances = useMemo( () => ({}), [ resolver ] )
  const instances = ( preResolved && preResolved.instances ) || []
  if ( resolver ) {
    // resolver won't be available until the JSweet init has dispatched ORBITS_INITIALIZED
    const resolve = resolver( shapes )
    // TODO: this breaks encapsulation of the mesh state; fix it
    mesh.shown.forEach( instance => instances.push( shapeInstance( instance, false, mesh.field, shapedInstances, resolve ) ) )
    mesh.selected.forEach( instance => instances.push( shapeInstance( instance, true, mesh.field, shapedInstances, resolve ) ) )
  }
  const sortByShape = () => Object.values( shapes ).map( shape => ( { shape, instances: filterInstances( shape, instances ) } ) )
  const sortedInstances = useMemo( sortByShape, [ shapes, instances ] )  // When !preRendered, instances will change every render
  return (
    <>
      { sortedInstances.map( ( { shape, instances } ) =>
        <InstancedShape key={shape.id} shape={shape} instances={instances} highlightBall={highlightBall} onClick={handleClick} onHover={onHover} />
      ) }
    </>
  )
}
