

// Most of this code is from Jacob Rus: https://observablehq.com/@jrus/zome-arithmetic

const SQRT2 = Math.sqrt(2)

function gcd( a, b )
{
  a = Math.abs(a)
  b = Math.abs(b)
  while ( b !== 0 ) [a, b] = [b, a % b]
  return a
}

function simplify3( v0, v1, v2 )
{
  if ((v0|0) !== v0 || (v1|0) !== v1 || (v2|0) !== v2)
    throw new TypeError( `simplify3 operates only on integers... ${v0} ${v1} ${v2}` )
  let s0 = (v0>0)-(v0<0), s1 = (v1>0)-(v1<0), s2 = (v2>0)-(v2<0) // signs
  v0 *= s0
  v1 *= s1
  v2 *= s2
  s0 *= s2
  s1 *= s2
  const g = gcd(gcd(v0, v1), v2)
  return [s0*v0/g, s1*v1/g, v2/g]
}

function reciprocal( x )
{
  const [ a=0, b=0, c=1 ] = x;
  return simplify3( a*c, 0 - b*c, a*a - 2*b*b )
}

function times( a, b )
{
  const [ a0=0, a1=0, ad=1] = a, [b0=0, b1=0, bd=1 ] = b
  return simplify3( a0*b0 + 2*a1*b1, a0*b1 + a1*b0, ad*bd )
}

function plus( a, b )
{
  const [ a0=0, a1=0, ad=1] = a, [b0=0, b1=0, bd=1 ] = b
  return simplify3( a0*bd + b0*ad, a1*bd + b1*ad, ad*bd )
}

function minus( a, b )
{
  const [ a0=0, a1=0, ad=1] = a, [b0=0, b1=0, bd=1 ] = b
  return simplify3( a0*bd - b0*ad, a1*bd - b1*ad, ad*bd )
}

function scalarmul( s, v )
{
  return [ ...v.values() ].map( ( vi=[0] ) => times( s, vi ) )
}

function negate( a )
{
  const [ a0=0, a1=0, ad=1 ] = a
  return [ 0 - a0, 0 - a1, ad ]
}

function vectoradd( u, v )
{
  return [ ...u.values() ].map( ( ui=[0], i ) => plus( ui, v[i] || [0] ) )
}

function embed( a )
{
  const [ a0=0, a1=0, ad=1 ] = a
  return ( a0 + SQRT2 * a1 ) / ad
}

function createRationalFromPairs( pairs )
{
  const [ a0=0, d0=1, a1=0, d1=1 ] = pairs
  return simplify3( a0*d1, a1*d0, d0*d1 )
}

function quatmul( u, v )
{
  const x = times, a = plus, s = minus,
    [u0=[0], u1=[0], u2=[0], u3=[0]] = u,
    [v0=[0], v1=[0], v2=[0], v3=[0]] = v
  return [
    s(x(u0, v0), a(a(x(u1, v1), x(u2, v2)), x(u3, v3))),
    a(a(x(u0, v1), x(u1, v0)), s(x(u2, v3), x(u3, v2))),
    a(a(x(u0, v2), x(u2, v0)), s(x(u3, v1), x(u1, v3))),
    a(a(x(u0, v3), x(u3, v0)), s(x(u1, v2), x(u2, v1)))]
}

// Everything here uses a W-first representation of quaternions.
//   These quaternions must be transformed to W-last when using with three.js.

function quatconj( [u0=[0], u1=[0], u2=[0], u3=[0]] )
{
  return [ u0, negate(u1), negate(u2), negate(u3) ]
}

function quatTransform( Q, v )
{
  return quatmul( Q, quatmul( [[0]].concat(v), quatconj(Q)) ).slice(1)
}


const origin = dimensions =>
{
  const result = []
  for ( let index = 0; index < dimensions; index++ ) {
    result.push( [ 0, 0, 1 ] )
  }
  return result
}


const field = {

  name: 'rootTwo',

  order: 2,
  scalarTerm: 1,

  zero: [ 0, 0, 1 ],
  one: [ 1, 0, 1 ],
  sqrt2: [ 0, 1, 1 ],

  origin,

  plus, minus, times, scalarmul, vectoradd, embed, reciprocal, negate, createRationalFromPairs, quatTransform,

  embedv: (v) => v.map( embed ),

  createRational: ( n, d ) =>
  {
    return simplify3( n, 0, d )
  },

}

export default field
