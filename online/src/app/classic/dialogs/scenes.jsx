
import { unwrap } from "solid-js/store"

import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button";
import IconButton from '@suid/material/IconButton';
import CameraswitchIcon from '@suid/icons-material/Cameraswitch';
import { Tooltip } from '../../framework/tooltip.jsx'

import { useViewer } from "../../../viewer/context/viewer.jsx";
import { useEditor } from '../../framework/context/editor.jsx';
import { SceneCanvas } from "../../../viewer/scenecanvas.jsx";
import { CameraProvider } from "../../../viewer/index.jsx"
import { SceneProvider } from "../../../viewer/context/scene.jsx"
import { useCamera } from "../../../viewer/context/camera.jsx"
import { Stack } from "@suid/material"

const AddSceneButton = () =>
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

const RemoveSceneButton = props =>
  {
    const { rootController, controllerAction, sceneIndex, setSceneIndex } = useEditor();
    const { scenes } = useViewer();

    const removeScene = () =>
      {
        const index = sceneIndex();
        if ( index === scenes.length - 1 )
          setSceneIndex( scenes.length - 2 );
        controllerAction( rootController(), 'removeScene', { index } );
      }
    
    return (
      <Button variant="outlined" size="medium" onClick={removeScene}>Remove Scene</Button>
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
    <Tooltip title='Use this camera' aria-label="camera-tip">
      <IconButton color="primary" aria-label="camera" sx={{ position: 'absolute', bottom: '0px', right: '0px' }}
          onClick={ (e) => handleUseCamera( e, props.index ) }>
        <CameraswitchIcon fontSize="small"/>
      </IconButton>
    </Tooltip>
  );
}

const SaveCameraButton = () =>
{
  const { rootController, controllerAction, sceneIndex, setReload } = useEditor();
  const { state: { camera } } = useCamera();

  const handleSaveCamera = (evt) =>
  {
    evt.stopPropagation();
    const params = { index: sceneIndex(), camera: unwrap( camera ) };
    controllerAction( rootController(), 'updateScene', params );
  }
    
  return (
    <Tooltip title='Save this camera to the scene' aria-label="camera-tip">
      <IconButton color="primary" aria-label="camera" sx={{ position: 'absolute', top: '0px', left: '0px' }}
          onClick={ (e) => handleSaveCamera( e ) }>
        <CameraswitchIcon fontSize="large"/>
      </IconButton>
    </Tooltip>
  );
}
    
const ScenesDialog = props =>
{
  const { scenes } = useViewer();
  const { sceneIndex, setSceneIndex, setReload } = useEditor();

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
                    <div class={ i()===sceneIndex()? 'scenes-entry scenes-selected' : 'scenes-entry' } style={{ position: 'relative' }}
                        onClick={ () => { setSceneIndex( i() ); setReload( true ); console.log( scene.content );
                         } }>
                      <span>{i()}</span>
                      <span>{scene.title}</span>
                      <UseCameraButton index={ i() } />
                    </div>
                  }</For>
                </div>
              </div>
              <Stack class='scene-actions'>
                <div>
                  <AddSceneButton/>
                  <RemoveSceneButton/>
                </div>
                <div>
                  <MoveSceneButton change={-1} label='Move Up' />
                  <MoveSceneButton change={1} label='Move Down' />
                </div>
              </Stack>
            </div>
            <div class='relative-h100'>
              <div class='absolute-0'>
                <SceneProvider name={ `#${sceneIndex()}` }
                      config={{ preview: true, debug: false, labels: props.config?.labels, source: false }}>
                  <SceneCanvas height="100%" width="100%" />
                </SceneProvider>
                <SaveCameraButton/>
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
