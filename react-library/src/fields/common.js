

// Most of this code is from Jacob Rus: https://observablehq.com/@jrus/zome-arithmetic

export function gcd( a, b )
{
  a = Math.abs(a)
  b = Math.abs(b)
  while ( b !== 0 ) [a, b] = [b, a % b]
  return a
}

export function simplify3( v0, v1, v2 )
{
  if ((v0|0) !== v0 || (v1|0) !== v1 || (v2|0) !== v2)
    throw new TypeError( `integer overflow!... ${v0} ${v1} ${v2}` )
  let s0 = (v0>0)-(v0<0), s1 = (v1>0)-(v1<0), s2 = (v2>0)-(v2<0) // signs
  v0 *= s0
  v1 *= s1
  v2 *= s2
  s0 *= s2
  s1 *= s2
  const g = gcd(gcd(v0, v1), v2)
  return [s0*v0/g, s1*v1/g, v2/g]
}

export function simplify4( v0, v1, v2, v3 )
{
  if ((v0|0) !== v0 || (v1|0) !== v1 || (v2|0) !== v2 || (v3|0) !== v3)
    throw new TypeError( `integer overflow!... ${v0} ${v1} ${v2} ${v3}` )
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

function plus2( a, b )
{
  const [ a0=0, a1=0, ad=1] = a, [b0=0, b1=0, bd=1 ] = b
  return simplify3( a0*bd + b0*ad, a1*bd + b1*ad, ad*bd )
}

function minus2( a, b )
{
  const [ a0=0, a1=0, ad=1] = a, [b0=0, b1=0, bd=1 ] = b
  return simplify3( a0*bd - b0*ad, a1*bd - b1*ad, ad*bd )
}

function negate2( a )
{
  const [ a0=0, a1=0, ad=1 ] = a
  return [ 0 - a0, 0 - a1, ad ]
}

function plus3( a, b )
{
  const [ a0=0, a1=0, a2=0, ad=1 ] = a, [ b0=0, b1=0, b2=0, bd=1 ] = b
  return simplify4( a0*bd + b0*ad, a1*bd + b1*ad, a2*bd + b2*ad, ad*bd )
}

function minus3( a, b )
{
  const [ a0=0, a1=0, a2=0, ad=1 ] = a, [ b0=0, b1=0, b2=0, bd=1 ] = b
  return simplify4( a0*bd - b0*ad, a1*bd - b1*ad, a2*bd - b2*ad, ad*bd )
}

function negate3( a )
{
  const [ a0=0, a1=0, a2=0, ad=1 ] = a
  return [ 0 - a0, 0 - a1, 0 - a2, ad ]
}

function createRationalFromPairs2( pairs )
{
  const [ a0=0, d0=1, a1=0, d1=1 ] = pairs
  return simplify3( a0*d1, a1*d0, d0*d1 )
}

function createRationalFromPairs3( pairs )
{
  const [ a0=0, d0=1, a1=0, d1=1, a2=0, d2=1 ] = pairs
  return simplify4( a0*d1*d2, a1*d0*d2, a2*d0*d1, d0*d1*d2 )
}

const origin2 = dimensions =>
{
  const result = []
  for ( let index = 0; index < dimensions; index++ ) {
    result.push( [ 0, 0, 1 ] )
  }
  return result
}

const origin3 = dimensions =>
{
  const result = []
  for ( let index = 0; index < dimensions; index++ ) {
    result.push( [ 0, 0, 0, 1 ] )
  }
  return result
}

export const createField = ( { name, order, times, embed, reciprocal } ) =>
{
  let scalarTerm = 1
  let zero = [ 0, 0, 1 ]
  let one = [ 1, 0, 1 ]
  let origin = origin2
  let negate = negate2
  let plus = plus2
  let minus = minus2
  let createRationalFromPairs = createRationalFromPairs2
  let createRational = ( n, d ) => simplify3( n, 0, d )
  if ( order === 3 ) {
    scalarTerm = 2
    zero = [ 0, 0, 0, 1 ]
    one = [ 1, 0, 0, 1 ]
    origin = origin3
    negate = negate3
    plus = plus3
    minus = minus3
    createRationalFromPairs = createRationalFromPairs3
    createRational = ( n, d ) => simplify4( n, 0, 0, d )
  }

  const scalarmul = ( s, v ) => [ ...v.values() ].map( ( vi=[0] ) => times( s, vi ) )
  const vectoradd = ( u, v ) => [ ...u.values() ].map( ( ui=[0], i ) => plus( ui, v[i] || [0] ) )

  const quatmul = ( u, v ) =>
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

  return {
    name, order,
    scalarTerm, zero, one, origin,
    plus, minus, times, embed, reciprocal, negate,
    scalarmul, vectoradd, createRationalFromPairs, quatTransform, quatmul,
    embedv: (v) => v.map( embed ),
    createRational,
  }
}