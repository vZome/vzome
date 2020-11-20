
import { field as goldenField } from '../fields/golden'
import * as mouseEvents from './mouseevents'

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
    case mouseEvents.GRID_HOVER_STARTED:
      
      break;
  
    case mouseEvents.GRID_HOVER_STOPPED:
    
      break;
    
    case mouseEvents.GRID_CLICKED:
      state = { ...state, buildingStruts: true, position: action.payload }
      break;
    
    case mouseEvents.SHAPE_CLICKED:
      const vectors = action.payload
      if ( vectors.length === 1 ) {
        // Ignore clicks on struts and panels
        state = { ...state, enabled: true, buildingStruts: true, position: vectors[ 0 ] }
      }
      break;
    
    case mouseEvents.BACKGROUND_CLICKED:
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
