
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
      if ( state.center ?.id )
        return { ...state, enabled: !state.enabled, buildingStruts: !state.enabled };
      else
        return state;
    
    case 'BUILD_TOGGLED':
      return { ...state, enabled: true, buildingStruts: !state.buildingStruts, endPt: undefined };

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

    case 'PLANES_DEFINED': {
      const buildPlanes = action.payload;
      const diskZone = {};
      diskZone .orbit = Object.keys( buildPlanes .planes )[ 0 ]; // TODO: use the camera look-at to find a plane
      const plane = buildPlanes .planes[ diskZone .orbit ];
      diskZone .orientation = plane .orientation;
      const { name, orientation } = plane .zones[ 0 ];
      const hingeZone = { orbit: name, orientation };
      return { ...state, buildPlanes, diskZone, hingeZone }
    }

    case 'SCENE_RENDERED': {
      const { scene } = action.payload;
      let center = state.center;
      if ( ! center && Object.keys( scene.shapes ) .length === 1 ) // This is a one-shot deal, designed to give us a plane center for a new design
        for ( const shapeId in scene.shapes ) {
          const shape = scene.shapes[ shapeId ];
          if ( shape .instances .length === 1 ) {
            const instance = shape .instances[ 0 ];
            if ( instance ?.type === 'ball' ) {
              center = instance;
              // TODO: create a more first-class contract for this event
              break;
            }
          }
        }
      return { ...state, enabled: true, buildingStruts: true, center, endPt: undefined }
    }

    default:
      return state
  }
}
