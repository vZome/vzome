

export const doStartGridHover = position =>
{
  return { type: 'GRID_HOVER_STARTED', payload: position }
}

export const doStopGridHover = position =>
{
  return { type: 'GRID_HOVER_STOPPED', payload: position }
}

export const doStartBallHover = position =>
{
  return { type: 'BALL_HOVER_STARTED', payload: position }
}

export const doStopBallHover = position =>
{
  return { type: 'BALL_HOVER_STOPPED', payload: position }
}

export const doBallClick = ( focus, position ) => ( dispatch, getState ) =>
{
  dispatch( { type: 'BALL_CLICKED', payload: position } )
  // dispatch( buildStrut( focus, position ) )
}

export const doBackgroundClick = () =>
{
  return { type: 'BACKGROUND_CLICKED' }
}

export const doChangeOrientation = () => {
  return { type: 'ORIENTATION_CHANGED' }
}

export const initialState = {
  position: [ 0,0,0 ],
  endPt: undefined,
  quaternion: [1,0,0,0],
  plane: 'blue',
  enabled: true,
  buildingStruts: false,
}

export const reducer = ( state=initialState, action ) =>
{
  switch ( action.type )
  {
    case 'GRID_HOVER_STARTED':
      return { ...state, endPt: action.payload }
          
    case 'BALL_HOVER_STARTED':
      if ( state.endPt )
        return state
      else
        return { ...state, endPt: action.payload }
  
    case 'BALL_HOVER_STOPPED':
      return { ...state, endPt: undefined }
        
    case 'BALL_CLICKED':
      const target = action.payload
      if ( JSON.stringify( state.position ) === JSON.stringify( target ) )
        return { ...state, enabled: true, buildingStruts: !state.buildingStruts, endPt: undefined }
      else
        return { ...state, enabled: true, buildingStruts: true, position: target, endPt: undefined }
    
    // not generated yet, so untested
    case 'BACKGROUND_CLICKED':
      return { ...state, enabled: !state.enabled }
    
    case 'ORIENTATION_CHANGED':
      return { ...state, enabled: true, orientation: (state.orientation+1)%3 }

    default:
      return state
  }
}
