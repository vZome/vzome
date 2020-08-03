
const LIGHTS_DEFINED = 'LIGHTS_DEFINED'

const initialState = {
  backgroundColor: '#99ccff',
  ambientColor: '#888888'
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case LIGHTS_DEFINED:
      return {
        ...state,
        ...action.payload
      }

    default:
      return state
  }
}
