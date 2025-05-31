
import { batch, createSignal, onCleanup, onMount } from "solid-js"
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
import Button from "@suid/material/Button";
import IconButton from '@suid/material/IconButton';
import CameraswitchIcon from '@suid/icons-material/Cameraswitch';
import TextField from "@suid/material/TextField";
import Stack from "@suid/material/Stack";
import Input from "@suid/material/Input";
import { Tooltip } from '../../framework/tooltip.jsx'

import { useViewer } from "../../../viewer/context/viewer.jsx";
import { useEditor } from '../../framework/context/editor.jsx';
import { SceneCanvas } from "../../../viewer/scenecanvas.jsx";
import { CameraProvider, copyOfCamera } from "../../../viewer/context/camera.jsx";
import { SceneProvider } from "../../../viewer/context/scene.jsx"
import { useCamera } from "../../../viewer/context/camera.jsx"

export const copyScenes = (scenes) => scenes .map( scene => { const { ...all } = unwrap( scene ); return { ...all }; } );

export const insertScene = ( scenes, newScene, index ) =>
{
  const newScenes = copyScenes( scenes );
  newScenes .splice( index, 0, newScene );
  return newScenes;
}

const moveScene = ( scenes, fromIndex, toIndex ) =>
{
  const newScenes = copyScenes( scenes );
  const movedScene = newScenes .splice( fromIndex+1, 1 )[ 0 ];
  newScenes .splice( toIndex+1, 0, movedScene );
  return newScenes;
}

const AddSceneButton = () =>
{
  const { sceneIndex, setSceneIndex, setEdited } = useEditor();
  const { state: { camera } } = useCamera();
  const { scenes, setScenes } = useViewer();

  const addScene = () =>
    {
      const newScene = { title: '', content: '', snapshot: scenes[ sceneIndex() ] .snapshot, camera: copyOfCamera( camera ) };
      batch( () => {
        setScenes( scenes => insertScene( scenes, newScene, sceneIndex() + 1 ) );
        setSceneIndex( i => ++i );
        setEdited( true );
      });
    }
  
  return (
    <Tooltip title='Capture a new scene with this camera' aria-label="add-scene-tip">
      <Button variant="outlined" size="medium" onClick={addScene} >
        Capture Scene
      </Button>
    </Tooltip>
  );
}

const RemoveSceneButton = props =>
  {
    const { sceneIndex, setSceneIndex, setReload, setEdited } = useEditor();
    const { scenes, setScenes } = useViewer();

    const removeScene = () =>
      {
        const index = sceneIndex();
        batch( () => {
          setScenes( scenes => {
            const newScenes = copyScenes( scenes );
            newScenes .splice( index, 1 );
            return newScenes;
          })
          if ( index === scenes.length )
            setSceneIndex( scenes.length - 1 );
          setReload( true );
          setEdited( true );
        });
      }
    
    return (
      <Tooltip title='Delete this scene' aria-label="delete-scene-tip">
        <Button variant="outlined" size="medium" onClick={removeScene} >
          Delete Scene
        </Button>
      </Tooltip>
    );
  }
  
const DraggableScene = (props) =>
{
  const { sceneIndex, setSceneIndex, setReload, setEdited } = useEditor();
  const { setScenes } = useViewer();
  const sortable = createSortable(props.item);
  const [state] = useDragDropContext();

  const saveTitle = item => ( evt, value ) =>
  {    
    evt.stopPropagation();
    setScenes( item, 'title', value );
    setEdited( true );
  }
  
  return (
    <div use:sortable
        class={ props.item===sceneIndex()? 'scenes-entry scenes-selected' : 'scenes-entry' }
        style={{ position: 'relative' }}
        classList={{
          "opacity-25": sortable.isActiveDraggable,
          "transition-transform": !!state.active.draggable,
        }}
        onClick={ () => { setSceneIndex( props.item ); setReload( true ); } } >
      <span>{props.item}</span>
      <Input sx={{ borderBottom: 0 }} value={ props.scene.title || ' ' } onChange={ saveTitle(props.item) } />
      <UseCameraButton index={ props.item } />
    </div>
  );
}

