// Regression tests for JavaBigRational, ported from
// core/src/test/java/com/vzome/core/algebra/BigRationalTest.java
//
// Run:  node --import ./test/setup.mjs --test ./test/bigRational.test.mjs

import { describe, it } from 'node:test';
import { strict as assert } from 'node:assert';
import { JavaBigRational, ZERO, ONE } from '../src/worker/legacy/bigRational.js';

// ── helpers ──────────────────────────────────────────────────────────

/** Create a BigRational from a string like "2/3", "-17", or "0". */
function br( s ) {
  const parts = s.split( '/' );
  if ( parts.length === 1 )
    return new JavaBigRational( BigInt( parts[0] ), 1n );
  return new JavaBigRational( BigInt( parts[0] ), BigInt( parts[1] ) );
}

/** Shorthand: create from two numbers/bigints */
function rat( num, den ) {
  return new JavaBigRational( num, den );
}

// Values analogous to Java Long.MAX_VALUE / MIN_VALUE (2^63).
// These exceed Number.MAX_SAFE_INTEGER, so they force the BigInt code path.
const LONG_MAX = 9223372036854775807n;
const LONG_MIN = -9223372036854775808n;

// Large values that definitely exceed 64-bit range (analogous to Java's "big" tests)
const BIG_NUM = LONG_MAX * 10n;
const BIG_DEN = LONG_MAX * 10n + 3n;

// ── tests ────────────────────────────────────────────────────────────

