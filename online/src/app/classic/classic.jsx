
import { createSignal, createContext, useContext, mergeProps } from "solid-js";

import { CameraControls } from './components/camera.jsx';
import { StrutBuildPanel } from './components/strutbuilder.jsx';
import { controllerAction, controllerProperty, subController } from '../../workerClient/controllers-solid.js';
import { BookmarkBar, ToolBar, ToolFactoryBar } from './components/toolbars.jsx';
import { SceneEditor } from './components/editor.jsx';
import { useWorkerClient } from "../../workerClient/index.js";
import { ErrorAlert } from '../../viewer/solid/alert.jsx';
import { OrbitsDialog } from "./components/orbits.jsx";
import { ShapesDialog } from "./components/shapes.jsx";
import { MenuAction } from "./components/menuaction.jsx";
import { RotationProvider } from "../../viewer/solid/rotation.jsx";
import { PolytopesDialog } from "./components/polytopes.jsx";

export const ClassicEditor = ( props ) =>
{
  const { rootController } = useWorkerClient();

  const bookmarkController = () => subController( rootController(), 'bookmark' );
  const strutBuilder       = () => subController( rootController(), 'strutBuilder' );
  const toolsController    = () => subController( strutBuilder(), 'tools' );

  return (
    <RotationProvider>
    <div id='classic' style={{ display: 'grid', 'grid-template-rows': '1fr' }} class='whitesmoke-bkgd'>
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
    </RotationProvider>
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

      <ShapesDialog controller={symmController()} open={showShapesDialog()} close={ ()=>setShowShapesDialog(false) } />

      <OrbitsDialog controller={symmController()} open={showOrbitsDialog()} close={ ()=>setShowOrbitsDialog(false) } />

      <PolytopesDialog controller={symmController()} open={showPolytopesDialog()} close={ ()=>setShowPolytopesDialog(false) } />

    </SymmetryContext.Provider>
  );
}

export const useSymmetry = () => useContext( SymmetryContext );

export const createSymmetryAction = ( doClose ) => ( props ) =>
{
  const { symmetryController } = useSymmetry();
  const onClick = () => 
  {
    doClose();
    controllerAction( symmetryController(), props.action );
  }
  // I was destructuring props here, and lost reactivity!
  //  It usually doesn't matter for menu items, except when there is checkbox state.
  return MenuAction( mergeProps( { onClick }, props ) );
}
