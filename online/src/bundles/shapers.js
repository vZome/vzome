
import { getDefaultShaper } from '@vzome/react-vzome'


export const shaperDefined = ( name, shapeRenderer ) => ({ type: 'SHAPER_DEFINED', payload: { name, shapeRenderer } } )

export const reducer = ( state = {}, action ) =>
{
  switch ( action.type ) {

    case 'SHAPER_DEFINED': {
      const { name, shapeRenderer } = action.payload
      return {
        ...state,
        [ name ]: shapeRenderer
      }
    }

    default:
      return state
  }
}

export const init = async ( window, store ) =>
{
  const shapeRenderer = await getDefaultShaper()
  store.dispatch( shaperDefined( shapeRenderer.name, shapeRenderer ) )
}

