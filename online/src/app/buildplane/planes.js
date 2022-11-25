
export const doGridHover = ( position, value ) =>
{
  return value? { type: 'GRID_HOVER_STARTED', payload: position } : { type: 'GRID_HOVER_STOPPED', payload: position };
}

export const doBallHover = ( position, value ) =>
{
  return value? { type: 'BALL_HOVER_STARTED', payload: position } : { type: 'BALL_HOVER_STOPPED', payload: position };
}

export const doBallClick = ( id, position ) =>
{
  return { type: 'BALL_CLICKED', payload: { id, position } };
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
  enabled: false,
  buildingStruts: true,
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
      const { id, position } = action.payload
      if ( state.focusId === id )
        return { ...state, enabled: true, buildingStruts: !state.buildingStruts, focusId: id, endPt: undefined }
      else
        return { ...state, enabled: true, buildingStruts: true, focusId: id, position, endPt: undefined }
    
    // not generated yet, so untested
    case 'BACKGROUND_CLICKED':
      return { ...state, enabled: !state.enabled }
    
    case 'ORIENTATION_CHANGED':
      return { ...state, enabled: true, orientation: (state.orientation+1)%3 }

    default:
      return state
  }
}
