
const CAMERA_DEFINED = 'CAMERA_DEFINED'

const initialState = {
  fov: 25,
  position: [ -23.6819, 12.3843, -46.8956 ],
  lookAt: [ 0, -3.4270, 5.5450 ],
  up: [ -0.8263, 0.3136, 0.4677 ]
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case CAMERA_DEFINED:
      return {
        ...state,
        position: [ ...Object.values( action.payload.position ) ],
        lookAt: [ ...Object.values( action.payload.lookAtPoint ) ],
        up: [ ...Object.values( action.payload.upDirection ) ],
        fov: action.payload.fieldOfView * 180 / Math.PI
      }

    default:
      return state
  }
}
