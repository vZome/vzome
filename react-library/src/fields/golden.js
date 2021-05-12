
import { simplify3, createField } from './common.js'

const PHI = 0.5 * (Math.sqrt(5) + 1)

function reciprocal( a )
{
  const [a0=0n, a1=0n, ad=1n] = a;
  return simplify3( (a0 + a1)*ad, 0n - a1*ad, a0*a0 + a0*a1 - a1*a1 )
}

function times( a, b )
{
  const [ a0=0n, a1=0n, ad=1n ] = a, [ b0=0n, b1=0n, bd=1n ] = b
  return simplify3( a0*b0 + a1*b1, a0*b1 + a1*b0 + a1*b1, ad*bd )
}

function embed( a )
{
  const [ a0=0n, a1=0n, ad=1n ] = a
  return ( Number(a0) + PHI * Number(a1) ) / Number(ad)
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
  return symbolSequence( depth ).map( symbol => ( ( symbol === "1" )? [1n,0n,1n] : [0n,1n,1n] ) )
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

const createQuaternions = ({ scalarmul, quatmul}) =>
{
  function grsign( a )
  {
    const [a0=0n, a1=0n, ad=1n] = a, float = Number(a0) + PHI * Number(a1)
    return ((ad>0n)-(ad<0n)) * ((float>0)-(float<0))
  }

  function quatnormalize(q)
  {
    return scalarmul( [ q.map( grsign ).reduce( (a, b) => a || b ) || 1n ], q )
  }

  const one = [[1n],[0n],[0n],[0n]], h = [1n,0n,2n], blue = [one, [[0n],[1n],[0n],[0n]], [[0n],[0n],[0n],[1n]], [[0n],[0n],[1n],[0n]]],
    yellow = [one, [h, h, h, h], [[-1n,0n,2n], h, h, h]], red = [one, [[0n,1n,2n], h, [-1n,1n,2n],]]
  for ( let i = 2; i < 5; i++ )
    red[i] = quatmul( red[i-1], red[1] )
  const vZomeIcosahedralQuaternions = []; let b, r, y
  for (b of blue) for (r of red) for (y of yellow)
    vZomeIcosahedralQuaternions.push( quatnormalize( quatmul( b, quatmul( y, r ) ) ) )

  return vZomeIcosahedralQuaternions
}

const baseField = createField( { name: 'golden', order: 2, times, embed, reciprocal } )

const field = {
  ...baseField,
  goldenRatio: [ 0n, 1n, 1n ],
  // quaternions: createQuaternions( baseField ),
  goldenSeries: goldenSeries( baseField )
}

export default field
