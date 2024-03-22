
import { createEffect, createResource } from "solid-js";
import { loadingFn, useThree } from "solid-three";
import { GLTFLoader, DRACOLoader } from "three-stdlib";

let dracoLoader = null;

function extensions(
  useDraco,
  useMeshopt,
  extendLoader
) {
  return (loader) => {
    if (extendLoader) {
      extendLoader(loader);
    }
    if (useDraco) {
      if (!dracoLoader) {
        dracoLoader = new DRACOLoader();
      }
      dracoLoader.setDecoderPath(
        typeof useDraco === "string"
          ? useDraco
          : "https://www.gstatic.com/draco/versioned/decoders/1.4.3/"
      );
      (loader).setDRACOLoader(dracoLoader);
    }
    // if (useMeshopt) {
    //   (loader as GLTFLoader).setMeshoptDecoder(
    //     typeof MeshoptDecoder === "function" ? MeshoptDecoder() : MeshoptDecoder
    //   );
    // }
  };
}

export function useGLTF(
  path,
  useDraco,
  useMeshOpt,
  extendLoader
) {
  return createResource(
    () => path,
    async path => {
      return (
        await loadingFn(extensions(useDraco, useMeshOpt, extendLoader), () => {})( GLTFLoader, path )
      )[0];
    }
  );
}

// useGLTF.preload = (
//   path: string | string[],
//   useDraco: boolean | string = true,
//   useMeshOpt: boolean = true,
//   extendLoader?: (loader: GLTFLoader) => void
// ) =>
//   useLoader.preload(
//     GLTFLoader,
//     path,
//     extensions(useDraco, useMeshOpt, extendLoader)
//   );

// useGLTF.clear = (input: string | string[]) =>
//   useLoader.clear(GLTFLoader, input);

export const GltfModel = (props) =>
{
  if ( ! props.url .endsWith( '.gltf' ) )
    return null;

  const [data] = useGLTF( props.url );
  const scene = useThree(({ scene }) => scene);
  createEffect( () => {
    const loadedScene = data() ?.scene;
    if ( !!loadedScene ) {
      for ( const child of loadedScene .children[ 0 ] .children ) {
        if ( child.type === 'Mesh' )
          scene() .add( child );
      }
    }
  } );
  return null;
}
