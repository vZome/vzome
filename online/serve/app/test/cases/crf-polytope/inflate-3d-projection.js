
/*
  Here, we will start with a 3D projection of a 4D polytope, with full topology,
  meaning vertices, edges, faces, and cells are all known, and each records which
  lower-dimensional elements it is bounded by, and which higher-dimensional elements it bounds.
  Given a starter set of blue struts (edges), known to be lying in the projection hyperplane,
  we will inflate the structure one cell at a time, using the Pythagorean theorem in 4D
  and planarity of faces, to find the vertex pre-images in 4D.
*/

import { downloadJSON, fetchUrlJSON, } from "./utils.js";
import { initialize, vzomePkg, util, coloredMeshToSimpleMesh, simpleMeshToTopologicalMesh,
          enhanced4dToSimpleMesh, enhanced4dToTopologicalMesh, } from "/modules/vzome-legacy.js";

Promise.all( [ initialize(), fetchUrlJSON( "./CRF.tmesh.json" ), fetchUrlJSON( "./starter-blues.cmesh.json" ) ] )
  .then( async ( [ api, topoMesh3d, starterCMesh ] ) =>
  {
    const enhancedMesh = api .enhanceTopologicalMesh( topoMesh3d );
    const field = enhancedMesh .field;

    const enhancedStartEdges = api .enhanceTopologicalMesh( simpleMeshToTopologicalMesh( coloredMeshToSimpleMesh( starterCMesh ) ) );

    // For each edge in enhancedStartEdges, find the matching edge in enhancedMesh, and add it to a queue
    const edgeQueue = [];
    enhancedStartEdges .edges .forEach( startEdge =>
    {
      const matchEdge = enhancedMesh .edges .find( e => e .centroid .equals( startEdge .centroid ) );
      if ( matchEdge ) {
        edgeQueue .push( matchEdge );
      }
    } );

    console .log( `Starting with ${ edgeQueue .length } blue edges.` );

    // Find the faces that are *only* adjacent to these edges, and add them to a list
    const pentagonFaces = new Set();
    edgeQueue .forEach( edge =>
    {
      edge .adjacentFaces .forEach( face =>
      {
        // Check if all adjacent edges of this face are in the edgeQueue
        const allEdgesInQueue = face .adjacentEdges .every( fe =>
          edgeQueue .some( qe => qe .index === fe .index )
        );
        if ( allEdgesInQueue ) {
          pentagonFaces .add( face );
        }
      } );
    } );

    console .log( `Found ${ pentagonFaces .size } faces bounded entirely by blue edges.` );

    // let blueMagSquared = null;

    // let yellowEdge = null;
    // Mark those faces as 4D-inflated and find their triangle neighbors
    const triangleFaces = new Set();
    pentagonFaces .forEach( face => { 
      face .is4D = true;
      // also mark its edges as 4D
      face .adjacentEdges .forEach( edge => {
        edge .is4D = true;
        face .seam = edge; // for later cell inflation
        // and inflate its vertices to 4D vectors
        edge .endpoints .forEach( vertex => {
          if ( ! vertex .is4D ) {
            // Inflate to 4D by adding a zero W coordinate
            const vec3d = vertex .vector;
            vertex .vector4d = vec3d .inflateTo4d( true ); // w is first coordinate
            vertex .is4D = true;
          }
        } );
        edge .adjacentFaces .forEach( triangleFace => {
          if ( triangleFace !== face && triangleFace .adjacentEdges .length === 3 ) {
            triangleFaces .add( triangleFace );
            // find a non-4D edge in that face
            triangleFace .adjacentEdges .forEach( triangleEdge => {
              // if ( ! triangleEdge .is4D ) {
              //   yellowEdge = triangleEdge;
              // }
              // WARNING: this code is completely specific to the particular polytope projection
              //   I am using!  It should look like I pulled this W value out of thin air, but
              //   I actually computed wMagSquared on a prior run, and manually took the square root.
              const wValue = field .createPower( 3 ); // phi^3
              triangleEdge .endpoints .forEach( vertex => {
                if ( ! vertex .is4D ) {
                  const vec3d = vertex .vector;
                  vertex .vector4d = vec3d .inflateTo4d( true );
                  vertex .vector4d .setComponent( 0, wValue ); // w is first coordinate
                  vertex .is4D = true;
                }
              } );
              triangleEdge .is4D = true;
            } );
            triangleFace .is4D = true;
          }
        } );
        // Also, record the blue edge length squared
        // if ( blueMagSquared === null ) {
        //   const vA = edge .endpoints[0] .vector;
        //   const vB = edge .endpoints[1] .vector;
        //   const diff = vA .minus( vB );
        //   blueMagSquared = diff .dot( diff );
        // }
      } );
    } );

    // const yellowVector = yellowEdge .endpoints[0] .vector .minus( yellowEdge .endpoints[1] .vector );
    // const yellowMagSquared = yellowVector .dot( yellowVector );
    // console .log( `Blue edge length squared: ${ blueMagSquared }, yellow edge length squared: ${ yellowMagSquared }` );
    // const wMagSquared = blueMagSquared .minus( yellowMagSquared );


    // At this point, we have four pentagon faces and 20 surrounding triangular faces marked as 4D.

    // First, build a queue of cells adjacent to these faces
    const cellQueue = [];
    pentagonFaces .forEach( face =>
      face .adjacentCells .forEach( cell => {
        // Check if the cell has a face in triangleFaces
        if ( cell .adjacentFaces .some( f => triangleFaces .has( f ) ) ) {
          cellQueue .push( cell );
        }
      } )
    );
    
    // Now, process each cell in the queue
    console .log( `Inflating ${ cellQueue .length } cells.` );
    let cellsLifted = 0;
    let portalFaces = null;
    while ( cellQueue .length > 0 )
    {
      const cell = cellQueue .shift();
      if ( cell .is4D ) {
        continue; // already processed
      }
      console.log( `Lifting cell ${cell.index} (with ${cell.adjacentFaces.length} faces) to 4D`);

      const lift3to4 = vector3 => vector3 .inflateTo4d( true ) .setComponent( 0, field .one() );

      // To be on the queue, this cell must have at least two faces already inflated to 4D
      portalFaces = cell .adjacentFaces .filter( face => face .is4D );
      if ( portalFaces .length < 2 ) {
        console .error( `Cell ${ cell .index } does not have two portal faces!` );
        continue;
      }
      // Now find two non-coplanar edges among those faces
      let edge1, edge2, face1, face2;
      portalFaces .forEach( face => {
        face .adjacentEdges .forEach( edge => {
          if ( ! edge1 ) {
            edge1 = edge;
            face1 = face;
          } else if ( edge !== edge1 && face !== face1 && ! edge2 ) {
            if ( face1 .adjacentEdges .includes( edge ) ) {
              // skip coplanar edges
              return;
            }
            const edgeColl = new util.HashSet();
            edgeColl .add( edge1 .endpoints[0] .vector );
            edgeColl .add( edge1 .endpoints[1] .vector );
            edgeColl .add( edge  .endpoints[0] .vector );
            edgeColl .add( edge  .endpoints[1] .vector );
            if ( vzomePkg.core.algebra.AlgebraicVectors.areCoplanar( edgeColl ) ) {
              return; // skip coplanar edges
            }
            edge2 = edge;
            face2 = face;
          }
        });
      } );
      if ( ! edge1 || ! edge2 ) {
        console .error( `Cell ${ cell .index } does not have two non-coplanar portal edges!` );
        continue;
      }
      const endpoints = [ ...edge1 .endpoints, ...edge2 .endpoints ];

      const oldColVectors = endpoints .map( e => lift3to4( e .vector ) );
      const newColVectors = endpoints .map( e => e .vector4d );

      const oldMatrix = new vzomePkg.core.algebra.AlgebraicMatrix( oldColVectors );
      const newMatrix = new vzomePkg.core.algebra.AlgebraicMatrix( newColVectors );
      const transform = newMatrix .times( oldMatrix .inverse() );
      // Now, apply this transform to all vertices in the cell that are not yet inflated
      cell .adjacentFaces .forEach( face => {
        if ( ! face .is4D ) {
          face .adjacentEdges .forEach( edge => {
            if ( ! edge .is4D ) {
              edge .endpoints .forEach( vertex => {
                if ( ! vertex .is4D ) {
                  vertex .vector4d = transform .timesColumn( lift3to4( vertex .vector ) );
                  vertex .is4D = true;
                }
              } );
              edge .is4D = true;
            }
          } );
          face .is4D = true;

          // If this face borders a non-4D cell with two 4D faces, add that cell to the queue
          face .adjacentCells .forEach( neighborCell => {
            if ( neighborCell !== cell && ! neighborCell .is4D ) {
              const num4DFaces = neighborCell .adjacentFaces .filter( f => f .is4D ) .length;
              if ( num4DFaces >= 2 && ! cellQueue .includes( neighborCell ) ) {
                cellQueue .push( neighborCell );
              }
            }
          } );
        }
      } );

      cell .is4D = true;
      ++ cellsLifted;
    }

    // // create a colored mesh of the last portal face pair for download
    // const portalFacesMesh = { field: enhancedMesh.field .getName(), vertices: [], edges: [], faces: [] };
    // const vertexSet = new Set();
    // let vertexIndex = 0;
    // portalFaces .forEach( face => {
    //   const faceVertexIndices = [];
    //   face .adjacentEdges .forEach( edge => {
    //     edge .endpoints .forEach( vertex => {
    //       if ( ! vertexSet .has( vertex ) ) {
    //         vertex .newIndex = vertexSet .size;
    //         vertexSet .add( vertex );
    //         portalFacesMesh .vertices .push( vertex .newIndex );
    //         ++ vertexIndex;
    //       }
    //       faceVertexIndices .push( vertex .newIndex );
    //     } );
    //   } );
    //   portalFacesMesh .faces .push( faceVertexIndices );
    // } );
    // portalFacesMesh .vertices = Array .from( vertexSet ) .map( vertex => {
    //   const xyzANs = vertex .vector .getComponents();
    //   // toTD returns an array of strings, and we want actual BigInts
    //   return xyzANs .map( an => an.toTrailingDivisor() .map( str => BigInt( str ) ) );
    // } );

    // downloadJSON( portalFacesMesh, "portal-faces.mesh.json" );

    // Extract a topological mesh to be used in Observable notebooks
    downloadJSON( enhanced4dToTopologicalMesh( enhancedMesh ), "lifted-4d.tmesh.json" );

    // Finally, extract all 4D faces, edge, and vertices into a simple mesh for download
    downloadJSON( enhanced4dToSimpleMesh( enhancedMesh ), "lifted-4d.mesh.json" );
  } );