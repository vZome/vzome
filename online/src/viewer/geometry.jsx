
import { createContext, createEffect, createMemo, onCleanup, onMount, useContext } from "solid-js";
import { Vector2, Vector3, Matrix4, BufferGeometry, Float32BufferAttribute, Color, Group, Mesh, MeshLambertMaterial, LineSegments, LineBasicMaterial, Raycaster } from "three";

import { createT, useThree } from 'solid-three';
const T = createT({ Group, Mesh, MeshLambertMaterial, LineSegments, LineBasicMaterial });

import { useInteractionSurface } from "./context/interaction.jsx";
import { createRectTest } from "./rectpicking.js";

import { GLTFExporter } from "three-stdlib";
import { Label } from "./labels.jsx";
import { useGltfExporter, useImageCapture } from "./context/export.jsx";
import { useScene } from "./context/scene.jsx";

// Maps each Instance's mesh to a function returning its hit record; see ShapedGeometry.
const PickablesContext = createContext();

const Instance = ( props ) =>
{
  let meshRef, linesRef;
  createEffect( () => {
    const m = new Matrix4();
    m.set( ...props.rotation );
    meshRef.matrix = m;
    if ( !! linesRef )
      linesRef.matrix = m.clone();
  } );

  // Make this mesh pickable by ShapedGeometry's hit source, which answers with this record.
  const pickables = useContext( PickablesContext );
  onMount( () => pickables ?.set( meshRef, () => ( {
    source: 'scene', id: props.id, position: props.position, type: props.type, selected: props.selected, label: props.label,
  } ) ) );
  onCleanup( () => pickables ?.delete( meshRef ) );

  onMount( () => linesRef && linesRef.layers.set( 4 ) );

  // Adopting changes as required by https://discourse.threejs.org/t/updates-to-color-management-in-three-js-r152/50791
  const color = new Color() .setStyle( props.color ); // not reactive, don't care, I think

  // TODO give users control over emissive color
  const emissive = () => props.selected? "#c8c8c8" : "black"
  // TODO: cache materials
  return (
    <T.Group position={ props.position } name={props.id} >
      <T.Mesh matrixAutoUpdate={false} ref={meshRef} geometry={props.geometry} >
        <T.MeshLambertMaterial attach="material" color={color} emissive={emissive()} />
      </T.Mesh>
      { !!props.outlineGeometry &&
        <T.LineSegments matrixAutoUpdate={false} ref={linesRef} geometry={props.outlineGeometry} >
          <T.LineBasicMaterial attach="material" linewidth={4.4} color='black' />
        </T.LineSegments>
      }
      {!!props.label && <Label parent={meshRef} position={props.geometry.shapeCentroid} text={props.label} />}
    </T.Group>
  )
}

const centroid = vertices =>
{
  let [ sx, sy, sz ] = [ 0,0,0 ];
  vertices .forEach( ({ x, y, z }) => {
    sx += x; sy += y; sz += z;
  });
  const num = vertices .length;
  return { x: sx/num, y: sy/num, z: sz/num };
}

// Builds a triangulated (or polygon-fan) BufferGeometry for a shape, decoupled from
// SolidJS so it can be reused outside a reactive/component context (e.g. by a
// symmetry renderer that owns its geometries imperatively).
export const buildShapeGeometry = ( shape, polygons ) =>
{
  // TODO: use indexed vertices, if I can understand how to do normals
  const { vertices, faces } = shape;
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
    const addVertex = ( { x, y, z } ) =>
    {
      positions.push( x, y, z );
      normals.push( nx, ny, nz )
    }
    if ( polygons ) {
      for (let index = 0; index < corners.length-2; index++) {
        addVertex( corners[ 0 ] );
        addVertex( corners[ (index+1) % corners.length ] );
        addVertex( corners[ (index+2) % corners.length ] );
      }
    } else {
      corners.forEach( addVertex );
    }
  } );
  const geometry = new BufferGeometry();
  geometry.setAttribute( 'position', new Float32BufferAttribute( positions, 3 ) );
  geometry.setAttribute( 'normal', new Float32BufferAttribute( normals, 3 ) );
  geometry.computeBoundingSphere();
  geometry.shapeCentroid = centroid( vertices );
  return geometry;
}

// Builds a wireframe (edge) BufferGeometry for a shape's outline, decoupled from SolidJS.
export const buildOutlineGeometry = ( shape ) =>
{
  const { vertices, faces } = shape;
  let positions = [];
  vertices .map( ( { x, y, z } ) => positions.push( x, y, z ) );
  let indices = [];
  faces.forEach( face => {
    for (let i = 0; i < face.vertices.length; i++) {
      indices .push( face.vertices[ i ] );
      indices .push( face.vertices[ (i+1) % face.vertices.length ] );
    }
  } );
  const geometry = new BufferGeometry();
  geometry .setIndex( indices );
  geometry .setAttribute( 'position', new Float32BufferAttribute( positions, 3 ) );
  geometry .computeBoundingSphere();
  return geometry;
}

