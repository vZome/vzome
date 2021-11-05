
import * as mesh from '../bundles/mesh.js'
import * as designs from '../bundles/designs.js'
import { createInstance } from '../../wc/legacy/adapter.js'

export default ( start, end ) => ( dispatch, getState ) =>
{
  let { shown, hidden, selected } = designs.selectMesh( getState() )
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )

  let newBall = createInstance( [ end ] ) // canonically, all mesh objects are arrays of vectors

  // Avoid creating a duplicate... make this reusable
  newBall = shown.get( newBall.id ) || selected.get( newBall.id ) || hidden.get( newBall.id ) || newBall
  selected.delete( newBall.id ) || hidden.delete( newBall.id )
  shown.set( newBall.id, newBall )

  if ( start ) {
    let newStrut = createInstance( [ start, end ] )
    newStrut = shown.get( newStrut.id ) || selected.get( newStrut.id ) || hidden.get( newStrut.id ) || newStrut
    selected.delete( newStrut.id ) || hidden.delete( newStrut.id )
    shown.set( newStrut.id, newStrut )
  }

  dispatch( mesh.meshChanged( shown, selected, hidden ) )
}