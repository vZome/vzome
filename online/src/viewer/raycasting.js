
import { Raycaster, Vector2 } from 'three';

import { createRectTest } from './rectpicking.js';

// A hit source (see context/interaction.jsx) that raycasts a set of meshes, nearest first.
// ShapedGeometry uses one for its instances; a tool can use one for widgets it draws (see
// app/buildplane).  add( mesh, getHit ) registers a mesh, with a function returning its hit
// record, and returns a function that unregisters it.
//
//   three   solid-three's useThree() context; its camera and canvas are read fresh on each use
export const createRaycastHitSource = ( three ) =>
{
  const meshes = new Map(); // mesh -> () => hit record
  const raycaster = new Raycaster();
  const pointer = new Vector2();

  const add = ( mesh, getHit ) =>
  {
    meshes .set( mesh, getHit );
    return () => meshes .delete( mesh );
  }

  const pickAt = ( clientX, clientY ) =>
  {
    const rect = three.canvas .getBoundingClientRect();
    pointer .set( ( clientX - rect.left ) / rect.width * 2 - 1, - ( clientY - rect.top ) / rect.height * 2 + 1 );
    raycaster .setFromCamera( pointer, three.camera );
    const [ nearest ] = raycaster .intersectObjects( [ ...meshes.keys() ], false );
    return nearest? meshes .get( nearest.object )() : null;
  }

  // Every mesh whose vertices all project inside the rectangle.
  const hitsInRect = ( rect ) =>
  {
    const test = createRectTest( three.camera, three.canvas, rect );
    const hits = [];
    for ( const [ mesh, getHit ] of meshes ) {
      test .setModelMatrix( mesh.matrixWorld );
      const positions = mesh.geometry .getAttribute( 'position' );
      let inside = true;
      for ( let i = 0; inside && i < positions.count; i++ )
        inside = test .contains( positions .getX( i ), positions .getY( i ), positions .getZ( i ) );
      if ( inside )
        hits .push( getHit() );
    }
    return hits;
  }

  return { add, pickAt, hitsInRect };
}
