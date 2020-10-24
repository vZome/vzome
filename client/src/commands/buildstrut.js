
import * as mesh from '../bundles/mesh'

export default ( { start, end, preview } ) => ( dispatch, getState ) =>
{
  let { shown, selected, hidden, resolver } = getState().mesh
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )
  const previewed = new Map()

  const news = []

  let newBall = mesh.createInstance( [ end ] ) // canonically, all mesh objects are arrays of vectors

  // Avoid creating a duplicate... make this reusable
  newBall = shown.get( newBall.id ) || selected.get( newBall.id ) || hidden.get( newBall.id ) || newBall
  if ( preview ) {
    previewed.set( newBall.id, newBall )
  } else {
    selected.delete( newBall.id ) || hidden.delete( newBall.id )
    shown.set( newBall.id, newBall )
  }
  news.push( newBall )

  if ( start ) {
    let newStrut = mesh.createInstance( [ start, end ] )
    newStrut = shown.get( newStrut.id ) || selected.get( newStrut.id ) || hidden.get( newStrut.id ) || newStrut
    if ( preview ) {
      previewed.set( newStrut.id, newStrut )
    } else {
      selected.delete( newStrut.id ) || hidden.delete( newStrut.id )
      shown.set( newStrut.id, newStrut )
    }
    news.push( newStrut )
  }

  if ( preview )
    dispatch( mesh.previewChanged( previewed ) )
  else
    dispatch( mesh.meshChanged( shown, selected, hidden ) )
  dispatch( resolver.resolve( news ) )
}