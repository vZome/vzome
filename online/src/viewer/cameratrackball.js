
import { Quaternion, Vector2, Vector3 } from 'three';

// Our replacement for three-stdlib's TrackballControls; see developer-docs/mouse-tools.md.
//
// It attaches no DOM listeners.  A tool starts a camera gesture from the InteractionToolProvider
// (rotate, pan or zoom, each from a drag's pointerdown event) and returns the drag object it gets
// back; the wheel goes to wheel().  The math follows TrackballControls with staticMoving = true,
// so the camera moves the same way it always has.
//
// It reads the camera fresh at the start of every gesture rather than caching camera state, so
// nothing needs resyncing when something else (a tween, a snap, a WebXR session) moves the camera.
//
//   camera      the camera to move: CameraProvider's trackballProps.camera
//   getTarget   returns the current look-at point, as [ x, y, z ]
//   sync        sync( target ), called after every change; CameraProvider's trackballProps.sync
//   onStart     called at the start of every gesture (e.g. to cancel camera tweens)
//   getCanvas   returns the canvas element, for mapping pointer coordinates
//   settings    returns { rotateSpeed, zoomSpeed, panSpeed, rotationOnly }

const EPS = 0.000001;

export const createCameraTrackball = ( { camera, getTarget, sync, onStart, getCanvas, settings } ) =>
{
  let suspended = false;

  const target = new Vector3();
  const eye = new Vector3();

  const begin = () =>
  {
    target.set( ...getTarget() );
    onStart ?.();
  }

  const apply = () =>
  {
    camera.position .addVectors( target, eye );
    camera .lookAt( target );
    sync( target );
  }

  // Pointer position relative to the canvas, x and y each 0 to 1.
  const onScreen = ( e, vector ) =>
  {
    const rect = getCanvas() .getBoundingClientRect();
    return vector .set( ( e.clientX - rect.left ) / rect.width, ( e.clientY - rect.top ) / rect.height );
  }

  // Pointer position on TrackballControls' virtual trackball, centered on the canvas;
  // both coordinates are scaled by the half-width, as in TrackballControls.
  const onCircle = ( e, vector ) =>
  {
    const rect = getCanvas() .getBoundingClientRect();
    return vector .set(
      ( e.clientX - rect.width * 0.5 - rect.left ) / ( rect.width * 0.5 ),
      ( rect.height + 2 * ( rect.top - e.clientY ) ) / rect.width
    );
  }

  const zoomBy = factor =>
  {
    if ( Math.abs( factor - 1 ) <= EPS || factor <= 0 )
      return;
    eye .subVectors( camera.position, target ) .multiplyScalar( factor );
    apply();
  }

  const canZoom = () => ! suspended && ! settings() .rotationOnly && !! settings() .zoomSpeed;
  const canPan = () => ! suspended && ! settings() .rotationOnly && !! settings() .panSpeed;

  // options.onEnd is called when the drag ends (e.g. to snap the camera).
  const rotate = ( down, options = {} ) =>
  {
    if ( suspended )
      return null;
    begin();
    const previous = onCircle( down, new Vector2() );
    const current = new Vector2();
    const up = new Vector3(), sideways = new Vector3(), move = new Vector3(), axis = new Vector3();
    const quaternion = new Quaternion();
    return {
      onDrag: e => {
        onCircle( e, current );
        const dx = current.x - previous.x;
        const dy = current.y - previous.y;
        previous .copy( current );
        const angle = Math.hypot( dx, dy );
        if ( ! angle )
          return;
        eye .subVectors( camera.position, target );
        up .copy( camera.up ) .normalize();
        sideways .crossVectors( up, eye ) .normalize();
        up .setLength( dy );
        sideways .setLength( dx );
        move .addVectors( up, sideways );
        axis .crossVectors( move, eye ) .normalize();
        quaternion .setFromAxisAngle( axis, angle * settings() .rotateSpeed );
        eye .applyQuaternion( quaternion );
        camera.up .applyQuaternion( quaternion );
        apply();
      },
      onDragEnd: () => options.onEnd ?.(),
    };
  }

  const pan = down =>
  {
    if ( ! canPan() )
      return null;
    begin();
    const start = onScreen( down, new Vector2() );
    const end = new Vector2(), change = new Vector2();
    const offset = new Vector3(), upOffset = new Vector3();
    return {
      onDrag: e => {
        onScreen( e, end );
        change .subVectors( end, start );
        if ( change .lengthSq() <= EPS )
          return;
        start .copy( end );
        eye .subVectors( camera.position, target );
        change .multiplyScalar( eye .length() * settings() .panSpeed );
        offset .copy( eye ) .cross( camera.up ) .setLength( change.x );
        offset .add( upOffset .copy( camera.up ) .setLength( change.y ) );
        target .add( offset );
        apply();
      },
    };
  }

  // Zoom by dragging vertically.
  const zoom = down =>
  {
    if ( ! canZoom() )
      return null;
    begin();
    const start = onScreen( down, new Vector2() );
    const end = new Vector2();
    return {
      onDrag: e => {
        onScreen( e, end );
        const factor = 1 + ( end.y - start.y ) * settings() .zoomSpeed;
        start .copy( end );
        zoomBy( factor );
      },
    };
  }

  // Returns false when the wheel isn't used, so the page can still scroll.
  const wheel = e =>
  {
    if ( ! canZoom() )
      return false;
    begin();
    const scale = ( e.deltaMode === 2 )? 0.025 : ( e.deltaMode === 1 )? 0.01 : 0.00025; // pages, lines, pixels
    zoomBy( 1 + e.deltaY * scale * settings() .zoomSpeed );
    return true;
  }

  // For WebXR, which drives the camera itself during a session.
  const suspend = () => { suspended = true; }
  const resume = () => { suspended = false; }

  return { rotate, pan, zoom, wheel, suspend, resume };
}
