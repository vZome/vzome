
import { com } from '../core-java.js';
import { documentFactory, parse } from '../core.js'
import { interpret, RenderHistory, Step } from '../interpreter.js';
import { ControllerWrapper } from './wrapper.js';
import { getSceneIndex } from '../../vzome-worker-static.js';
import { renderedModelTransducer, resolveBuildPlanes } from '../scenes.js';
import { EditorController } from './editor.js';
import { PickingController } from './picking.js';
import { BuildPlaneController } from './buildplane.js';

const createControllers = ( design, renderingChanges, clientEvents ) =>
{
  const { orbitSource, renderedModel, toolsModel, bookmarkFactory, history, symmetrySystems, legacyField, editor, editContext } = design;

  const planes = resolveBuildPlanes( orbitSource .buildPlanes );
  const { orientations, symmetry, permutations } = orbitSource;
  const scalars = [ symmetry .getField() .getAffineScalar() .evaluate() ]; //TODO get them all!
  const resourcePath = orbitSource .getModelResourcePath();
  clientEvents .symmetryChanged( { orientations, permutations, scalars, planes, resourcePath } );

  const controller = new EditorController(design, clientEvents); // this is the equivalent of DocumentController
  controller.setErrorChannel({
    reportError: (message, args) => {
      console.log('controller error:', message, args);
      if (message === com.vzome.desktop.api.Controller.UNKNOWN_ERROR_CODE) {
        const ex = args[0];
        clientEvents.errorReported(ex.message);
      }
      else
        clientEvents.errorReported(message);
    },
  });

  // This has similar function to the Java equivalent, but a very different mechanism
  const pickingController = new PickingController(renderedModel);
  controller.addSubController('picking', pickingController);

  // This has no desktop equivalent
  const buildPlaneController = new BuildPlaneController(renderedModel, orbitSource);
  controller.addSubController('buildPlane', buildPlaneController);

  const polytopesController = new com.vzome.desktop.controller.PolytopesController( editor, editContext );
  controller.addSubController('polytopes', polytopesController);

  const undoRedoController = new com.vzome.desktop.controller.UndoRedoController(history);
  controller.addSubController('undoRedo', undoRedoController);

  const bookmarkController = new com.vzome.desktop.controller.ToolFactoryController(bookmarkFactory);
  controller.addSubController('bookmark', bookmarkController);

  const quaternionController = new com.vzome.desktop.controller.VectorController( legacyField .basisVector( 4, com.vzome.core.algebra.AlgebraicVector.W4 ) );
  controller .addSubController( "quaternion", quaternionController );

  const strutBuilder = new com.vzome.desktop.controller.StrutBuilderController( editContext, legacyField )
    .withGraphicalViews( true )   // TODO use preset
    .withShowStrutScales( true ); // TODO use preset
  controller.addSubController('strutBuilder', strutBuilder);
  strutBuilder .setMainScene( renderingChanges );

  for (const [name, symmetrySystem] of Object.entries(symmetrySystems)) {
    const symmController = new com.vzome.desktop.controller.SymmetryController( strutBuilder, symmetrySystem, renderedModel );
    strutBuilder.addSubController(`symmetry.${name}`, symmController);
  }

  controller .setSymmetrySystem( null ); // gets the symmetry name from the design

  const toolsController = new com.vzome.desktop.controller.ToolsController(toolsModel);
  toolsController.addTool(toolsModel.get("bookmark.builtin/ball at origin"));
  strutBuilder.addSubController('tools', toolsController);

  // enable shape changes
  renderedModel .addListener( {
    shapesChanged: () => false, // this allows RenderedModel.setShapes() to not fail and re-render all the parts
    manifestationAdded: () => {},  // We don't need these incremental changes, since we'll batch render after
    manifestationRemoved: () => {},
    colorChanged: () => {},
    glowChanged: () => {},
  } );


  const wrapper = new ControllerWrapper( '', '', controller, clientEvents );

  // hacky, for trackball model
  wrapper.getTrackballUrl = () => {
    const symm = controller .getProperty( 'symmetry' );
    const symmController = strutBuilder .getSubController( `symmetry.${symm}` );
    return symmController .getProperty( 'modelResourcePath' );
  }

  // hacky, for preview strut
  wrapper.startPreviewStrut = ( ballId, direction ) => {
    const [ x, y, z ] = direction;
    const rm = renderedModel.getRenderedManifestation( ballId );
    const point = rm ?.getManifestation() ?.toConstruction();
    if ( point ) {
      strutBuilder .startRendering( point, new com.vzome.core.math.RealVector( x, y, z ) );
    } else
      throw new Error( `No ball for ID ${ballId}` );
  }
  wrapper.movePreviewStrut = ( direction ) => {
    const [ x, y, z ] = direction;
    strutBuilder .previewStrut .zoneBall .setVector( new com.vzome.core.math.RealVector( x, y, z ) );
  }
  wrapper.endPreviewStrut = () =>
  {
    strutBuilder .previewStrut .finishPreview();
    wrapper .renderScene();
  }

  return wrapper;
};

