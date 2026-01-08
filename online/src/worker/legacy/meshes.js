
import { com } from './core-java.js'

const getCentroid = com.vzome.core.algebra.AlgebraicVectors.getCentroid;

/*
  A simple mesh has vertices, edges, and faces, and each face lists its vertex indices.
  There is no guarantee that all edges implied by the faces actually exist as edges in the mesh.

  A colored mesh has vertices, balls, struts, and panels.  Each ball has a vertex index;
  each strut or panel has a list of vertex indices.  There is no guarantee that struts
  exist for all edges implied by the panels, or balls for all strut or panel vertices.

  A topological mesh has vertices, edges, faces, and cells.  Each object lists indices of its
  constituent lower-dimensional objects, so there is no possibility of implied-but-missing elements.

  An *enhanced* topological mesh has vertices, edges, faces, and cells.  Each object lists its adjacent
  lower- and higher-dimensional objects.  For example, each face lists its adjacent edges
  and its adjacent cells; each edge lists its endpoint vertices and its adjacent faces.
  These lists are NOT indices, but references to the actual objects.
  Every object also has a centroid.
  The mesh also has a field object, and each vertex has a vector in that field.
*/



const faceVerticesToFace = ( vertexIndices, index, edges, vertices ) =>
{
  const face = { index, adjacentEdges: [], adjacentCells: [] };
  
  for (let i = 0; i < vertexIndices.length; i++) {
    const v1 = vertexIndices[i];
    const v2 = vertexIndices[(i + 1) % vertexIndices.length]; // Wrap around to first vertex
        
    // Find the edge that connects v1 and v2
    const edge = edges.find( ( { endpoints } ) => 
      (endpoints[0].index === v1 && endpoints[1].index === v2) ||
      (endpoints[0].index === v2 && endpoints[1].index === v1)
    );
    
    if (edge !== undefined) {
      face .adjacentEdges .push( edge );
      edge .adjacentFaces .push( face );
    }
  }

  face.centroid = getCentroid( face .adjacentEdges .map( e => e .centroid ) );
  
  return face;
};

export const coloredMeshToSimpleMesh = ( coloredMesh ) =>
{
  // Convert a colored mesh (vertices, balls, struts, panels) to a simple mesh (vertices, edges, faces)
  // Note: Color information is discarded in the conversion
  
  const simpleMesh = {
    field: coloredMesh.field,
    vertices: coloredMesh.vertices,
    edges: [],
    faces: []
  };
  
  // Convert struts to edges (each strut becomes an edge with two vertex indices)
  if ( coloredMesh.struts ) {
    simpleMesh.edges = coloredMesh.struts.map( strut => strut.vertices );
  }
  
  // Convert panels to faces (each panel's vertex list becomes a face)
  if ( coloredMesh.panels ) {
    simpleMesh.faces = coloredMesh.panels.map( panel => panel.vertices );
  }
  
  return simpleMesh;
};

export const simpleMeshToTopologicalMesh = ( simpleMesh ) =>
{
  // Convert a simple mesh (faces with vertex indices) to a topological mesh (faces with edge indices)
  // In a topological mesh, each object lists indices of its constituent lower-dimensional objects
  
  const topologicalMesh = {
    field: simpleMesh.field,
    vertices: simpleMesh.vertices,
    edges: [],
    faces: [],
    cells: []
  };
  
  // Build a map to track edges by their vertex pairs (normalize order to handle both directions)
  const edgeMap = new Map();
  const getEdgeKey = (v1, v2) => v1 < v2 ? `${v1},${v2}` : `${v2},${v1}`;
  
  // First, add any existing edges from the simple mesh
  if ( simpleMesh.edges ) {
    simpleMesh.edges.forEach( ([v1, v2]) => {
      const key = getEdgeKey(v1, v2);
      if ( !edgeMap.has(key) ) {
        const edgeIndex = topologicalMesh.edges.length;
        topologicalMesh.edges.push( [v1, v2] );
        edgeMap.set(key, edgeIndex);
      }
    });
  }
  
  // Convert faces: each face's vertex indices become edge indices
  if ( simpleMesh.faces ) {
    simpleMesh.faces.forEach( vertexIndices => {
      const edgeIndices = [];
      
      for (let i = 0; i < vertexIndices.length; i++) {
        const v1 = vertexIndices[i];
        const v2 = vertexIndices[(i + 1) % vertexIndices.length]; // Wrap around to first vertex
        const key = getEdgeKey(v1, v2);
        
        // Get existing edge or create a new one
        let edgeIndex = edgeMap.get(key);
        if ( edgeIndex === undefined ) {
          edgeIndex = topologicalMesh.edges.length;
          topologicalMesh.edges.push( [v1, v2] );
          edgeMap.set(key, edgeIndex);
        }
        
        edgeIndices.push(edgeIndex);
      }
      
      topologicalMesh.faces.push(edgeIndices);
    });
  }
  
  return topologicalMesh;
};

