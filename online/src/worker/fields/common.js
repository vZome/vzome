

BigInt.prototype.toJSON = function() // Can't use arrow function here, because 'this' is needed
{
  const float = Number(this);
  return Number.isSafeInteger(float) ? float : this.toString();
}

// The original code is from Jacob Rus: https://observablehq.com/@jrus/zome-arithmetic,
//   but has now been optimized by Claude Opus 4.6 to use a dual Number/BigInt representation
//   for better performance on small integers.

// ── Dual Number/BigInt representation ────────────────────────
// Trailing-divisor arrays (e.g. [a0, a1, d]) are either ALL Number
// or ALL BigInt.  We detect which via typeof on the last element (the
// divisor), which is never destructured with a default.
//
// Number arithmetic is exact for integers up to 2^53−1.  We use
// overflow-checked helpers and fall back to BigInt when needed.

const MAX_SAFE = Number.MAX_SAFE_INTEGER; // 9007199254740991

// ── Number-safe helpers ──────────────────────────────────────

function safeAdd( a, b ) {
  const r = a + b;
  return ( r >= -MAX_SAFE && r <= MAX_SAFE ) ? r : null;
}

function safeSub( a, b ) {
  const r = a - b;
  return ( r >= -MAX_SAFE && r <= MAX_SAFE ) ? r : null;
}

function safeMul( a, b ) {
  const r = a * b;
  return ( r >= -MAX_SAFE && r <= MAX_SAFE ) ? r : null;
}

function gcdNumber( a, b ) {
  // Inputs are non-negative (callers pass abs values)
  if ( a === 0 ) return b;
  if ( b === 0 || a === b ) return a;
  if ( a === 1 || b === 1 ) return 1;
  do { const t = b; b = a % t; a = t; } while ( b !== 0 );
  return a;
}

// ── BigInt helpers (unchanged logic) ─────────────────────────

export function gcd( a, b )
{
  a = ( a < 0 )? -a : a
  b = ( b < 0 )? -b : b
  while ( b !== 0n ) [a, b] = [b, a % b]
  return a
}

// ── Promote a Number array to BigInt ─────────────────────────

function toBigInts3( v0, v1, v2 ) {
  return [ BigInt(v0), BigInt(v1), BigInt(v2) ];
}
function toBigInts4( v0, v1, v2, v3 ) {
  return [ BigInt(v0), BigInt(v1), BigInt(v2), BigInt(v3) ];
}

// ── Demote a BigInt result to Number if it fits ──────────────

function demote3( v0, v1, v2 ) {
  const a0 = v0 < 0n ? -v0 : v0, a1 = v1 < 0n ? -v1 : v1, a2 = v2 < 0n ? -v2 : v2;
  if ( a0 <= MAX_SAFE && a1 <= MAX_SAFE && a2 <= MAX_SAFE )
    return [ Number(v0), Number(v1), Number(v2) ];
  return [ v0, v1, v2 ];
}
function demote4( v0, v1, v2, v3 ) {
  const a0 = v0 < 0n ? -v0 : v0, a1 = v1 < 0n ? -v1 : v1, a2 = v2 < 0n ? -v2 : v2, a3 = v3 < 0n ? -v3 : v3;
  if ( a0 <= MAX_SAFE && a1 <= MAX_SAFE && a2 <= MAX_SAFE && a3 <= MAX_SAFE )
    return [ Number(v0), Number(v1), Number(v2), Number(v3) ];
  return [ v0, v1, v2, v3 ];
}

// ── simplify (generic, BigInt only — used for higher-order fields and toString) ──

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

// ── simplify3: Number fast path with BigInt fallback ─────────

function simplifyNumber3( v0, v1, v2 ) {
  if ( v2 < 0 ) { v0 = -v0; v1 = -v1; v2 = -v2; }
  if ( v2 <= 1 ) return v2 === 0 ? [ 0, 0, 0 ] : [ v0, v1, 1 ];
  const a0 = v0 < 0 ? -v0 : v0, a1 = v1 < 0 ? -v1 : v1;
  let g = gcdNumber( a0, a1 );
  if ( g !== 1 ) g = gcdNumber( g, v2 );
  if ( g <= 1 ) return [ v0, v1, v2 ];
  return [ v0 / g, v1 / g, v2 / g ];
}

