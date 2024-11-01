

BigInt.prototype.toJSON = function() { return Number(this)  }  // What will this do when the BigInt is too big?

// Most of this code is from Jacob Rus: https://observablehq.com/@jrus/zome-arithmetic

export function gcd( a, b )
{
  a = ( a < 0 )? -a : a
  b = ( b < 0 )? -b : b
  while ( b !== 0n ) [a, b] = [b, a % b]
  return a
}

export function simplify( values )
{
  const signs = []
  const signedValues = []
  let lastSign
  let divisor
  for (let i = 0; i < values.length; i++) {
    const value = values[ i ];
    if ( (value|0n) !== value )
      throw new TypeError( `integer overflow!... ${value}` )
    lastSign = BigInt( (value>0n)-(value<0n) )
    signs.push( lastSign )
    signedValues.push( lastSign * value )
    divisor = value
  }
  for (let i = 0; i < values.length-1; i++) {
    signs[ i ] = lastSign * signs[ i ]
    divisor = gcd( signedValues[ i ], divisor )
  }
  const result = []
  for (let i = 0; i < values.length; i++) {
    let fraction = signedValues[ i ] / divisor
    result.push( ( i === values.length-1 )? fraction : signs[ i ] * fraction )
  }
  return result
}

export function simplify3( v0, v1, v2 )
{
  if ((v0|0n) !== v0 || (v1|0n) !== v1 || (v2|0n) !== v2)
    throw new TypeError( `integer overflow!... ${v0} ${v1} ${v2}` )
  let s0 = BigInt((v0>0n)-(v0<0n)), s1 = BigInt((v1>0n)-(v1<0n)), s2 = BigInt((v2>0n)-(v2<0n)) // signs
  v0 = s0 * v0
  v1 = s1 * v1
  v2 = s2 * v2
  s0 = s2 * s0
  s1 = s2 * s1
  const g = gcd(gcd(v0, v1), v2)
  return [s0*v0/g, s1*v1/g, v2/g]
}

export function simplify4( v0, v1, v2, v3 )
{
  if ((v0|0n) !== v0 || (v1|0n) !== v1 || (v2|0n) !== v2 || (v3|0n) !== v3)
    throw new TypeError( `integer overflow!... ${v0} ${v1} ${v2} ${v3}` )
  let s0 = BigInt((v0>0n)-(v0<0n)), s1 = BigInt((v1>0n)-(v1<0n)), s2 = BigInt((v2>0n)-(v2<0n)), s3 = BigInt((v3>0n)-(v3<0n)) // signs
  v0 = s0 * v0
  v1 = s1 * v1
  v2 = s2 * v2
  v3 = s3 * v3
  s0 = s3 * s0
  s1 = s3 * s1
  s2 = s3 * s2
  const g = gcd(gcd(gcd(v0, v1), v2), v3)
  return [ s0*v0/g, s1*v1/g, s2*v2/g, v3/g ]
}

function plus2( a, b )
{
  const [ a0=0n, a1=0n, ad=1n] = a, [b0=0n, b1=0n, bd=1n ] = b
  return simplify3( a0*bd + b0*ad, a1*bd + b1*ad, ad*bd )
}

function minus2( a, b )
{
  const [ a0=0n, a1=0n, ad=1n] = a, [b0=0n, b1=0n, bd=1n ] = b
  return simplify3( a0*bd - b0*ad, a1*bd - b1*ad, ad*bd )
}

function negate2( a )
{
  const [ a0=0n, a1=0n, ad=1n ] = a
  return [ 0n - a0, 0n - a1, ad ]
}

function plus3( a, b )
{
  const [ a0=0n, a1=0n, a2=0n, ad=1n ] = a, [ b0=0n, b1=0n, b2=0n, bd=1n ] = b
  return simplify4( a0*bd + b0*ad, a1*bd + b1*ad, a2*bd + b2*ad, ad*bd )
}

function minus3( a, b )
{
  const [ a0=0n, a1=0n, a2=0n, ad=1n ] = a, [ b0=0n, b1=0n, b2=0n, bd=1n ] = b
  return simplify4( a0*bd - b0*ad, a1*bd - b1*ad, a2*bd - b2*ad, ad*bd )
}

function negate3( a )
{
  const [ a0=0n, a1=0n, a2=0n, ad=1n ] = a
  return [ 0n - a0, 0n - a1, 0n - a2, ad ]
}

function createNumberFromPairs2( pairs )
{
  const [ a0=0n, d0=1n, a1=0n, d1=1n ] = pairs
  const b0 = BigInt(a0) * BigInt(d1)
  const b1 = BigInt(a1) * BigInt(d0)
  const d2 = BigInt(d0) * BigInt(d1)
  return simplify3( b0, b1, d2 )
}

function createNumberFromPairs3( pairs )
{
  const [ a0=0n, d0=1n, a1=0n, d1=1n, a2=0n, d2=1n ] = pairs
  const b0 = BigInt(a0) * BigInt(d1) * BigInt(d2)
  const b1 = BigInt(a1) * BigInt(d0) * BigInt(d2)
  const b2 = BigInt(a2) * BigInt(d0) * BigInt(d1)
  const d = BigInt(d0) * BigInt(d1) * BigInt(d2)
  return simplify4( b0, b1, b2, d )
}

