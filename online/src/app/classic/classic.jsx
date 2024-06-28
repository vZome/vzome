
import { subController, useEditor } from '../framework/context/editor.jsx';

import Button from "@suid/material/Button"

import { CameraControls } from './components/camera.jsx';
import { StrutBuildPanel } from './components/strutbuilder.jsx';
import { BookmarkBar, ToolBar, ToolFactoryBar } from './components/toolbars.jsx';
import { SceneEditor } from './components/editor.jsx';
import { ErrorAlert } from "./components/alert.jsx";
import { createSignal } from 'solid-js';
import { ScenesDialog } from './dialogs/scenes.jsx';
import { resumeMenuKeyEvents, suspendMenuKeyEvents } from './context/commands.jsx';

const SceneControls = props =>
{
  const buttonStyle = { margin: '4px', 'min-width': '10rem' };
  const [ showScenes, setShowScenes ] = createSignal( false );
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
  
  return (
    <div id='model-article' >
      <Button variant="outlined" style={buttonStyle} onClick={ ()=>{} } disabled={true} >Capture Scene</Button>
      <Button variant="outlined" style={buttonStyle} onClick={ doOpen } >Show Scenes</Button>
      <ScenesDialog open={ showScenes() } close={ doClose } />
    </div>
  );
}

export const ClassicEditor = () =>
{
  const { rootController, indexResources } = useEditor();
  indexResources();

  const bookmarkController = () => subController( rootController(), 'bookmark' );
  const strutBuilder       = () => subController( rootController(), 'strutBuilder' );
  const toolsController    = () => subController( strutBuilder(), 'tools' );

  let alertRoot;
  return (
    <div id='classic' ref={alertRoot} style={{ display: 'grid', 'grid-template-rows': '1fr' }} class='whitesmoke-bkgd'>
      <div id='editor-main' class='grid-cols-1-min whitesmoke-bkgd' >

        <div id='editor-canvas' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content 1fr min-content' }}>
          <ToolFactoryBar/>
          <ToolBar toolsController={toolsController()} editorController={rootController()} />
          <div id='canvas-and-bookmarks' style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
            <BookmarkBar bookmarkController={bookmarkController()} toolsController={toolsController()} />
            <SceneEditor/>
          </div>
          <div id='article-and-status' style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
            <SceneControls/>
            <div id='stats-bar' class='placeholder' style={{ 'min-height': '30px' }} >Status</div>
          </div>
        </div>

        <div id='editor-drawer' class='grid-rows-min-1 editor-drawer'>
          <CameraControls/>
          <div id="build-parts-measure" style={{ height: '100%' }}>
            <StrutBuildPanel/>
          </div>
        </div>

      </div>
      <ErrorAlert/>
    </div>
  )
}
