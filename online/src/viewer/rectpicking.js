
import { Matrix4, Vector4, WebGPUCoordinateSystem } from 'three';

// Shared by the hit sources' hitsInRect(), for marquee selection: tests whether points project
// inside a screen rectangle.  An object counts as inside when every vertex of its shape does.
//
//   camera   the camera the canvas renders with
//   canvas   the canvas element
//   rect     { left, top, right, bottom }, in client (CSS pixel) coordinates
//
// Set the object's model (local-to-world) matrix with setModelMatrix(), then test its
// local-space vertices with contains().  Points the renderer would clip -- behind the camera,
// or outside the near and far planes -- are never inside.
export const createRectTest = ( camera, canvas, rect ) =>
{
  const canvasRect = canvas .getBoundingClientRect();
  camera .updateMatrixWorld();
  const viewProjection = new Matrix4() .multiplyMatrices( camera.projectionMatrix, camera.matrixWorldInverse );
  const toClip = new Matrix4();
  const point = new Vector4();
  // Clip-space depth runs from -1 at the near plane in WebGL's convention, from 0 in WebGPU's.
  const nearDepth = ( camera.coordinateSystem === WebGPUCoordinateSystem )? 0 : -1;

  const setModelMatrix = matrix => toClip .multiplyMatrices( viewProjection, matrix );

  const contains = ( x, y, z ) =>
  {
    point .set( x, y, z, 1 ) .applyMatrix4( toClip );
    if ( point.w <= 0 )
      return false;
    const depth = point.z / point.w;
    if ( depth < nearDepth || depth > 1 )
      return false;
    const screenX = canvasRect.left + ( point.x / point.w + 1 ) / 2 * canvasRect.width;
    const screenY = canvasRect.top + ( 1 - point.y / point.w ) / 2 * canvasRect.height;
    return screenX >= rect.left && screenX <= rect.right && screenY >= rect.top && screenY <= rect.bottom;
  }

  return { setModelMatrix, contains };
}
