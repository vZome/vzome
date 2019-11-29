
const ORIGIN = [0,0,0]
const INITIAL_POINTS = { [JSON.stringify(ORIGIN)]: ORIGIN }

const doAddPoint = (point) => ({dispatch}) => {
  dispatch( { type: 'POINT_ADDED', payload: point } )
}

const reducer = ( state=INITIAL_POINTS, action ) => {
  if ( action.type === 'POINT_ADDED' ) {
    const point = action.payload
    state = Object.assign( {}, state, { [JSON.stringify(point)]: point } )
  }
  return state
}

export default {
  name: 'points',
  reducer,
  doAddPoint,
  selectPoints: state => state.points
}
