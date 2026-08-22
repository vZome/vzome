
// Optimized BigRational with dual Number/BigInt representation.
// Ported from core/src/main/java/com/vzome/core/algebra/BigRationalImpl.java
//
// Invariant: this.num and this.denom are BOTH Number or BOTH BigInt.
//   - When both are Number, this.big === false
//   - When both are BigInt,  this.big === true
//   - The fraction is always in reduced form (gcd === 1)
//   - The denominator is always positive
//   - 0 is always represented as 0/1

// JS Number is a 64-bit IEEE 754 double.  Integer arithmetic is exact
// up to 2^53 − 1.  We use this as the "safe" threshold (analogous to
// Java's Long.MAX_VALUE for the long fast path).
const MAX_SAFE = Number.MAX_SAFE_INTEGER; // 9007199254740991

// ── Number-based GCD (Euclidean) ─────────────────────────────
function gcdNumber( a, b ) {
  // Precondition: a >= 0, b >= 0
  while ( b !== 0 ) {
    const t = b;
    b = a % t;
    a = t;
  }
  return a;
}

// ── BigInt-based GCD ─────────────────────────────────────────
function gcdBigInt( a, b ) {
  if ( a < 0n ) a = -a;
  if ( b < 0n ) b = -b;
  while ( b !== 0n ) {
    const t = b;
    b = a % t;
    a = t;
  }
  return a;
}

// ── Overflow-checked Number arithmetic ───────────────────────
// Returns null if the result exceeds safe integer range.

function safeAdd( a, b ) {
  const r = a + b;
  return ( r >= -MAX_SAFE && r <= MAX_SAFE ) ? r : null;
}

function safeMul( a, b ) {
  const r = a * b;
  return ( r >= -MAX_SAFE && r <= MAX_SAFE ) ? r : null;
}

// ── Reduce a Number pair ─────────────────────────────────────
// Returns [num, denom] with denom > 0, gcd === 1, both Number.
function reduceNumbers( num, denom ) {
  if ( denom === 0 )
    throw new Error( "Denominator is zero" );
  if ( num === 0 )
    return [ 0, 1 ];
  if ( denom === 1 )
    return [ num, 1 ];

  // Ensure denom > 0
  if ( denom < 0 ) {
    num = -num;
    denom = -denom;
  }
  const g = gcdNumber( Math.abs( num ), denom );
  num = num / g;
  denom = denom / g;
  return [ num, denom ];
}

// ── Reduce a BigInt pair ─────────────────────────────────────
// Returns { num, denom, big } — may demote to Number if small enough.
function reduceBigInts( num, denom ) {
  if ( denom === 0n )
    throw new Error( "Denominator is zero" );
  if ( num === 0n )
    return { num: 0, denom: 1, big: false };

  // Ensure denom > 0
  if ( denom < 0n ) {
    num = -num;
    denom = -denom;
  }
  if ( denom !== 1n ) {
    const g = gcdBigInt( num < 0n ? -num : num, denom );
    if ( g !== 1n ) {
      num = num / g;
      denom = denom / g;
    }
  }
  // Try to demote to Number
  const absNum = num < 0n ? -num : num;
  if ( absNum <= MAX_SAFE && denom <= MAX_SAFE ) {
    return { num: Number( num ), denom: Number( denom ), big: false };
  }
  return { num, denom, big: true };
}

// ── Private constructor for pre-reduced values ───────────────
// This avoids repeating the reduction logic.  The public constructor
// handles arbitrary inputs; this one trusts the caller.
function createReduced( num, denom, big ) {
  const r = Object.create( JavaBigRational.prototype );
  r.num = num;
  r.denom = denom;
  r.big = big;
  return r;
}

export class JavaBigRational
{
  // Accepts Number, BigInt, or mixed.  Always reduces.
  constructor( num, denom )
  {
    // Normalise input types
    const numIsBig  = typeof num   === 'bigint';
    const denIsBig  = typeof denom === 'bigint';

    if ( !numIsBig && !denIsBig ) {
      // Both are Number — fast path
      const pair = reduceNumbers( num, denom );
      this.num   = pair[0];
      this.denom = pair[1];
      this.big   = false;
      return;
    }

    // At least one is BigInt — promote both to BigInt
    const bn = numIsBig  ? num   : BigInt( num );
    const bd = denIsBig  ? denom : BigInt( denom );
    const r  = reduceBigInts( bn, bd );
    this.num   = r.num;
    this.denom = r.denom;
    this.big   = r.big;
  }

