
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
  const { rootController, controllerAction, sceneIndex, setSceneIndex } = useEditor();
  const { state: { camera } } = useCamera();

  const addScene = () =>
    {
      const params = { after: sceneIndex(), camera: unwrap( camera ) };
      controllerAction( rootController(), 'duplicateScene', params );
      setSceneIndex( i => ++i );
    }
  
  return (
    <Button variant="outlined" size="medium" onClick={addScene}>Add Scene</Button>
  );
}

const MoveSceneButton = props =>
{
  const { scenes } = useViewer();
  const { rootController, controllerAction, sceneIndex, setSceneIndex } = useEditor();

  const atLimit = () =>
  {
    const target = sceneIndex() + props.change;
    return ( target === 0 ) || ( target >= scenes.length );
  }
  
  const moveScene = () =>
  {
    const params = { index: sceneIndex(), change: props.change };
    controllerAction( rootController(), 'moveScene', params );
    setSceneIndex( i => i + props.change );
  }
  
  return (
    <Button variant="outlined" size="medium" onClick={moveScene} disabled={atLimit()}>{props.label}</Button>
  );
}
  
const ScenesDialog = props =>
{
  const { scenes } = useViewer();
  const { sceneIndex, setSceneIndex } = useEditor();

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
                    <div class={ i()===sceneIndex()? 'scenes-entry scenes-selected' : 'scenes-entry' }
                        onClick={ () => setSceneIndex( i() ) }>
                      <span>{i()}</span>
                      <span>{scene.title}</span>
                    </div>
                  }</For>
                </div>
              </div>
              <div class='scenes-add'>
                <AddSceneButton/>
                <MoveSceneButton change={-1} label='Move Up' />
                <MoveSceneButton change={1} label='Move Down' />
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
