
import { documentFactory, parse } from './core.js'
import { java } from "./candies/j4ts-2.1.0-SNAPSHOT/bundle.js"
import { com } from './core-java.js'
import { JsProperties } from './jsweet2js.js';
import { resolveBuildPlanes } from './scenes.js';
import { interpret, RenderHistory, Step } from './interpreter.js';

class EditorController extends com.vzome.desktop.controller.DefaultController
{
  constructor( design, clientEvents )
  {
    super();
    this.design = design;
    this.clientEvents = clientEvents;
  }

  // There are no implementors of doParamAction() in the Java code
  doParamAction( action, params )
  {
    switch ( action ) {

      case "exportText":
        const format = params .getConfig() .format;  // TODO remove the STL hardcoding!

        switch ( format ) {

          case 'stl': {
            const exporter = new com.vzome.core.exporters.StlExporter();
            const out = new java.io.StringWriter();
            exporter .exportGeometry( this.design .renderedModel, null, out, 500, 800 );
            this.clientEvents .textExported( action, out .toString() );  // returning the action for Promise correlation on the client
            return;
          }
        
          default: // vZome
            const text = this .design .serializeToDom() .toIndentedString( "" );
            this.clientEvents .textExported( action, text );
            return;
        }
        break;
    
      default:
        this.design .configureAndPerformEdit( action, params && params .getConfig() );

        // For the classic client app, this is redundant, since it can use the exportText action,
        //   but I still need it for React-based clients.
        const text = this .design .serializeToDom() .toIndentedString( "" );
        this.clientEvents .designSerialized( text );
        break;
    }
  }

  doAction( action )
  {
    this .doParamAction( action, new JsProperties() );
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
    switch (action) {

      case 'STRUT_CREATION_TRIGGERED': {
        const { id, plane, zone, index, orientation } = config;
        if ( id ) {
          const rm = this .renderedModel .getRenderedManifestation( id );
          const anchor = rm .getManifestation() .toConstruction();

          const buildPlane = this.buildPlanes[ plane ];
          const buildZone = buildPlane .zones[ zone ];
          let axis = buildZone .zone; // the Axis object
          const orbit = axis .getOrbit();
          const symmetry = orbit .getSymmetry();
          const permutation = symmetry .getPermutation( orientation );
          axis = permutation .permute( axis, 0 ); // TODO: is PLUS always right?
          const length = buildZone .vectors[ index ] .scale;

          super.doParamAction( 'StrutCreation', new JsProperties( { anchor, zone: axis, length } ) );
          return;
        } // else fall through
      }
    
      case 'JOIN_BALLS_TRIGGERED': {
        const { id1, id2 } = config;
        if ( id1 && id2 ) {
          const rm1 = this .renderedModel .getRenderedManifestation( id1 );
          const rm2 = this .renderedModel .getRenderedManifestation( id2 );
          const start = rm1 .getManifestation() .toConstruction();
          const end = rm2 .getManifestation() .toConstruction();
          super.doParamAction( 'JoinPointPair', new JsProperties( { start, end } ) );
          return;
        } // else fall through
      }
    
      default:
        super.doParamAction( action, params );
    }
  }
}

