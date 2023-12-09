
import { REVISION } from '../revision.js'

console.log( `vzome-viewer revision ${REVISION}` );

import { urlViewerCSS } from "./urlviewer.css";

import { createSignal, mergeProps, onMount, Show } from 'solid-js';
import { render } from 'solid-js/web';

import { useWorkerClient, WorkerStateProvider, SceneProvider, useScene } from '../viewer/context/context.jsx';
import { InteractionToolProvider } from '../viewer/context/interaction.jsx';
import { CameraProvider, useCamera } from '../viewer/context/camera.jsx';

import { SceneCanvas } from "./scenecanvas.jsx";
import { Spinner } from './spinner.jsx';
import { ErrorAlert } from './alert.jsx';
import { SceneMenu } from './scenes.jsx';
import { FullscreenButton } from './fullscreen.jsx';
import { ExportMenu } from './export.jsx';
import { UndoRedoButtons } from './undoredo.jsx';
import { GltfExportProvider } from './geometry.jsx';

let stylesAdded = false; // for the onMount in DesignViewer

const normalStyle = {
  display: 'flex',       // flex is for the light dom content, usually an image
  height: '100%',
  width: '100%',
  position: 'relative',
  overflow: 'hidden',    // curiously, this forces Canvas to recompute its size when changing back
};

const fullScreenStyle = {
  height: '100%',
  width: '100%',
  position: 'fixed',
  top: '0px',
  left: '0px',
  'z-index': '1300',
};

const DesignViewer = ( props ) =>
{
  const config = mergeProps( { showScenes: false, useSpinner: false, allowFullViewport: false, undoRedo: false }, props.config );
  const { state, subscribeFor } = useWorkerClient();
  const { scene } = useScene();
  const { state: cameraState, setCamera, setLighting } = useCamera();
  const [ fullScreen, setFullScreen ] = createSignal( false );
  const toggleFullScreen = () =>
  {
    // const { perspective } = sceneCamera || {};
    // This is a complete hack to work around the issue with resize in OrthographicCamera.
    //  We simply use a thunk to switch to a perspective camera before we toggle fullScreen.
    //  Note that this does NOT help with window resize.
    setFullScreen( v => !v );
  }
  const showSceneMenu = () =>
  {
    const { showScenes, sceneTitle } = props.config;
    // Only show the menu when the scene is not being controlled explicitly,
    //  and when more than one scene has been discovered.
    return (typeof sceneTitle === 'undefined' ) && showScenes && state.scenes && state.scenes[1];
  }

  const showSpinner = () => {
    return props.config?.useSpinner && state.waiting;
  }

  onMount( () => {
    if ( ! props.componentRoot && ! stylesAdded ) {
      // Not in a web component context, or props.componentRoot would be set
      document.body .appendChild( document.createElement("style") ).textContent = urlViewerCSS;
      // We don't want to add multiple identical style elements, one for each DesignViewer on the page.
      stylesAdded = true;
    }
  });

  subscribeFor( 'SCENE_RENDERED', ( { scene } ) => {
    if ( scene.camera ) {
      setCamera( scene.camera );
    }
    if ( scene.lighting ) {
      const { backgroundColor } = scene.lighting;
      setLighting( { ...cameraState.lighting, backgroundColor } );
    }
  });

  let rootRef;
  return (
    <InteractionToolProvider>
      <GltfExportProvider>

    <div id='design-viewer' ref={rootRef} style={ fullScreen()? fullScreenStyle : normalStyle }>
      {/* This renders the light DOM if the scene couldn't load */}
      <Show when={scene} fallback={props.children}>
        <SceneCanvas id='scene-canvas' scene={scene} height={props.height} width={props.width} >
          {props.children3d}
        </SceneCanvas>
      </Show>

      <Show when={config.undoRedo} >
        <UndoRedoButtons root={rootRef} />
      </Show>

      <Show when={showSceneMenu()}>
        <SceneMenu root={rootRef} />
      </Show>

      <Show when={ state.source?.text || state.source?.changedText }>
        <ExportMenu root={rootRef} />
      </Show>

      <Show when={config.allowFullViewport}>
        <FullscreenButton fullScreen={fullScreen()} root={rootRef} toggle={toggleFullScreen} />
      </Show>

      <Show when={showSpinner()}>
        <Spinner root={rootRef} />
      </Show>

      <ErrorAlert root={rootRef} />
    </div>      
      </GltfExportProvider>
    </InteractionToolProvider>
  );
}

const UrlViewer = (props) =>
{
  return (
    <CameraProvider>
    <WorkerStateProvider store={props.store} config={{ url: props.url, preview: true, debug: false, showScenes: props.showScenes }}>
    <SceneProvider>
      <DesignViewer config={ { ...props.config, allowFullViewport: true } }
          componentRoot={props.componentRoot}
          height="100%" width="100%" >
        {props.children}
      </DesignViewer>
    </SceneProvider>
    </WorkerStateProvider>
    </CameraProvider>
  );
}

const renderViewer = ( store, container, url, config ) =>
{
  // if ( url === null || url === "" ) {
  //   ReactDOM.unmountComponentAtNode( container );
  //   return null;
  // }

  // The URL was prefetched, so we don't pass it here.
  // Note the addition of a slot child element; this lets us potentially render the light dom,
  //   for example if the worker cannot load.
  // LG: Can we handle canvas resizing using `ResizeObserver` without modifying `vZome` or recreating the element constantly?
  // const viewerElement = React.createElement( UrlViewer, { store, config }, (<slot></slot>) );

  // We need JSS to inject styles on our shadow root, not on the document head.
  // I found this solution here:
  //   https://stackoverflow.com/questions/51832583/react-components-material-ui-theme-not-scoped-locally-to-shadow-dom
  // const jss = create({
  //     ...jssPreset(),
  //     insertionPoint: container
  // });
  // const reactElement = React.createElement( StylesProvider, { jss: jss }, [ viewerElement ] );

  const bindComponent = () =>
  {
    return (
      <UrlViewer store={store} config={config} componentRoot={container} >
        {/* Make a slot for the light DOM, somehow! */}
      </UrlViewer>
    );
  }

  container .appendChild( document.createElement("style") ).textContent = urlViewerCSS;
  render( bindComponent, container );
}


export { DesignViewer, UrlViewer, SceneCanvas, renderViewer };