function simplifyBigInt3( v0, v1, v2 ) {
  let s0 = BigInt((v0>0n)-(v0<0n)), s1 = BigInt((v1>0n)-(v1<0n)), s2 = BigInt((v2>0n)-(v2<0n));
  v0 = s0 * v0;
  v1 = s1 * v1;
  v2 = s2 * v2;
  s0 = s2 * s0;
  s1 = s2 * s1;
  const g = gcd( gcd( v0, v1 ), v2 );
  return demote3( s0*v0/g, s1*v1/g, v2/g );
}

export function simplify3( v0, v1, v2 )
{
  if ( typeof v0 === 'bigint' ) {
    if ((v0|0n) !== v0 || (v1|0n) !== v1 || (v2|0n) !== v2)
      throw new TypeError( `integer overflow!... ${v0} ${v1} ${v2}` )
    return simplifyBigInt3( v0, v1, v2 );
  }
  // Number path
  return simplifyNumber3( v0, v1, v2 );
}

// ── simplify4: Number fast path with BigInt fallback ─────────

function simplifyNumber4( v0, v1, v2, v3 ) {
  if ( v3 < 0 ) { v0 = -v0; v1 = -v1; v2 = -v2; v3 = -v3; }
  if ( v3 <= 1 ) return v3 === 0 ? [ 0, 0, 0, 0 ] : [ v0, v1, v2, 1 ];
  const a0 = v0 < 0 ? -v0 : v0, a1 = v1 < 0 ? -v1 : v1, a2 = v2 < 0 ? -v2 : v2;
  let g = gcdNumber( gcdNumber( a0, a1 ), a2 );
  if ( g !== 1 ) g = gcdNumber( g, v3 );
  if ( g <= 1 ) return [ v0, v1, v2, v3 ];
  return [ v0 / g, v1 / g, v2 / g, v3 / g ];
}

function simplifyBigInt4( v0, v1, v2, v3 ) {
  let s0 = BigInt((v0>0n)-(v0<0n)), s1 = BigInt((v1>0n)-(v1<0n)), s2 = BigInt((v2>0n)-(v2<0n)), s3 = BigInt((v3>0n)-(v3<0n));
  v0 = s0 * v0;
  v1 = s1 * v1;
  v2 = s2 * v2;
  v3 = s3 * v3;
  s0 = s3 * s0;
  s1 = s3 * s1;
  s2 = s3 * s2;
  const g = gcd( gcd( gcd( v0, v1 ), v2 ), v3 );
  return demote4( s0*v0/g, s1*v1/g, s2*v2/g, v3/g );
}

export function simplify4( v0, v1, v2, v3 )
{
  if ( typeof v0 === 'bigint' ) {
    if ((v0|0n) !== v0 || (v1|0n) !== v1 || (v2|0n) !== v2 || (v3|0n) !== v3)
      throw new TypeError( `integer overflow!... ${v0} ${v1} ${v2} ${v3}` )
    return simplifyBigInt4( v0, v1, v2, v3 );
  }
  // Number path
  return simplifyNumber4( v0, v1, v2, v3 );
}

function parseInt( s )
{
  const n = Number( s );
  if ( Number.isSafeInteger( n ) )
    return n;
  return BigInt( s );
}

// ── plus / minus / negate for order-2 fields ─────────────────

function plus2( a, b )
{
  if ( typeof a[0] === 'number' && typeof b[0] === 'number' ) {
    const [ a0=0, a1=0, ad=1 ] = a, [ b0=0, b1=0, bd=1 ] = b;
    if ( ad === bd ) {
      const n0 = safeAdd( a0, b0 ), n1 = safeAdd( a1, b1 );
      if ( n0 !== null && n1 !== null )
        return simplifyNumber3( n0, n1, ad );
    } else {
      const n0a = safeMul( a0, bd ), n0b = safeMul( b0, ad );
      const n1a = safeMul( a1, bd ), n1b = safeMul( b1, ad );
      const d = safeMul( ad, bd );
      if ( n0a !== null && n0b !== null && n1a !== null && n1b !== null && d !== null ) {
        const r0 = safeAdd( n0a, n0b ), r1 = safeAdd( n1a, n1b );
        if ( r0 !== null && r1 !== null )
          return simplifyNumber3( r0, r1, d );
      }
    }
  }
  // Fallback to BigInt — promote any Number arrays
  const [ a0=0n, a1=0n, ad=1n ] = typeof a[0] === 'bigint' ? a : toBigInts3(...a);
  const [ b0=0n, b1=0n, bd=1n ] = typeof b[0] === 'bigint' ? b : toBigInts3(...b);
  return simplifyBigInt3( a0*bd + b0*ad, a1*bd + b1*ad, ad*bd )
}

