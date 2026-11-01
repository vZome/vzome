
import { createContext, createEffect, createSignal, useContext } from "solid-js";

// The mouse tool framework.  See developer-docs/mouse-tools.md.
//
// InteractionToolProvider owns all gesture recognition for one canvas.  Renderers never see
// the tool: they register as hit sources (addHitSource), answering "what is under this
// point?" with an opaque hit record.  The canvas registers itself (attachCanvas), and the
// provider turns its raw pointer events into tool calls:
//
//   onClick( e, hit )         a left-button click on something
//   bkgdClick( e )            a left-button click on nothing
//   onDragStart( e, hit )     a drag with any button (see e.button), once the pointer has moved
//                             far enough; `hit` may be null.  Returns a drag object
//                             { onDrag?, onDragEnd?, onWheel? } that gets the rest of the
//                             gesture, or null to ignore the drag.
//   onWheel( e )              the wheel, unless the active drag object claims it.  Either may
//                             return false to leave the wheel unused, so the page can scroll.
//   onHover( e, hit )         the hit under the pointer changed (hit is null when it leaves)
//   onContextMenu( e, hit )   a right-click on something (always picks, regardless of `picks`)
//
// A tool declares which gestures need a pick with `picks` (default: all of them).  A gesture
// that doesn't pick never reaches the tool, since without a pick the provider can't tell an
// object from the background.  A tool with `suppressContextMenu: true` blocks the browser's
// context menu, e.g. so right-drag can pan.  A tool's `hint` describes what the mouse does, as
// { title, lines } or just a title string, for a canvas that displays it (see hint below).
//
// The canvas also registers its camera control (setCameraControl), a CameraTrackball (see
// ../cameratrackball.js); tools reach it through useCameraControl().

const InteractionToolContext = createContext();

const ALL_PICKS = { click: true, drag: true, hover: true };
export const NO_PICKS = { click: false, drag: false, hover: false };

export const grabTool = {

  allowTrackball: true, // TrackballControls handles every drag

  picks: NO_PICKS,

  cursor: 'grab',

  onTrackballEnd: () => {},
};

const MAX_CLICK_DURATION = 500; // msec
const MAX_CLICK_DISTANCE = 2; // pixels

const picksOf = tool => ( { ...ALL_PICKS, ...tool ?.picks } );

const sameHit = ( a, b ) => ( a ?.source === b ?.source ) && ( a ?.id === b ?.id );

