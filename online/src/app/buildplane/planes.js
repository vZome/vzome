
export const doStrutPreview= ( position ) =>
{
  return { type: 'STRUT_PREVIEW_TOGGLED', payload: position };
}

export const doSetCenter = ( id, position ) =>
{
  return { type: 'CENTER_SET', payload: { id, position } };
}

export const doToggleBuild = () =>
{
  return { type: 'BUILD_TOGGLED' }
}
export const doToggleDisk = () =>
{
  return { type: 'PLANE_TOGGLED' }
}

export const doSelectPlane = ( orbit, orientation ) =>
{
  return { type: 'PLANE_SELECTED', payload: { orbit, orientation } }
}

export const doSelectHinge = ( orbit, orientation ) =>
{
  return { type: 'HINGE_SELECTED', payload: { orbit, orientation } }
}

export const initialState = {
  center: {},
  endPt: undefined,
  diskZone: {
    orbit: 'blue',
    orientation: 2,
  },
  hingeZone: {
    orbit: 'blue',
    orientation: 0,
  },
  enabled: false,
  buildingStruts: false,
}

export const reducer = ( state=initialState, action ) =>
{
  switch ( action.type )
  {
    case 'STRUT_PREVIEW_TOGGLED':
      return { ...state, endPt: action.payload }
        
    case 'CENTER_SET':
      const { id, position } = action.payload
      return { ...state, enabled: true, buildingStruts: true, center: { id, position }, endPt: undefined }
    
    case 'PLANE_TOGGLED':
      if ( state.center.id )
        return { ...state, enabled: !state.enabled, buildingStruts: !state.enabled };
      else
        return state;
    
    case 'BUILD_TOGGLED':
      return { ...state, enabled: true, buildingStruts: !state.buildingStruts, endPt: undefined };
    
    case 'PLANE_SELECTED':
      return { ...state, enabled: true, diskZone: action.payload };
    
    case 'HINGE_SELECTED':
      return { ...state, enabled: true, hingeZone: action.payload };

    default:
      return state
  }
}
