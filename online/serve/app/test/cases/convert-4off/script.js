
const values_old = [
  { real: 0.353553390, algebraic: [ 1, 0, 1 ] },
  { real: 1.060660171, algebraic: [ 3, 0, 1 ] },
]

// These values are for the compound of 20 octahedra, which uses the sqrtPhi field.
const field = "sqrtPhi";
const values = [
  { real: 0.0000000, algebraic: [0,0,0,0,1]   }, // (phi - k) * k^3
  { real: 0.1429011, algebraic: [-2,-1,1,1,1] }, // (phi - k) * k^3
  { real: 0.1817730, algebraic: [1,-2,0,1,1]  }, // (phi - k) * k^2
  { real: 0.2312188, algebraic: [1,1,-1,0,1]  }, // (phi - k) * k
  { real: 0.4129918, algebraic: [2,-1,-1,1,1] }, // (phi + k) * k^3
  { real: 0.4370160, algebraic: [-2,0,2,0,1]  }, // 2k^2
  { real: 0.5253337, algebraic: [1,2,0,-1,1]  }, // (phi + k) * k^2
  { real: 0.5558929, algebraic: [0,-2,0,2,1]  }, // 2k
  { real: 0.6682348, algebraic: [-1,1,1,0,1]  }, // (phi + k) * k
];

const DELTA = 0.0000001;

const negative = tdiv => tdiv .map( (x, i) => i === tdiv.length-1? x : -x );

const realToAlgebraic = real => {
  const negate = real < -DELTA;
  const alg = values .filter( value => Math.abs( value.real - Math.abs( real ) ) < DELTA ) .pop() .algebraic;
  return negate? negative( alg ) : alg;
}

const toNumArray = line => line .split( ' ' ) .map( str => Number(str) );

const interpret4OFF = lines =>
{
  // skip "OFF" or "4OFF" or whatever at lines[ 0 ]
  const [ vertexCount, faceCount, edgeCount, cellCount ] = toNumArray( lines[ 1 ] );
  let start = 2;
  const vertices = [];
  for ( var i = 0; i < vertexCount; i++ ) {
    const data = toNumArray( lines[ i + start ] );
    vertices .push( data .map( real => realToAlgebraic( real ) ) );
  }
  start += vertexCount;
  const edges = [];
  const faces = [];
  for ( var i = 0; i < faceCount; i++ ) {
    const data = toNumArray( lines[ i + start ] );
    const arity = data[ 0 ];
    if ( arity === 2 ) {
      edges .push( data .slice( 1, 3 ) );       // ignore any color data
    } else if ( arity >= 3 )
      faces .push( data .slice( 1, arity+1 ) ); // ignore any color data
  }
  start += faceCount;
  const cells = [];
  if ( cellCount ) {
    for ( var i = 0; i < cellCount; i++ ) {
      const data = toNumArray( lines[ i + start ] );
      const arity = data[ 0 ];
      cells .push( data .slice( 1, arity+1 ) );
    }
  }
  // Output simple mesh JSON (4D); vZome can import it and project to 3D.
  const outputEl = document .getElementById( "output" );
  outputEl .innerHTML = JSON .stringify( { field, vertices, edges, faces, cells } ); // not formatting, too much whitespace
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
