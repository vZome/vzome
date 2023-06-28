
import { REVISION } from '../../revision.js'

console.log( `vzome-viewer revision ${REVISION}` );

import { urlViewerCSS } from "./urlviewer.css";

import { createSignal, mergeProps, onMount, Show } from 'solid-js';
import { render } from 'solid-js/web';

import { useWorkerClient, WorkerStateProvider } from '../../workerClient/index.js';
import { SceneCanvas } from "./scenecanvas.jsx";
import { Spinner } from './spinner.jsx';
import { ErrorAlert } from './alert.jsx';
import { SceneMenu } from './scenes.jsx';
import { FullscreenButton } from './fullscreen.jsx';
import { ExportMenu } from './export.jsx';

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
  const { state, postMessage } = useWorkerClient();
  const [ fullScreen, setFullScreen ] = createSignal( false );
  const toggleFullScreen = () =>
  {
    // const { perspective } = sceneCamera || {};
    // This is a complete hack to work around the issue with resize in OrthographicCamera.
    //  We simply use a thunk to switch to a perspective camera before we toggle fullScreen.
    //  Note that this does NOT help with window resize.
    setFullScreen( v => !v );
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

  let rootRef;
  return (
    <div ref={rootRef} style={ fullScreen()? fullScreenStyle : normalStyle }>
      {/* This renders the light DOM if the scene couldn't load */}
      <Show when={state.scene} fallback={props.children}>
        <SceneCanvas scene={state.scene} height={props.height} width={props.width} />
      </Show>

      <Show when={props.config?.showScenes && state.scenes && state.scenes[1]}>
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
  );
}

const UrlViewer = (props) =>
{
  return (
    <WorkerStateProvider store={props.store} config={{ url: props.url, preview: true, debug: false, showScenes: props.showScenes }}>
      <DesignViewer config={ { ...props.config, allowFullViewport: true } }
          componentRoot={props.componentRoot}
          height="100%" width="100%" >
        {props.children}
      </DesignViewer>
    </WorkerStateProvider>
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