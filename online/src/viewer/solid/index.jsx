
import { useWorkerClient, WorkerStateProvider } from '../../workerClient/index.js';
import { SceneCanvas } from "./scenecanvas.jsx";

const DesignViewer = ( props ) =>
{
  const { getScene } = useWorkerClient();
  
  return (
    <SceneCanvas toolActions={props.toolActions} scene={getScene()}
        trackball={props.trackball}
        height={props.height} width={props.width} >
      {props.children}
    </SceneCanvas>
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