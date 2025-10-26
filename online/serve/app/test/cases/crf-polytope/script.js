
import { initialize, vzomePkg, util } from "/modules/vzome-legacy.js";

const fetchUrlText = async ( url ) =>
{
  let response;
  try {
    response = await fetch( url )
  } catch ( error ) {
    console.log( `Fetching ${url} failed with "${error}"; trying cors-anywhere` )
    // TODO: I should really deploy my own copy of this proxy on Heroku
    response = await fetch( 'https://cors-anywhere.herokuapp.com/' + url )
  }
  if ( !response.ok ) {
    throw new Error( `Failed to fetch "${url}": ${response.statusText}` )
  }
  return response.text()
}

const findVertex = ( index, vertices ) => vertices .find( vtx => vtx .index === index );

initialize() .then( api => {

  fetchUrlText( "./dodec-projection.mesh.json" )
  .then( text => {

    const simpleMesh = JSON.parse( text );
    const field = api .getField( simpleMesh .field );

    const vertexSet = new util.HashSet();
    const vertices = simpleMesh.vertices.map( (nums,index) => {
      let vector = field.createVectorFromTDs( nums );
      vertexSet .add( vector );
      return { index, vector, adjacentEdges: [] };
    } );
    const edges = simpleMesh.edges.map( (strut,index) => {
      const v1 = vertices[ strut[ 0 ] ];
      const v2 = vertices[ strut[ 1 ] ];
      v1 .adjacentEdges .push( index );
      v2 .adjacentEdges .push( index );
      return { index, endpoints: [ v1, v2 ] };
    } );

    console.log( 'projection loaded' );

    // First, find the faces of the projected polytope.  These will be cycles of vertices that
    //   equal their convex hull.  We will need these faces to enforce planarity of their pre-images.

    
    // Graph created, now find the convex hull of the vertices
    const hull2d = vzomePkg.core.math.convexhull.GrahamScan2D.buildHull( vertexSet );
    // Now recover the vertex corresponding to each hull point
    const hullVertices = hull2d .map( vector => {
      return vertices .find( vtx => vector .equals( vtx .vector  ) ) ;
    } );

    console.log( 'convex hull computed' );

    // Now, find edges that join two hull vertices
    //   and differ only in X or only in Y.  These will be the blue struts.
    const blueEdges = edges .filter( edge => {
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

  } );
});

