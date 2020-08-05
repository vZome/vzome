
const CAMERA_DEFINED = 'CAMERA_DEFINED'

const initialState = {
  fov: 12,
  position: [ -23.6819, 12.3843, -46.8956 ],
  lookAt: [ 0, -3.4270, 5.5450 ],
  up: [ -0.8263, 0.3136, 0.4677 ],
  far: 119.34,
  near: 0.1491
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case CAMERA_DEFINED:
      const { position, lookAtPoint, upDirection, fieldOfView, nearClipDistance, farClipDistance } = action.payload
      // This is a rough guess at w/h, and good enough.  We need it because the value
      //   from vZome is the FOV in the horizontal direction.
      const aspectRatio = 2
      return {
        ...state,
        position: [ ...Object.values( position ) ],
        lookAt: [ ...Object.values( lookAtPoint ) ],
        up: [ ...Object.values( upDirection ) ],
        fov: ( fieldOfView / aspectRatio ) * 180 / Math.PI,  // converting radians to degrees
        near: nearClipDistance,
        far: farClipDistance
      }

    default:
      return state
  }
}