const enhanceFace = ( edgeIndices, index, edges ) =>
{
  const face = { index, adjacentEdges: [], adjacentCells: [] };
  
  edgeIndices .forEach( edgeIndex =>
  {
    const edge = edges[ edgeIndex ];
    face .adjacentEdges .push( edge );
    edge .adjacentFaces .push( face );
  } );

  face.centroid = getCentroid( face .adjacentEdges .map( e => e .centroid ) );
  
  return face;
}

const enhanceCell = ( faceIndices, index, faces ) =>
{
  const cell = { index, adjacentFaces: [] };
  
  faceIndices .forEach( faceIndex =>
  {
    const face = faces[ faceIndex ];
    cell .adjacentFaces .push( face );
    face .adjacentCells .push( cell );
  } );

  cell.centroid = getCentroid( cell .adjacentFaces .map( f => f .centroid ) );
  
  return cell;
}

export const enhanceTopologicalMesh = ( getField ) => ( topoMesh ) =>
{
  const field = getField( topoMesh .field );

  const vertices = topoMesh.vertices.map( (nums,index) => {
    let vector = field.createVectorFromTDs( nums );
    return { index, vector, adjacentEdges: [] };
  } );
  const edges = topoMesh.edges.map( (strut,index) => {
    const v1 = vertices[ strut[ 0 ] ];
    const v2 = vertices[ strut[ 1 ] ];
    const edge = { index, endpoints: [ v1, v2 ], adjacentFaces: [] };
    edge.centroid = getCentroid( [ v1.vector, v2.vector ] );
    v1 .adjacentEdges .push( edge );
    v2 .adjacentEdges .push( edge );
    return edge;
  } );
  const faces = ( topoMesh .faces || [] ) .map( ( edgeIndices, index ) => enhanceFace( edgeIndices, index, edges ) );
  const cells = ( topoMesh .cells || [] ) .map( ( faceIndices, index ) => enhanceCell( faceIndices, index, faces ) );

  return { field, vertices, edges, faces, cells };
}

export const enhanced4dToTopologicalMesh = ( enhancedMesh ) =>
{
  // Extract only the 4D faces, edges, and vertices from the enhanced mesh,
  // and build a topological mesh for download.

  const cellSet = new Set();
  const faceSet = new Set();
  const edgeSet = new Set();
  const vertexSet = new Set();
  enhancedMesh .cells .forEach( cell => {
    if ( cell .is4D ) {
      cellSet .add( cell );
      cell .adjacentFaces .forEach( face => {
        if ( face .is4D ) {
          faceSet .add( face );
          face .adjacentEdges .forEach( edge => {
            edgeSet .add( edge );
            edge .endpoints .forEach( vertex => {
              vertexSet .add( vertex );
            } );
          } );
        }
      } );
    }
  } );

  // Now, assign new indices to the vertices, edges, faces, and cells
  const vertexArray = Array .from( vertexSet );
  vertexArray .forEach( ( vertex, index ) => {
    vertex .newIndex = index;
  } );
  const edgeArray = Array .from( edgeSet );
  edgeArray .forEach( ( edge, index ) => {
    edge .newIndex = index;
  } );
  const faceArray = Array .from( faceSet );
  faceArray .forEach( ( face, index ) => {
    face .newIndex = index;
  } );
  const cellArray = Array .from( cellSet );
  cellArray .forEach( ( cell, index ) => {
    cell .newIndex = index;
  } );

  const topologicalMesh = {
    field: enhancedMesh .field .getName(),
    vertices: [],
    edges: [],
    faces: [],
    cells: []
  };
  vertexArray .forEach( vertex => {
    const vec4d = vertex .vector4d;
    topologicalMesh .vertices .push(
      [ vec4d .getComponent( 0 ) .toTrailingDivisor(),
        vec4d .getComponent( 1 ) .toTrailingDivisor(),
        vec4d .getComponent( 2 ) .toTrailingDivisor(),
        vec4d .getComponent( 3 ) .toTrailingDivisor() ] ); // w, x, y, z,
    } );
  edgeArray .forEach( edge => {
    const vIndices = edge .endpoints .map( vertex => vertex .newIndex );
    topologicalMesh .edges .push( vIndices );
  } );
  faceArray .forEach( face => {
    const edgeIndices = face .adjacentEdges .map( edge => edge .newIndex );
    topologicalMesh .faces .push( edgeIndices );
  } );
  cellArray .forEach( cell => {
    const faceIndices = cell .adjacentFaces .map( face => face .newIndex );
    topologicalMesh .cells .push( faceIndices );
  } );
  
  return topologicalMesh;
}

