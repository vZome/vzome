import { createEffect, createMemo, createSignal } from "solid-js";
import { Vector3, Matrix4, BufferGeometry, Float32BufferAttribute } from "three";


const Instance = ( props ) =>
{
  let meshRef;
  createEffect( () => {
    const m = new Matrix4();
    m.set( ...props.rotation );
    meshRef.matrix = m;
  } );
  const [hovered, setHovered] = createSignal( false );

  const emissive = () => props.selected? "#f6f6f6" : "black"
  // TODO: cache materials
  return (
    <group position={ props.position } >
      <mesh matrixAutoUpdate={false} ref={meshRef} geometry={props.geometry} 
        onPointerEnter={ ()=>setHovered(true) }
        onPointerLeave={ ()=>setHovered(false) }
      >
          {/* onPointerOver={handleHover(true)} onPointerOut={handleHover(false)} onClick={handleClick}
          onPointerDown={handlePointerDown} > */}
        <meshLambertMaterial attach="material" color={ hovered() ? "blue" : props.color } emissive={emissive()} />
      </mesh>
    </group>
  )
}

const InstancedShape = ( props ) =>
{
  const geometry = createMemo( () => {
    const { vertices, faces } = props.shape;
    const computeNormal = ( [ v0, v1, v2 ] ) => {
      const e1 = new Vector3().subVectors( v1, v0 )
      const e2 = new Vector3().subVectors( v2, v0 )
      return new Vector3().crossVectors( e1, e2 ).normalize()
    }
    let positions = [];
    let normals = [];
    faces.forEach( face => {
      const corners = face.vertices.map( i => vertices[ i ] )
      const { x:nx, y:ny, z:nz } = computeNormal( corners )
      corners.forEach( ( { x, y, z } ) => {
        positions.push( x, y, z )
        normals.push( nx, ny, nz )
      } )
    } );
    const geometry = new BufferGeometry();
    geometry.setAttribute( 'position', new Float32BufferAttribute( positions, 3 ) );
    geometry.setAttribute( 'normal', new Float32BufferAttribute( normals, 3 ) );
    geometry.computeBoundingSphere();
    return geometry;
  } );

  if ( props.shape.instances.length === 0 )
    return null;
  return (
    <>
      { props.shape.instances.map( instance =>
        <Instance key={instance.id} {...instance} geometry={geometry()} /> ) }
    </>
  )
}

export const ShapedGeometry = ( props ) =>
{
  const bkgdClick = () =>
  {
    console.log( 'bkgdClick happened' );
  }

  return ( props.shapes &&
    <group matrixAutoUpdate={false} onPointerMissed={bkgdClick}>
      { Object.values( props.shapes ).map( shape =>
        <InstancedShape key={shape.id} shape={shape} />
      ) }
    </group>
  ) || null
};
