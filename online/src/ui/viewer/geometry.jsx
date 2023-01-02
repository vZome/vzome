
import React, { useState, useEffect } from 'react'
import { forwardRef, useImperativeHandle } from 'react';
import { useEmbedding, useRotation, useGeometry } from './hooks.js'
import { useThree, useFrame } from '@react-three/fiber'
import { GLTFExporter } from 'https://cdn.skypack.dev/three@0.129.0/examples/jsm/exporters/GLTFExporter.js';

const Instance = ( { id, position, rotation, geometry, color, selected, type, toolRef } ) =>
{
  const ref = useRotation( rotation );
  const { onClick, onHover } = toolRef?.current || {};
  
  const handleHover = value => e =>
  {
    e.stopPropagation()
    onHover && onHover( id, position, type, value )
  }
  const handleClick = ( e ) =>
  {
    if ( onClick ) { // may be undefined when the model is not editable, or when the object is not clickable in the current mode
      e.stopPropagation()
      onClick( id, position, type, selected )
    }
  }
  const emissive = selected? "#f6f6f6" : "black"
  // TODO: cache materials
  return (
    <group position={ position } >
      <mesh matrixAutoUpdate={false} ref={ ref } geometry={geometry}
          onPointerOver={handleHover(true)} onPointerOut={handleHover(false)} onClick={handleClick}>
        <meshLambertMaterial attach="material" color={color} emissive={emissive} />
      </mesh>
    </group>
  )
}

const InstancedShape = ( { shape, toolRef } ) =>
{
  const geometry = useGeometry( shape );
  if ( shape.instances.length === 0 )
    return null;
  return (
    <>
      { shape.instances.map( instance =>
        <Instance key={instance.id} {...instance} geometry={geometry} toolRef={toolRef} /> ) }
    </>
  )
}

export const ShapedGeometry = forwardRef(( { shapes, embedding, toolRef }, exporterRef ) =>
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
        <InstancedShape key={shape.id} {...{ shape, toolRef }} />
      ) }
    </group>
  ) || null
});
