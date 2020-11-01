
import centroid from './centroid'
import shortred from './shortred'
import buildstrut from './buildstrut'
import * as mesh from '../bundles/mesh'

export const init = ( window, store ) =>
{
  store.dispatch( mesh.commandsDefined( { centroid, shortred, buildstrut } ) )
}
