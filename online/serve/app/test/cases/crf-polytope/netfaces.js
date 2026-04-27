// Vibe coded with Claude Sonnet 4.5, prompt:
//
// Given the net of a polyhedron as a 2D graph with just edge and vertex
// connectivity, is there an algorithm to recover the faces of the polyhedron?
//
// Response:
// Yes, there are several algorithms to recover faces from a planar graph.
// The most common approach is to find all minimal cycles in the graph.
//
// Algorithm: Face Traversal (also called "turning left at every vertex")
//
// For each edge, traverse both directions and always turn to the next edge
// that makes the smallest counter-clockwise angle. This finds all faces
// including the outer face.
//
// This algorithm finds all faces by traversing the boundary of each
// face counter-clockwise. It's commonly used in computational geometry for
// planar graphs.

export const findFaces = ( edges ) => {
  const faces = [];
  const usedEdges = new Set(); // Track each directed edge
  
  // For each edge, try traversing in both directions
  edges.forEach( edge => {
    [0, 1].forEach( direction => {
      const startVertex = edge.endpoints[direction];
      const edgeKey = `${edge.index}-${direction}`;
      
      if (usedEdges.has(edgeKey)) return;
      
      const face = [];
      let currentVertex = startVertex;
      let currentEdge = edge;
      let currentDirection = direction;
      
      do {
        // Mark this directed edge as used
        usedEdges.add(`${currentEdge.index}-${currentDirection}`);
        face.push(currentVertex.index);
        
        // Move to next vertex
        const nextVertex = currentEdge.endpoints[1 - currentDirection];
        
        // Find the next edge by turning left (smallest counter-clockwise angle)
        const nextEdge = findNextEdgeCounterClockwise(
          nextVertex, 
          currentVertex, 
          edges
        );
        
        // Determine direction for next edge
        currentDirection = nextEdge.endpoints[0] === nextVertex ? 0 : 1;
        currentVertex = nextVertex;
        currentEdge = nextEdge;
        
      } while (currentVertex !== startVertex);
      
      if (face.length >= 3) {
        faces.push(face);
      }
    });
  });
  
  return faces;
};

const findNextEdgeCounterClockwise = ( vertex, prevVertex, edges ) => {
  const adjacentEdges = vertex.adjacentEdges.map(i => edges[i]);
  
  // Calculate angles for each adjacent edge
  const edgesWithAngles = adjacentEdges.map( edge => {
    const neighbor = edge.endpoints[0] === vertex ? 
                     edge.endpoints[1] : edge.endpoints[0];
    
    if (neighbor === prevVertex) return null; // Skip the edge we came from
    
    const angle = calculateAngle(vertex.vector, prevVertex.vector, neighbor.vector);
    return { edge, angle };
  }).filter(x => x !== null);
  
  // Find edge with smallest positive angle (leftmost turn)
  edgesWithAngles.sort((a, b) => a.angle - b.angle);
  return edgesWithAngles[0].edge;
};

const calculateAngle = ( center, from, to ) => {
  // Calculate counter-clockwise angle from 'from' to 'to' around 'center'
  const v1x = from.getComponent(0).evaluate() - center.getComponent(0).evaluate();
  const v1y = from.getComponent(1).evaluate() - center.getComponent(1).evaluate();
  const v2x = to.getComponent(0).evaluate() - center.getComponent(0).evaluate();
  const v2y = to.getComponent(1).evaluate() - center.getComponent(1).evaluate();
  
  let angle = Math.atan2(v2y, v2x) - Math.atan2(v1y, v1x);
  if (angle < 0) angle += 2 * Math.PI;
  return angle;
};