
import React, { useState, useEffect } from 'react'
import { forwardRef, useImperativeHandle } from 'react';
import { useEmbedding, useRotation, useGeometry } from './hooks.js'
import { useThree, useFrame } from '@react-three/fiber'
import { GLTFExporter } from 'three-stdlib';

const Instance = ( { id, position, rotation, geometry, color, selected, type, toolActions } ) =>
{
  const ref = useRotation( rotation );
  const { onClick, onHover, onDragStart } = toolActions || {}; // may be undefined when the model is not editable, or when the object is not clickable in the current mode
  
  const handleHover = value => e =>
  {
    e.stopPropagation()
    onHover && onHover( id, position, type, value )
  }
  const handleClick = ( e ) =>
  {
    if ( onClick ) {
      e.stopPropagation()
      onClick( id, position, type, selected )
    }
  }
  const handlePointerDown = ( e ) =>
  {
    if ( onDragStart ) {
      e.stopPropagation()
      onDragStart( id, position, type, selected, e )
    }
  }
  const emissive = selected? "#f6f6f6" : "black"
  // TODO: cache materials
  return (
    <group position={ position } >
      <mesh matrixAutoUpdate={false} ref={ ref } geometry={geometry}
          onPointerOver={handleHover(true)} onPointerOut={handleHover(false)} onClick={handleClick}
          onPointerDown={handlePointerDown} >
        <meshLambertMaterial attach="material" color={color} emissive={emissive} />
      </mesh>
    </group>
  )
}

const InstancedShape = ( { shape, toolActions } ) =>
{
  const geometry = useGeometry( shape );
  if ( shape.instances.length === 0 )
    return null;
  return (
    <>
      { shape.instances.map( instance =>
        <Instance key={instance.id} {...instance} geometry={geometry} toolActions={toolActions} /> ) }
    </>
  )
}

export const ShapedGeometry = forwardRef(( { shapes, embedding, toolActions }, exporterRef ) =>
{
  const { scene } = useThree();

  const [ dirty, setDirty ] = useState( true );
  useFrame( ({ gl, scene, camera }) => {
    if ( dirty ) {
      gl.render( scene, camera );
      setDirty( false );
    }
  }, 2 );
  useEffect( () => setDirty( true ), [ shapes ] );

  const gltfExporter = {
    exportGltfJson: writeFile =>
    {
      const exporter = new GLTFExporter();
      // Parse the input and generate the glTF output
      exporter.parse( scene, gltf => {
        writeFile( JSON.stringify( gltf, null, 2 ) );
      }, {} );
    }
  };
  // This lets the download menu make the generateGltfJson call
  useImperativeHandle( exporterRef, () => gltfExporter );

  const bkgdClick = () =>
  {
    // no-op for now
  }

  const ref = useEmbedding( embedding );
  return ( shapes &&
    <group matrixAutoUpdate={false} ref={ref} onPointerMissed={bkgdClick}>
      { Object.values( shapes ).map( shape =>
        <InstancedShape key={shape.id} {...{ shape, toolActions }} />
      ) }
    </group>
  ) || null
});
