
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
  showScenes: 'none',
  load: defaultLoad,
};

const workerAction = ( type, payload ) => ( { type, payload } );

export const selectSnapshot = ( snapshot, load=defaultLoad ) => workerAction( 'SNAPSHOT_SELECTED', { snapshot, load } );

export const selectEditBefore = nodeId => workerAction( 'EDIT_SELECTED', { before: nodeId } );

export const selectEditAfter = nodeId => workerAction( 'EDIT_SELECTED', { after: nodeId } );

export const fetchDesign = ( url, config=defaultConfig ) => workerAction( 'URL_PROVIDED', { url, config } );

export const openDesignFile = ( file, debug=false ) => workerAction( 'FILE_PROVIDED', { file, debug } );

export const openTextContent = ( name, contents ) => workerAction( 'TEXT_PROVIDED', { name, contents } );

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

export const scalePreviewStrut = ( increment ) => workerAction( 'PREVIEW_STRUT_SCALE', { increment } );

export const endPreviewStrut = () => workerAction( 'PREVIEW_STRUT_END', {} );

// This is for buildplane
export const setHingeStrut = ( strutId, centerId, diskZone, hingeZone ) => workerAction( 'HINGE_STRUT_SELECTED', { strutId, centerId, diskZone, hingeZone } );

export const requestExport = ( format, params={} ) => workerAction( 'EXPORT_TRIGGERED', { ...params, format } );


// Public Domain/MIT, from a Stack Overflow answer
export const generateUUID = () =>
{
  var d = new Date().getTime();//Timestamp
  var d2 = ((typeof performance !== 'undefined') && performance.now && (performance.now()*1000)) || 0;//Time in microseconds since page-load or 0 if unsupported
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = Math.random() * 16;//random number between 0 and 16
      if(d > 0){//Use timestamp until depleted
          r = (d + r)%16 | 0;
          d = Math.floor(d/16);
      } else {//Use microseconds since page-load if supported
          r = (d2 + r)%16 | 0;
          d2 = Math.floor(d2/16);
      }
      return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
  });
}

