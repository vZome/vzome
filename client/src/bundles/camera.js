

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
      const { position, lookAtPoint, upDirection, nearClipDistance, farClipDistance } = action.payload
      return {
        ...state,
        position: [ ...Object.values( position ) ],
        lookAt: [ ...Object.values( lookAtPoint ) ],
        up: [ ...Object.values( upDirection ) ],
        near: nearClipDistance,
        far: farClipDistance
      }

    default:
      return state
  }
}
