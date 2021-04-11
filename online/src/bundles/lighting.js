
const LIGHTS_DEFINED = 'LIGHTS_DEFINED'

const initialState = {
  backgroundColor: '#BBDAED',
  ambientColor: '#292929',
  directionalLights: [ // These are the vZome defaults, for consistency
    { direction: [ 1, -1, -1 ], color: '#EBEBE4' },
    { direction: [ -1, 0, 0 ], color: '#E4E4EB' },
    { direction: [ 0, 0, -1 ], color: '#1E1E1E' }
  ]
}

const mapLightDir = ( { color, direction } ) => ({
  color,
  direction: [ ...Object.values( direction ) ] // from {x,y,z} to [x,y,z]
})

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case LIGHTS_DEFINED:
      return {
        ...state,
        ...action.payload,
        directionalLights: action.payload.directionalLights.map( mapLightDir )
      }

    default:
      return state
  }
}
