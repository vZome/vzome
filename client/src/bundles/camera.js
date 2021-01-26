

const CAMERA_DEFINED = 'CAMERA_DEFINED'

const aspectRatio = window.innerWidth / window.innerHeight
const convertFOV = (fovX) => ( fovX / aspectRatio ) * 180 / Math.PI  // converting radians to degrees

export const initialState = {
  fov: convertFOV( 0.75 ), // 0.44 in vZome
  position: [ 0, 0, 75 ],
  lookAt: [ 0, 0, 0 ],
  up: [ 0, 1, 0 ],
  far: 217.46,
  near: 0.271,
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case CAMERA_DEFINED:
      return {
        ...state,
        ...action.payload
      }

    default:
      return state
  }
}

export const cameraDefined = ( camera ) => ({ type: CAMERA_DEFINED, payload: camera })
