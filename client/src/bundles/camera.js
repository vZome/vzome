

const CAMERA_DEFINED = 'CAMERA_DEFINED'
const MODEL_LOADED = 'MODEL_LOADED'

const aspectRatio = window.innerWidth / window.innerHeight
const convertFOV = (fovX) => ( fovX / aspectRatio ) * 180 / Math.PI  // converting radians to degrees

const vZomeInitialState = {  // These values match the camera in the default vZomeLogo model file
  fov: convertFOV( 0.442 ),
  position: [ -23.6819, 12.3843, -46.8956 ],
  lookAt: [ 0, -3.4270, 5.5450 ],
  up: [ -0.8263, 0.3136, 0.4677 ],
  far: 119.34,
  near: 0.1491,
  next: undefined
}

const initialState = {  // These values match the camera in the default vZomeLogo model file
  fov: convertFOV( 0.442 ),
  position: [ 0, 0, 75 ],
  lookAt: [ 0, 0, 0 ],
  up: [ 0, 1, 0 ],
  far: 217.46,
  near: 0.271,
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

export const parseViewXml = ( viewingElement, getChildElement ) => dispatch =>
{
  const parseVector = ( element, name ) =>
  {
    const child = getChildElement( element, name )
    const x = parseFloat( child.getAttribute( "x" ) )
    const y = parseFloat( child.getAttribute( "y" ) )
    const z = parseFloat( child.getAttribute( "z" ) )
    return { x, y, z }
  }
  const viewModel = getChildElement( viewingElement, "ViewModel" )
  const distance = parseFloat( viewModel.getAttribute( "distance" ) )
  const nearClipDistance = parseFloat( viewModel.getAttribute( "near" ) )
  const farClipDistance = parseFloat( viewModel.getAttribute( "far" ) )
  const lookAtPoint = parseVector( viewModel, "LookAtPoint" )
  const upDirection = parseVector( viewModel, "UpDirection" )
  const lookDirection = parseVector( viewModel, "LookDirection" )
  const x = lookAtPoint.x - distance * lookDirection.x
  const y = lookAtPoint.y - distance * lookDirection.y
  const z = lookAtPoint.z - distance * lookDirection.z
  const position = { x, y, z }
  const payload = {
    position, lookAtPoint, upDirection, fieldOfView: 0.442, nearClipDistance, farClipDistance
  }
  dispatch( { type: CAMERA_DEFINED, payload } )
  dispatch( { type: MODEL_LOADED } )
}