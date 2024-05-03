
import { REVISION } from '../revision.js'

console.log( `vzome-viewer revision ${REVISION}` );

import { urlViewerCSS } from "./urlviewer.css";

import { createSignal, getOwner, mergeProps, onCleanup, onMount, Show } from 'solid-js';
import { render } from 'solid-js/web';

import { WorkerStateProvider } from './context/worker.jsx';
import { ViewerProvider, useViewer } from './context/viewer.jsx';
import { InteractionToolProvider } from './context/interaction.jsx';
import { CameraProvider } from './context/camera.jsx';

import { SceneCanvas } from "./scenecanvas.jsx";
import { Spinner } from './spinner.jsx';
import { ErrorAlert } from './alert.jsx';
import { SceneMenu } from './scenes.jsx';
import { FullscreenButton } from './fullscreen.jsx';
import { ExportMenu } from './export.jsx';
import { UndoRedoButtons } from './undoredo.jsx';
import { GltfExportProvider } from './geometry.jsx';
import { CameraMode } from './cameramode.jsx';

let stylesAdded = false; // for the onMount in DesignViewer

const normalStyle = {
  display: 'flex',       // flex is for the light dom content, usually an image
  height: '100%',
  width: '100%',
  position: 'relative',
  overflow: 'hidden',    // curiously, this forces Canvas to recompute its size when changing back
};

const DesignViewer = ( props ) =>
{
  const config = mergeProps( { showScenes: 'none', useSpinner: false, allowFullViewport: false, undoRedo: false }, props.config );
  const { scene, waiting } = useViewer();
  let rootRef;
  
  const [ fullScreen, setFullScreen ] = createSignal( false );
  const toggleFullScreen = () =>
  {
    if ( document.fullscreenElement ) {
      document .exitFullscreen();
    }
    else if ( document.fullscreenEnabled && rootRef.requestFullscreen) {
      rootRef.requestFullscreen();
    }
  }
  const fullscreenListener = () => setFullScreen( !! document.fullscreenElement );
  document .addEventListener( "fullscreenchange", fullscreenListener );
  getOwner() &&
    onCleanup(() => {
      document .removeEventListener( "fullscreenchange", fullscreenListener );
      if (isActive()) {
        document .exitFullscreen();
      }
    });

  const whichScenes = () =>
  {
    switch (config.showScenes) {
      case 'true':
      case 'all':
        return 'all';
      case 'named':
        return 'named';
      default:
        return 'none';
    }
  }
  const showSceneMenu = () =>
  {
    const { sceneTitle } = props.config;
    return (typeof sceneTitle === 'undefined' ) && whichScenes() !== 'none';
  }

  const showSpinner = () => {
    return props.config?.useSpinner && waiting();
  }

  onMount( () => {
    if ( ! props.componentRoot && ! stylesAdded ) {
      // Not in a web component context, or props.componentRoot would be set
      document.body .appendChild( document.createElement("style") ).textContent = urlViewerCSS;
      // We don't want to add multiple identical style elements, one for each DesignViewer on the page.
      stylesAdded = true;
    }
  });

  return (
    <InteractionToolProvider>
      <GltfExportProvider>

    <div id='design-viewer' ref={rootRef} style={ normalStyle }>
      {/* This renders the light DOM if the scene couldn't load */}
      <Show when={scene} fallback={props.children}>
        <SceneCanvas id='scene-canvas' scene={scene} height={props.height} width={props.width} >
          {props.children3d}
        </SceneCanvas>
      </Show>

      <Show when={config.undoRedo} >
        <UndoRedoButtons root={rootRef} />
      </Show>

      <Show when={config.showPerspective} >
        <CameraMode/>
      </Show>

      <Show when={showSceneMenu()}>
        <SceneMenu root={rootRef} show={whichScenes()} />
      </Show>

      <ExportMenu root={rootRef} />

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
      <WorkerStateProvider workerClient={props.workerClient}>
        <ViewerProvider config={{ url: props.url, preview: true, debug: false, showScenes: props.showScenes, labels: props.config?.labels }}>
          <DesignViewer config={ { ...props.config, allowFullViewport: true } }
              componentRoot={props.componentRoot}
              height="100%" width="100%" >
            {props.children}
          </DesignViewer>
        </ViewerProvider>
      </WorkerStateProvider>
    </CameraProvider>
  );
}

const renderViewer = ( workerClient, container, config ) =>
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
      <UrlViewer workerClient={workerClient} config={config} componentRoot={container} >
        {/* Make a slot for the light DOM, somehow! */}
      </UrlViewer>
    );
  }

  container .appendChild( document.createElement("style") ).textContent = urlViewerCSS;
  // Apply external override styles to the shadow dom
  const linkElem = document.createElement("link");
  linkElem .setAttribute("rel", "stylesheet");
  linkElem .setAttribute("href", "./vzome-viewer-styles.css");
  container .appendChild( linkElem );

  render( bindComponent, container );
}


export { CameraProvider, DesignViewer, UrlViewer, SceneCanvas, renderViewer };