const InstancedShape = ( props ) =>
{
  const { scene } = useScene();
  const showOutlines = () => scene.polygons;

  const geometry = createMemo( () =>
  {
    const geometry = buildShapeGeometry( props.shape, scene.polygons );
    onCleanup( () => geometry.dispose() );
    return geometry;
  } );

  const outlineGeometry = createMemo( () =>
  {
    const geometry = buildOutlineGeometry( props.shape );
    onCleanup( () => geometry.dispose() );
    return geometry;
  } );

  return (
    <>
      <For each={props.shape.instances}>{ instance =>
        <Instance {...instance} geometry={geometry()} outlineGeometry={ showOutlines() && outlineGeometry() } />
      }</For>
    </>
  )
}

// Wires the two "grab what's on the canvas" context APIs -- image capture (used by File >
// Export image and by the GitHub sharing dialog) and glTF export (File > Export glTF) -- to
// this canvas's live three.js context. Rendered as a null-returning behavioral component so
// BOTH geometry paths can mount it: ShapedGeometry (WebGL fallback) and SymmetryGeometry (the
// GPU symmetry renderer). It used to live inline in ShapedGeometry only, which meant that on
// the symmetry-renderer path -- now the default everywhere WebGPURenderer initializes --
// nothing ever called setCapturer/setExporter, and capturer() stayed the provider's initial
// empty object, so sharing failed with "capturer(...).capture is not a function".
//
// Both capture and export read from the shared three.js scene (the renderer's GPU-instanced
// meshes are parented into it just like ShapedGeometry's per-instance meshes), so neither
// needs to know which geometry component mounted it.
export const CanvasExportBindings = () =>
{
  const { setExporter } = useGltfExporter();
  const { setCapturer } = useImageCapture();
  const three = useThree();

  const exportGltf = callback => {
    const exporter = new GLTFExporter();
    const onError = ( error ) => {
      console.error( 'An error happened during glTF export:', error );
    }
    // Parse the input and generate the glTF output
    exporter.parse( three.scene, callback, onError, { onlyVisible: false, binary: true } );
  };
  setExporter( { exportGltf } );

  const capture = ( mimeType, saveBlob ) => {
    // See:
    //   https://github.com/pmndrs/react-three-fiber/discussions/2054
    //   https://stackoverflow.com/questions/12168909/blob-from-dataurl
    // The HTML canvas state is only guaranteed immediately after render. solid-three's
    // render() ignores any arguments and always draws its own context.scene with
    // context.camera, and both renderers we construct (WebGLRenderer, and WebGPURenderer with
    // forceWebGL) are set up with preserveDrawingBuffer, so the buffer is still readable here.
    three.render();
    three.canvas .toBlob( blob => {
      console.log( `Captured ${mimeType} image of size ${blob.size} bytes` );
      saveBlob( blob );
    }, mimeType );
  }
  setCapturer( { capture } );

  return null;
}

export const ShapedGeometry = ( props ) =>
{
  // Picking: register as a hit source with the InteractionToolProvider, which owns all pointer
  // handling (see context/interaction.jsx).  Every ball/strut/panel is its own mesh here, so a
  // raycast finds the nearest one; each Instance registers its mesh in `pickables`.  (The outline
  // LineSegments are on layer 4, which the raycaster ignores.)
  const three = useThree();
  const surface = useInteractionSurface();
  const pickables = new Map(); // mesh -> () => hit record
  const raycaster = new Raycaster();
  const pointer = new Vector2();
  const pickAt = ( clientX, clientY ) =>
  {
    const rect = three.canvas .getBoundingClientRect();
    pointer.set( ( clientX - rect.left ) / rect.width * 2 - 1, - ( clientY - rect.top ) / rect.height * 2 + 1 );
    raycaster .setFromCamera( pointer, three.camera ); // three.camera is a live getter; read it fresh
    const [ nearest ] = raycaster .intersectObjects( [ ...pickables.keys() ], false );
    return nearest? pickables .get( nearest.object )() : null;
  }
  // Marquee selection: every instance whose mesh vertices all project inside the rectangle.
  const hitsInRect = ( rect ) =>
  {
    const test = createRectTest( three.camera, three.canvas, rect );
    const hits = [];
    for ( const [ mesh, getHit ] of pickables ) {
      test .setModelMatrix( mesh.matrixWorld );
      const positions = mesh.geometry .getAttribute( 'position' );
      let inside = true;
      for ( let i = 0; inside && i < positions.count; i++ )
        inside = test .contains( positions .getX( i ), positions .getY( i ), positions .getZ( i ) );
      if ( inside )
        hits .push( getHit() );
    }
    return hits;
  }
  if ( surface )
    onCleanup( surface .addHitSource( { pickAt, hitsInRect } ) );

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
      <PickablesContext.Provider value={pickables}>
        <CanvasExportBindings />
        <T.Group matrixAutoUpdate={false} ref={groupRef} >
          <For each={Object.values( props.shapes || {} )}>{ shape =>
            <InstancedShape shape={shape} />
          }</For>
        </T.Group>
      </PickablesContext.Provider>
    // </Show>
  )
};