export function createNumberFromPairs( pairs )
{
  const order = pairs.length / 2
  let result = []
  for (let i = 0; i < order; i++) {
    let term = pairs[ 2*i ]
    for (let j = 0; j < order; j++) {
      if ( i !== j )
        term *= pairs[ 2*j + 1 ]
    }
    result.push( term )
  }
  let divisor = 1n
  for (let j = 0; j < order; j++) {
    divisor *= pairs[ 2*j + 1 ]
  }
  result.push( divisor )
  return simplify( result )
}

function createNumber2( trailingDivisor )
{
  const [ a0=0n, a1=0n, d=1n ] = trailingDivisor
  return simplify3( BigInt(a0), BigInt(a1), BigInt(d) )
}

function createNumber3( trailingDivisor )
{
  const [ a0=0n, a1=0n, a2=0n, d=1n ] = trailingDivisor
  return simplify4( BigInt(a0), BigInt(a1), BigInt(a2), BigInt(d) )
}

const origin2 = dimensions =>
{
  const result = []
  for ( let index = 0; index < dimensions; index++ ) {
    result.push( [ 0n, 0n, 1n ] )
  }
  return result
}

const origin3 = dimensions =>
{
  const result = []
  for ( let index = 0; index < dimensions; index++ ) {
    result.push( [ 0n, 0n, 0n, 1n ] )
  }
  return result
}

const Format = { DEFAULT: 0, EXPRESSION: 1, ZOMIC: 2, VEF: 3, MATHML: 4, MATH: 5 }

function bigRationalToString( num, denom )
{
  if ( denom === 1n )
    return num.toString()
  else
    return num.toString() + "/" + denom.toString()
}

function bigRationalToMathML( num, denom )
{
  if ( denom === 1n )
    return `<mn>${num.toString()}</mn>`;
  else
    return `<mfrac><mn>${num.toString()}</mn><mn>${denom.toString()}</mn></mfrac>`;
}

const toString2 = getIrrational => ( trailingDivisor, format ) =>
{
  const [ a0=0n, a1=0n, d=1n ] = trailingDivisor
  const [ n0, d0 ] = simplify( [ BigInt(a0), BigInt(d) ] );
  const [ n1, d1 ] = simplify( [ BigInt(a1), BigInt(d) ] );
  const irrat = getIrrational();
  switch (format)
  {
    case Format.ZOMIC:
      return bigRationalToString( n0, d0 ) + " " + bigRationalToString( n1, d1 );
  
    case Format.VEF:
      return "(" + bigRationalToString( n1, d1 ) + "," + bigRationalToString( n0, d0 ) + ")";

    case Format.MATHML:
      if ( a0 === 0n ) {
        if ( a1 === 0n )
          return "<mn>0</mn>";
        else
          return `<mrow>${bigRationalToMathML( a1, d )}<mi>${irrat}</mi></mrow>`;
      }
      else {
        if ( a1 === 0n )
          return bigRationalToMathML( a0, d );
        else if ( a1 < 0n )
          return `<mrow>${bigRationalToMathML( a0, d )}<mo>-</mo>${bigRationalToMathML( 0n-a1, d )}<mi>${irrat}</mi></mrow>`;
        else
          return `<mrow>${bigRationalToMathML( a0, d )}<mo>+</mo>${bigRationalToMathML( a1, d )}<mi>${irrat}</mi></mrow>`;
      }

    case Format.MATH:
      if ( a0 === 0n ) {
        if ( a1 === 0n )
          return "0";
        else if ( a1 === 1n && d === 1n )
          return irrat;
        else
          return bigRationalToString( a1, d ) + irrat;
      }
      else {
        if ( a1 === 0n )
          return bigRationalToString( a0, d );
        else if ( a1 === 1n && d === 1n )
          return bigRationalToString( a0, d ) + "+" + irrat;
        else
          return bigRationalToString( a0, d ) + "+" + bigRationalToString( a1, d ) + irrat;
      }
  
    default:
      if ( a0 === 0n ) {
        if ( a1 === 0n )
          return "0";
        else
          return bigRationalToString( a1, d ) + "*" + irrat;
      }
      else {
        if ( a1 === 0n )
          return bigRationalToString( a0, d );
        else
          return bigRationalToString( a0, d ) + "+" + bigRationalToString( a1, d ) + "*" + irrat;
      }
  }
}

