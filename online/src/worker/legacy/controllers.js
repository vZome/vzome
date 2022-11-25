
import { documentFactory } from './core.js'
import { com } from './core-java.js'
import { JsProperties } from './jsweet2js.js';
import { renderedModelTransducer } from './scenes.js';

class EditorController extends com.vzome.desktop.controller.DefaultController
{
  constructor( performer )
  {
    super();
    this.performer = performer;
  }

  // There are no implementors of doParamAction() in the Java code
  doParamAction( action, params )
  {
    this.performer .configureAndPerformEdit( action, params && params .getConfig() );
  }

  doAction( action )
  {
    this.performer .configureAndPerformEdit( action, {} );
  }
}

class PickingController extends com.vzome.desktop.controller.DefaultController
{
  constructor( renderedModel )
  {
    super();
    this.renderedModel = renderedModel;
  }

  // There are no implementors of doParamAction() in the Java code
  doParamAction( action, params )
  {
    const config = params .getConfig();
    const { id } = config;
    if ( id ) {
      const rm = this .renderedModel .getRenderedManifestation( id );
      const picked = rm .getManifestation();
      super.doParamAction( action, new JsProperties( { ...config, picked } ) );
    }
    else
      super.doParamAction( action, params );
  }
}

class BuildPlaneController extends com.vzome.desktop.controller.DefaultController
{
  constructor( renderedModel, orbitSource )
  {
    super();
    this.renderedModel = renderedModel;
    this.buildPlanes = orbitSource .buildPlanes;
  }

  // There are no implementors of doParamAction() in the Java code
  doParamAction( action, params )
  {
    const config = params .getConfig();
    const { id, plane, zone, index } = config;
    if ( id ) {
      const rm = this .renderedModel .getRenderedManifestation( id );
      const anchor = rm .getManifestation() .toConstruction();

      const buildPlane = this.buildPlanes[ plane ];
      const buildZone = buildPlane .zones[ zone ];
      const length = buildZone .vectors[ index ] .scale;

      super.doParamAction( 'StrutCreation', new JsProperties( { anchor, zone: buildZone.zone, length } ) );
    }
    else
      super.doParamAction( action, params );
  }
}

export const newDesign = ( fieldName, sceneReporter ) =>
{
  const { orbitSource, renderedModel, configureAndPerformEdit, batchRender, toolsModel, bookmarkFactory } = documentFactory( fieldName );
  
  const controller = new com.vzome.desktop.controller.DefaultController(); // this is the equivalent of DocumentController

  // This one has no equivalent in Java, though I've considered it.  Too much change.
  const editorController = new EditorController( { configureAndPerformEdit }, orbitSource.buildPlanes );
  controller .addSubController( 'editor', editorController );

  // This has similar function to the Java equivalent, but a very different mechanism
  const pickingController = new PickingController( renderedModel );
  editorController .addSubController( 'picking', pickingController );

  // This has no desktop equivalent
  const buildPlaneController = new BuildPlaneController( renderedModel, orbitSource );
  editorController .addSubController( 'buildPlane', buildPlaneController );

  const bookmarkController = new com.vzome.desktop.controller.ToolFactoryController( bookmarkFactory );
  controller .addSubController( 'bookmark', bookmarkController );

  const strutBuilder = new com.vzome.desktop.controller.DefaultController(); // this is the equivalent of StrutBuilderController
  controller .addSubController( 'strutBuilder', strutBuilder );

  const symmController = new com.vzome.desktop.controller.SymmetryController( strutBuilder, orbitSource, null );
  strutBuilder .addSubController( 'symmetry', symmController );

  const toolsController = new com.vzome.desktop.controller.ToolsController( toolsModel );
  toolsController .addTool( toolsModel .get( "bookmark.builtin/ball at origin" ) );
  strutBuilder .addSubController( 'tools', toolsController );

  const shapes = {};
  const transducer = renderedModelTransducer( shapes, sceneReporter ); // reports changes back to the client
  renderedModel .addListener( transducer );

  const embedding = orbitSource .getEmbedding();
  const scene = { embedding, shapes: {} };
  sceneReporter .sceneChanged( scene );

  batchRender( transducer );

  // const config = {
  //   groupName: "H4",
  //   renderGroupName: "H4",
  //   index: 8,
  //   edgesToRender: 8
  // };
  // configureAndPerformEdit( "Polytope4d", config );

  return { controller, orbitSource };
}
