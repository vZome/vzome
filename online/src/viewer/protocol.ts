//  The worker <-> client message protocol.
//
//  Everything between the client and the design worker crosses postMessage, so it is
//  structurally unchecked at runtime: a renamed field or a wrong payload shape produces
//  no error anywhere, it just silently does nothing.  developer-docs/worker-client-scene-protocol.md
//  records how expensive that has been -- those failures "fail *silently* (no console
//  error, no thrown exception, just wrong or missing rendering)".
//
//  This file is the one place the message vocabulary is written down.  It declares types
//  only; there is no runtime code here and no behaviour change from adding it.
//
//  Both directions are centralised on the sending side, which is what makes typing them
//  cheap:
//
//    client -> worker   viewer/util/actions.js, 23 creators funnelled through workerAction()
//    worker -> client   vzome-worker-static.js, clientEvents(), 14 named senders
//                       (+ 8 more types built inline; see WorkerEvent below)
//
//  SCOPE: payload *interiors* are deliberately loose here.  Scene records -- the shape,
//  instance and orientation objects inside a Scene -- are a separate piece of work with
//  real design questions in it (shapeId vs shapeKey, the rotation/orientations
//  redundancy).  Pinning the message vocabulary and the envelope is worth doing on its
//  own, and each payload can tighten later without touching a single call site.

//  ---------------------------------------------------------------------------
//  Scene data (intentionally loose; see SCOPE above)
//  ---------------------------------------------------------------------------

/**
 * A rendered scene, as sent to the client.  The fields present vary by which of the
 * several worker-side scene paths produced it -- `prepareSceneResponse` returns the full
 * set, while `prepareEditSceneResponse` and `reportDefaultScene` each return a subset.
 * That variation is real and load-bearing, so the fields are optional rather than
 * pretending to a uniformity the code does not have.
 */
export type Scene = {
  shapes?: unknown;
  instances?: unknown;
  embedding?: unknown;
  polygons?: boolean;
  parts?: unknown;
  orientations?: unknown;
  camera?: unknown;
  lighting?: unknown;
  /**
   * Stamps the render with the symmetry system it was built for, so the client can drop a
   * SCENE_RENDERED that arrives after a symmetry switch and would otherwise index
   * out-of-range into the new symmetry group.  Omitted by the edit and legacy render
   * paths.
   */
  symmetryId?: string;
};

//  ---------------------------------------------------------------------------
//  client -> worker
//  ---------------------------------------------------------------------------

/** Which parts of a design a load should apply.  Defaults differ per call site. */
export type LoadFlags = {
  camera?: boolean;
  lighting?: boolean;
  design?: boolean;
  bom?: boolean;
};

/** Options for fetching a design by URL.  Every field has a worker-side default. */
export type DesignConfig = {
  preview?: boolean;
  debug?: boolean;
  polygons?: boolean;
  showScenes?: string;
  snapshot?: number;
  load?: LoadFlags;
  source?: boolean;
};

/** The formats accepted by MESH_FILE_PROVIDED and recognised in a URL by importFormat(). */
export type ImportFormat = 'mesh' | 'cmesh' | 'vef';

/**
 * A direction in world space, as a unit vector.  The preview-strut call sites all build
 * one the same way -- destructuring a THREE.Vector3 into [ x, y, z ] -- so this is a tuple
 * rather than number[]: it is the one place a length mistake could silently produce a
 * strut pointing somewhere unintended.
 */
export type Direction = [ number, number, number ];

/** One controller action, as sent singly by ACTION_TRIGGERED or in a list by MACRO_TRIGGERED. */
export type ControllerAction = {
  controllerPath: string;
  action: string;
  parameters?: unknown;
};

export type WorkerAction =
  //  Lifecycle.  Sent directly rather than through an action creator: WORKER_PROBE is the
  //  handshake createWorker() sends before it will post anything else, and WINDOW_LOCATION
  //  seeds the worker's baseURL for resource resolution.
  | { type: 'WORKER_PROBE'; payload?: undefined }
  | { type: 'WINDOW_LOCATION'; payload: string }

  //  Loading a design.
  | { type: 'URL_PROVIDED'; payload: { url: string; config?: DesignConfig } }
  | { type: 'FILE_PROVIDED'; payload: { file: File; debug?: boolean; polygons?: boolean } }
  | { type: 'TEXT_PROVIDED'; payload: { name: string; contents: string } }
  | { type: 'MESH_FILE_PROVIDED'; payload: { file: File; format: ImportFormat } }
  | { type: 'NEW_DESIGN_STARTED'; payload: { field: string } }

  //  Scene selection.  SNAPSHOT_SELECTED is the one action sent via postRequest (it
  //  resolves a promise rather than broadcasting); EDIT_SELECTED carries exactly one of
  //  `before` or `after`.
  | { type: 'SNAPSHOT_SELECTED'; payload: { snapshot: number; load?: LoadFlags } }
  | { type: 'EDIT_SELECTED'; payload: { before: string; after?: undefined } | { after: string; before?: undefined } }

  //  Controllers.
  | { type: 'ACTION_TRIGGERED'; payload: ControllerAction }
  | { type: 'MACRO_TRIGGERED'; payload: ControllerAction[] }
  | { type: 'PROPERTY_REQUESTED'; payload: { controllerPath: string; propName: string; changeName: string; isList: boolean } }
  | { type: 'PROPERTY_SET'; payload: { controllerPath: string; name: string; value: unknown } }

  //  Building, from the buildplane app.
  | { type: 'STRUT_CREATION_TRIGGERED'; payload: { id: string; plane: unknown; zone: unknown; index: unknown; orientation: unknown } }
  | { type: 'JOIN_BALLS_TRIGGERED'; payload: { id1: string; id2: string } }
  | { type: 'HINGE_STRUT_SELECTED'; payload: { strutId: string; centerId: string; diskZone: unknown; hingeZone: unknown } }

  //  The preview strut, dragged in the editor.
  | { type: 'PREVIEW_STRUT_START'; payload: { ballId: string; direction: Direction } }
  | { type: 'PREVIEW_STRUT_MOVE'; payload: { direction: Direction } }
  | { type: 'PREVIEW_STRUT_SCALE'; payload: { increment: number } }
  | { type: 'PREVIEW_STRUT_END'; payload: Record<string, never> }

  //  Output.
  | { type: 'EXPORT_TRIGGERED'; payload: { format: string; camera?: unknown; lighting?: unknown; scenes?: unknown } }
  | { type: 'BOM_REQUESTED'; payload: unknown }
  ;

