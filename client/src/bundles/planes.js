
import { field as goldenField } from '../fields/golden'

export const doMoveWorkingPlane = position => ( dispatch ) => {
  dispatch( { type: 'WORKING_PLANE_MOVED', payload: position } )
}

export const doToggleWorkingPlane = () => ( dispatch ) => {
  dispatch( { type: 'WORKING_PLANE_TOGGLED' } )
}

export const doToggleStrutMode = () => ( dispatch ) => {
  dispatch( { type: 'STRUT_MODE_TOGGLED' } )
}

export const doChangeOrientation = () => ( dispatch ) => {
  dispatch( { type: 'ORIENTATION_CHANGED' } )
}

const samePosition = (a1,a2) => (JSON.stringify(a1)===JSON.stringify(a2))

const size = goldenField .times( goldenField .goldenRatio, goldenField .goldenRatio )

const grid = []
for (const x of [-2,-1,0,1,2]) {
  for (const y of [-2,-1,0,1,2]) {
    const gx = goldenField.times( size, [ x, 0, 1 ] )
    const gy = goldenField.times( size, [ y, 0, 1 ] )
    grid.push( [ gx, gy ] )
  }
}

const initialState = {
  position: [ goldenField.zero, goldenField.zero, goldenField.zero ],
  quaternion: goldenField.quaternions[ 0 ],
  size: goldenField.times( [5,0,1], size ),
  grid,
  color: "#00aacc",
  enabled: true,
  buildingStruts: true,
  field: goldenField
}

export const reducer = ( state=initialState, action ) => {
  if ( action.type === 'WORKING_PLANE_TOGGLED' ) {
    state = Object.assign( {}, state, { enabled: !state.enabled } )
  }
  else if ( action.type === 'STRUT_MODE_TOGGLED' ) {
    state = Object.assign( {}, state, { buildingStruts: !state.buildingStruts } )
  }
  else if ( action.type === 'WORKING_PLANE_MOVED' ) {
    state = Object.assign( {}, state, {
      position: action.payload,
      buildingStruts: true,
      enabled: true
    })
  }
  else if ( action.type === 'ORIENTATION_CHANGED' ) {
    state = Object.assign( {}, state, {
      orientation: (state.orientation+1)%3,
      enabled: true
    })
  }
  return state
}
