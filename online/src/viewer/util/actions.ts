import type {
  ActionOf, ControllerAction, DesignConfig, Direction, ImportFormat, LoadFlags, WorkerAction,
} from '../protocol.js';

// This is necessary as long as the worker uses tXml, and they haven't fixed issue 44:
//   https://github.com/TobiasNickel/tXml/issues/44
// I'm fixing it here simply because I have access to the DOM, which is not supported in the worker.
export const decodeEntities = ( html: string ): string =>
{
  var txt = document.createElement("textarea");
  txt.innerHTML = html;
  return txt.value;
}
export const encodeEntities = ( title: string ): string =>
{
  var text = document.createTextNode(title);
  var p = document.createElement('p');
  p.appendChild(text);
  return p.innerHTML;
}

const defaultLoad: LoadFlags = {
  camera: true,
  lighting: true,
  design: true,
};

const defaultConfig: DesignConfig = {
  preview: false,
  debug: false,
  showScenes: 'none',
  load: defaultLoad,
};

//  Every creator below funnels through this, which is what makes one edit here cover the
//  whole client -> worker direction.  The generic is pinned by each creator's declared
//  return type, so `type` and `payload` are checked against each other: naming one
//  message type and passing another's payload does not compile.
const workerAction = <T extends WorkerAction>( type: T[ 'type' ], payload: T[ 'payload' ] ): T =>
  ( { type, payload } as T );

export const selectSnapshot = ( snapshot: number, load: LoadFlags = defaultLoad ) =>
  workerAction<ActionOf<'SNAPSHOT_SELECTED'>>( 'SNAPSHOT_SELECTED', { snapshot, load } );

export const selectEditBefore = ( nodeId: string ) =>
  workerAction<ActionOf<'EDIT_SELECTED'>>( 'EDIT_SELECTED', { before: nodeId } );

export const selectEditAfter = ( nodeId: string ) =>
  workerAction<ActionOf<'EDIT_SELECTED'>>( 'EDIT_SELECTED', { after: nodeId } );

export const fetchDesign = ( url: string, config: DesignConfig = defaultConfig ) =>
  workerAction<ActionOf<'URL_PROVIDED'>>( 'URL_PROVIDED', { url, config } );

export const openDesignFile = ( file: File, debug = false ) =>
  workerAction<ActionOf<'FILE_PROVIDED'>>( 'FILE_PROVIDED', { file, debug } );

export const openTextContent = ( name: string, contents: string ) =>
  workerAction<ActionOf<'TEXT_PROVIDED'>>( 'TEXT_PROVIDED', { name, contents } );

export const importMeshFile = ( file: File, format: ImportFormat ) =>
  workerAction<ActionOf<'MESH_FILE_PROVIDED'>>( 'MESH_FILE_PROVIDED', { file, format } );

export const newDesign = ( field = 'golden' ) =>
  workerAction<ActionOf<'NEW_DESIGN_STARTED'>>( 'NEW_DESIGN_STARTED', { field } );

export const doControllerAction = ( controllerPath = '', action: string, parameters?: unknown ) =>
  workerAction<ActionOf<'ACTION_TRIGGERED'>>( 'ACTION_TRIGGERED', { controllerPath, action, parameters } );

export const doControllerMacro = ( actions: ControllerAction[] ) =>
  workerAction<ActionOf<'MACRO_TRIGGERED'>>( 'MACRO_TRIGGERED', actions );

export const requestControllerProperty = ( controllerPath = '', propName: string, changeName = propName, isList = false ) =>
  workerAction<ActionOf<'PROPERTY_REQUESTED'>>( 'PROPERTY_REQUESTED', { controllerPath, propName, changeName, isList } );

export const setControllerProperty = ( controllerPath = '', name: string, value: unknown ) =>
  workerAction<ActionOf<'PROPERTY_SET'>>( 'PROPERTY_SET', { controllerPath, name, value } );

export const createStrut = ( id: string, plane: unknown, zone: unknown, index: unknown, orientation: unknown ) =>
  workerAction<ActionOf<'STRUT_CREATION_TRIGGERED'>>( 'STRUT_CREATION_TRIGGERED', { id, plane, zone, index, orientation } );

export const joinBalls = ( id1: string, id2: string ) =>
  workerAction<ActionOf<'JOIN_BALLS_TRIGGERED'>>( 'JOIN_BALLS_TRIGGERED', { id1, id2 } );

export const startPreviewStrut = ( ballId: string, direction: Direction ) =>
  workerAction<ActionOf<'PREVIEW_STRUT_START'>>( 'PREVIEW_STRUT_START', { ballId, direction } );

export const movePreviewStrut = ( direction: Direction ) =>
  workerAction<ActionOf<'PREVIEW_STRUT_MOVE'>>( 'PREVIEW_STRUT_MOVE', { direction } );

export const scalePreviewStrut = ( increment: number ) =>
  workerAction<ActionOf<'PREVIEW_STRUT_SCALE'>>( 'PREVIEW_STRUT_SCALE', { increment } );

export const endPreviewStrut = () =>
  workerAction<ActionOf<'PREVIEW_STRUT_END'>>( 'PREVIEW_STRUT_END', {} );

// This is for buildplane
export const setHingeStrut = ( strutId: string, centerId: string, diskZone: unknown, hingeZone: unknown ) =>
  workerAction<ActionOf<'HINGE_STRUT_SELECTED'>>( 'HINGE_STRUT_SELECTED', { strutId, centerId, diskZone, hingeZone } );

export const requestExport = ( format: string, params: Record<string, unknown> = {} ) =>
  workerAction<ActionOf<'EXPORT_TRIGGERED'>>( 'EXPORT_TRIGGERED', { ...params, format } );


// Public Domain/MIT, from a Stack Overflow answer
export const generateUUID = (): string =>
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
