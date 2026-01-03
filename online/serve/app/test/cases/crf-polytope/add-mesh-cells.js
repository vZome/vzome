
/*
  Each file in `cells/*.json` was copied from a set of panels in the master model,
  in Dropbox as 2025/12-Dec/31-Scott-CRF/JK-new-CRF-RYB-SV-all-panels.vZome.
  The panels were painstakingly selected as collections of cells,
  so that two panels share an edge only when they are part of a single cell.
  The code here combines that data with the simple mesh in `CRF.mesh.json`,
  to output `CRF.tmesh.json`, a full topological mesh.
*/

import { downloadJSON, fetchUrlJSON, } from "./utils.js";
import { initialize, vzomePkg, simpleMeshToTopologicalMesh, coloredMeshToSimpleMesh, } from "/modules/vzome-legacy.js";

const getCentroid = vzomePkg.core.algebra.AlgebraicVectors.getCentroid;

const cellSets = [
  "core-j91",
  "IDs-layer1-L",
  "IDs-layer1-R",
  "IDs-layer2-L",
  "IDs-layer2-R",
  "IDs-layer3-L",
  "IDs-layer3-R",
  "J63s-layer1",
  "j63s-layer2",
  "j91s-layer1",
  "j92s-layer1",
  "j92s-layer2-L",
  "j92s-layer2-R",
  "j92s-layer3-L",
  "j92s-layer3-R",
  "octas-layer6",
  "octs-tets-layer1",
  "octs-tets-layer2",
  "octs-tets-layer3",
  "octs-tets-layer4",
  "octs-tets-layer5",
  "octs-tets-layer7",
  "prisms-layer1",
  "prisms-layer2",
  "prisms-layer3",
  "truncated-tets",
];

const flatCells = [
  "flat-IDs",
  "flat-j91s",
  "flat-j92s",
  "flat-octas",
];

// Convert panels from colored mesh to simple mesh edges and faces
const panelsToMesh = ( { field, vertices, panels } ) =>
{
  const edgeSet = new Set();
  const edges = [];
  panels .forEach( panel => {
    const vertices = panel.vertices;
    for (let i = 0; i < vertices.length; i++) {
      const v1 = vertices[i];
      const v2 = vertices[(i + 1) % vertices.length]; // Wrap around to first vertex
      const edgeKey = v1 < v2 ? `${v1},${v2}` : `${v2},${v1}`;
      if (!edgeSet.has(edgeKey)) {
        edgeSet.add(edgeKey);
        edges.push([v1, v2]);
      }
    }
  });
  return {
    field, vertices, edges,
    faces : panels .map( panel => panel .vertices ),
  };
}

const installCells = async ( enhancedMesh, cellSet, api ) =>
{
  await fetchUrlJSON( `./cells/${cellSet}.json` )
    .then( cmesh =>
    {
      const enhancedFaces = api.enhanceTopologicalMesh( simpleMeshToTopologicalMesh( coloredMeshToSimpleMesh( cmesh ) ) );

      // Now join edge-neighboring faces into cells, using breadth-first search.
      const visited = new Set();
      
      enhancedFaces.faces.forEach((face, startIndex) => {
        if (visited.has(startIndex)) return;
        
        // Start a new cell with BFS
        const cell = { index: enhancedMesh.cells.length, adjacentFaces: [] };
        const queue = [startIndex];
        visited.add(startIndex);
        
        while (queue.length > 0) {
          const faceIndex = queue.shift();
          const currentFace = enhancedFaces.faces[faceIndex];

          // Find the corresponding face in the main enhancedMesh, by matching centroids
          let targetFace = enhancedMesh .faces .find( f => f.centroid.equals( currentFace .centroid ) );
          if (!targetFace) {
            // construct a new face in the main mesh with the same vertices as currentFace
            targetFace = {
              index: enhancedMesh.faces.length,
              adjacentEdges: [],
              adjacentCells: [],
            };
            // Find edges in the main mesh with the same centroids as those in currentFace
            currentFace.adjacentEdges.forEach( edge => {
              let targetEdge = enhancedMesh .edges .find( e => e.centroid.equals( edge .centroid ) );
              targetFace.adjacentEdges.push(targetEdge);
              targetEdge.adjacentFaces.push(targetFace);
            });
            targetFace.centroid = getCentroid( targetFace .adjacentEdges .map( e => e .centroid ) );
            enhancedMesh.faces.push(targetFace);
          }

          cell.adjacentFaces.push(targetFace);
          targetFace.adjacentCells.push(cell);
          
          // Check all edges of this face
          currentFace.adjacentEdges.forEach( edge => {
            
            // Find neighboring faces that share this edge
            edge.adjacentFaces.forEach( (face) => {
              if (!visited.has(face.index)) {
                visited.add(face.index);
                queue.push(face.index);
              }
            });
          });
        }
        
        if (cell.adjacentFaces.length > 0) {
          enhancedMesh.cells.push(cell);
        }
      });

    } );
}

Promise.all( [ initialize(), fetchUrlJSON( "./CRF.mesh.json" ) ] )
  .then( async ( [ api, simpleMesh ] ) =>
  {
    const enhancedMesh = api .enhanceTopologicalMesh( simpleMeshToTopologicalMesh( simpleMesh ) );

    // Now, install all the cells
    for (const cellSet of cellSets) {
      await installCells( enhancedMesh, cellSet, api );
    }

    const topologicalMesh = {
      field   : simpleMesh.field,
      vertices: simpleMesh.vertices,
      edges   : simpleMesh.edges,
      faces   : enhancedMesh.faces.map( face => face.adjacentEdges .map( edge => edge.index ) ),
      cells   : enhancedMesh.cells.map( cell => cell.adjacentFaces .map( face => face.index ) ),
    };

    downloadJSON( topologicalMesh, 'CRF.tmesh.json' );
  }
);