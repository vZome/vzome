const initialPoints = [
  [0,0,0], [0,1,0], [1,0,-1]
]

const doAddPoint = point => ({dispatch}) => {
  dispatch( { type: 'POINT_ADDED', payload: point } )
}

const reducer = ( state=initialPoints, action ) => {
  if ( action.type === 'POINT_ADDED' ) {
    state = [ ...state, action.payload ]
  }
  return state
}

export default {
  name: 'points',
  reducer,
  doAddPoint,
  selectPoints: state => state.points
}
