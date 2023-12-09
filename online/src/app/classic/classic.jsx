
import { createSignal, createContext, useContext } from "solid-js";

import { CameraControls } from './components/camera.jsx';
import { StrutBuildPanel } from './components/strutbuilder.jsx';
import { controllerAction, controllerProperty, subController } from '../../workerClient/controllers-solid.js';
import { BookmarkBar, ToolBar, ToolFactoryBar } from './components/toolbars.jsx';
import { SceneEditor } from './components/editor.jsx';
import { useWorkerClient } from "../../workerClient/context.jsx";
import { OrbitsDialog } from "./dialogs/orbits.jsx";
import { ShapesDialog } from "./dialogs/shapes.jsx";
import { PolytopesDialog } from "./dialogs/polytopes.jsx";
import { ErrorAlert } from "./components/alert.jsx";
import { CameraProvider } from "../../workerClient/camera.jsx";

export const ClassicEditor = () =>
{
  const { rootController, postMessage } = useWorkerClient();
  postMessage( { type: 'WINDOW_LOCATION', payload: window.location.toString() } );

  const bookmarkController = () => subController( rootController(), 'bookmark' );
  const strutBuilder       = () => subController( rootController(), 'strutBuilder' );
  const toolsController    = () => subController( strutBuilder(), 'tools' );

  let alertRoot;
  return (
    <CameraProvider name='common'>
    <div id='classic' ref={alertRoot} style={{ display: 'grid', 'grid-template-rows': '1fr' }} class='whitesmoke-bkgd'>
      <div id='editor-main' class='grid-cols-1-min whitesmoke-bkgd' >

        <div id='editor-canvas' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content min-content 1fr' }}>
          <div id='article-and-status' style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
            <div id='model-article' class='placeholder' style={{ 'min-width': '250px' }} >Model | Capture | Article</div>
            <div id='stats-bar' class='placeholder' style={{ 'min-height': '30px' }} >Status</div>
          </div>
          <ToolFactoryBar/>
          <ToolBar toolsController={toolsController()} editorController={rootController()} />
          <div id='canvas-and-bookmarks' style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
            <BookmarkBar bookmarkController={bookmarkController()} toolsController={toolsController()} />
            <SceneEditor/>
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
    </CameraProvider>
  )
}

const SymmetryContext = createContext();

export const SymmetryProvider = (props) =>
{
  const { rootController } = useWorkerClient();
  const symmetry = () => controllerProperty( rootController(), 'symmetry' );
  const strutBuilder = () => subController( rootController(), 'strutBuilder' );
  const symmController = () => subController( strutBuilder(), `symmetry.${symmetry()}` );

  const [ showShapesDialog, setShowShapesDialog ] = createSignal( false );
  const [ showOrbitsDialog, setShowOrbitsDialog ] = createSignal( false );
  const [ showPolytopesDialog, setShowPolytopesDialog ] = createSignal( false );
  const api = {
    symmetryDefined: () => !!symmetry(),
    symmetryController: () => symmController(),
    showShapesDialog: () => setShowShapesDialog( true ),
    showOrbitsDialog: () => setShowOrbitsDialog( true ),
    showPolytopesDialog: () => {
      controllerAction( subController( symmController(), 'polytopes' ), 'setQuaternion' );
      setShowPolytopesDialog( true );
    },
  };

  return (
    <SymmetryContext.Provider value={api}>

      {props.children}

      <Show when={!!symmetry()}>
        <ShapesDialog controller={symmController()} open={showShapesDialog()} close={ ()=>setShowShapesDialog(false) } />

        <OrbitsDialog controller={symmController()} open={showOrbitsDialog()} close={ ()=>setShowOrbitsDialog(false) } />

        <PolytopesDialog controller={symmController()} open={showPolytopesDialog()} close={ ()=>setShowPolytopesDialog(false) } />
      </Show>

    </SymmetryContext.Provider>
  );
}

export const useSymmetry = () => useContext( SymmetryContext );
