
const values = [
  { real: 0.353553390593274, algebraic: [ 1, 0, 1 ] },
  { real: 1.06066017177982,  algebraic: [ 3, 0, 1 ] },
  { real: -0.353553390593274, algebraic: [ -1, 0, 1 ] },
  { real: -1.06066017177982,  algebraic: [ -3, 0, 1 ] },
]

const realToAlgebraic = real => values .filter( value => Math.abs( value.real - real ) < 0.000000001 ) .pop() .algebraic;

const toNumArray = line => line .split( ' ' ) .map( str => Number(str) );

const interpret4OFF = lines =>
{
  // skip "4OFF" at lines[ 0 ]
  const [ vertexCount, faceCount, edgeCount, cellCount ] = toNumArray( lines[ 1 ] );
  let start = 2;
  const vertices = [];
  for ( var i = 0; i < vertexCount; i++ ) {
    const data = toNumArray( lines[ i + start ] );
    vertices .push( data .map( real => realToAlgebraic( real ) ) );
  }
  start += vertexCount;
  const faces = [];
  for ( var i = 0; i < faceCount; i++ ) {
    const data = toNumArray( lines[ i + start ] );
    const arity = data[ 0 ];
    faces .push( data .slice( 1, arity+1 ) );
  }
  start += faceCount;
  const cells = [];
  for ( var i = 0; i < cellCount; i++ ) {
    const data = toNumArray( lines[ i + start ] );
    const arity = data[ 0 ];
    cells .push( data .slice( 1, arity+1 ) );
  }
  // Output simple mesh JSON (4D); vZome can import it and project to 3D.
  console.log( JSON .stringify( { field: "golden", edges: [], vertices, faces, cells } ) );
}

document .getElementById( 'file' ) .onchange = function()
{
  var file = this.files[0];
  var reader = new FileReader();
  reader.onload = function()
  {
    var lines = this.result .split( /\r\n|\n/ ) .filter( line => line.trim() && ! line.startsWith( '#' ) );
    interpret4OFF( lines );
  };
  reader .readAsText( file );
};
