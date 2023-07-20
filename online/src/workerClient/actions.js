
const workerAction = ( type, payload ) => ({ type, payload, meta: 'WORKER' } );

export const selectScene = title => workerAction( 'SCENE_SELECTED', title );

export const selectEditBefore = nodeId => workerAction( 'EDIT_SELECTED', { before: nodeId } );

export const selectEditAfter = nodeId => workerAction( 'EDIT_SELECTED', { after: nodeId } );

export const fetchDesign = ( url, config={ preview: false, debug: false, showScenes: false } ) => workerAction( 'URL_PROVIDED', { url, config } );

export const openDesignFile = ( file, debug=false ) => workerAction( 'FILE_PROVIDED', { file, debug } );

export const newDesign = () => workerAction( 'NEW_DESIGN_STARTED', { field: 'golden' } );

export const doControllerAction = ( controllerPath='', action, parameters ) => workerAction( 'ACTION_TRIGGERED', { controllerPath, action, parameters } );

export const requestControllerProperty = ( controllerPath='', propName, changeName=propName, isList=false ) => workerAction( 'PROPERTY_REQUESTED', { controllerPath, propName, changeName, isList } );

export const setControllerProperty = ( controllerPath='', name, value ) => workerAction( 'PROPERTY_SET', { controllerPath, name, value } );

export const createStrut = ( id, plane, zone, index, orientation ) => workerAction( 'STRUT_CREATION_TRIGGERED', { id, plane, zone, index, orientation } );

export const joinBalls = ( id1, id2 ) => workerAction( 'JOIN_BALLS_TRIGGERED', { id1, id2 } );

export const startPreviewStrut = ( ballId, direction ) => workerAction( 'PREVIEW_STRUT_START', { ballId, direction } );

export const movePreviewStrut = ( direction ) => workerAction( 'PREVIEW_STRUT_MOVE', { direction } );

export const endPreviewStrut = () => workerAction( 'PREVIEW_STRUT_END', {} );

export const initialState = {
  scene: {
    camera: {
      near: 0.271828,
      far: 217,
      width: 48,
      distance: 108,
      up: [ 0, 1, 0 ],
      lookAt: [ 0, 0, 0 ],
      lookDir: [ 0, 0, -1 ],
      perspective: true,
      default: true,
    },
    lighting: {
      backgroundColor: '#8CC2E7',
      ambientColor: '#333333',
      directionalLights: [ // These are the vZome defaults, for consistency
        { direction: [ 1, -1, -0.3 ], color: '#FDFDFD' },
        { direction: [ -1, 0, -0.2 ], color: '#B5B5B5' },
        { direction: [ 0, 0, -1 ], color: '#303030' },
      ]
    },
  }
};