// Extract only the 4D faces, edges, and vertices from the enhanced mesh,
// and build a simple mesh for download.
export const enhanced4dToSimpleMesh = ( enhancedMesh ) =>
{
  const faceSet = new Set();
  const edgeSet = new Set();
  const vertexSet = new Set();
  enhancedMesh .faces .forEach( face => {
    if ( face .is4D ) {
      faceSet .add( face );
      face .adjacentEdges .forEach( edge => {
        edgeSet .add( edge );
        edge .endpoints .forEach( vertex => {
          vertexSet .add( vertex );
        } );
      } );
    }
  } );

  // Now, assign new indices to the vertices, edges, and faces
  const vertexArray = Array .from( vertexSet );
  vertexArray .forEach( ( vertex, index ) => {
    vertex .newIndex = index;
  } );
  const edgeArray = Array .from( edgeSet );
  edgeArray .forEach( ( edge, index ) => {
    edge .newIndex = index;
  } );
  const faceArray = Array .from( faceSet );
  faceArray .forEach( ( face, index ) => {
    face .newIndex = index;
  } );

  // Now, build the simple mesh
  const inflatedSimpleMesh = { field: enhancedMesh .field .getName(), vertices: [], edges: [], faces: [] };
  vertexArray .forEach( vertex => {
    const vec4d = vertex .vector4d;
    inflatedSimpleMesh .vertices .push(
      [ vec4d .getComponent( 1 ) .toTrailingDivisor(),
        vec4d .getComponent( 2 ) .toTrailingDivisor(),
        vec4d .getComponent( 3 ) .toTrailingDivisor(),
        vec4d .getComponent( 0 ) .toTrailingDivisor() ] ); // x, y, z, w
  } );
  edgeArray .forEach( edge => {
    const vIndices = edge .endpoints .map( vertex => vertex .newIndex );
    inflatedSimpleMesh .edges .push( vIndices );
  } );
  faceArray .forEach( face => {
    // first, build a set of all vertices adjacent to the edges of this face
    const uniqueVIndices = [];
    const vIndexSet = new Set();
    face .adjacentEdges .forEach( edge => {
      edge .endpoints .forEach( vertex => {
        if ( ! vIndexSet .has( vertex .newIndex ) ) {
          vIndexSet .add( vertex .newIndex );
          uniqueVIndices .push( vertex .newIndex );
        }
      } );
    } );
    // Now, pick a starting vertex, and follow the edges around to order the vertices correctly
    const orderedVIndices = [];
    let currentVIndex = uniqueVIndices[0];
    orderedVIndices .push( currentVIndex );
    while ( orderedVIndices .length < uniqueVIndices .length ) {
      // find an edge that includes currentVIndex and leads to a new vertex
      let foundNext = false;
      for ( const edge of face .adjacentEdges ) {
        const v1 = edge .endpoints[0] .newIndex;
        const v2 = edge .endpoints[1] .newIndex;
        if ( v1 === currentVIndex && ! orderedVIndices .includes( v2 ) ) {
          orderedVIndices .push( v2 );
          currentVIndex = v2;
          foundNext = true;
          break;
        } else if ( v2 === currentVIndex && ! orderedVIndices .includes( v1 ) ) {
          orderedVIndices .push( v1 );
          currentVIndex = v1;
          foundNext = true;
          break;
        }
      }
      if ( ! foundNext ) {
        console .error( "Could not find next vertex in face!" );
        break;
      }
    }
    inflatedSimpleMesh .faces .push( orderedVIndices );
  } );
  return inflatedSimpleMesh;
}

