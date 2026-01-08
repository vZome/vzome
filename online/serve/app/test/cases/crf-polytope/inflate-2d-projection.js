
/*
  This script is a start on an algorithm to inflate a 2D projection of a polytope
  back into a 3D representation of the original polytope.
  So far, all it does is identify the zero-height starting blue edges,
  and derive the faces of the projected polytope.  These faces can then be used
  to enforce planarity constraints on their pre-images during inflation.
*/

import { findFaces } from "./netfaces.js";
import { enhanceMesh } from "./enhance-mesh.js";
import { downloadJSON, fetchUrlJSON } from "./utils.js";
import { initialize, vzomePkg, util } from "/modules/vzome-legacy.js";

Promise.all( [ initialize(), fetchUrlJSON( "./dodec-projection.mesh.json" ) ] )
.then( ( [ api, simpleMesh ] ) =>
{
  const enhancedMesh = enhanceMesh( simpleMesh, api );
  const vertexSet = new util.HashSet();
  enhancedMesh .vertices .forEach( vertex => vertexSet .add( vertex .vector ) );

  // First, find the faces of the projected polytope.  These will be cycles of vertices that
  //   equal their convex hull.  We will need these faces to enforce planarity of their pre-images.

  // Graph created, now find the convex hull of the vertices
  const hull2d = vzomePkg.core.math.convexhull.GrahamScan2D.buildHull( vertexSet );
  // Now recover the vertex corresponding to each hull point
  const hullVertices = hull2d .map( vector => {
    return enhancedMesh .vertices .find( vtx => vector .equals( vtx .vector  ) ) ;
  } );

  console.log( 'convex hull computed' );

  // Now, find edges that join two hull vertices
  //   and differ only in X or only in Y.  These will be the blue struts.
  const blueEdges = enhancedMesh .edges .filter( edge => {
    const v1 = edge .endpoints[ 0 ] .vector;
    const v2 = edge .endpoints[ 1 ] .vector;
    const onHull = hullVertices .includes( edge .endpoints[ 0 ] ) &&
                    hullVertices .includes( edge .endpoints[ 1 ] );
    if ( ! onHull )
      return false;
    const xEqual = v1 .getComponent( 0 ) .equals( v2 .getComponent( 0 ) );
    const yEqual = v1 .getComponent( 1 ) .equals( v2 .getComponent( 1 ) );
    return xEqual || yEqual;
  } );

  console.log( `found blue edges ${JSON.stringify( blueEdges .map( edge => edge .index ) )}` );

  // const root = document .getElementById( "root" );

  const faces = findFaces( enhancedMesh .edges );

  const simpleMeshWithFaces = { ...simpleMesh, faces };

  // Write out the mesh with faces
  downloadJSON( simpleMeshWithFaces, 'with-faces.mesh.json' );
} );

