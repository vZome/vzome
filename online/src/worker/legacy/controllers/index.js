
import { com } from '../core-java.js';
import { documentFactory, parse } from '../core.js'
import { EditCursor, interpret, RenderHistory, Step } from '../interpreter.js';
import { ControllerWrapper } from './wrapper.js';
import { getSceneIndex } from '../../vzome-worker-static.js';
import { renderedModelTransducer, resolveBuildPlanes } from '../scenes.js';
import { EditorController } from './editor.js';

const createControllers = ( design, renderingChanges, clientEvents ) =>
{
  const { getOrbitSource, renderedModel } = design;

  const orbitSource = getOrbitSource();
  const planes = resolveBuildPlanes( orbitSource .buildPlanes );
  const { orientations, symmetry, permutations } = orbitSource;
  const scalars = [ symmetry .getField() .getAffineScalar() .evaluate() ]; //TODO get them all!
  const resourcePath = orbitSource .getModelResourcePath();
  clientEvents .symmetryChanged( { orientations, permutations, scalars, planes, resourcePath } );

  const controller = new EditorController( design, clientEvents ); // this is the equivalent of DocumentController
  const symmLabel = controller .initialize();
  const strutBuilder = controller .getSubController( 'strutBuilder' );
  
  strutBuilder .setMainScene( renderingChanges );
  // preceding call seems required before the next call, or preview strut won't work
  controller .setSymmetryController( symmLabel );

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
    controller.firePropertyChange( 'edited', '', 'true' ); // value really doesn't matter
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

  const renderHistory = new RenderHistory( design );
  const renderingChanges = renderedModelTransducer( renderHistory.getShapes(), clientEvents );
  const editCursor = new EditCursor( design );

  if ( ! debug ) {
    // interpretation may take several seconds, which is why we already reported PARSE_COMPLETED
    interpret( Step.DONE, editCursor, renderHistory, [] );
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
  const embedding = design .getOrbitSource() .getEmbedding();
  const scene = { lighting, camera: sceneCamera, embedding, shapes };
  clientEvents .sceneChanged( scene, edit );

  const wrapper = createControllers( design, renderingChanges, clientEvents );

  // Not beautiful, but functional
  wrapper.getScene = (editId, before = false) => {
    const embedding = design .getOrbitSource() .getEmbedding();
    return { ...renderHistory.getScene(editId, before), embedding };
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
  const embedding = design .getOrbitSource() .getEmbedding();
  clientEvents .sceneChanged( { embedding, shapes }, edit ); // let client determine default lighting and camera

  const wrapper = createControllers( design, renderingChanges, clientEvents );

  // Not beautiful, but functional
  wrapper.getScene = (editId, before = false) => {
    const embedding = design .getOrbitSource() .getEmbedding();
    return { ...renderHistory.getScene(editId, before), embedding };
  };

  // TODO: fix this terrible hack!
  wrapper.renderScene = () => renderHistory.recordSnapshot('--END--', '--END--', []);

  return wrapper;
}