const InteractionToolProvider = (props) =>
{
  const [ tool, setTool ] = createSignal( props.defaultTool || grabTool );
  const [ hitSources, setHitSources ] = createSignal( [] );
  const [ cameraControl, setCameraControlSignal ] = createSignal( null );

  const setCameraControl = control =>
  {
    setCameraControlSignal( () => control );
    return () => setCameraControlSignal( current => ( current === control )? null : current );
  }

  const addHitSource = source =>
  {
    setHitSources( sources => [ ...sources, source ] );
    return () => setHitSources( sources => sources .filter( s => s !== source ) );
  }

  // The first hit any source reports, asking them in registration order.
  const pickAt = async ( clientX, clientY ) =>
  {
    for ( const source of hitSources() ) {
      const hit = await source .pickAt( clientX, clientY );
      if ( hit )
        return hit;
    }
    return null;
  }

  // Let hit sources prepare for picking (e.g. the GPU picking renderer, whose lazy
  // initialization otherwise stalls the first click) as soon as a tool that picks is active.
  createEffect( () => {
    const picks = picksOf( tool() );
    if ( picks.click || picks.drag || picks.hover )
      hitSources() .forEach( source => source .warmUp ?.() );
  });

  // The one gesture in progress: a button press, from pointerdown to pointerup.
  //   down        the pointerdown event
  //   canvas      the canvas it happened on
  //   tool        the tool active at pointerdown, which handles the whole gesture
  //   pick        Promise of the hit under the pointerdown, started immediately to hide its latency
  //               (left button only; other buttons only drag, so they pick when the drag starts)
  //   drag        Promise of the tool's drag object, once the press has become a drag
  //   dragObject  the same drag object, once resolved (for routing the wheel)
  let press = null;

  const onPointerDown = e =>
  {
    if ( e.button > 2 || press )
      return;
    const pressTool = tool();
    const picks = picksOf( pressTool );
    press = {
      down: e,
      canvas: e.currentTarget,
      tool: pressTool,
      pick: ( e.button === 0 && ( picks.click || picks.drag ) )? pickAt( e.clientX, e.clientY ) : null,
      drag: null,
      dragObject: null,
    };
  }

  const onPointerMove = e =>
  {
    if ( ! press ) {
      hover( e );
      return;
    }
    if ( e.pointerId !== press.down.pointerId )
      return;
    if ( ! press.drag ) {
      const { down, tool: pressTool, pick } = press;
      if ( Math.hypot( e.clientX - down.clientX, e.clientY - down.clientY ) <= MAX_CLICK_DISTANCE )
        return;
      // This is the decision point: the press has become a drag, so ask the tool what to do,
      // with the hit under the original pointerdown.  Moves that arrive while the pick is
      // still resolving are queued behind it, in order, by chaining on this promise.
      const current = press;
      // Keep receiving this pointer's events even if it leaves the canvas mid-drag.  (Not for
      // clicks, so a context menu that opens on a right-button press still gets the release.)
      current.canvas .setPointerCapture ?.( e.pointerId );
      current.drag = ( async () => {
        const hit = picksOf( pressTool ).drag ? await ( pick || pickAt( down.clientX, down.clientY ) ) : null;
        return pressTool ?.onDragStart ?.( down, hit ) || null;
      } )();
      current.drag .then( dragObject => current.dragObject = dragObject );
    }
    press.drag .then( dragObject => dragObject ?.onDrag ?.( e ) );
  }

  const onPointerUp = e =>
  {
    if ( ! press || e.pointerId !== press.down.pointerId )
      return;
    const { down, tool: pressTool, pick, drag } = press;
    press = null;
    if ( drag ) {
      drag .then( dragObject => dragObject ?.onDragEnd ?.( e ) );
      return;
    }
    if ( down.button !== 0 || e.timeStamp - down.timeStamp >= MAX_CLICK_DURATION || ! picksOf( pressTool ).click )
      return;
    pick .then( hit => hit? pressTool ?.onClick ?.( e, hit ) : pressTool ?.bkgdClick ?.( e ) );
  }

  const onPointerCancel = e =>
  {
    if ( ! press || e.pointerId !== press.down.pointerId )
      return;
    const { drag } = press;
    press = null;
    drag ?.then( dragObject => dragObject ?.onDragEnd ?.( e ) );
  }

  const onWheel = e =>
  {
    const handler = press ?.dragObject ?.onWheel || tool() ?.onWheel;
    if ( handler && handler( e ) !== false )
      e.preventDefault();
  }

  const onContextMenu = async e =>
  {
    const currentTool = tool();
    if ( currentTool ?.suppressContextMenu )
      e.preventDefault();
    if ( ! currentTool ?.onContextMenu )
      return;
    const hit = await pickAt( e.clientX, e.clientY );
    if ( hit )
      currentTool .onContextMenu( e, hit );
  }

  // Hover picks at most once per frame, and only one pick at a time; a pointermove that
  // arrives during a pick is remembered, and picked once the current one finishes.
  let hoverHit = null;
  let hoverEvent = null;
  let hovering = false;
  const hover = e =>
  {
    const hoverTool = tool();
    if ( ! hoverTool ?.onHover || ! picksOf( hoverTool ).hover )
      return;
    hoverEvent = e;
    if ( hovering )
      return;
    hovering = true;
    requestAnimationFrame( async () => {
      while ( hoverEvent ) {
        const event = hoverEvent;
        hoverEvent = null;
        const hit = await pickAt( event.clientX, event.clientY );
        if ( ! sameHit( hit, hoverHit ) ) {
          hoverHit = hit;
          hoverTool .onHover( event, hit );
        }
      }
      hovering = false;
    } );
  }

  const onPointerLeave = e =>
  {
    hoverEvent = null;
    if ( hoverHit ) {
      hoverHit = null;
      tool() ?.onHover ?.( e, null );
    }
  }

  const attachCanvas = canvas =>
  {
    // Touch drags are gestures for the tool, not page scrolling or zooming.
    canvas.style.touchAction = 'none';
    canvas .addEventListener( 'pointerdown', onPointerDown );
    canvas .addEventListener( 'pointermove', onPointerMove );
    canvas .addEventListener( 'pointerup', onPointerUp );
    canvas .addEventListener( 'pointercancel', onPointerCancel );
    canvas .addEventListener( 'pointerleave', onPointerLeave );
    canvas .addEventListener( 'wheel', onWheel, { passive: false } );
    canvas .addEventListener( 'contextmenu', onContextMenu );
    return () => {
      canvas .removeEventListener( 'pointerdown', onPointerDown );
      canvas .removeEventListener( 'pointermove', onPointerMove );
      canvas .removeEventListener( 'pointerup', onPointerUp );
      canvas .removeEventListener( 'pointercancel', onPointerCancel );
      canvas .removeEventListener( 'pointerleave', onPointerLeave );
      canvas .removeEventListener( 'wheel', onWheel );
      canvas .removeEventListener( 'contextmenu', onContextMenu );
    };
  }

  // The active tool's hint, for a canvas that displays one.
  const hint = () => tool() ?.hint;

  // What the canvas and TrackballControls read from the tool.
  const wrappedTool = {

    allowTrackball: () => tool() ?.allowTrackball,

    cursor: () => tool() ?.cursor,

    onTrackballEnd: () => tool() ?.onTrackballEnd ?.(),
  };

  return (
    <InteractionToolContext.Provider value={ { tool: wrappedTool, setTool, addHitSource, attachCanvas, setCameraControl, cameraControl, hint } }>
      {props.children}
    </InteractionToolContext.Provider>
  );
}

// For tools (setTool) and for the canvas and camera controls that read the tool.
// Returns an empty array outside any InteractionToolProvider.
const useInteractionTool = () =>
{
  const context = useContext( InteractionToolContext );
  return context? [ context.tool, context.setTool ] : [];
}

// For renderers and the canvas: { addHitSource, attachCanvas, setCameraControl, hint }, or
// undefined outside any InteractionToolProvider.
const useInteractionSurface = () => useContext( InteractionToolContext );

// For tools: the canvas's camera control.  Each gesture method takes a drag's pointerdown event
// and returns a drag object, or null if the camera can't do that (e.g. pan on a rotation-only
// canvas).  onWheel returns false when it leaves the wheel unused.
const useCameraControl = () =>
{
  const context = useContext( InteractionToolContext );
  const current = () => context ?.cameraControl();
  return {
    rotate: ( e, options ) => current() ?.rotate( e, options ) || null,
    pan: e => current() ?.pan( e ) || null,
    zoom: e => current() ?.zoom( e ) || null,
    onWheel: e => current() ?.wheel( e ) ?? false,
  };
}

export { InteractionToolProvider, useInteractionTool, useInteractionSurface, useCameraControl };
