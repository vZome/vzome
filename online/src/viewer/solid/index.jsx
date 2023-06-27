
import { REVISION } from '../../revision.js'
export { REVISION } from '../../revision.js'

console.log( `vzome-viewer revision ${REVISION}` );

import { urlViewerCSS } from "./urlviewer.css";

import { createSignal, onMount, Show } from 'solid-js';
import { render } from 'solid-js/web';

import { useWorkerClient, WorkerStateProvider } from '../../workerClient/index.js';
import { SceneCanvas } from "./scenecanvas.jsx";
import { Spinner } from './spinner.jsx';
import { ErrorAlert } from './alert.jsx';
import { SceneMenu } from './scenes.jsx';

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
  zIndex: '1300',
};

const DesignViewer = ( props ) =>
{
  const { state } = useWorkerClient();
  const [ fullScreen, setFullScreen ] = createSignal( false );

  const showSpinner = () => {
    return props.config?.useSpinner && state.waiting;
  }

  let rootRef;
  onMount( () => {
    rootRef.appendChild( document.createElement("style") ).textContent = urlViewerCSS;
  });

  return (
    <div ref={rootRef} style={ fullScreen()? fullScreenStyle : normalStyle }>
      {/* This renders the light DOM if the scene couldn't load */}
      <Show when={state.scene} fallback={props.children}>
        <SceneCanvas scene={state.scene} height={props.height} width={props.width} />
      </Show>

      <Show when={props.config?.showScenes && state.scenes && state.scenes[1]}>
        <SceneMenu root={rootRef} />
      </Show>

      <Spinner visible={showSpinner()} />
      <ErrorAlert/>
    </div>
  );
}

const UrlViewer = (props) =>
{
  return (
    <WorkerStateProvider store={props.store} config={{ url: props.url, preview: true, debug: false, showScenes: props.showScenes }}>
      <DesignViewer config={ { ...props.config, allowFullViewport: true } }
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
      <UrlViewer store={store} config={config} >
        {/* Make a slot for the light DOM, somehow! */}
      </UrlViewer>
    );
  }

  render( bindComponent, container );
}


export { DesignViewer, UrlViewer, SceneCanvas, renderViewer };