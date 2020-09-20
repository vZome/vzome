
import * as mesh from '../bundles/mesh'

export default () => ( dispatch, getState ) =>
{
  let { field, shown, selected, hidden, resolver } = getState().mesh
  shown = new Map( shown )

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
  const { id } = newStrut
  newStrut = shown.get( id ) || selected.get( id ) || hidden.get( id ) || newStrut
  shown.delete( id ) || selected.delete( id ) || hidden.delete( id )
  selected = new Map().set( newStrut.id, newStrut )
  dispatch( mesh.meshChanged( shown, selected, hidden ) )
  dispatch( resolver.resolve( [ newStrut ] ) )
}