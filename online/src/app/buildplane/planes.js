
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

export const doSelectPlane = ( orbit, orientation, hingeZone ) =>
{
  return { type: 'PLANE_SELECTED', payload: { orbit, orientation, hingeZone } }
}

export const doSelectHinge = ( orbit, orientation ) =>
{
  return { type: 'HINGE_SELECTED', payload: { orbit, orientation } }
}

export const initialState = {
  preview: undefined,
  diskZone: {
    orbit: 'blue',
    orientation: 11,
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
      return { ...state, preview: action.payload }
        
      case 'LAST_BALL_CREATED': // This one is actually from the worker
      case 'CENTER_SET':
      const { id, position } = action.payload
      return { ...state, enabled: true, buildingStruts: true, center: { id, position }, preview: undefined }
    
    case 'PLANE_TOGGLED':
      if ( state.center ?.id )
        return { ...state, enabled: !state.enabled, buildingStruts: !state.enabled };
      else
        return state;
    
    case 'BUILD_TOGGLED':
      return { ...state, enabled: true, buildingStruts: !state.buildingStruts, preview: undefined };

    case 'PLANE_SELECTED':
      return { ...state, enabled: true, diskZone: action.payload };
    
    case 'HINGE_SELECTED':
      return { ...state, enabled: true, hingeZone: action.payload };

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    // The rest of the "actions" are events from the worker
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    case 'FETCH_STARTED': {
      return { ...state, center: undefined, buildPlanes: undefined };
    }

    case 'SYMMETRY_CHANGED': {
      const buildPlanes = action.payload;
      const diskZone = {};
      diskZone .orbit = Object.keys( buildPlanes .planes )[ 0 ]; // TODO: use the camera look-at to find a plane
      const plane = buildPlanes .planes[ diskZone .orbit ];
      diskZone .orientation = 2;
      const { name, orientation } = plane .zones[ 0 ];
      const hingeZone = { orbit: name, orientation };
      return { ...state, buildPlanes, diskZone, hingeZone }
    }

    default:
      return state
  }
}
