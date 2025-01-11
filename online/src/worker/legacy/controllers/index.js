
import { com } from '../core-java.js';
import { documentFactory, parse } from '../core.js'
import { Interpreter, RenderHistory, Step } from '../interpreter.js';
import { ControllerWrapper } from './wrapper.js';
import { renderedModelTransducer, resolveBuildPlanes } from '../scenes.js';
import { EditorController } from './editor.js';
import { serializeVZomeXml } from '../serializer.js';

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
  wrapper.scalePreviewStrut = increment => {
    const lengthController = strutBuilder .previewStrut .getLengthController();
    lengthController .setScale( lengthController .getScale() + increment ); // will trigger side-effects
  }
  wrapper.endPreviewStrut = () =>
  {
    strutBuilder .previewStrut .finishPreview();
    wrapper .renderScene();
    controller.firePropertyChange( 'edited', '', 'true' ); // value really doesn't matter
  }

  return wrapper;
};

export const snapCamera = ( symmController, upArray, lookArray ) =>
{
  const snapper = symmController .getSnapper();
  let up = new com.vzome.core.math.RealVector( ...upArray );
  let look = new com.vzome.core.math.RealVector( ...lookArray );
  look = snapper .snapZ( look ) .normalize();
  up = snapper .snapY( look, up ) .normalize();
  const toArray = rv => {
    const { x, y, z } = rv;
    return [ x, y, z ];
  }
  return { up: toArray( up ), lookDir: toArray( look ) };
}

const initializeDesign = ( loading, polygons, legacyDesign, clientEvents ) =>
{
  const { lighting, camera, scenes, snapshotNodes } = legacyDesign;
  const renderHistory = new RenderHistory( legacyDesign, polygons );                          // used for all normal rendering
  const interpreter = new Interpreter( legacyDesign, renderHistory );
  const renderingChanges = renderedModelTransducer( renderHistory.getShapes(), clientEvents ); // used only for preview struts
  
  if ( loading ) {
    // interpretation may take several seconds, which is why we already reported PARSE_COMPLETED
    interpreter .interpret( Step.DONE );
  } // else in debug mode, we'll interpret incrementally

  // TODO: define a better contract for before/after.
  //  Here we are using before=false with targetEditId, which is meant to be the *next*
  //  edit to be executed, so this really should be before=true.
  //  However, the semantics of the HistoryInspector UI require the edit field to contain the "after" edit ID.
  //  Thus, we are too tightly coupled to the UI here!
  //  See also the 'EDIT_SELECTED' case in onmessage().

  const shapes = renderHistory .getShapes();
  const orbitSource = legacyDesign .getOrbitSource();
  const { orientations } = orbitSource;
  const embedding = orbitSource .getEmbedding();
  
  let instances;
  let snapshots;
  if ( loading ) {
    // TODO: get rid of this; snapshots should be captured as needed, during interpret() above
    snapshots = snapshotNodes .map( nodeId => renderHistory .getSnapshot( nodeId, true ) );

    instances = snapshots .pop();
  } else {
    instances = renderHistory .getSnapshot( '--START--' );
    snapshots = [];
  }

  const wrapper = createControllers( legacyDesign, renderingChanges, clientEvents );

  wrapper.snapCamera = snapCamera;

  wrapper.renderScene = () => {
    renderHistory .recordSnapshot( '--END--', '--END--', [] );
    // replace instances content with the new snapshot... don't change the ref!
    instances .splice( 0, Infinity, ...renderHistory.currentSnapshot );
  } // happens at end of every wrapper.doAction()

  wrapper.getScene = ( editId, before ) => renderHistory .getSnapshot( editId, before );

  wrapper.interpretToBreakpoint = ( editId, before ) => {
    interpreter .interpret( Step.DONE, editId );
    return before? '--END--' : interpreter .getLastEdit();
  }

  wrapper.serializeVZomeXml = ( camera, lighting ) => serializeVZomeXml( legacyDesign, camera, lighting, scenes );

  const rendered = { lighting, camera, embedding, orientations, polygons, shapes, instances, snapshots, scenes };
  return { wrapper, rendered };
}

export const newDesign = ( fieldName, clientEvents ) =>
  {
    const legacyDesign = documentFactory( fieldName );

    return initializeDesign( false, true, legacyDesign, clientEvents );
  }
  
export const loadDesign = ( xml, debug, polygons, clientEvents ) =>
{
  const legacyDesign = parse( xml );
  const { xmlTree, field } = legacyDesign;
  if ( field.unknown ) {
    throw new Error( `Field "${field.name}" is not supported.` );
  }
  clientEvents .xmlParsed( xmlTree );

  return initializeDesign( !debug, polygons, legacyDesign, clientEvents );
}
