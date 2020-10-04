
import centroid from './centroid'
import shortred from './shortred'
import * as mesh from '../bundles/mesh'

export const init = ( window, store ) =>
{
  store.dispatch( mesh.commandsDefined( { centroid, shortred } ) )
}
