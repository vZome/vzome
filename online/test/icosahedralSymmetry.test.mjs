// Performance benchmark: apply icosahedral symmetry to random golden-field vectors.
//
// Run:  node --import ./test/setup.mjs --test ./test/icosahedralSymmetry.test.mjs

import { describe, it } from 'node:test';
import goldenField from '../src/worker/fields/golden.js';

const { quatTransform, quaternions } = goldenField;

function randomGoldenNumber() {
  // Random golden-field number: a0 + a1*φ, with small random integer coefficients
  const a0 = Math.floor( Math.random() * 21 ) - 10;  // -10..10
  const a1 = Math.floor( Math.random() * 21 ) - 10;
  return [ a0, a1, 1 ];
}

function randomVector3() {
  return [ randomGoldenNumber(), randomGoldenNumber(), randomGoldenNumber() ];
}

describe( 'Icosahedral symmetry benchmark', () => {
  it( 'applies 120 quaternions to 20000 random vectors', () => {
    const iterations = 20000;
    const start = performance.now();
    for ( let i = 0; i < iterations; i++ ) {
      const v = randomVector3();
      for ( const q of quaternions ) {
        quatTransform( q, v );
      }
    }
    const elapsed = performance.now() - start;
    console.log( `${iterations} vectors × ${quaternions.length} quaternions = ${iterations * quaternions.length} transforms in ${elapsed.toFixed(1)} ms` );
  });
});
