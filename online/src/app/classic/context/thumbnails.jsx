import { createContext, createEffect, onCleanup, untrack, useContext } from "solid-js";
import { createStore, reconcile } from "solid-js/store";

import { useWorkerClient } from "../../../viewer/context/worker.jsx";
import { useViewer } from "../../../viewer/context/viewer.jsx";
import { useCamera } from "../../../viewer/context/camera.jsx";
import { useSceneIndexing } from "../../../viewer/context/scene.jsx";
import { useImageCapture } from "../../../viewer/context/export.jsx";

// Scene thumbnails for the Scenes dialog's list.  See developer-docs/scene-thumbnails.md.
//
// A thumbnail depends only on (snapshot, camera, lighting), and snapshots are immutable, so
// thumbnails are cached under a key built from that content rather than the scene's list
// index.  Reordering, retitling, and deleting scenes need no invalidation, and Save Camera
// simply produces a new key.  The cache is kept OUT of the viewer's `scenes` store, which is
// serialized into the .vZome file and rebuilt by copyScenes on every mutation.

export const THUMBNAIL_WIDTH = 200;  // device pixels; displayed at half this size (100px CSS)
export const THUMBNAIL_HEIGHT = 150;

export const thumbnailKey = ( scene, lighting ) =>
  scene && JSON.stringify( [ scene.snapshot, scene.camera, lighting ] );

const ThumbnailsContext = createContext( { thumbnail: () => undefined, setThumbnail: () => {} } );

export const useThumbnails = () => useContext( ThumbnailsContext );

// Holds the cache.  Mount it where it outlives the Scenes dialog (which unmounts when closed).
export const ThumbnailsProvider = ( props ) =>
{
  const { subscribeFor } = useWorkerClient();
  const [ urls, setUrls ] = createStore( {} );

  const revokeAll = () => Object.values( urls ) .forEach( url => URL.revokeObjectURL( url ) );

  // Snapshot indices are per design, so every load (or new design) starts a fresh cache.
  subscribeFor( 'SCENES_DISCOVERED', () => {
    revokeAll();
    setUrls( reconcile( {} ) );
  } );
  onCleanup( revokeAll );

  const thumbnail = key => key && urls[ key ];
  const setThumbnail = ( key, blob ) =>
  {
    const old = urls[ key ];
    setUrls( key, URL.createObjectURL( blob ) );
    old && URL.revokeObjectURL( old );
  }

  return (
    <ThumbnailsContext.Provider value={ { thumbnail, setThumbnail } }>
      {props.children}
    </ThumbnailsContext.Provider>
  );
}

const nextFrame = () => new Promise( resolve => requestAnimationFrame( resolve ) );

// Center-crop the captured canvas image to the thumbnail aspect, and scale it down.
const shrinkImage = async ( blob ) =>
{
  const bitmap = await createImageBitmap( blob );
  const targetAspect = THUMBNAIL_WIDTH / THUMBNAIL_HEIGHT;
  let sw = bitmap.width, sh = bitmap.height;
  if ( sw / sh > targetAspect )
    sw = sh * targetAspect;
  else
    sh = sw / targetAspect;
  const sx = ( bitmap.width - sw ) / 2, sy = ( bitmap.height - sh ) / 2;

  const canvas = document.createElement( 'canvas' );
  canvas.width = THUMBNAIL_WIDTH;
  canvas.height = THUMBNAIL_HEIGHT;
  const ctx = canvas.getContext( '2d' );
  ctx.imageSmoothingQuality = 'high';
  ctx.drawImage( bitmap, sx, sy, sw, sh, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT );
  bitmap.close();
  return new Promise( resolve => canvas.toBlob( resolve, 'image/png' ) );
}

// Behavioral component: captures a thumbnail of whichever scene the surrounding canvas has
// just finished showing, if that scene's thumbnail isn't already cached.  Must be mounted
// inside the canvas's own ImageCaptureProvider, CameraProvider, and SceneIndexingProvider
// (as in the Scenes dialog), so the capture, the lighting, and the "settled" signal all
// refer to that same canvas.
//
// This one placement covers every case: navigating to a scene, Add Scene (the new scene
// becomes current and is shown), Save Camera (the current scene's key changes while the
// canvas already shows the new camera), and the main view's Capture Scene (which opens
// the dialog on the new scene).
export const ThumbnailCapturer = () =>
{
  const { thumbnail, setThumbnail } = useThumbnails();
  const { captureImage } = useImageCapture();
  const { settledSceneIndex } = useSceneIndexing();
  const { scenes } = useViewer();
  const { state } = useCamera();

  const currentKey = () => {
    const index = settledSceneIndex?.();
    return ( index === null || index === undefined )? undefined : thumbnailKey( scenes[ index ], state.lighting );
  }

  createEffect( () => {
    const key = currentKey();
    if ( !key || untrack( () => thumbnail( key ) ) )
      return;
    // Give the renderer a couple of frames to draw the updated shapes, then make sure the
    //  same scene is still showing before capturing it.
    nextFrame() .then( nextFrame ) .then( () => {
      if ( untrack( currentKey ) !== key )
        return;
      return captureImage( 'image/png' )
        .then( shrinkImage )
        .then( blob => blob && setThumbnail( key, blob ) );
    } )
    .catch( error => console.log( 'Scene thumbnail capture failed:', error ) );
  } );

  return null;
}
