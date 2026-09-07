import type { EventOf, Scene, WorkerEvent } from '../viewer/protocol.js';

/**
 * How a message reaches the client.  In the worker this is either `postMessage` itself, or
 * the wrapper the message handler builds to stamp a `requestId` onto the reply.
 *
 * The two are NOT interchangeable at the call sites: a response that answers a
 * `postRequest` must go through the stamping wrapper, or the client's promise never
 * resolves and the caller hangs.  Which one a given send needs is a property of the send,
 * not of this module.
 */
export type Report = ( event: WorkerEvent ) => void;

/**
 * The worker -> client half of the protocol: one named sender per message type, so the
 * worker never builds a message object by hand.
 *
 * Everything here crosses postMessage, which is structurally unchecked at runtime -- a
 * renamed field or a wrong payload shape produces no error, just wrong or missing
 * rendering.  Typing the senders against WorkerEvent moves that class of mistake to
 * compile time.
 *
 * These 14 are the events the legacy design code emits.  Eight more outbound types
 * (lifecycle and one-off responses such as FETCH_STARTED, TEXT_FETCHED, CONTROLLER_CREATED
 * and the share results) are still built inline in vzome-worker-static.js and are not
 * routed through here yet.
 */
export const clientEvents = ( report: Report ) =>
{
  const send = < T extends WorkerEvent[ 'type' ] >( type: T, payload: EventOf<T>[ 'payload' ] ) =>
    report( { type, payload } as WorkerEvent );

  /**
   * A whole scene, replacing whatever the client was showing.  `edit` defaults to the
   * history's start marker, which is what the non-edit render paths mean by "no particular
   * edit".
   */
  const sceneChanged = ( scene: Scene, edit = '--START--' ) => send( 'SCENE_RENDERED', { scene, edit } );

  const shapeDefined = ( shape: unknown ) => send( 'SHAPE_DEFINED', shape );

  const instanceAdded = ( instance: unknown ) => send( 'INSTANCE_ADDED', instance );

  const latestBallAdded = ( instance: unknown ) => send( 'LAST_BALL_CREATED', instance );

  const instanceRemoved = ( shapeId: string, id: string ) => send( 'INSTANCE_REMOVED', { shapeId, id } );

  const selectionToggled = ( shapeId: string, id: string, selected: boolean ) =>
    send( 'SELECTION_TOGGLED', { shapeId, id, selected } );

  const symmetryChanged = ( details: EventOf<'SYMMETRY_CHANGED'>[ 'payload' ] ) => send( 'SYMMETRY_CHANGED', details );

  const xmlParsed = ( xmlTree: unknown ) => send( 'DESIGN_XML_PARSED', xmlTree );

  const propertyChanged = ( controllerPath: string, name: string, value: unknown ) =>
    send( 'CONTROLLER_PROPERTY_CHANGED', { controllerPath, name, value } );

  /** The single channel for anything the user should see about a failure. */
  const errorReported = ( message: string ) => send( 'ALERT_RAISED', message );

  const scenesDiscovered = ( s: EventOf<'SCENES_DISCOVERED'>[ 'payload' ] ) => send( 'SCENES_DISCOVERED', s );

  const snapshotCaptured = ( s: number ) => send( 'SNAPSHOT_CAPTURED', s );

  /** `action` is echoed back so the client can correlate the text with the request. */
  const textExported = ( action: string, text: string ) => send( 'TEXT_EXPORTED', { action, text } );

  const buildPlaneSelected = ( center: unknown, diskZone: unknown, hingeZone: unknown ) =>
    send( 'PLANE_CHANGED', { center, diskZone, hingeZone } );

  return { sceneChanged, shapeDefined, instanceAdded, instanceRemoved, selectionToggled, symmetryChanged, latestBallAdded,
    xmlParsed, scenesDiscovered, snapshotCaptured, propertyChanged, errorReported, textExported, buildPlaneSelected, };
}

/** What the legacy design code is handed, and calls back into. */
export type ClientEvents = ReturnType< typeof clientEvents >;
