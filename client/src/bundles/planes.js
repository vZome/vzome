
import { field as goldenField } from '../fields/golden'
import buildStrut from '../commands/buildstrut'

export const doStartGridHover = position =>
{
  return { type: 'GRID_HOVER_STARTED', payload: position }
}

export const doStopGridHover = position =>
{
  return { type: 'GRID_HOVER_STOPPED', payload: position }
}

export const doBallClick = ( focus, position ) => ( dispatch, getState ) =>
{
  dispatch( { type: 'BALL_CLICKED', payload: position } )
  dispatch( buildStrut( focus, position ) )
}

export const doBackgroundClick = () =>
{
  return { type: 'BACKGROUND_CLICKED' }
}

export const doSetWorkingPlaneGrid = grid => {
  return { type: 'WORKING_PLANE_GRID_DEFINED', payload: grid }
}

export const doChangeOrientation = () => {
  return { type: 'ORIENTATION_CHANGED' }
}

const series = goldenField.goldenSeries( 6 ).map( n => goldenField.times( goldenField.goldenRatio, n ) )

const zero = goldenField.zero
const grid = [ [ zero, zero ] ]

series.forEach( x => {
  const nx = goldenField.negate( x )
  grid.push( [ x, zero ] )
  grid.push( [ zero, x ] )
  grid.push( [ nx, zero ] )
  grid.push( [ zero, nx ] )
  series.forEach( y => {
    const ny = goldenField.negate( y )
    grid.push( [ x, y ] )
    grid.push( [ x, ny ] )
    grid.push( [ nx, y ] )
    grid.push( [ nx, ny ] )
  });  
});

const initialState = {
  position: [ goldenField.zero, goldenField.zero, goldenField.zero ],
  endPt: undefined,
  quaternion: goldenField.quaternions[ 0 ],
  size: goldenField.times( [5,0,1], goldenField.goldenRatio ),
  grid: [],
  color: "#00aacc",
  enabled: false,
  buildingStruts: false,
  field: goldenField
}

export const reducer = ( state=initialState, action ) =>
{
  switch ( action.type )
  {
    case 'GRID_HOVER_STARTED':
      state = { ...state, endPt: action.payload }
      break;
  
    case 'GRID_HOVER_STOPPED':
      state = { ...state, endPt: undefined }
      break;
        
    case 'BALL_CLICKED':
      const target = action.payload
      if ( JSON.stringify( state.position ) === JSON.stringify( target ) )
        state = { ...state, enabled: true, buildingStruts: !state.buildingStruts, endPt: undefined }
      else
        state = { ...state, enabled: true, buildingStruts: true, position: target, endPt: undefined }
      break;
    
    // not generated yet, so untested
    case 'BACKGROUND_CLICKED':
      state = { ...state, enabled: !state.enabled }
      break;
    
    case 'WORKING_PLANE_GRID_DEFINED':
      state = { ...state, enabled: true, buildingStruts: true, grid: action.payload }
      break;

    case 'ORIENTATION_CHANGED':
      state = { ...state, enabled: true, orientation: (state.orientation+1)%3 }
      break;

    default:
      break;
  }
  return state
}
