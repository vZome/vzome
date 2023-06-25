
import { createSignal, Show } from 'solid-js';
import { useWorkerClient, WorkerStateProvider } from '../../workerClient/index.js';
import { SceneCanvas } from "./scenecanvas.jsx";
import { Spinner } from './spinner.jsx';
import { ErrorAlert } from './alert.jsx';

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
  const { getScene, state } = useWorkerClient();
  const [ fullScreen, setFullScreen ] = createSignal( false );

  const showSpinner = () => {
    return props.config?.useSpinner && state.waiting;
  }

  return (
    <div style={ fullScreen()? fullScreenStyle : normalStyle }>
      {/* This renders the light DOM if the scene couldn't load */}
      <Show when={getScene()} fallback={props.children}>
        <SceneCanvas scene={getScene()} height={props.height} width={props.width} />
      </Show>

      {/* { showScenes && <SceneMenu/> } */}

      <Spinner visible={showSpinner()} />
      <ErrorAlert/>
    </div>
  );
}

const UrlViewer = (props) =>
{
  return (
    <WorkerStateProvider config={{ url: props.url, preview: true, debug: false, showScenes: props.showScenes }}>
      <DesignViewer config={ { ...props.config, allowFullViewport: true } }
          height="100%" width="100%" >
        {props.children}
      </DesignViewer>
    </WorkerStateProvider>
  );
}

export { DesignViewer, UrlViewer, SceneCanvas };