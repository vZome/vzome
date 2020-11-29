
import * as mesh from '../bundles/mesh'

export default ( start, end ) => ( dispatch, getState ) =>
{
  let { shown, selected, hidden } = getState().mesh
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )

  let newBall = mesh.createInstance( [ end ] ) // canonically, all mesh objects are arrays of vectors

  // Avoid creating a duplicate... make this reusable
  newBall = shown.get( newBall.id ) || selected.get( newBall.id ) || hidden.get( newBall.id ) || newBall
  selected.delete( newBall.id ) || hidden.delete( newBall.id )
  shown.set( newBall.id, newBall )

  if ( start ) {
    let newStrut = mesh.createInstance( [ start, end ] )
    newStrut = shown.get( newStrut.id ) || selected.get( newStrut.id ) || hidden.get( newStrut.id ) || newStrut
    selected.delete( newStrut.id ) || hidden.delete( newStrut.id )
    shown.set( newStrut.id, newStrut )
  }

  dispatch( mesh.meshChanged( shown, selected, hidden ) )
}