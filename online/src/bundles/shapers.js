
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
