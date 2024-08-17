
import { createSignal } from "solid-js"
import { unwrap } from "solid-js/store"

import {
  useDragDropContext,
  DragDropProvider,
  DragDropSensors,
  DragOverlay,
  SortableProvider,
  createSortable,
  closestCenter,
} from "@thisbeyond/solid-dnd";

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

const DraggableScene = (props) =>
{
  const { sceneIndex, setSceneIndex } = useEditor();
  const sortable = createSortable(props.item);
  const [state] = useDragDropContext();
  return (
    <div use:sortable
        class={ props.item===sceneIndex()? 'scenes-entry scenes-selected' : 'scenes-entry' }
        classList={{
          "opacity-25": sortable.isActiveDraggable,
          "transition-transform": !!state.active.draggable,
        }}
        onClick={ () => setSceneIndex( props.item ) }>
      <span>{props.item}</span>
      <span>{props.scene.title}</span>
    </div>
  );
};

const ScenesDialog = props =>
{
  const { scenes } = useViewer();
  const trueScenes = () => scenes .slice( 1 ); // ignore the default scene
  const { sceneIndex, setSceneIndex, rootController, controllerAction } = useEditor();

  const [draggedScene, setDraggedScene] = createSignal(null);
  const ids = () => trueScenes() .map( ( scene, i ) => i + 1 );

  const onDragStart = ({ draggable }) =>
  {
    setDraggedScene(draggable.id);
  }

  const onDragEnd = ({ draggable, droppable }) => {
    if (draggable && droppable) {
      const currentItems = ids();
      const fromIndex = currentItems .indexOf( draggable.id );
      const toIndex = currentItems .indexOf( droppable.id );
      if (fromIndex !== toIndex) {
        const params = { index: fromIndex+1, change: toIndex-fromIndex };
        controllerAction( rootController(), 'moveScene', params );
        // setSceneIndex( fromIndex + 1 );
      }
    }
  };

  return (
    <CameraProvider>
      <Dialog onClose={ () => props.close() } open={props.open} fullWidth='true' maxWidth='xl'>
        <DialogTitle id="scenes-dialog">Scenes</DialogTitle>
        <DialogContent>
          <div class='scenes-dialog-content'>
            <div class='scenes-list-outer'>
              <div class='scenes-scroller'>
                <DragDropProvider
                  onDragStart={onDragStart}
                  onDragEnd={onDragEnd}
                  collisionDetector={closestCenter}
                >
                  <DragDropSensors />
                  <div class='scenes-list'>
                    <SortableProvider ids={ids()}>
                      <For each={ trueScenes() } >{ (scene,i) =>
                        <DraggableScene scene={scene} item={i()+1} />
                      }</For>
                    </SortableProvider>
                  </div>
                  <DragOverlay>
                    <div class="scenes-entry">{draggedScene()}</div>
                  </DragOverlay>
                </DragDropProvider>
              </div>
              <div class='scenes-add'>
                <AddSceneButton/>
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
