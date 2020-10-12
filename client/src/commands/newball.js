
import * as mesh from '../bundles/mesh'

export default ( { end } ) => ( dispatch, getState ) =>
{
  let { shown, selected, hidden, resolver } = getState().mesh
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )

  const news = []

  let newBall = mesh.createInstance( [ end ] ) // canonically, all mesh objects are arrays of vectors
  // let newStrut = mesh.createInstance( [ start, end ] )

  // Avoid creating a duplicate... make this reusable
  newBall = shown.get( newBall.id ) || selected.get( newBall.id ) || hidden.get( newBall.id ) || newBall
  selected.delete( newBall.id ) || hidden.delete( newBall.id )
  shown.set( newBall.id, newBall )
  news.push( newBall )

  // newStrut = shown.get( newStrut.id ) || selected.get( newStrut.id ) || hidden.get( newStrut.id ) || newStrut
  // shown.delete( newStrut.id ) || selected.delete( newStrut.id ) || hidden.delete( newStrut.id )
  // news.push( newStrut )

  dispatch( mesh.meshChanged( shown, selected, hidden ) )
  dispatch( resolver.resolve( news ) )
}