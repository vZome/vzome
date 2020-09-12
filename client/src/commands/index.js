
import centroid from './centroid'
import * as mesh from '../bundles/mesh'

export const init = ( window, store ) =>
{
  store.dispatch( mesh.commandsDefined( { centroid } ) )
}
