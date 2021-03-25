

// Most of this code is from Jacob Rus: https://observablehq.com/@jrus/zome-arithmetic

const RHO = 1.8019377358
const SIGMA = 2.2469796037

function simplify4( v0, v1, v2, v3 )
{
  if ((v0|0) !== v0 || (v1|0) !== v1 || (v2|0) !== v2 || (v3|0) !== v3)
    throw new TypeError( `simplify4 operates only on integers... ${v0} ${v1} ${v2} ${v3}` )
  let s0 = (v0>0)-(v0<0), s1 = (v1>0)-(v1<0), s2 = (v2>0)-(v2<0), s3 = (v3>0)-(v3<0) // signs
  v0 *= s0
  v1 *= s1
  v2 *= s2
  v3 *= s3
  s0 *= s3
  s1 *= s3
  s2 *= s3
  const g = gcd(gcd(gcd(v0, v1), v2), v3)
  return [ s0*v0/g, s1*v1/g, s2*v2/g, v3/g ]
}

// Thanks to Nan Ma for getting the matrix inverse of
//     A     B     C
//     B    A+C   B+C
//     C    B+C  A+B+C
function reciprocal( x )
{
  const [ A=0, B=0, C=0, D=1 ] = x
  const A1 = A*A + A*B + 2*A*C - B*B - B*C
  const B1 = C*C - A*B - B*B
  const C1 = B*B + B*C - A*C - C*C
  const determinant = A*A1 + B*B1 + C*C1
  return simplify4( D*A1, D*B1, D*C1, determinant )
}

function times( first, second )
{
  const [ a=0, b=0, c=0, d1=1 ] = first, [ d=0, e=0, f=0, d2=1 ] = second
  return simplify4( a*d + b*e + c*f, a*e + b*d + b*f + c*e + c*f, a*f + b*e + b*f + c*d + c*e + c*f, d1*d2 )
}

function plus( a, b )
{
  const [ a0=0, a1=0, a2=0, ad=1 ] = a, [ b0=0, b1=0, b2=0, bd=1 ] = b
  return simplify4( a0*bd + b0*ad, a1*bd + b1*ad, a2*bd + b2*ad, ad*bd )
}

function minus( a, b )
{
  const [ a0=0, a1=0, a2=0, ad=1 ] = a, [ b0=0, b1=0, b2=0, bd=1 ] = b
  return simplify4( a0*bd - b0*ad, a1*bd - b1*ad, a2*bd - b2*ad, ad*bd )
}

function negate( a )
{
  const [ a0=0, a1=0, a2=0, ad=1 ] = a
  return [ 0 - a0, 0 - a1, 0 - a2, ad ]
}


function embed( a )
{
  const [ a0=0, a1=0, a2=0, ad=1 ] = a
  return ( a0 + RHO * a1 + SIGMA * a2 ) / ad
}

function createRationalFromPairs( pairs )
{
  const [ a0=0, d0=1, a1=0, d1=1, a2=0, d2=1 ] = pairs
  return simplify4( a0*d1*d2, a1*d0*d2, a2*d0*d1, d0*d1*d2 )
}

const origin = dimensions =>
{
  const result = []
  for ( let index = 0; index < dimensions; index++ ) {
    result.push( [ 0, 0, 0, 1 ] )
  }
  return result
}

const field = {

  name: 'heptagon',

  order: 3,
  scalarTerm: 2,

  zero: [ 0, 0, 0, 1 ],
  one: [ 1, 0, 0, 1 ],
  rho: [ 0, 1, 0, 1 ],
  sigma: [ 0, 0, 1, 1 ],

  origin,

  plus, minus, times, scalarmul, vectoradd, embed, reciprocal, negate, createRationalFromPairs, quatTransform,

  embedv: (v) => v.map( embed ),

  createRational: ( n, d ) =>
  {
    return simplify4( n, 0, 0, d )
  },
}

const sigma4 = field.times( field.sigma, field.times( field.sigma, field.times( field.sigma, field.sigma ) ) )
console.log( JSON.stringify( sigma4 ) )
const sigma5 = field.times( field.sigma, sigma4 )
console.log( JSON.stringify( sigma5 ) )
console.log( JSON.stringify( field.reciprocal( field.sigma ) ) )

// Below here are generic, the same for every field

function gcd( a, b )
{
  a = Math.abs(a)
  b = Math.abs(b)
  while ( b !== 0 ) [a, b] = [b, a % b]
  return a
}

function scalarmul( s, v )
{
  return [ ...v.values() ].map( ( vi=[0] ) => times( s, vi ) )
}

function vectoradd( u, v )
{
  return [ ...u.values() ].map( ( ui=[0], i ) => plus( ui, v[i] || [0] ) )
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

export default field
