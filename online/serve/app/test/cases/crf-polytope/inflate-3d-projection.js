
/*
  Here, we will start with a 3D projection of a 4D polytope, with full topology,
  meaning vertices, edges, faces, and cells are all known, and each records which
  lower-dimensional elements it is bounded by, and which higher-dimensional elements it bounds.
  Given a starter set of blue struts (edges), known to be lying in the projection hyperplane,
  we will inflate the structure one cell at a time, using the Pythagorean theorem in 4D
  and planarity of faces, to find the vertex pre-images in 4D.
*/

import { downloadJSON, downloadText, fetchUrlJSON, fetchUrlText, } from "./utils.js";
import { initialize, vzomePkg, util, coloredMeshToSimpleMesh, simpleMeshToTopologicalMesh,
          enhanced4dToSimpleMesh, enhanced4dToTopologicalMesh, enhancedMeshTo4OFF, } from "/modules/vzome-legacy.js";

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

    console .log( `Lifted ${ cellsLifted } cells to 4D.` );

    // Now, mirror everything in the negative W direction
    enhancedMesh .vertices .forEach( vertex => {
      if ( vertex .is4D && ! vertex .reflection ) {
        const w = vertex .vector4d .getComponent( 0 );
        if ( w .isZero() ) {
          vertex .reflection = vertex .index; // mark for later processing
        } else {
          const negVec4d = new vzomePkg.core.algebra.AlgebraicVector(
            w .negate(),
            vertex .vector4d .getComponent( 1 ),
            vertex .vector4d .getComponent( 2 ),
            vertex .vector4d .getComponent( 3 )
          );
          const rVertex = { index: enhancedMesh .vertices .length, vector4d: negVec4d, is4D: true, reflection: vertex.index, adjacentEdges: [] };
          vertex .reflection = rVertex .index;
          enhancedMesh .vertices .push( rVertex );
        }
      }
    } );
    enhancedMesh .edges .forEach( edge => {
      if ( edge .is4D && ! edge .reflection ) {
        if ( edge .endpoints .some( v => v.reflection !== v.index ) ) {
          const rEdge = { index: enhancedMesh .edges .length, adjacentFaces: [], is4D: true, reflection: edge.index };
          edge .reflection = rEdge .index;
          enhancedMesh .edges .push( rEdge );
          rEdge .endpoints = edge .endpoints .map( vertex => {
            const rVertex = enhancedMesh .vertices[ vertex .reflection ];
            rVertex .adjacentEdges .push( rEdge );
            return rVertex;
          } );
        } else {
          edge .reflection = edge .index;
        }
      }
    } );
    enhancedMesh .faces .forEach( face => {
      if ( face .is4D && ! face .reflection ) {
        if ( face .adjacentEdges .some( e => e .reflection !== e .index ) ) {
          const rFace = { index: enhancedMesh .faces .length, adjacentCells: [], is4D: true, reflection: face.index };
          face .reflection = rFace .index;
          enhancedMesh .faces .push( rFace );
          rFace .adjacentEdges = face .adjacentEdges .map( edge => {
            const rEdge = enhancedMesh .edges[ edge .reflection ];
            rEdge .adjacentFaces .push( rFace );
            return rEdge;
          } );
        } else {
          face .reflection = face .index;
        }
      }
    } );
    enhancedMesh .cells .forEach( cell => {
      if ( cell .is4D && ! cell .reflection ) {
        if ( cell .adjacentFaces .some( f => f .reflection !== f .index ) ) {
          const rCell = { index: enhancedMesh .cells .length, is4D: true, reflection: cell.index };
          cell .reflection = rCell .index;
          enhancedMesh .cells .push( rCell );
          rCell .adjacentFaces = cell .adjacentFaces .map( face => {
            const rFace = enhancedMesh .faces[ face .reflection ];
            rFace .adjacentCells .push( rCell );
            return rFace;
          } );
        } else {
          cell .reflection = cell .index;
        }
      }
    } );

    // downloadText( enhancedMeshTo4OFF( enhancedMesh ), "JK-CRF-4d.off", "text/plain" );

    // Now, a miracle occurs: the OFF file just exported was loaded into Stella4D by
    //  Nathaniel from the vZome Discord server, and Stella4D confirmed that the vertex
    //  data was complete for convex 4D polytope.  It reported:
    //     vertices: 704
    //     edges:    2352
    //     faces:    2096
    //     cells:    448
    // Next, I installed `qhull` (http://www.qhull.org/), and handcrafted an input file,
    //  `qhull-input.txt`.  I then ran:
    //      $  cat qhull-input.txt | qhull o > JK-CRF-qhull.off
    //      $  cat qhull-input.txt | qhull Fv > qhull-cell-vertices.txt
    //      $  cat qhull-input.txt | qhull Fn > qhull-cell-neighbors.txt
    //  Note that I cannot use `qhull p` to get vertices because the indices will be off.
    //  In `qhull-cell-vertices.txt`, the vertex indices match my internal indices.

    // Now, I can load `qhull-cell-vertices.txt` and `qhull-cell-neighbors.txt` to reconstruct
    //  the topological mesh with complete edge, face, and cell data, *replacing* the existing topology
    //  (which is incomplete anyway).

    const representativeEdgeVector = enhancedMesh .edges[ 0 ] .endpoints[0] .vector4d .minus( enhancedMesh .edges[0] .endpoints[1] .vector4d );
    const repEdgeLengthSquared = representativeEdgeVector .dot( representativeEdgeVector );

    enhancedMesh .edges = [];
    enhancedMesh .faces = [];
    enhancedMesh .cells = [];
    enhancedMesh .vertices .forEach( vertex => {
      vertex .adjacentEdges = [];
      if ( ! vertex .is4D ) {
        // This is legit, since the OFF export already works this way, and Stella4D accepted it.
        vertex .vector4d = vertex .vector .inflateTo4d( true ); // w=0
        vertex .is4D = true;
      }
    } );
    Promise.all( [ fetchUrlText( "./output/qhull-cell-vertices.txt" ), fetchUrlText( "./output/qhull-cell-neighbors.txt" ) ] )
      .then( async ( [ verticesText, neighborCellsText ] ) => {

        const verticesLines = verticesText .trim() .split( "\n" ) .slice( 1 ); // skip first line
        const neighborCellsLines = neighborCellsText .trim() .split( "\n" ) .slice( 1 ); // skip first line

        verticesLines .forEach( ( line, index ) => {
          const adjacentVertices = line .trim() .split( /\s+/ ) .slice( 1 ) .map( s => parseInt( s ) );
          const adjacentCells = neighborCellsLines[ index ] .trim() .split( /\s+/ ) .slice( 1 ) .map( s => parseInt( s ) );
          const cell = { index: enhancedMesh .cells .length, adjacentFaces: [], adjacentVertices, adjacentCells, is4D: true };
          enhancedMesh .cells .push( cell );
        } );

        console.log( verticesLines[ 0 ] );

        enhancedMesh .cells .forEach( cell => {
          // For each neighboring cell, find or create the face that separates them
          cell .adjacentCells .forEach( neighborCellIndex => {
            // To avoid duplicating faces, only create the face if this cell's index is less than the neighbor's index
            if ( cell .index < neighborCellIndex ) {
              const neighborCell = enhancedMesh .cells[ neighborCellIndex ];
              // Find the common vertices between the two cells
              const commonVertices = cell .adjacentVertices .filter( vIndex => neighborCell .adjacentVertices .includes( vIndex ) );
              // Create edges for each pair of common vertices
              const faceEdges = [];
              for ( let i = 0; i < commonVertices .length; i++ ) {
                for ( let j = i + 1; j < commonVertices .length; j++ ) {
                  const vAIndex = commonVertices[ i ];
                  const vBIndex = commonVertices[ j ];
                  // Check if this edge already exists
                  let edge = enhancedMesh .edges .find( e =>
                    ( e .endpoints[0] .index === vAIndex && e .endpoints[1] .index === vBIndex ) ||
                    ( e .endpoints[0] .index === vBIndex && e .endpoints[1] .index === vAIndex )
                  );
                  if ( ! edge ) {
                    // Create new edge
                    const vA = enhancedMesh .vertices[ vAIndex ];
                    const vB = enhancedMesh .vertices[ vBIndex ];
                    // Check if this potential edge is the right length
                    const edgeVector = vA .vector4d .minus( vB .vector4d );
                    const edgeLengthSquared = edgeVector .dot( edgeVector );
                    if ( ! edgeLengthSquared .equals( repEdgeLengthSquared ) ) {
                      continue; // skip edges that are not the correct length
                    }
                    edge = { index: enhancedMesh .edges .length, endpoints: [ vA, vB ], adjacentFaces: [], is4D: true };
                    enhancedMesh .edges .push( edge );
                    // Link edge to vertices
                    vA .adjacentEdges .push( edge );
                    vB .adjacentEdges .push( edge );
                  }
                  faceEdges .push( edge );
                }
              }
              // Create the face
              const face = { index: enhancedMesh .faces .length, adjacentEdges: faceEdges, adjacentCells: [ cell, neighborCell ], is4D: true };
              enhancedMesh .faces .push( face );
              // Link face to edges
              faceEdges .forEach( edge => {
                edge .adjacentFaces .push( face );
              } );
              // Link face to cells
              cell .adjacentFaces .push( face );
              neighborCell .adjacentFaces .push( face );
            }
          } );
        } );

        console .log( `Reconstructed mesh has ${ enhancedMesh .vertices .length } vertices, ${ enhancedMesh .edges .length } edges, ${ enhancedMesh .faces .length } faces, and ${ enhancedMesh .cells .length } cells.` );

        downloadText( enhancedMeshTo4OFF( enhancedMesh ), "JK-CRF-4d.off", "text/plain" );

        // Extract a topological mesh to be used in Observable notebooks
        downloadJSON( enhanced4dToTopologicalMesh( enhancedMesh ), "lifted-4d.tmesh.json" );

        // Finally, extract all 4D faces, edge, and vertices into a simple mesh for download
        downloadJSON( enhanced4dToSimpleMesh( enhancedMesh ), "lifted-4d.mesh.json" );
      } );
  } );