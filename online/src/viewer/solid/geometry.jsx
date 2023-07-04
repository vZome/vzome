import { createEffect, createMemo, createSignal } from "solid-js";
import { Vector3, Matrix4, BufferGeometry, Float32BufferAttribute } from "three";
import { useInteractionTool } from "./interaction.jsx";


const Instance = ( props ) =>
{
  let meshRef;
  createEffect( () => {
    const m = new Matrix4();
    m.set( ...props.rotation );
    meshRef.matrix = m;
  } );

  const [ tool ] = useInteractionTool();

  const handleHover = value => e =>
  {
    const handler = tool && tool() ?.onHover;
    if ( handler ) {
      e.stopPropagation();
      handler( props.id, props.position, props.type, value );
    }
  }
  const handleClick = ( e ) =>
  {
    const handler = tool && tool() ?.onClick;
    if ( handler ) {
      e.stopPropagation()
      handler( props.id, props.position, props.type, props.selected )
    }
  }
  const handlePointerDown = ( e ) =>
  {
    const handler = tool && tool() ?.onDragStart;
    if ( handler ) {
      e.stopPropagation()
      handler( props.id, props.position, props.type, props.selected, e )
    }
  }
  const handlePointerUp = ( e ) =>
  {
    const handler = tool && tool() ?.onDragEnd;
    if ( handler ) {
      e.stopPropagation()
      handler( props.id, props.position, props.type, props.selected, e )
    }
  }
  const emissive = () => props.selected? "#f6f6f6" : "black"
  // TODO: cache materials
  return (
    <group position={ props.position } >
      <mesh matrixAutoUpdate={false} ref={meshRef} geometry={props.geometry} 
          onPointerOver={handleHover(true)} onPointerOut={handleHover(false)} onClick={handleClick}
          onPointerDown={handlePointerDown} onPointerUp={handlePointerUp} >
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

  return (
    <>
      <For each={props.shape.instances}>{ instance =>
        <Instance {...instance} geometry={geometry()} />
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
          <InstancedShape shape={shape} />
        }</For>
      </group>
    // </Show>
  )
};