const UseCameraButton = props =>
{
  const { tweenCamera } = useCamera();
  const { scenes } = useViewer();

  const handleUseCamera = (evt,index) =>
  {
    evt.stopPropagation();
    console.log( 'Using camera from scene', index );
    tweenCamera( scenes[ index ] .camera );
  }
  
  return (
    <Tooltip title='Use this camera' aria-label="use-camera-tip">
      <IconButton color="primary" aria-label="camera" sx={{ position: 'absolute', bottom: '0px', right: '0px' }}
          onClick={ (e) => handleUseCamera( e, props.index ) }>
        <CameraswitchIcon fontSize="small"/>
      </IconButton>
    </Tooltip>
  );
}

const SaveCameraButton = () =>
{
  const { sceneIndex, setEdited } = useEditor();
  const { state: { camera } } = useCamera();
  const { setScenes } = useViewer();

  const handleSaveCamera = (evt) =>
  {
    evt.stopPropagation();
    setScenes( sceneIndex(), 'camera', unwrap( camera ) );
    setEdited( true );
  }
    
  return (
    <Tooltip title='Save this camera to the current scene' aria-label="save-camera-tip">
      <Button variant="outlined" size="medium" onClick={ (e) => handleSaveCamera( e ) } >
        Save Camera
      </Button>
    </Tooltip>
  );
}

const ScenesList = () =>
{
  const { scenes, setScenes } = useViewer();
  const trueScenes = () => scenes .slice( 1 ); // ignore the default scene
  const { sceneIndex, setSceneIndex, setReload, setEdited } = useEditor();
  const arrowKeyListener = (evt) =>
  {
    if ( ( sceneIndex() > 1 ) && (( evt.code === "ArrowUp" ) )) {
      evt.preventDefault();
      evt.stopPropagation();
      setSceneIndex( i => --i );
      setReload( true );
    } else if ( ( sceneIndex() < scenes.length - 1 ) && (( evt.code === "ArrowDown" ) )) {
      evt.preventDefault();
      evt.stopPropagation();
      setSceneIndex( i => ++i );
      setReload( true );
    }
  }
  onMount(   () => document .body .addEventListener(    "keydown", arrowKeyListener ) );
  onCleanup( () => document .body .removeEventListener( "keydown", arrowKeyListener ) );

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
        batch( () => {
          setScenes( scenes => moveScene( scenes, fromIndex, toIndex ) );
          setSceneIndex( toIndex + 1 );
          setReload( true );
          setEdited( true );
        } );
      }
    }
  };

  return (
    <div class='scenes-list'>
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
  );
}
    
const ScenesDialog = props =>
{
  const { sceneIndex, setEdited } = useEditor();
  const { scenes, setScenes } = useViewer();

  const saveNotes = ( evt, value ) =>
  {
    evt.stopPropagation();
    setScenes( sceneIndex(), 'content', value );
    setEdited( true );
  }

  return (
    <CameraProvider>
      <Dialog onClose={ () => props.close() } open={props.open} fullWidth='true' maxWidth='xl'>
        <DialogTitle id="scenes-dialog">Scenes</DialogTitle>
        <DialogContent>
          <div class='scenes-dialog-content'>
            <div class='scenes-list-outer'>
              <div class='scenes-scroller'>
                <ScenesList/>
              </div>
            </div>
            <div class="scene-details">
              <div class='relative-h100'>
                <div class='absolute-0'>
                  <SceneProvider index={ sceneIndex() }
                        config={{ preview: true, debug: false, labels: props.config?.labels, source: false }}>
                    <SceneCanvas height="100%" width="100%" />
                  </SceneProvider>
                  <Stack class='scene-actions'>
                    <SaveCameraButton/>
                    <AddSceneButton/>
                    <RemoveSceneButton/>
                  </Stack>
                </div>
              </div>
              <div class="scene-text">
                {/* <TextField id="scene-title" label="Title" multiline rows={3}
                  value={ scenes[ sceneIndex() ]?.title } onChange={ (event, value) => {} }
                /> */}
                <TextField id="scene-description" label="Notes" multiline rows={2}
                  value={ scenes[ sceneIndex() ]?.content || ' ' } onChange={ saveNotes }
                />
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
