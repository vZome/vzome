

export const shaperDefined = ( name, shaper ) => ({ type: 'SHAPER_DEFINED', payload: { name, shaper } } )

export const reducer = ( state = {}, action ) =>
{
  switch ( action.type ) {

    case 'SHAPER_DEFINED': {
      const { name, shaper } = action.payload
      return {
        ...state,
        [ name ]: shaper
      }
    }

    default:
      return state
  }
}