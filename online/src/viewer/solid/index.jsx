
import { useWorkerClient } from "../../workerClient/index.js";
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

export { DesignViewer, SceneCanvas };