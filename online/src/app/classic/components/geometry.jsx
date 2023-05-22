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

  const handleHover = value => e =>
  {
    if ( props.toolActions?.onHover ) {
      e.stopPropagation();
      props.toolActions?.onHover( props.id, props.position, props.type, value );
    }
  }
  const handleClick = ( e ) =>
  {
    if ( props.toolActions?.onClick ) {
      e.stopPropagation()
      props.toolActions?.onClick( props.id, props.position, props.type, props.selected )
    }
  }
  const handlePointerDown = ( e ) =>
  {
    if ( props.toolActions?.onDragStart ) {
      e.stopPropagation()
      props.toolActions?.onDragStart( props.id, props.position, props.type, props.selected, e )
    }
  }
  const emissive = () => props.selected? "#f6f6f6" : "black"
  // TODO: cache materials
  return (
    <group position={ props.position } >
      <mesh matrixAutoUpdate={false} ref={meshRef} geometry={props.geometry} 
          onPointerOver={handleHover(true)} onPointerOut={handleHover(false)} onClick={handleClick}
          onPointerDown={handlePointerDown} >
        <meshLambertMaterial attach="material" color={props.color} emissive={emissive()} />
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
      <For each={props.shape.instances}>{ instance =>
        <Instance {...instance} geometry={geometry()} toolActions={props.toolActions} />
      }</For>
    </>
  )
}

export const ShapedGeometry = ( props ) =>
{
  return (
    // <Show when={ () => props.shapes }>
      <group matrixAutoUpdate={false}>
        <For each={Object.values( props.shapes || {} )}>{ shape =>
          <InstancedShape shape={shape} toolActions={props.toolActions} />
        }</For>
      </group>
    // </Show>
  )
};
