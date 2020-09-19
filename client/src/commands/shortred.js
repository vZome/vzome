
import * as mesh from '../bundles/mesh'

export default ( state ) =>
{
  const { field, selected } = state
  const shown = new Map( state.shown )

  const red = [ [ 2, 3, 1 ], [ 1, 2, 1 ], [ 0, 0, 1 ] ]
  let start = undefined
  for ( let [id, instance] of selected ) {
    shown.set( id, instance )
    if ( ! start ) {
      start = instance.vectors[0]  // ball, strut, whatever
    }
  }
  // shown now has all the previously selected mesh objects
  const end = field.vectoradd( start, red )
  const vectors = [ start, end ] // canonically, all mesh objects are arrays of vectors
  let newStrut = mesh.createInstance( vectors )

  // Avoid creating a duplicate... make this reusable
  const existing = shown.get( newStrut.id )
  if ( existing ) {
    shown.delete( newStrut.id )
    newStrut = existing
  }
  else {
    // must resolve the orbit, rotation, and length
  }

  return {
    ...state,
    shown,
    selected : new Map().set( newStrut.id, newStrut )
  }
}