
import { goldenField, getDefaultRenderer } from '@vzome/react-vzome'

export const rendererDefined = ( name, renderer ) => ({ type: 'RENDERER_DEFINED', payload: { name, renderer } } )

export const reducer = ( state = {}, action ) =>
{
  switch ( action.type ) {

    case 'RENDERER_DEFINED': {
      const { name, renderer } = action.payload
      return {
        ...state,
        [ name ]: renderer
      }
    }

    default:
      return state
  }
}

export const init = async ( window, store ) =>
{
  const renderer = await getDefaultRenderer( goldenField )
  store.dispatch( rendererDefined( renderer.name, renderer ) )
}
