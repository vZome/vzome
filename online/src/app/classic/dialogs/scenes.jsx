
import { createSignal } from "solid-js"
import { unwrap } from "solid-js/store"

import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"

import { useViewer } from "../../../viewer/context/viewer.jsx";
import { useEditor } from '../../framework/context/editor.jsx';
import { SceneCanvas } from "../../../viewer/scenecanvas.jsx";
import { CameraProvider } from "../../../viewer/index.jsx"
import { SceneProvider } from "../../../viewer/context/scene.jsx"
import { useCamera } from "../../../viewer/context/camera.jsx"

const AddSceneButton = props =>
{
  const { state: { source }, rootController, controllerAction } = useEditor();
  const { state: { camera } } = useCamera();

  const addScene = () =>
    {
      const params = { after: props.sceneIndex, camera: unwrap( camera ) };
      controllerAction( rootController(), 'duplicateScene', params );
    }
  
  return (
    <Button variant="outlined" size="medium" onClick={addScene}>Add Scene</Button>
  );
}

const ScenesDialog = props =>
{
  const [ sceneIndex, setSceneIndex ] = createSignal( 1 );
  const { scenes } = useViewer();

  return (
    <CameraProvider>
      <Dialog onClose={ () => props.close() } open={props.open} fullWidth='true' maxWidth='xl'>
        <DialogTitle id="scenes-dialog">Scenes</DialogTitle>
        <DialogContent>
          <div class='scenes-dialog-content'>
            <div class='scenes-list-outer'>
              <div class='scenes-scroller'>
                <div class='scenes-list'>
                  <For each={ scenes } >{ (scene,i) =>
                    (i() > 0) &&
                    <div class={ i()===sceneIndex()? 'scenes-entry scenes-selected' : 'scenes-entry'}
                        onClick={ () => setSceneIndex( i() ) }>
                      <span>{i()}</span>
                      <span>{scene.title}</span>
                    </div>
                  }</For>
                </div>
              </div>
              <div class='scenes-add'>
                <AddSceneButton sceneIndex={sceneIndex()}/>
              </div>
            </div>
            <div class='scenes-canvas-outer'>
              <div class='scenes-canvas-inner'>
                <SceneProvider name={ `#${sceneIndex()}` } config={{ preview: true, debug: false, labels: props.config?.labels, source: false }}>
                  <SceneCanvas height="100%" width="100%" />
                </SceneProvider>
              </div>
            </div>
          </div>
        </DialogContent>
        <DialogActions>
          <Button size="small" onClick={ ()=>props.close() } color="primary">Close</Button>
        </DialogActions>
      </Dialog>
    </CameraProvider>
  );
}

export { ScenesDialog };