export const loadDesign = ( xml, debug, clientEvents ) =>
{
  const design = parse( xml );
      
  const { orbitSource, camera, lighting, xmlTree, targetEditId, field, scenes } = design;
  if ( field.unknown ) {
    throw new Error( `Field "${field.name}" is not supported.` );
  }
  clientEvents .xmlParsed( xmlTree );
  clientEvents .scenesDiscovered( scenes );

  // the next step may take several seconds, which is why we already reported PARSE_COMPLETED
  const renderHistory = new RenderHistory( design );
  if ( ! debug ) {
    interpret( Step.DONE, renderHistory, [] );
  } // else in debug mode, we'll interpret incrementally

  // TODO: define a better contract for before/after.
  //  Here we are using before=false with targetEditId, which is meant to be the *next*
  //  edit to be executed, so this really should be before=true.
  //  However, the semantics of the HistoryInspector UI require the edit field to contain the "after" edit ID.
  //  Thus, we are too tightly coupled to the UI here!
  //  See also the 'EDIT_SELECTED' case in onmessage(), below.
  const { shapes, edit } = renderHistory .getScene( debug? '--START--' : targetEditId, false );
  const embedding = orbitSource .getEmbedding();
  const scene = { lighting, camera, embedding, shapes };
  clientEvents .sceneChanged( scene, edit );

  const planes = resolveBuildPlanes( orbitSource .buildPlanes );
  const { orientations, symmetry, permutations } = orbitSource;
  const scalars = [ symmetry .getField() .getAffineScalar() .evaluate() ]; //TODO get them all!
  clientEvents .symmetryChanged( { orientations, permutations, scalars, planes } );

  return createControllers( design, renderHistory, clientEvents );
}

export const newDesign = ( fieldName, clientEvents ) =>
{
  const design = documentFactory( fieldName );
  const { orbitSource } = design;

  const renderHistory = new RenderHistory( design );
  const { shapes, edit } = renderHistory .getScene( '--START--', false );
  const embedding = orbitSource .getEmbedding();
  const scene = { embedding, shapes };
  clientEvents .sceneChanged( scene, edit );

  const planes = resolveBuildPlanes( orbitSource .buildPlanes );
  const { orientations, symmetry, permutations } = orbitSource;
  const scalars = [ symmetry .getField() .getAffineScalar() .evaluate() ]; //TODO get them all!
  clientEvents .symmetryChanged( { orientations, permutations, scalars, planes } );

  return createControllers( design, renderHistory, clientEvents );
}

const createControllers = ( design, renderHistory, clientEvents ) =>
{
  const { orbitSource, renderedModel, toolsModel, bookmarkFactory, history } = design;

  const controller = new EditorController( design, clientEvents ); // this is the equivalent of DocumentController
  controller .setErrorChannel( {
    reportError: ( message, args ) => {
      console.log( 'controller error:', message, args );
      if ( message === com.vzome.desktop.api.Controller.UNKNOWN_ERROR_CODE ) {
        const ex = args[ 0 ];
        clientEvents .errorReported( ex.message );
      } else
        clientEvents .errorReported( message )
    },
  } );

  // This has similar function to the Java equivalent, but a very different mechanism
  const pickingController = new PickingController( renderedModel );
  controller .addSubController( 'picking', pickingController );

  // This has no desktop equivalent
  const buildPlaneController = new BuildPlaneController( renderedModel, orbitSource );
  controller .addSubController( 'buildPlane', buildPlaneController );

  const undoRedoController = new com.vzome.desktop.controller.UndoRedoController( history );
  controller .addSubController( 'undoRedo', undoRedoController );

  const bookmarkController = new com.vzome.desktop.controller.ToolFactoryController( bookmarkFactory );
  controller .addSubController( 'bookmark', bookmarkController );

  const strutBuilder = new com.vzome.desktop.controller.DefaultController(); // this is the equivalent of StrutBuilderController
  controller .addSubController( 'strutBuilder', strutBuilder );

  const symmController = new com.vzome.desktop.controller.SymmetryController( strutBuilder, orbitSource, null );
  strutBuilder .addSubController( 'symmetry', symmController );

  const toolsController = new com.vzome.desktop.controller.ToolsController( toolsModel );
  toolsController .addTool( toolsModel .get( "bookmark.builtin/ball at origin" ) );
  strutBuilder .addSubController( 'tools', toolsController );

  // Not beautiful, but functional
  controller .getScene = ( editId, before=false ) =>
  {
    return renderHistory .getScene( editId, before );
  }

  // TODO: fix this terrible hack!
  controller .renderScene = () => renderHistory .recordSnapshot( '--END--', '--END--', [] );

  return controller;
}
