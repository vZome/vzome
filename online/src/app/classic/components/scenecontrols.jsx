
import { batch, createSignal } from 'solid-js';
import { unwrap } from 'solid-js/store';

import Button from "@suid/material/Button"

import { resumeMenuKeyEvents, suspendMenuKeyEvents } from '../context/commands.jsx';
import { copyScenes, insertScene, ScenesDialog } from '../dialogs/scenes.jsx';
import { copyOfCamera, useCamera } from '../../../viewer/context/camera.jsx';
import { useEditor } from '../../framework/context/editor.jsx';
import { useViewer } from '../../../viewer/context/viewer.jsx';

export const SceneControls = () =>
  {
    const buttonStyle = { margin: '4px', 'min-width': '10rem' };
    const [ showScenes, setShowScenes ] = createSignal( false );
    const { rootController, expectResponse, sceneIndex, setSceneIndex, setReload } = useEditor();
    const { state: { camera } } = useCamera();
    const { scenes, setScenes } = useViewer();
    const noScenes = () => !scenes || scenes.length < 2;

    const doOpen = () =>
    {
      if ( sceneIndex() === 0 ) {
        setSceneIndex( 1 );
      }
      setReload( true );
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
      expectResponse( '', 'captureSnapshot', {} )
        .then( snapshot => {
          const newScene = { title: '', content: ' ', snapshot, camera: copyOfCamera( camera ) };
          batch( () => {
            setScenes( scenes => insertScene( scenes, newScene, sceneIndex() + 1 ) );
            setSceneIndex( i => ++i );
          });    
          doOpen();
        });
    }
  
    return (
      <div id='model-article' >
        <Button variant="outlined" style={buttonStyle} onClick={ doCapture } >Capture Scene</Button>
        <Button variant="outlined" style={buttonStyle} onClick={ doOpen } disabled={noScenes()} >Show Scenes</Button>
        <ScenesDialog open={ showScenes() && !noScenes() } close={ doClose }/>
      </div>
    );
  }
  