const toString3 = getIrrational => ( trailingDivisor, format ) =>
{
  const [ a0=0n, a1=0n, a2=0n, d=1n ] = trailingDivisor
  const [ n0, d0 ] = simplify( [ BigInt(a0), BigInt(d) ] );
  const [ n1, d1 ] = simplify( [ BigInt(a1), BigInt(d) ] );
  const [ n2, d2 ] = simplify( [ BigInt(a2), BigInt(d) ] );
  switch (format)
  {
    case Format.ZOMIC:
      return bigRationalToString( n0, d0 ) + " " + bigRationalToString( n1, d1 ) + " " + bigRationalToString( n2, d2 );
  
    case Format.VEF:
      return "(" + bigRationalToString( n2, d2 ) + "," + bigRationalToString( n1, d1 ) + "," + bigRationalToString( n0, d0 ) + ")";

    case Format.MATHML:
      let result = '';
      if ( a0 !== 0n ) {
        result += bigRationalToMathML( a0, d );
      }
      if ( a1 !== 0n ) {
        if ( a1 < 0n )
          result += `<mo>-</mo>${bigRationalToMathML( 0n-a1, d )}<mi>${getIrrational(1)}</mi>`;
        else {
          if ( result )
            result += '<mo>+</mo>';
          result += `${bigRationalToMathML( a1, d )}<mi>${getIrrational(1)}</mi>`;
        }
      }
      if ( a2 !== 0n ) {
        if ( a2 < 0n )
          result += `<mo>-</mo>${bigRationalToMathML( 0n-a2, d )}<mi>${getIrrational(2)}</mi>`;
        else {
          if ( result )
            result += '<mo>+</mo>';
          result += `${bigRationalToMathML( a2, d )}<mi>${getIrrational(2)}</mi>`;
        }
      }
      if ( !result )
        return '<mn>0</mn>';
      return '<mrow>' + result + '</mrow>';
  
    default:
      // NOTE: this is untested code as of this writing
      if ( a0 === 0n ) {
        if ( a1 === 0n ) {
          if ( a2 === 0n )
            return "0";
          else
            return bigRationalToString( a2, d ) + "*b";
        }
        else {
          if ( a2 === 0n )
            return bigRationalToString( a1, d ) + "*a";
          else
            return bigRationalToString( a1, d ) + "*a +" + bigRationalToString( a2, d ) + "*b";
        }
      }
      else {
        if ( a1 === 0n ) {
          if ( a2 === 0n )
            return bigRationalToString( a0, d );
          else
            return bigRationalToString( a0, d ) + " +" + bigRationalToString( a2, d ) + "*b";
        }
        else {
          if ( a2 === 0n )
            return bigRationalToString( a0, d ) + " +" + bigRationalToString( a1, d ) + "*a";
          else
            return bigRationalToString( a0, d ) + " +" + bigRationalToString( a1, d ) + "*a +" + bigRationalToString( a2, d ) + "*b";
        }
      }
  }
}

export const createField = ( { name, order, times, embed, reciprocal, getIrrational } ) =>
{
  let scalarTerm = 1
  let zero = [ 0n, 0n, 1n ]
  let one = [ 1n, 0n, 1n ]
  let origin = origin2
  let negate = negate2
  let plus = plus2
  let minus = minus2
  let createNumberFromPairs = createNumberFromPairs2
  let createNumber = createNumber2
  let toString = toString2( getIrrational )
  if ( order === 3 ) {
    scalarTerm = 2
    zero = [ 0n, 0n, 0n, 1n ]
    one = [ 1n, 0n, 0n, 1n ]
    origin = origin3
    negate = negate3
    plus = plus3
    minus = minus3
    createNumberFromPairs = createNumberFromPairs3
    createNumber = createNumber3
    toString = toString3( getIrrational )
  }

  const scalarmul = ( s, v ) => [ ...v.values() ].map( ( vi=[0n] ) => times( s, vi ) )
  const vectoradd = ( u, v ) => [ ...u.values() ].map( ( ui=[0n], i ) => plus( ui, v[i] || [0n] ) )

  const quatmul = ( u, v ) =>
  {
    const x = times, a = plus, s = minus,
      [u0=[0n], u1=[0n], u2=[0n], u3=[0n]] = u,
      [v0=[0n], v1=[0n], v2=[0n], v3=[0n]] = v
    return [
      s(x(u0, v0), a(a(x(u1, v1), x(u2, v2)), x(u3, v3))),
      a(a(x(u0, v1), x(u1, v0)), s(x(u2, v3), x(u3, v2))),
      a(a(x(u0, v2), x(u2, v0)), s(x(u3, v1), x(u1, v3))),
      a(a(x(u0, v3), x(u3, v0)), s(x(u1, v2), x(u2, v1)))]
  }

  // Everything here uses a W-first representation of quaternions.
  //   These quaternions must be transformed to W-last when using with three.js.

  function quatconj( [u0=[0n], u1=[0n], u2=[0n], u3=[0n]] )
  {
    return [ u0, negate(u1), negate(u2), negate(u3) ]
  }

  function quatTransform( Q, v )
  {
    return quatmul( Q, quatmul( [[0n]].concat(v), quatconj(Q)) ).slice(1)
  }

  return {
    name, order,
    scalarTerm, zero, one, origin,
    plus, minus, times, embed, reciprocal, negate, getIrrational,
    scalarmul, vectoradd, quatTransform, quatmul,
    embedv: (v) => v.map( embed ),
    createNumberFromPairs, createNumber, toString,
  }
}