
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
  grid,
  color: "#00aacc",
  enabled: true,
  buildingStruts: false,
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
