

const doMoveWorkingPlane = (position) => ({dispatch}) => {
  dispatch( { type: 'WORKING_PLANE_MOVED', payload: position } )
}

const samePosition = (a1,a2) => (JSON.stringify(a1)===JSON.stringify(a2))

const initialState = { position:[0,0,0], orientation:0 }

const reducer = ( state=initialState, action ) => {
  if ( action.type === 'WORKING_PLANE_MOVED' ) {
    state = samePosition(state.position, action.payload)?
      Object.assign( {}, state, {
        orientation: (state.orientation+1)%3
      })
    :
      Object.assign( {}, state, {
        position: action.payload
      })
  }
  return state
}

export default {
  name: 'workingPlane',
  reducer,
  doMoveWorkingPlane,
  selectWorkingPlane: state => state.workingPlane
}
