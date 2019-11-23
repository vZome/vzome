

const doToggleWorkingPlane = () => ({dispatch}) => {
  dispatch( { type: 'WORKING_PLANE_TOGGLED' } )
}

const reducer = ( state=false, action ) => {
  if ( action.type === 'WORKING_PLANE_TOGGLED' ) {
    state = ! state
  }
  return state
}

export default {
  name: 'workingPlane',
  reducer,
  doToggleWorkingPlane,
  selectWorkingPlaneEnabled: state => state.workingPlane
}