function minus2( a, b )
{
  if ( typeof a[0] === 'number' && typeof b[0] === 'number' ) {
    const [ a0=0, a1=0, ad=1 ] = a, [ b0=0, b1=0, bd=1 ] = b;
    if ( ad === bd ) {
      const n0 = safeSub( a0, b0 ), n1 = safeSub( a1, b1 );
      if ( n0 !== null && n1 !== null )
        return simplifyNumber3( n0, n1, ad );
    } else {
      const n0a = safeMul( a0, bd ), n0b = safeMul( b0, ad );
      const n1a = safeMul( a1, bd ), n1b = safeMul( b1, ad );
      const d = safeMul( ad, bd );
      if ( n0a !== null && n0b !== null && n1a !== null && n1b !== null && d !== null ) {
        const r0 = safeSub( n0a, n0b ), r1 = safeSub( n1a, n1b );
        if ( r0 !== null && r1 !== null )
          return simplifyNumber3( r0, r1, d );
      }
    }
  }
  const [ a0=0n, a1=0n, ad=1n ] = typeof a[0] === 'bigint' ? a : toBigInts3(...a);
  const [ b0=0n, b1=0n, bd=1n ] = typeof b[0] === 'bigint' ? b : toBigInts3(...b);
  return simplifyBigInt3( a0*bd - b0*ad, a1*bd - b1*ad, ad*bd )
}

function negate2( a )
{
  if ( typeof a[0] === 'number' ) {
    const [ a0=0, a1=0, ad=1 ] = a;
    return [ -a0, -a1, ad ];
  }
  const [ a0=0n, a1=0n, ad=1n ] = a
  return [ 0n - a0, 0n - a1, ad ]
}

// ── plus / minus / negate for order-3 fields ─────────────────

function plus3( a, b )
{
  if ( typeof a[0] === 'number' && typeof b[0] === 'number' ) {
    const [ a0=0, a1=0, a2=0, ad=1 ] = a, [ b0=0, b1=0, b2=0, bd=1 ] = b;
    if ( ad === bd ) {
      const n0 = safeAdd( a0, b0 ), n1 = safeAdd( a1, b1 ), n2 = safeAdd( a2, b2 );
      if ( n0 !== null && n1 !== null && n2 !== null )
        return simplifyNumber4( n0, n1, n2, ad );
    } else {
      const n0a = safeMul( a0, bd ), n0b = safeMul( b0, ad );
      const n1a = safeMul( a1, bd ), n1b = safeMul( b1, ad );
      const n2a = safeMul( a2, bd ), n2b = safeMul( b2, ad );
      const d = safeMul( ad, bd );
      if ( n0a !== null && n0b !== null && n1a !== null && n1b !== null && n2a !== null && n2b !== null && d !== null ) {
        const r0 = safeAdd( n0a, n0b ), r1 = safeAdd( n1a, n1b ), r2 = safeAdd( n2a, n2b );
        if ( r0 !== null && r1 !== null && r2 !== null )
          return simplifyNumber4( r0, r1, r2, d );
      }
    }
  }
  const [ a0=0n, a1=0n, a2=0n, ad=1n ] = typeof a[0] === 'bigint' ? a : toBigInts4(...a);
  const [ b0=0n, b1=0n, b2=0n, bd=1n ] = typeof b[0] === 'bigint' ? b : toBigInts4(...b);
  return simplifyBigInt4( a0*bd + b0*ad, a1*bd + b1*ad, a2*bd + b2*ad, ad*bd )
}

