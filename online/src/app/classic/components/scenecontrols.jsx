
import { createSignal } from 'solid-js';
import { unwrap } from 'solid-js/store';

import Button from "@suid/material/Button"

import { resumeMenuKeyEvents, suspendMenuKeyEvents } from '../context/commands.jsx';
import { ScenesDialog } from '../dialogs/scenes.jsx';
import { useCamera } from '../../../viewer/context/camera.jsx';
import { useEditor } from '../../framework/context/editor.jsx';
import { useViewer } from '../../../viewer/context/viewer.jsx';

export const SceneControls = () =>
  {
    const buttonStyle = { margin: '4px', 'min-width': '10rem' };
    const [ showScenes, setShowScenes ] = createSignal( false );
    const { rootController, controllerAction, sceneIndex, setSceneIndex } = useEditor();
    const { state: { camera } } = useCamera();
    const { scenes } = useViewer();
    const noScenes = () => !scenes || scenes.length < 2;

    const doOpen = () =>
    {
      setShowScenes( true );
      suspendMenuKeyEvents();
    }
    
    const doClose = () =>
    {
      resumeMenuKeyEvents();
      setShowScenes( false );
    }
    
    const doCapture = () =>
    {
      const params = { after: sceneIndex(), camera: unwrap( camera ) };
      controllerAction( rootController(), 'captureScene', params );
      setSceneIndex( i => ++i );
      doOpen();
    }
  
    return (
      <div id='model-article' >
        <Button variant="outlined" style={buttonStyle} onClick={ doCapture } >Capture Scene</Button>
        <Button variant="outlined" style={buttonStyle} onClick={ doOpen } disabled={noScenes()} >Show Scenes</Button>
        <ScenesDialog open={ showScenes() } close={ doClose }/>
      </div>
    );
  }
  