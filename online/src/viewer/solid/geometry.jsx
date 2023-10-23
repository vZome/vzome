
import { createContext, createEffect, createMemo, createSignal, useContext } from "solid-js";
import { Vector3, Matrix4, BufferGeometry, Float32BufferAttribute } from "three";
import { useThree } from "solid-three";

import { useInteractionTool } from "./interaction.jsx";
import { GLTFExporter } from "three-stdlib";


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
    if ( e.button !== 0 ) // left-clicks only, please
      return;
    const handler = tool && tool() ?.onClick;
    if ( handler ) {
      e.stopPropagation()
      handler( props.id, props.position, props.type, props.selected )
    }
  }
  const handleContextMenu = ( e ) =>
  {
    const handler = tool && tool() ?.onContextMenu;
    if ( handler ) {
      e.stopPropagation();
      handler( props.id, props.position, props.type, props.selected )
    }
  }
  const handlePointerDown = ( e ) =>
  {
    if ( e.button !== 0 ) // left-clicks only, please
      return;
    const handler = tool && tool() ?.onDragStart;
    if ( handler ) {
      e.stopPropagation()
      handler( props.id, props.position, props.type, props.selected, e )
    }
  }
  const handlePointerUp = ( e ) =>
  {
    if ( e.button !== 0 ) // left-clicks only, please
      return;
    const handler = tool && tool() ?.onDragEnd;
    if ( handler ) {
      e.stopPropagation()
      handler( props.id, props.position, props.type, props.selected, e )
    }
  }
  // TODO give users control over emissive color
  const emissive = () => props.selected? "#dddddd" : "black"
  // TODO: cache materials
  return (
    <group position={ props.position } name={props.id} >
      <mesh matrixAutoUpdate={false} ref={meshRef} geometry={props.geometry}
          onPointerOver={handleHover(true)} onPointerOut={handleHover(false)} onClick={handleClick}
          onPointerDown={handlePointerDown} onPointerUp={handlePointerUp} onContextMenu={handleContextMenu}>
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
  const scene = useThree(({ scene }) => scene);
  const exportGltf = callback => {
    const exporter = new GLTFExporter();
    // Parse the input and generate the glTF output
    exporter.parse( scene(), callback, { onlyVisible: false } );
  };
  const { setExporter } = useContext( GltfExportContext );
  setExporter( { exportGltf } );

  let groupRef;
  createEffect( () => {
    if ( props.embedding && groupRef && groupRef.matrix ) {
      const m = new Matrix4()
      m.set( ...props.embedding )
      m.transpose()
      groupRef.matrix.identity()  // Required, or applyMatrix4() changes will accumulate
      // This imperative approach is required because I was unable to decompose the
      //   embedding matrix (a shear) into a scale and rotation.
      groupRef.applyMatrix4( m );
    }
  })
  return (
    // <Show when={ () => props.shapes }>
      <group matrixAutoUpdate={false} ref={groupRef} onClick={exportGltf}>
        <For each={Object.values( props.shapes || {} )}>{ shape =>
          <InstancedShape shape={shape} />
        }</For>
      </group>
    // </Show>
  )
};

const GltfExportContext = createContext( {} );

export const GltfExportProvider = (props) =>
{
  const [ exporter, setExporter ] = createSignal( {} );

  return (
    <GltfExportContext.Provider value={ { exporter, setExporter } }>
      {props.children}
    </GltfExportContext.Provider>
  );
}

export const useGltfExporter = () => { return useContext( GltfExportContext ); };
