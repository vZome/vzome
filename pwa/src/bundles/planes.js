

const doMoveWorkingPlane = (position) => ({dispatch}) => {
  dispatch( { type: 'WORKING_PLANE_MOVED', payload: position } )
}

const doToggleWorkingPlane = () => ({dispatch}) => {
  dispatch( { type: 'WORKING_PLANE_TOGGLED' } )
}

const doToggleStrutMode = () => ({dispatch}) => {
  dispatch( { type: 'STRUT_MODE_TOGGLED' } )
}

const doChangeOrientation = () => ({dispatch}) => {
  dispatch( { type: 'ORIENTATION_CHANGED' } )
}

const samePosition = (a1,a2) => (JSON.stringify(a1)===JSON.stringify(a2))

const initialState = { position:[0,0,0], orientation:0, enabled: true, buildingStruts: true }

const reducer = ( state=initialState, action ) => {
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

export default {
  name: 'workingPlane',
  reducer,
  doMoveWorkingPlane,
  doToggleWorkingPlane,
  doToggleStrutMode,
  doChangeOrientation,
  selectWorkingPlane: state => state.workingPlane
}