describe( 'JavaBigRational', () => {

  // ── toString ───────────────────────────────────────────────

  describe( 'toString', () => {
    it( 'formats a fraction', () => {
      assert.equal( rat( 2n, 3n ).toString(), '2/3' );
    });
    it( 'formats a negative fraction', () => {
      assert.equal( rat( -2n, 3n ).toString(), '-2/3' );
    });
    it( 'formats zero', () => {
      assert.equal( rat( 0n, 3n ).toString(), '0' );
    });
    it( 'formats a whole number (denominator 1)', () => {
      assert.equal( rat( 42n, 1n ).toString(), '42' );
    });
  });

  // ── isZero / isOne / isNegative ────────────────────────────

  describe( 'isZero', () => {
    it( 'zero is zero', () => assert.ok( ZERO.isZero() ) );
    it( 'one is not zero', () => assert.ok( !ONE.isZero() ) );
    it( '-1 is not zero', () => assert.ok( !rat( -1n, 1n ).isZero() ) );
  });

  describe( 'isOne', () => {
    it( 'one is one', () => assert.ok( ONE.isOne() ) );
    it( 'zero is not one', () => assert.ok( !ZERO.isOne() ) );
    it( '-1 is not one', () => assert.ok( !rat( -1n, 1n ).isOne() ) );
  });

  describe( 'isNegative', () => {
    it( '-1 is negative', () => assert.ok( rat( -1n, 1n ).isNegative() ) );
    it( '0 is not negative', () => assert.ok( !ZERO.isNegative() ) );
    it( '1 is not negative', () => assert.ok( !ONE.isNegative() ) );
  });

  // ── equals ─────────────────────────────────────────────────

  describe( 'equals', () => {
    it( 'zero equals zero', () => assert.ok( ZERO.equals( ZERO ) ) );
    it( 'zero equals new zero', () => assert.ok( ZERO.equals( rat( 0n, 1n ) ) ) );
    it( 'one equals new one', () => assert.ok( ONE.equals( rat( 1n, 1n ) ) ) );
    it( 'one does not equal negative one', () => assert.ok( !ONE.equals( rat( -1n, 1n ) ) ) );
    it( 'reduced forms are equal', () => {
      assert.ok( rat( 2n, 4n ).equals( rat( 1n, 2n ) ) );
    });
    it( 'negative denominator is normalised', () => {
      // -1/-1 should reduce to 1/1
      assert.ok( rat( -1n, -1n ).isOne() );
    });
  });

  // ── reduction ──────────────────────────────────────────────

  describe( 'reduction', () => {
    it( 'reduces 81/54 to 3/2', () => {
      const r = rat( 81n, 54n );
      assert.equal( r.getNumerator(), 3n );
      assert.equal( r.getDenominator(), 2n );
    });
    it( 'handles negative / negative → positive', () => {
      const r = rat( -81n, -54n );
      assert.equal( r.getNumerator(), 3n );
      assert.equal( r.getDenominator(), 2n );
    });
    it( 'reduces to whole number', () => {
      const r = rat( 6n, 3n );
      assert.equal( r.toString(), '2' );
    });
    it( 'zero numerator → 0', () => {
      const r = rat( 0n, 12345n );
      assert.ok( r.isZero() );
      assert.equal( r.toString(), '0' );
    });
  });

  // ── negative denominators ──────────────────────────────────

  describe( 'negative denominators', () => {
    it( '-1 / -1 is one', () => {
      const r = rat( -1n, -1n );
      assert.ok( r.isOne() );
      assert.equal( r.getNumerator(), 1n );
      assert.equal( r.getDenominator(), 1n );
    });
    it( '1 / -3 normalises sign to numerator', () => {
      const r = rat( 1n, -3n );
      assert.ok( r.isNegative() );
      assert.equal( r.getNumerator(), -1n );
      assert.equal( r.getDenominator(), 3n );
    });
  });

  // ── negate ─────────────────────────────────────────────────

  describe( 'negate', () => {
    it( 'negate(0) is 0', () => assert.ok( ZERO.negate().isZero() ) );
    it( 'negate(positive) is negative', () => {
      for ( let n = 1n; n <= 10n; n++ ) {
        const pos = rat( n, 1n );
        const neg = rat( -n, 1n );
        assert.ok( !pos.equals( neg ) );
        assert.ok( pos.negate().equals( neg ) );
        assert.ok( neg.negate().equals( pos ) );
      }
    });
    it( 'negate(big positive) is big negative', () => {
      const pos = rat( BIG_NUM, 1n );
      const neg = rat( -BIG_NUM, 1n );
      assert.ok( !pos.equals( neg ) );
      assert.ok( pos.negate().equals( neg ) );
      assert.ok( neg.negate().equals( pos ) );
    });
  });

  // ── reciprocal ─────────────────────────────────────────────

  describe( 'reciprocal', () => {
    it( 'reciprocal of 1 is 1', () => assert.ok( ONE.reciprocal().isOne() ) );
    it( 'reciprocal of a fraction', () => {
      const r = br( '37/19' );
      const rec = r.reciprocal();
      assert.equal( rec.getNumerator(), 19n );
      assert.equal( rec.getDenominator(), 37n );
      assert.ok( r.times( rec ).isOne() );
    });
    it( 'reciprocal of a big fraction', () => {
      const r = rat( BIG_NUM, 19n );
      const rec = r.reciprocal();
      assert.ok( r.times( rec ).isOne() );
    });
    it( 'reciprocal of zero throws', () => {
      assert.throws( () => ZERO.reciprocal(), /Denominator is zero/ );
    });
  });

  // ── evaluate (double approximation) ────────────────────────

  describe( 'evaluate', () => {
    it( '17 evaluates to 17.0', () => {
      assert.equal( rat( 17n, 1n ).evaluate(), 17 );
    });
    it( '-9/-36 evaluates to 0.25', () => {
      assert.equal( rat( -9n, -36n ).evaluate(), 0.25 );
    });
    it( '2/3 evaluates close to 0.6667', () => {
      const val = rat( 2n, 3n ).evaluate();
      assert( Math.abs( val - 2/3 ) < 1e-15 );
    });
  });

  // ── plus ───────────────────────────────────────────────────

  describe( 'plus', () => {
    it( '0 + 0 = 0', () => assert.equal( ZERO.plus( ZERO ).toString(), '0' ) );
    it( '0 + 1 = 1', () => assert.equal( ZERO.plus( ONE ).toString(), '1' ) );
    it( '1 + 0 = 1', () => assert.equal( ONE.plus( ZERO ).toString(), '1' ) );
    it( '1 + 1 = 2', () => assert.equal( ONE.plus( ONE ).toString(), '2' ) );

    it( '1/2 + 1/3 = 5/6', () => {
      assert.equal( br( '1/2' ).plus( br( '1/3' ) ).toString(), '5/6' );
    });
    it( '8/9 + 1/9 = 1', () => {
      assert.equal( br( '8/9' ).plus( br( '1/9' ) ).toString(), '1' );
    });
    it( '1/200000000 + 1/300000000 = 1/120000000', () => {
      assert.equal(
        br( '1/200000000' ).plus( br( '1/300000000' ) ).toString(),
        '1/120000000'
      );
    });
    it( '1073741789/20 + 1073741789/30 = 1073741789/12', () => {
      assert.equal(
        br( '1073741789/20' ).plus( br( '1073741789/30' ) ).toString(),
        '1073741789/12'
      );
    });
    it( '-1/200000000 + 1/300000000 = -1/600000000', () => {
      assert.equal(
        br( '-1/200000000' ).plus( br( '1/300000000' ) ).toString(),
        '-1/600000000'
      );
    });
    it( 'large overflow: MAX + 1 stays correct', () => {
      const max = rat( LONG_MAX, 1n );
      const result = max.plus( ONE );
      assert.equal( result.toString(), ( LONG_MAX + 1n ).toString() );
    });
    it( 'fraction overflow: MAX/3 + MAX/3', () => {
      const r = rat( LONG_MAX, 3n );
      const result = r.plus( r );
      // LONG_MAX*2 = 18446744073709551614, which is not divisible by 3
      // so the result stays as a fraction.
      assert.equal( result.toString(), '18446744073709551614/3' );
      assert.equal( result.getNumerator(), LONG_MAX * 2n ); // cross-check
      assert.equal( result.getDenominator(), 3n );
    });
  });

  // ── minus ──────────────────────────────────────────────────

  describe( 'minus (via plus + negate)', () => {
    it( '0 - 0 = 0', () => assert.equal( ZERO.plus( ZERO.negate() ).toString(), '0' ) );
    it( '0 - 1 = -1', () => assert.equal( ZERO.plus( ONE.negate() ).toString(), '-1' ) );
    it( '1 - 0 = 1', () => assert.equal( ONE.plus( ZERO.negate() ).toString(), '1' ) );
    it( '1 - 1 = 0', () => assert.equal( ONE.plus( ONE.negate() ).toString(), '0' ) );
    it( '5/7 - 11/7 = -6/7', () => {
      assert.equal( br( '5/7' ).plus( br( '11/7' ).negate() ).toString(), '-6/7' );
    });
    it( '7/5 - 11/7 = -6/35', () => {
      assert.equal( br( '7/5' ).plus( br( '11/7' ).negate() ).toString(), '-6/35' );
    });
    it( '1/6 - (-4/-8) = -1/3', () => {
      // -4/-8 reduces to 1/2
      assert.equal( br( '1/6' ).plus( rat( -4n, -8n ).negate() ).toString(), '-1/3' );
    });
  });

  // ── times ──────────────────────────────────────────────────

  describe( 'times', () => {
    it( '7/5 * 11/7 = 11/5  (7s cancel)', () => {
      assert.equal( br( '7/5' ).times( br( '11/7' ) ).toString(), '11/5' );
    });
    it( '2 * 0 = 0', () => assert.ok( rat( 2n, 1n ).times( ZERO ).isZero() ) );
    it( '0 * 2 = 0', () => assert.ok( ZERO.times( rat( 2n, 1n ) ).isZero() ) );
    it( '0 * 1 = 0', () => assert.ok( ZERO.times( ONE ).isZero() ) );
    it( '1 * 0 = 0', () => assert.ok( ONE.times( ZERO ).isZero() ) );
    it( '1 * 2 = 2 * 1  (commutative)', () => {
      assert.ok( ONE.times( rat( 2n, 1n ) ).equals( rat( 2n, 1n ).times( ONE ) ) );
    });
    it( '4/17 * 17/4 = 1', () => {
      assert.equal( br( '4/17' ).times( br( '17/4' ) ).toString(), '1' );
    });
    it( '3037141/3247033 * 3037547/3246599 = 841/961', () => {
      assert.equal(
        br( '3037141/3247033' ).times( br( '3037547/3246599' ) ).toString(),
        '841/961'
      );
    });
    it( 'large multiplication stays correct', () => {
      const big2 = rat( 2n, 1n );
      const max = rat( LONG_MAX, 1n );
      const result = max.times( big2 );
      assert.equal( result.toString(), ( LONG_MAX * 2n ).toString() );
    });
  });

  // ── timesInt ───────────────────────────────────────────────

  describe( 'timesInt', () => {
    it( 'n * 1 = n', () => {
      const r = rat( 37n, 1n );
      assert.equal( r.timesInt( 1 ).toString(), '37' );
    });
    it( 'n * 0 = 0', () => {
      assert.equal( rat( 37n, 1n ).timesInt( 0 ).toString(), '0' );
    });
    it( '43 * 7 = 301', () => {
      assert.equal( rat( 43n, 1n ).timesInt( 7 ).toString(), '301' );
    });
    it( '0 * 6 = 0', () => {
      assert.equal( ZERO.timesInt( 6 ).toString(), '0' );
    });
    it( 'fraction * int', () => {
      assert.equal( br( '1/3' ).timesInt( 6 ).toString(), '2' );
    });
  });

  // ── dividedBy (via reciprocal + times) ─────────────────────

  describe( 'dividedBy', () => {
    function dividedBy( a, b ) {
      return a.times( b.reciprocal() );
    }
    it( '1 / 2 = 1/2', () => {
      assert.equal( dividedBy( ONE, rat( 2n, 1n ) ).toString(), '1/2' );
    });
    it( '2 / 2 = 1', () => {
      assert.equal( dividedBy( rat( 2n, 1n ), rat( 2n, 1n ) ).toString(), '1' );
    });
    it( '2 / 1 = 2', () => {
      assert.equal( dividedBy( rat( 2n, 1n ), ONE ).toString(), '2' );
    });
    it( '0 / 1 = 0', () => {
      assert.equal( dividedBy( ZERO, ONE ).toString(), '0' );
    });
    it( 'divide by zero throws', () => {
      assert.throws( () => dividedBy( ONE, ZERO ), /Denominator is zero/ );
    });
  });

  // ── getMathML ──────────────────────────────────────────────

  describe( 'getMathML', () => {
    it( 'whole number', () => {
      assert.equal( rat( 42n, 1n ).getMathML(), '<mn>42</mn>' );
    });
    it( 'fraction', () => {
      assert.equal( rat( 2n, 3n ).getMathML(), '<mfrac><mn>2</mn><mn>3</mn></mfrac>' );
    });
  });

  // ── getNumerator / getDenominator ──────────────────────────

  describe( 'getNumerator / getDenominator', () => {
    it( 'returns correct values', () => {
      const r = rat( 2n, 3n );
      assert.equal( r.getNumerator(), 2n );
      assert.equal( r.getDenominator(), 3n );
    });
    it( 'returns reduced values', () => {
      const r = rat( 4n, 6n );
      assert.equal( r.getNumerator(), 2n );
      assert.equal( r.getDenominator(), 3n );
    });
  });

  // ── big numbers (beyond 64-bit) ────────────────────────────

  describe( 'big numbers', () => {
    it( 'compareTo with big numbers', () => {
      const r1 = rat( -BIG_NUM, BIG_DEN );
      const r2 = rat( BIG_NUM, -BIG_DEN );
      // Both represent the same negative fraction
      assert.ok( r1.equals( r2 ) );
    });
    it( 'big reciprocal roundtrip', () => {
      const r = rat( BIG_NUM, 19n );
      assert.ok( r.times( r.reciprocal() ).isOne() );
    });
    it( 'big addition', () => {
      const a = rat( BIG_NUM, 1n );
      const b = rat( 1n, 1n );
      assert.equal( a.plus( b ).toString(), ( BIG_NUM + 1n ).toString() );
    });
    it( 'big multiplication', () => {
      const a = rat( BIG_NUM, 1n );
      const b = rat( 2n, 1n );
      assert.equal( a.times( b ).toString(), ( BIG_NUM * 2n ).toString() );
    });
    it( 'big subtraction to zero', () => {
      const a = rat( BIG_NUM, BIG_DEN );
      const result = a.plus( a.negate() );
      assert.ok( result.isZero() );
    });
    it( 'very large relatively prime fraction', () => {
      const sNum = 55692240674506394991082821978149278838567587240332360n;
      const sDen = 82146474236873358310743188506279129062594221951901256610699966876769677815441n;
      const r = rat( sNum, sDen );
      assert.equal( r.getNumerator(), sNum );
      assert.equal( r.getDenominator(), sDen );
    });
  });

  // ── harmonic means (stress test) ───────────────────────────

  describe( 'harmonic means', () => {
    it( 'harmonic means of 1..500 stays positive and diminishing', () => {
      // Ported from Java: computes reciprocal of sum of reciprocals.
      // Validates no sign errors or precision loss in long chains of additions.
      let last = 2.0;
      let sum = ZERO;
      const limit = 500; // Java used 1000; 500 is adequate and faster
      for ( let i = 1; i <= limit; i++ ) {
        const recip = rat( 1n, BigInt( i ) );
        sum = sum.plus( recip );
        const harmonicMean = sum.reciprocal();
        const curr = harmonicMean.evaluate();
        assert.ok( curr > 0, `positive at i=${i}` );
        assert.ok( last > curr, `diminishing at i=${i}` );
        assert.ok( Number.isFinite( curr ), `finite at i=${i}` );
        last = curr;
      }
    });
  });

  // ── bugfix1 from Java tests ────────────────────────────────

  describe( 'bugfix1', () => {
    it( 'addition with power-of-two denominator', () => {
      const den = 4294967296n;
      const n1 = 19401945n;
      const n2 = 949299809n;
      const br1 = rat( n1, den );
      const br2 = rat( n2, den );
      // Should not throw
      const result = br1.plus( br2 );
      assert.equal( result.getDenominator(), den / ( function gcd( a, b ) {
        a = a < 0n ? -a : a; b = b < 0n ? -b : b;
        while ( b !== 0n ) { [a, b] = [b, a % b]; }
        return a;
      })( n1 + n2, den ) );
    });
  });

  // ── testMain from Java ─────────────────────────────────────

  describe( 'testMain (ported from Java)', () => {
    it( '1/2 + 1/3 = 5/6', () => {
      assert.equal( br('1/2').plus( br('1/3') ).toString(), '5/6' );
    });
    it( '8/9 + 1/9 = 1', () => {
      assert.equal( br('8/9').plus( br('1/9') ).toString(), '1' );
    });
    it( '1/200000000 + 1/300000000 = 1/120000000', () => {
      assert.equal( br('1/200000000').plus( br('1/300000000') ).toString(), '1/120000000' );
    });
    it( '1073741789/20 + 1073741789/30 = 1073741789/12', () => {
      assert.equal( br('1073741789/20').plus( br('1073741789/30') ).toString(), '1073741789/12' );
    });
    it( '4/17 * 17/4 = 1', () => {
      assert.equal( br('4/17').times( br('17/4') ).toString(), '1' );
    });
    it( '3037141/3247033 * 3037547/3246599 = 841/961', () => {
      assert.equal( br('3037141/3247033').times( br('3037547/3246599') ).toString(), '841/961' );
    });
    it( '1/6 - (-4/-8) = -1/3', () => {
      const x = br('1/6');
      const y = rat( -4n, -8n ); // reduces to 1/2
      assert.equal( x.plus( y.negate() ).toString(), '-1/3' );
    });
    it( '0/5 reciprocal throws', () => {
      const x = rat( 0n, 5n );
      assert.ok( x.plus( x ).equals( x ) ); // 0 + 0 = 0
      assert.throws( () => x.reciprocal(), /Denominator is zero/ );
    });
    it( '-1/200000000 + 1/300000000 = -1/600000000', () => {
      assert.equal( br('-1/200000000').plus( br('1/300000000') ).toString(), '-1/600000000' );
    });
  });

});