export type WorkerActionType = WorkerAction[ 'type' ];

/** The single WorkerAction with a given type, e.g. ActionOf<'PREVIEW_STRUT_MOVE'>. */
export type ActionOf< T extends WorkerActionType > = Extract< WorkerAction, { type: T } >;

/**
 * A WorkerAction as it actually goes over postMessage.
 *
 * `requestId` is added by postRequest() to correlate a reply; onWorkerMessage resolves the
 * pending promise for a message carrying one instead of broadcasting it to subscribers.
 *
 * `isInspector` is set by an older React-based client whose source we no longer have.  The
 * worker turns it into `payload.polygons = !isInspector` (and the same on `payload.config`)
 * for every message type, so it is an envelope concern rather than a per-message one.  It
 * must keep working for as long as that client exists: this protocol is
 * backward-compatible by requirement, not by accident.
 */
export type WorkerRequest = WorkerAction & {
  requestId?: string;
  isInspector?: boolean;
};

//  ---------------------------------------------------------------------------
//  worker -> client
//  ---------------------------------------------------------------------------

export type WorkerEvent =
  //  Lifecycle.  WORKER_READY answers WORKER_PROBE; CONTROLLER_CREATED is what
  //  WorkerProvider waits for before it will send actions or property requests.
  | { type: 'WORKER_READY'; payload?: undefined }
  | { type: 'CONTROLLER_CREATED'; payload?: undefined }

  //  Loading progress.  FETCH_STARTED and TEXT_FETCHED are built inline rather than via
  //  clientEvents.  TEXT_FETCHED deliberately withholds `name` on the first send of some
  //  paths -- parse or interpret may still fail -- and sends it again once loading has
  //  succeeded, which is why both fields are optional.
  | { type: 'FETCH_STARTED'; payload: { name?: string; url?: string; preview?: boolean; config?: DesignConfig } }
  | { type: 'TEXT_FETCHED'; payload: { text: string; name?: string; url?: string } }
  | { type: 'DESIGN_XML_PARSED'; payload: unknown }

  //  Scene rendering.
  | { type: 'SCENE_RENDERED'; payload: { scene: Scene; edit?: string } }
  | { type: 'SCENES_DISCOVERED'; payload: { lighting?: unknown; scenes?: unknown } }
  | { type: 'SYMMETRY_CHANGED'; payload: { orientations?: unknown; permutations?: unknown; scalars?: unknown; planes?: unknown; resourcePath?: string; fieldName?: string; symmetryName?: string; embedding?: unknown } }

  //  Incremental scene edits.  Editor-only in practice: the viewer/web-component path
  //  never mounts SceneChangeListener, and only ever swaps whole snapshots.
  | { type: 'SHAPE_DEFINED'; payload: unknown }
  | { type: 'INSTANCE_ADDED'; payload: unknown }
  | { type: 'INSTANCE_REMOVED'; payload: { shapeId: string; id: string } }
  | { type: 'SELECTION_TOGGLED'; payload: { shapeId: string; id: string; selected: boolean } }
  | { type: 'LAST_BALL_CREATED'; payload: unknown }

  //  Controllers and building.
  | { type: 'CONTROLLER_PROPERTY_CHANGED'; payload: { controllerPath: string; name: string; value: unknown } }
  | { type: 'PLANE_CHANGED'; payload: { center: unknown; diskZone: unknown; hingeZone: unknown } }
  | { type: 'CAMERA_SNAPPED'; payload: { up: unknown; lookDir: unknown } }
  | { type: 'SNAPSHOT_CAPTURED'; payload: number }

  //  Output.  TEXT_EXPORTED returns the originating action so the client can correlate it.
  | { type: 'TEXT_EXPORTED'; payload: { action: string; text: string } }
  | { type: 'BOM_CHANGED'; payload: unknown }
  | { type: 'SHARE_SUCCESS'; payload: string }
  | { type: 'SHARE_FAILURE'; payload: { message: string; status?: number } }

  //  Errors.  The single channel for anything the user should see.
  | { type: 'ALERT_RAISED'; payload: string }
  ;

export type WorkerEventType = WorkerEvent[ 'type' ];

/** A WorkerEvent as it goes over postMessage, echoing the requestId of the action it answers. */
export type WorkerResponse = WorkerEvent & {
  requestId?: string;
};

//  ---------------------------------------------------------------------------
//  Helpers
//  ---------------------------------------------------------------------------

/** The payload type belonging to one message type, for typing a handler or a subscriber. */
export type PayloadOf< T extends WorkerActionType | WorkerEventType > =
  Extract< WorkerAction | WorkerEvent, { type: T } > extends { payload: infer P } ? P : never;
