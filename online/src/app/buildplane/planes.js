
export const doStrutPreview= ( position ) =>
{
  return position? { type: 'STRUT_PREVIEW_STARTED', payload: position } : { type: 'STRUT_PREVIEW_STOPPED' };
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
  center: {},
  endPt: undefined,
  quaternion: [1,0,0,0],
  plane: 'blue',
  enabled: false,
  buildingStruts: false,
}

export const reducer = ( state=initialState, action ) =>
{
  switch ( action.type )
  {
    case 'STRUT_PREVIEW_STARTED':
      return { ...state, endPt: action.payload }
          
    case 'STRUT_PREVIEW_STOPPED':
      return { ...state, endPt: undefined }
          
    case 'BALL_HOVER_STARTED':
      if ( state.endPt )
        return state
      else
        return { ...state, endPt: action.payload }
  
    case 'BALL_HOVER_STOPPED':
      return { ...state, endPt: undefined }
        
    case 'BALL_CLICKED':
      const { id, position } = action.payload
      if ( state.center.id === id )
        return { ...state, enabled: true, buildingStruts: !state.buildingStruts, endPt: undefined }
      else
        return { ...state, enabled: true, buildingStruts: true, center: { id, position }, endPt: undefined }
    
    case 'BACKGROUND_CLICKED':
      return { ...state, enabled: !state.enabled, buildingStruts: !state.enabled }
    
    case 'ORIENTATION_CHANGED':
      return { ...state, enabled: true, orientation: (state.orientation+1)%3 }

    default:
      return state
  }
}