export const loadDesign = ( xml, debug, clientEvents, sceneTitle ) =>
{
  const design = parse( xml );
      
  const { camera, lighting, xmlTree, targetEditId, field, scenes } = design;
  if ( field.unknown ) {
    throw new Error( `Field "${field.name}" is not supported.` );
  }
  clientEvents .xmlParsed( xmlTree );
  clientEvents .scenesDiscovered( scenes );

  // the next step may take several seconds, which is why we already reported PARSE_COMPLETED
  const renderHistory = new RenderHistory( design );
  const renderingChanges = renderedModelTransducer( renderHistory.getShapes(), clientEvents );

  if ( ! debug ) {
    interpret( Step.DONE, renderHistory, [] );
  } // else in debug mode, we'll interpret incrementally

  // TODO: define a better contract for before/after.
  //  Here we are using before=false with targetEditId, which is meant to be the *next*
  //  edit to be executed, so this really should be before=true.
  //  However, the semantics of the HistoryInspector UI require the edit field to contain the "after" edit ID.
  //  Thus, we are too tightly coupled to the UI here!
  //  See also the 'EDIT_SELECTED' case in onmessage(), below.
  let before = false;
  let sceneEditId = targetEditId;
  let sceneCamera = camera;
  if ( debug ) {
    sceneEditId = '--START--';
  }
  else if ( sceneTitle ) {
    const targetScene = getSceneIndex( sceneTitle, scenes );
    if ( targetScene > 0 ) {
      sceneEditId = scenes[ targetScene ] .nodeId;
      sceneCamera = scenes[ targetScene ] .camera;
      before = true;
    }
  }

  const { shapes, edit } = renderHistory .getScene( sceneEditId, before );
  const embedding = design .orbitSource .getEmbedding();
  const scene = { lighting, camera: sceneCamera, embedding, shapes };
  clientEvents .sceneChanged( scene, edit );

  const wrapper = createControllers( design, renderingChanges, clientEvents );

  // Not beautiful, but functional
  wrapper.getScene = (editId, before = false) => {
    return renderHistory.getScene(editId, before);
  };

  // TODO: fix this terrible hack!
  wrapper.renderScene = () => renderHistory.recordSnapshot('--END--', '--END--', []);

  return wrapper;
}

export const newDesign = ( fieldName, clientEvents ) =>
{
  const design = documentFactory( fieldName );

  const renderHistory = new RenderHistory( design );
  const renderingChanges = renderedModelTransducer( renderHistory.getShapes(), clientEvents );

  const { shapes, edit } = renderHistory .getScene( '--START--', false );
  const embedding = design .orbitSource .getEmbedding();
  clientEvents .sceneChanged( { embedding, shapes }, edit ); // let client determine default lighting and camera

  const wrapper = createControllers( design, renderingChanges, clientEvents );

  // Not beautiful, but functional
  wrapper.getScene = (editId, before = false) => {
    return renderHistory.getScene(editId, before);
  };

  // TODO: fix this terrible hack!
  wrapper.renderScene = () => renderHistory.recordSnapshot('--END--', '--END--', []);

  return wrapper;
}


