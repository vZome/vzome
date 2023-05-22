
import { createSignal } from "solid-js";

import { CameraControls } from './components/camera.jsx';
import { StrutBuildPanel } from './components/strutbuilder.jsx';
import { controllerProperty, subController, controllerAction } from './controllers-solid.js';
import { BookmarkBar, ToolBar, ToolFactoryBar } from './components/toolbars.jsx';
import { SceneEditor } from "./editor.jsx";
import { createSwitcherTool } from "./tools/strutdrag.jsx";

export const ClassicEditor = ( props ) =>
{
  const syncCamera = camera => props.setState( 'scene', 'liveCamera', camera );

  const bkgdColor = () =>
    props.getScene() ?.lighting ?.backgroundColor;

  const bookmarkController = () => subController( props.controller, 'bookmark' );
  const pickingController  = () => subController( props.controller, 'picking' );
  const strutBuilder       = () => subController( props.controller, 'strutBuilder' );
  const symmetry = () => controllerProperty( props.controller, 'symmetry' );
  const symmController     = () => subController( strutBuilder(), `symmetry.${symmetry()}` );
  const toolsController    = () => subController( strutBuilder(), 'tools' );

  const [ strutting, setStrutting ] = createSignal( false );

  const selectionTool = {
    onClick: ( id, position, type, selected ) => {
      console.log( 'selectionTool clicked' );
      controllerAction( pickingController(), 'SelectManifestation', { id } )
    },
    bkgdClick: () =>
    {
      controllerAction( props.controller, 'DeselectAll' );
    },
    onDragStart: ( id, position, type, starting, evt ) => {
      console.log( 'selectionTool onDragStart?????!!!!!' );
    },
    onDrag: evt => {
      console.log( 'selectionTool onDrag?????!!!!!' );
    },
    onDragEnd: evt => {
      console.log( 'selectionTool onDragEnd?????!!!!!' );
    }
  };
  const strutPreviewTool = {
    onClick: ( id, position, type, selected ) => {
      console.log( 'strutPreviewTool clicked?????!!!!!' );
    },
    bkgdClick: () => {},
    onDragStart: ( id, position, type, starting, evt ) => {
      console.log( 'strutPreviewTool drag started' );
      setStrutting( true );
    },
    onDrag: evt => {
      if ( strutting() ) {
        console.log( 'strutPreviewTool drag ongoing' );
      }
    },
    onDragEnd: evt => {
      if ( strutting() ) {
        console.log( 'strutPreviewTool drag finished' );
        setStrutting( false );
      }
    }
  };

  const switcherTool = createSwitcherTool( selectionTool, strutPreviewTool, 100 );

  return (
    <div id='classic' style={{ display: 'grid', 'grid-template-rows': '1fr' }} class='whitesmoke-bkgd'>
      <div id='editor-main' class='grid-cols-1-min whitesmoke-bkgd' >

        <div id='editor-canvas' style={{ display: 'grid', 'grid-template-rows': 'min-content min-content min-content 1fr' }}>
          <div id='article-and-status' style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
            <div id='model-article' class='placeholder' style={{ 'min-width': '250px' }} >Model | Capture | Article</div>
            <div id='stats-bar' class='placeholder' style={{ 'min-height': '30px' }} >Status</div>
          </div>
          <ToolFactoryBar controller={symmController()} />
          <ToolBar symmetryController={symmController()} toolsController={toolsController()} editorController={props.controller} />
          <div id='canvas-and-bookmarks' style={{ display: 'grid', 'grid-template-columns': 'min-content 1fr' }}>
            <BookmarkBar bookmarkController={bookmarkController()} toolsController={toolsController()} symmetryController={symmController()} />

            <SceneEditor scene={props.getScene()} strutting={strutting()}
              syncCamera={syncCamera} toolActions={selectionTool}
              style={{ position: 'relative', height: '100%' }} />
          </div>
        </div>

        <div id='editor-drawer' class='grid-rows-min-1 editor-drawer'>
          <CameraControls controller={props.controller} bkgdColor={bkgdColor()} />
          <div id="build-parts-measure" style={{ height: '100%' }}>
            <StrutBuildPanel symmController={symmController()} />
          </div>
        </div>

      </div>
    </div>
  )
}
