
const ORIGIN = [0,0,0]
const initialState = {
  points: {
    [JSON.stringify(ORIGIN)]: ORIGIN
  },
  segments: {}
}

const doAddPoint = (point) => ({dispatch}) => {
  dispatch( { type: 'POINT_ADDED', payload: point } )
}

const doAddSegment = (start,end) => ({dispatch}) => {
  dispatch( { type: 'SEGMENT_ADDED', payload: { start, end } } )
}

const reducer = ( state=initialState, action ) => {
  if ( action.type === 'POINT_ADDED' ) {
    const point = action.payload
    state = Object.assign( {}, state, {
      points: Object.assign( {}, state.points,
        { [JSON.stringify(point)]: point }
      )
    } )
  }
  if ( action.type === 'SEGMENT_ADDED' ) {
    const { start, end } = action.payload
    const offset = end.map( ( xi, i ) => xi - ( start[i] ) )
    const segment = { position: start, offset }
    state = Object.assign( {}, state, {
      points: Object.assign( {}, state.points,
        { [JSON.stringify(start)]: start, [JSON.stringify(end)]: end }
      ),
      segments: { ...state.segments, ...{ [JSON.stringify([start,end])]: segment } }
    } )
  }
  return state
}

export default {
  name: 'structure',
  reducer,
  doAddPoint,
  doAddSegment,
  selectStructure: state => state.structure
}
