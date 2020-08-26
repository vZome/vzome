
import { MODEL_LOADED} from "./vzomejava"

const CAMERA_DEFINED = 'CAMERA_DEFINED'

const aspectRatio = window.innerWidth / window.innerHeight
const convertFOV = (fovX) => ( fovX / aspectRatio ) * 180 / Math.PI  // converting radians to degrees

const initialState = {  // These values match the camera in the default vZomeLogo model file
  fov: convertFOV( 0.442 ),
  position: [ -23.6819, 12.3843, -46.8956 ],
  lookAt: [ 0, -3.4270, 5.5450 ],
  up: [ -0.8263, 0.3136, 0.4677 ],
  far: 119.34,
  near: 0.1491,
  next: undefined
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case CAMERA_DEFINED:
      const { position, lookAtPoint, upDirection, fieldOfView, nearClipDistance, farClipDistance } = action.payload
      return {
        ...state,
        next: {
          position: [ ...Object.values( position ) ],
          lookAt: [ ...Object.values( lookAtPoint ) ],
          up: [ ...Object.values( upDirection ) ],
          fov: convertFOV( fieldOfView ),
          near: nearClipDistance,
          far: farClipDistance
        }
      }

    case MODEL_LOADED:
      return {
        ...state.next,
        next: undefined
      }


    default:
      return state
  }
}
