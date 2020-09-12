
import { createInstance } from '../bundles/mesh'

export default ( state ) =>
{
  const { field, selected } = state
  const shown = new Map( state.shown )

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
  const newBall = createInstance( vectors )

  return {
    ...state,
    shown,
    selected : new Map().set( newBall.id, newBall )
  }
}