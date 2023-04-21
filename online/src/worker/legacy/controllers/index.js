
import { documentFactory, parse } from '../core.js'
import { resolveBuildPlanes } from '../scenes.js';
import { interpret, RenderHistory, Step } from '../interpreter.js';
import { createControllers } from './wrapper.js';

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

  // TODO reuse this code here and in loadDesign
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


