
import * as mesh from '../bundles/mesh'
import * as designs from '../bundles/designs'
import { createInstance } from 'react-vzome'

export default () => ( dispatch, getState ) =>
{
  let { shown, hidden, selected } = designs.selectMesh( getState() )
  const field = designs.selectField( getState() )
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
  let newBall = createInstance( [ end ] ) // canonically, all mesh objects are arrays of vectors
  let newStrut = createInstance( [ start, end ] )

  // Avoid creating a duplicate... make this reusable
  newBall = shown.get( newBall.id ) || selected.get( newBall.id ) || hidden.get( newBall.id ) || newBall
  shown.delete( newBall.id ) || selected.delete( newBall.id ) || hidden.delete( newBall.id )

  newStrut = shown.get( newStrut.id ) || selected.get( newStrut.id ) || hidden.get( newStrut.id ) || newStrut
  shown.delete( newStrut.id ) || selected.delete( newStrut.id ) || hidden.delete( newStrut.id )

  selected = new Map().set( newStrut.id, newStrut ).set( newBall.id, newBall )
  dispatch( mesh.meshChanged( shown, selected, hidden ) )
}