function minus3( a, b )
{
  if ( typeof a[0] === 'number' && typeof b[0] === 'number' ) {
    const [ a0=0, a1=0, a2=0, ad=1 ] = a, [ b0=0, b1=0, b2=0, bd=1 ] = b;
    if ( ad === bd ) {
      const n0 = safeSub( a0, b0 ), n1 = safeSub( a1, b1 ), n2 = safeSub( a2, b2 );
      if ( n0 !== null && n1 !== null && n2 !== null )
        return simplifyNumber4( n0, n1, n2, ad );
    } else {
      const n0a = safeMul( a0, bd ), n0b = safeMul( b0, ad );
      const n1a = safeMul( a1, bd ), n1b = safeMul( b1, ad );
      const n2a = safeMul( a2, bd ), n2b = safeMul( b2, ad );
      const d = safeMul( ad, bd );
      if ( n0a !== null && n0b !== null && n1a !== null && n1b !== null && n2a !== null && n2b !== null && d !== null ) {
        const r0 = safeSub( n0a, n0b ), r1 = safeSub( n1a, n1b ), r2 = safeSub( n2a, n2b );
        if ( r0 !== null && r1 !== null && r2 !== null )
          return simplifyNumber4( r0, r1, r2, d );
      }
    }
  }
  const [ a0=0n, a1=0n, a2=0n, ad=1n ] = typeof a[0] === 'bigint' ? a : toBigInts4(...a);
  const [ b0=0n, b1=0n, b2=0n, bd=1n ] = typeof b[0] === 'bigint' ? b : toBigInts4(...b);
  return simplifyBigInt4( a0*bd - b0*ad, a1*bd - b1*ad, a2*bd - b2*ad, ad*bd )
}

function negate3( a )
{
  if ( typeof a[0] === 'number' ) {
    const [ a0=0, a1=0, a2=0, ad=1 ] = a;
    return [ -a0, -a1, -a2, ad ];
  }
  const [ a0=0n, a1=0n, a2=0n, ad=1n ] = a
  return [ 0n - a0, 0n - a1, 0n - a2, ad ]
}

// ── createNumberFromPairs ────────────────────────────────────

function createNumberFromPairs2( pairs )
{
  // Try Number path if all inputs are Number
  if ( typeof pairs[0] !== 'bigint' && typeof pairs[1] !== 'bigint' &&
       typeof pairs[2] !== 'bigint' && typeof pairs[3] !== 'bigint' ) {
    const a0 = pairs[0] || 0, d0 = pairs[1] || 1, a1 = pairs[2] || 0, d1 = pairs[3] || 1;
    const b0 = safeMul( a0, d1 ), b1 = safeMul( a1, d0 ), d2 = safeMul( d0, d1 );
    if ( b0 !== null && b1 !== null && d2 !== null )
      return simplifyNumber3( b0, b1, d2 );
  }
  const a0 = BigInt(pairs[0] || 0), d0 = BigInt(pairs[1] || 1), a1 = BigInt(pairs[2] || 0), d1 = BigInt(pairs[3] || 1);
  return simplifyBigInt3( a0 * d1, a1 * d0, d0 * d1 );
}

function createNumberFromPairs3( pairs )
{
  if ( typeof pairs[0] !== 'bigint' && typeof pairs[1] !== 'bigint' &&
       typeof pairs[2] !== 'bigint' && typeof pairs[3] !== 'bigint' &&
       typeof pairs[4] !== 'bigint' && typeof pairs[5] !== 'bigint' ) {
    const a0 = pairs[0] || 0, d0 = pairs[1] || 1, a1 = pairs[2] || 0, d1 = pairs[3] || 1, a2 = pairs[4] || 0, d2 = pairs[5] || 1;
    const b0a = safeMul( a0, d1 ), b0 = b0a !== null ? safeMul( b0a, d2 ) : null;
    const b1a = safeMul( a1, d0 ), b1 = b1a !== null ? safeMul( b1a, d2 ) : null;
    const b2a = safeMul( a2, d0 ), b2 = b2a !== null ? safeMul( b2a, d1 ) : null;
    const da = safeMul( d0, d1 ), d = da !== null ? safeMul( da, d2 ) : null;
    if ( b0 !== null && b1 !== null && b2 !== null && d !== null )
      return simplifyNumber4( b0, b1, b2, d );
  }
  const a0 = BigInt(pairs[0] || 0), d0 = BigInt(pairs[1] || 1), a1 = BigInt(pairs[2] || 0), d1 = BigInt(pairs[3] || 1), a2 = BigInt(pairs[4] || 0), d2 = BigInt(pairs[5] || 1);
  return simplifyBigInt4( a0*d1*d2, a1*d0*d2, a2*d0*d1, d0*d1*d2 );
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
  // Try Number path
  if ( typeof trailingDivisor[0] !== 'bigint' && typeof trailingDivisor[1] !== 'bigint' && typeof trailingDivisor[2] !== 'bigint' ) {
    const a0 = trailingDivisor[0] || 0, a1 = trailingDivisor[1] || 0, d = trailingDivisor[2] || 1;
    return simplifyNumber3( a0, a1, d );
  }
  const a0 = BigInt(trailingDivisor[0] || 0), a1 = BigInt(trailingDivisor[1] || 0), d = BigInt(trailingDivisor[2] || 1);
  return simplifyBigInt3( a0, a1, d );
}

