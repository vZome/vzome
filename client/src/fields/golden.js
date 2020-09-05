
// Most of this code is from Jacob Rus: https://observablehq.com/@jrus/zome-arithmetic

const PHI = 0.5 * (Math.sqrt(5) + 1)

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
    throw new TypeError('simplify3 operates only on integers.')
  let s0 = (v0>0)-(v0<0), s1 = (v1>0)-(v1<0), s2 = (v2>0)-(v2<0) // signs
  v0 *= s0
  v1 *= s1
  v2 *= s2
  s0 *= s2
  s1 *= s2
  const g = gcd(gcd(v0, v1), v2)
  return [s0*v0/g, s1*v1/g, v2/g]
}

export default {

  name: 'golden',

  order: 2,

  zero: [ 0, 0, 1 ],
  one: [ 1, 0, 1 ],

  times: (a, b) =>
  {
    const [ a0=0, a1=0, ad=1] = a, [b0=0, b1=0, bd=1 ] = b
    return simplify3( a0*b0 + a1*b1, a0*b1 + a1*b0 + a1*b1, ad*bd )
  },

  embed: ( a ) =>
  {
    const [ a0=0, a1=0, ad=1 ] = a
    return ( a0 + PHI * a1 ) / ad
  },

  createRational: ( n, d ) =>
  {
    return simplify3( n, 0, d )
  }
}