  equals( that )
  {
    return this.num === that.num && this.denom === that.denom;
  }

  toString()
  {
    if ( this.big )
      return this.denom === 1n
        ? this.num.toString()
        : this.num.toString() + "/" + this.denom.toString();
    return this.denom === 1
      ? this.num.toString()
      : this.num + "/" + this.denom;
  }

  getMathML()
  {
    const ns = this.num.toString();
    const ds = this.denom.toString();
    const isWhole = this.big ? this.denom === 1n : this.denom === 1;
    return isWhole
      ? `<mn>${ns}</mn>`
      : `<mfrac><mn>${ns}</mn><mn>${ds}</mn></mfrac>`;
  }

  evaluate()
  {
    return Number( this.num ) / Number( this.denom );
  }

  isZero()
  {
    // After reduction, zero is always 0/1 in Number form
    return this.num === 0;
  }

  isOne()
  {
    return this.num === 1 && this.denom === 1;
  }

  isNegative()
  {
    return this.big ? this.num < 0n : this.num < 0;
  }

  negate()
  {
    if ( this.isZero() ) return this;
    return createReduced( -this.num, this.denom, this.big );
  }

  reciprocal()
  {
    if ( this.isOne() ) return this;
    if ( this.isZero() ) throw new Error( "Denominator is zero" );

    // Swap num and denom; fix sign so denom stays positive
    let n = this.denom;
    let d = this.num;
    if ( this.big ? d < 0n : d < 0 ) {
      n = -n;
      d = -d;
    }
    return createReduced( n, d, this.big );
  }

  // Always returns BigInt — callers in common.js expect BigInt for multiplication
  getNumerator()
  {
    return this.big ? this.num : BigInt( this.num );
  }

  getDenominator()
  {
    return this.big ? this.denom : BigInt( this.denom );
  }

  plus( that )
  {
    if ( this.isZero() ) return that;
    if ( that.isZero() ) return this;

    // Fast path: both are Number representation
    if ( !this.big && !that.big ) {
      if ( this.denom === that.denom ) {
        const n = safeAdd( this.num, that.num );
        if ( n !== null )
          return new JavaBigRational( n, this.denom );
      } else {
        const n1 = safeMul( this.num, that.denom );
        const n2 = safeMul( that.num, this.denom );
        if ( n1 !== null && n2 !== null ) {
          const n = safeAdd( n1, n2 );
          const d = safeMul( this.denom, that.denom );
          if ( n !== null && d !== null )
            return new JavaBigRational( n, d );
        }
      }
    }

    // Slow path: promote to BigInt
    const tn = this.big ? this.num : BigInt( this.num );
    const td = this.big ? this.denom : BigInt( this.denom );
    const on = that.big ? that.num : BigInt( that.num );
    const od = that.big ? that.denom : BigInt( that.denom );

    if ( td === od ) {
      return new JavaBigRational( tn + on, td );
    }
    return new JavaBigRational( tn * od + on * td, td * od );
  }

  times( that )
  {
    if ( this.isOne() ) return that;
    if ( that.isOne() ) return this;
    if ( this.isZero() || that.isZero() ) return ZERO;

    // Fast path: both are Number representation
    if ( !this.big && !that.big ) {
      const n = safeMul( this.num, that.num );
      const d = safeMul( this.denom, that.denom );
      if ( n !== null && d !== null )
        return new JavaBigRational( n, d );
    }

    // Slow path: promote to BigInt
    const n = ( this.big ? this.num : BigInt( this.num ) )
            * ( that.big ? that.num : BigInt( that.num ) );
    const d = ( this.big ? this.denom : BigInt( this.denom ) )
            * ( that.big ? that.denom : BigInt( that.denom ) );
    return new JavaBigRational( n, d );
  }

  timesInt( i )
  {
    if ( i === 1 ) return this;
    if ( i === 0 ) return ZERO;

    if ( !this.big ) {
      const n = safeMul( this.num, i );
      if ( n !== null )
        return new JavaBigRational( n, this.denom );
    }

    // Slow path
    const bn = this.big ? this.num : BigInt( this.num );
    const bd = this.big ? this.denom : BigInt( this.denom );
    return new JavaBigRational( bn * BigInt( i ), bd );
  }
}

export const ZERO = new JavaBigRational( 0, 1 );
export const ONE  = new JavaBigRational( 1, 1 );