function createNumber3( trailingDivisor )
{
  if ( typeof trailingDivisor[0] !== 'bigint' && typeof trailingDivisor[1] !== 'bigint' &&
       typeof trailingDivisor[2] !== 'bigint' && typeof trailingDivisor[3] !== 'bigint' ) {
    const a0 = trailingDivisor[0] || 0, a1 = trailingDivisor[1] || 0, a2 = trailingDivisor[2] || 0, d = trailingDivisor[3] || 1;
    return simplifyNumber4( a0, a1, a2, d );
  }
  const a0 = BigInt(trailingDivisor[0] || 0), a1 = BigInt(trailingDivisor[1] || 0), a2 = BigInt(trailingDivisor[2] || 0), d = BigInt(trailingDivisor[3] || 1);
  return simplifyBigInt4( a0, a1, a2, d );
}

const Format = { DEFAULT: 0, EXPRESSION: 1, ZOMIC: 2, VEF: 3, MATHML: 4, MATH: 5 }

function bigRationalToString( num, denom )
{
  if ( denom == 1 )
    return num.toString()
  else
    return num.toString() + "/" + denom.toString()
}

function bigRationalToMathML( num, denom )
{
  if ( denom == 1 )
    return `<mn>${num.toString()}</mn>`;
  else
    return `<mfrac><mn>${num.toString()}</mn><mn>${denom.toString()}</mn></mfrac>`;
}

const toString2 = getIrrational => ( trailingDivisor, format ) =>
{
  const a0 = BigInt(trailingDivisor[0] || 0), a1 = BigInt(trailingDivisor[1] || 0), d = BigInt(trailingDivisor[2] || 1);
  const [ n0, d0 ] = simplify( [ a0, d ] );
  const [ n1, d1 ] = simplify( [ a1, d ] );
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
  const a0 = BigInt(trailingDivisor[0] || 0), a1 = BigInt(trailingDivisor[1] || 0), a2 = BigInt(trailingDivisor[2] || 0), d = BigInt(trailingDivisor[3] || 1);
  const [ n0, d0 ] = simplify( [ a0, d ] );
  const [ n1, d1 ] = simplify( [ a1, d ] );
  const [ n2, d2 ] = simplify( [ a2, d ] );
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
  let zero = [ 0, 0, 1 ]
  let one = [ 1, 0, 1 ]
  let negate = negate2
  let plus = plus2
  let minus = minus2
  let createNumberFromPairs = createNumberFromPairs2
  let createNumber = createNumber2
  let toString = toString2( getIrrational )
  if ( order === 3 ) {
    scalarTerm = 2
    zero = [ 0, 0, 0, 1 ]
    one = [ 1, 0, 0, 1 ]
    negate = negate3
    plus = plus3
    minus = minus3
    createNumberFromPairs = createNumberFromPairs3
    createNumber = createNumber3
    toString = toString3( getIrrational )
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
    scalarTerm, zero, one,
    zeroCopy: () => zero.slice(),
    plus, minus, times, embed, reciprocal, negate, getIrrational,
    scalarmul, vectoradd, quatTransform, quatmul,
    embedv: (v) => v.map( embed ),
    createNumberFromPairs, createNumber, toString, parseInt,
  }
}