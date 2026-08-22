
import { simplify3, createField } from './common.js'

const PHI = 0.5 * (Math.sqrt(5) + 1)

const MAX_SAFE = Number.MAX_SAFE_INTEGER;

function reciprocal( a )
{
  if ( typeof a[0] !== 'bigint' ) {
    // Number path
    const [ a0=0, a1=0, ad=1 ] = a;
    // (a0 + a1)*ad, -a1*ad, a0*a0 + a0*a1 - a1*a1
    const s = a0 + a1;
    const r0 = s * ad, r1 = -a1 * ad, det = a0*a0 + a0*a1 - a1*a1;
    if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE && det >= -MAX_SAFE && det <= MAX_SAFE )
      return simplify3( r0, r1, det );
  }
  const a0 = BigInt(a[0] || 0), a1 = BigInt(a[1] || 0), ad = BigInt(a[2] || 1);
  return simplify3( (a0 + a1)*ad, 0n - a1*ad, a0*a0 + a0*a1 - a1*a1 )
}

function times( a, b )
{
  if ( typeof a[0] !== 'bigint' && typeof b[0] !== 'bigint' ) {
    const [ a0=0, a1=0, ad=1 ] = a, [ b0=0, b1=0, bd=1 ] = b;
    if ( a0 === 0 && a1 === 0 ) return [ 0, 0, 1 ];
    if ( b0 === 0 && b1 === 0 ) return [ 0, 0, 1 ];
    if ( a1 === 0 ) {
      // integer * golden: [a0*b0, a0*b1, ad*bd]
      const r0 = a0*b0, r1 = a0*b1, d = ad*bd;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE && d >= -MAX_SAFE && d <= MAX_SAFE )
        return simplify3( r0, r1, d );
    } else if ( b1 === 0 ) {
      // golden * integer: [a0*b0, a1*b0, ad*bd]
      const r0 = a0*b0, r1 = a1*b0, d = ad*bd;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE && d >= -MAX_SAFE && d <= MAX_SAFE )
        return simplify3( r0, r1, d );
    } else {
      // general case: a0*b0 + a1*b1, a0*b1 + a1*b0 + a1*b1, ad*bd
      const t0a = a0*b0, t0b = a1*b1;
      const t1a = a0*b1, t1b = a1*b0;
      const d = ad*bd;
      const r0 = t0a + t0b, r1 = t1a + t1b + t0b;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE && d >= -MAX_SAFE && d <= MAX_SAFE
        && t0a >= -MAX_SAFE && t0a <= MAX_SAFE && t0b >= -MAX_SAFE && t0b <= MAX_SAFE
        && t1a >= -MAX_SAFE && t1a <= MAX_SAFE && t1b >= -MAX_SAFE && t1b <= MAX_SAFE )
        return simplify3( r0, r1, d );
    }
  }
  const a0 = BigInt(a[0] || 0), a1 = BigInt(a[1] || 0), ad = BigInt(a[2] || 1);
  const b0 = BigInt(b[0] || 0), b1 = BigInt(b[1] || 0), bd = BigInt(b[2] || 1);
  return simplify3( a0*b0 + a1*b1, a0*b1 + a1*b0 + a1*b1, ad*bd )
}

function embed( a )
{
  const a0 = Number(a[0] || 0), a1 = Number(a[1] || 0), ad = Number(a[2] || 1);
  return ( a0 + PHI * a1 ) / ad
}

// All of this code for goldenSeries, etc. came from https://observablehq.com/@vorth/finding-golden-numbers

const replacement = item => (item === "1")? [ "φ" ] : [ "1", "φ" ]

const symbolSequence = ( depth ) =>
{
  function recurrence( arr ){
    return arr.reduce( (acc, item) => {
      return [ ...acc, ...replacement( item ) ]
    }, [] )
  }
  var seq = [ "1" ]
  for (var i = 0; i < depth; i++) {
    seq = recurrence( seq )
  }
  return seq
}

const goldenSequence = ( depth ) =>
{
  return symbolSequence( depth ).map( symbol => ( ( symbol === "1" )? [1,0,1] : [0,1,1] ) )
}

const goldenSeries = ({ plus }) => depth =>
{
  return goldenSequence( depth ).reduce( function( r, a ) {
    if (r.length > 0)
      a = plus( a, r[r.length - 1] );
    r.push( a );
    return r;
  }, [])
}

const createQuaternions = ({ scalarmul, quatmul }) =>
{
  function grsign( a )
  {
    const a0 = Number(a[0] || 0), a1 = Number(a[1] || 0), ad = Number(a[2] || 1);
    const float = a0 + PHI * a1;
    return ((ad>0)-(ad<0)) * ((float>0)-(float<0))
  }

  function quatnormalize(q)
  {
    return scalarmul( [ q.map( grsign ).reduce( (a, b) => a || b ) || 1 ], q )
  }

  const one = [[1],[0],[0],[0]], h = [1,0,2], blue = [one, [[0],[1],[0],[0]], [[0],[0],[0],[1]], [[0],[0],[1],[0]]],
    yellow = [one, [h, h, h, h], [[-1,0,2], h, h, h]], red = [one, [[0,1,2], h, [-1,1,2],]]
  for ( let i = 2; i < 5; i++ )
    red[i] = quatmul( red[i-1], red[1] )
  const vZomeIcosahedralQuaternions = []; let b, r, y
  for (b of blue) for (r of red) for (y of yellow)
    vZomeIcosahedralQuaternions.push( quatnormalize( quatmul( b, quatmul( y, r ) ) ) )

  return vZomeIcosahedralQuaternions
}

const getIrrational = () => 'φ';

const baseField = createField( { name: 'golden', order: 2, times, embed, reciprocal, getIrrational } )

const field = {
  ...baseField,
  goldenRatio: [ 0, 1, 1 ],
  quaternions: createQuaternions( baseField ),
  goldenSeries: goldenSeries( baseField )
}

export default field
