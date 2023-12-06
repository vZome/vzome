
// This is necessary as long as the worker uses tXml, and they haven't fixed issue 44:
//   https://github.com/TobiasNickel/tXml/issues/44
// I'm fixing it here simply because I have access to the DOM, which is not supported in the worker.
export const decodeEntities = (html) =>
{
  var txt = document.createElement("textarea");
  txt.innerHTML = html;
  return txt.value;
}
export const encodeEntities = (title) =>
{
  var text = document.createTextNode(title);
  var p = document.createElement('p');
  p.appendChild(text);
  return p.innerHTML;
}

const defaultLoad = {
  camera: true,
  lighting: true,
  design: true,
};

const defaultConfig = {
  preview: false,
  debug: false,
  showScenes: false,
  load: defaultLoad,
};

const workerAction = ( type, payload ) => ({ type, payload, meta: 'WORKER' } );

export const selectScene = ( title, load=defaultLoad ) => 
{
  title = encodeEntities( title );
  return workerAction( 'SCENE_SELECTED', { title, load } );
}

export const selectEditBefore = nodeId => workerAction( 'EDIT_SELECTED', { before: nodeId } );

export const selectEditAfter = nodeId => workerAction( 'EDIT_SELECTED', { after: nodeId } );

export const fetchDesign = ( url, config=defaultConfig ) => workerAction( 'URL_PROVIDED', { url, config } );

export const openDesignFile = ( file, debug=false ) => workerAction( 'FILE_PROVIDED', { file, debug } );

export const importMeshFile = ( file, format ) => workerAction( 'MESH_FILE_PROVIDED', { file, format } );

export const newDesign = ( field='golden' ) => workerAction( 'NEW_DESIGN_STARTED', { field } );

export const doControllerAction = ( controllerPath='', action, parameters ) => workerAction( 'ACTION_TRIGGERED', { controllerPath, action, parameters } );

export const doControllerMacro = ( actions ) => workerAction( 'MACRO_TRIGGERED', actions );

export const requestControllerProperty = ( controllerPath='', propName, changeName=propName, isList=false ) => workerAction( 'PROPERTY_REQUESTED', { controllerPath, propName, changeName, isList } );

export const setControllerProperty = ( controllerPath='', name, value ) => workerAction( 'PROPERTY_SET', { controllerPath, name, value } );

export const createStrut = ( id, plane, zone, index, orientation ) => workerAction( 'STRUT_CREATION_TRIGGERED', { id, plane, zone, index, orientation } );

export const joinBalls = ( id1, id2 ) => workerAction( 'JOIN_BALLS_TRIGGERED', { id1, id2 } );

export const startPreviewStrut = ( ballId, direction ) => workerAction( 'PREVIEW_STRUT_START', { ballId, direction } );

export const movePreviewStrut = ( direction ) => workerAction( 'PREVIEW_STRUT_MOVE', { direction } );

export const endPreviewStrut = () => workerAction( 'PREVIEW_STRUT_END', {} );

// This is for buildplane
export const setHingeStrut = ( strutId, centerId, diskZone, hingeZone ) => workerAction( 'HINGE_STRUT_SELECTED', { strutId, centerId, diskZone, hingeZone } );
