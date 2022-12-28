
import { Canvas } from './components/canvas.jsx';
import { CameraControls } from './components/camera.jsx';
import { StrutBuildPanel } from './components/strutbuilder.jsx';
import { MenuBar } from './components/menubar.jsx';
import { createWorkerStore, subController, controllerAction } from './controllers-solid.js';
import { BookmarkBar, ToolBar, ToolFactoryBar } from './components/toolbars.jsx';
import { solidify } from './solid-react.jsx';

const GeometryCanvas = solidify( Canvas );

export const ClassicEditor = ( { worker } ) =>
{
  const { rootController, getScene } = createWorkerStore( worker );

  const bkgdColor = () => getScene() ?.lighting .backgroundColor;

  const undoRedoController = () => subController( rootController(), "undoRedo" );
  const bookmarkController = () => subController( rootController(), 'bookmark' );
  const editorController   = () => subController( rootController(), 'editor' );
  const pickingController  = () => subController( editorController(), 'picking' );
  const strutBuilder       = () => subController( rootController(), 'strutBuilder' );
  const symmController     = () => subController( strutBuilder(), 'symmetry' );
  const toolsController    = () => subController( strutBuilder(), 'tools' );

  const handleKeyDown = event => {
    switch ( event.code ) {
      case "KeyZ":
        if ( event .metaKey ) {
          controllerAction( undoRedoController(), "undo" );
          event .preventDefault();
        }
        break;
    
      case "KeyY":
        if ( event .metaKey ) {
          controllerAction( undoRedoController(), "redo" );
          event .preventDefault();
        }
        break;
    
      default:
        break;
    }
  }
  document .addEventListener( "keydown", handleKeyDown );

  const callbacks = {
    onClick: ( id, position, type, selected ) =>
      controllerAction( pickingController(), 'SelectManifestation', { id } ),
    bkgdClick: () =>
      controllerAction( editorController(), 'DeselectAll' ),
  }

  return (
    <div id='classic' style={{ display: 'grid', 'grid-template-rows': 'min-content 1fr' }} class='whitesmoke-bkgd'>
      <MenuBar controller={rootController()} />
      <div id='editor-main' class='grid-cols-1-min whitesmoke-bkgd' >

        <div id='editor-canvas' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content min-content 1fr' }}>
          <div id='article-and-status' style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
            <div id='model-article' class='placeholder' style={{ 'min-width': '250px' }} >Model | Capture | Article</div>
            <div id='stats-bar' class='placeholder' style={{ 'min-height': '30px' }} >Status</div>
          </div>
          <ToolFactoryBar controller={symmController()} />
          <ToolBar symmetryController={symmController()} toolsController={toolsController()} editorController={editorController()} />
          <div id='canvas-and-bookmarks' style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
            <BookmarkBar bookmarkController={bookmarkController()} toolsController={toolsController()} symmetryController={symmController()} />
            <GeometryCanvas scene={getScene()} callbacks={callbacks} style={{ position: 'relative', height: '100%' }} />
          </div>
        </div>

        <div id='editor-drawer' class='grid-rows-min-1 editor-drawer'>
          <CameraControls symmController={symmController()} bkgdColor={bkgdColor()} />
          <div id="build-parts-measure" style={{ height: '100%' }}>
            <StrutBuildPanel symmController={symmController()} />
          </div>
        </div>

      </div>
    </div>
  )
}
