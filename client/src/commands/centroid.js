
import * as mesh from '../bundles/mesh'
import * as designs from '../bundles/models'

export default () => ( dispatch, getState ) =>
{
  let { shown, hidden, selected } = designs.selectCurrentMesh( getState() )
  const field = designs.selectCurrentField( getState() )
  shown = new Map( shown )

  const scale = field.createRational( 1, selected.size )
  let sum = undefined
  for ( let [id, instance] of selected ) {
    shown.set( id, instance )
    if ( sum ) {
      sum = field.vectoradd( sum, instance.vectors[0] )
    }
    else {
      sum = instance.vectors[0]
    }
  }
  const vectors = [ field.scalarmul( scale, sum ) ] // canonically, all mesh objects are arrays of vectors
  let newBall = mesh.createInstance( vectors )
  const { id } = newBall

  // Avoid creating a duplicate... make this reusable
  newBall = shown.get( id ) || selected.get( id ) || hidden.get( id ) || newBall
  shown.delete( id ) || selected.delete( id ) || hidden.delete( id )

  selected = new Map().set( newBall.id, newBall )
  dispatch( mesh.meshChanged( shown, selected, hidden ) )
}