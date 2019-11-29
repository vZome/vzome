

const doMoveWorkingPlane = (position) => ({dispatch}) => {
  dispatch( { type: 'WORKING_PLANE_MOVED', payload: position } )
}

const reducer = ( state=null, action ) => {
  if ( action.type === 'WORKING_PLANE_MOVED' ) {
    state = action.payload
  }
  return state
}

export default {
  name: 'workingPlane',
  reducer,
  doMoveWorkingPlane,
  selectWorkingPlanePosition: state => state.workingPlane
}
