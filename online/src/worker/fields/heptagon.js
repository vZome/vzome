

import { simplify4, createField } from './common.js'

const RHO = 1.8019377358
const SIGMA = 2.2469796037

const MAX_SAFE = Number.MAX_SAFE_INTEGER;

// Thanks to Nan Ma for getting the matrix inverse of
//     A     B     C
//     B    A+C   B+C
//     C    B+C  A+B+C
function reciprocal( x )
{
  if ( typeof x[0] !== 'bigint' ) {
    const [ A=0, B=0, C=0, D=1 ] = x;
    const A1 = A*A + A*B + 2*A*C - B*B - B*C;
    const B1 = C*C - A*B - B*B;
    const C1 = B*B + B*C - A*C - C*C;
    const det = A*A1 + B*B1 + C*C1;
    const r0 = D*A1, r1 = D*B1, r2 = D*C1;
    if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE
      && r2 >= -MAX_SAFE && r2 <= MAX_SAFE && det >= -MAX_SAFE && det <= MAX_SAFE
      && A1 >= -MAX_SAFE && A1 <= MAX_SAFE && B1 >= -MAX_SAFE && B1 <= MAX_SAFE && C1 >= -MAX_SAFE && C1 <= MAX_SAFE )
      return simplify4( r0, r1, r2, det );
  }
  const A = BigInt(x[0] || 0), B = BigInt(x[1] || 0), C = BigInt(x[2] || 0), D = BigInt(x[3] || 1);
  const A1 = A*A + A*B + 2n*A*C - B*B - B*C
  const B1 = C*C - A*B - B*B
  const C1 = B*B + B*C - A*C - C*C
  const determinant = A*A1 + B*B1 + C*C1
  return simplify4( D*A1, D*B1, D*C1, determinant )
}

function times( first, second )
{
  if ( typeof first[0] !== 'bigint' && typeof second[0] !== 'bigint' ) {
    const [ a=0, b=0, c=0, d1=1 ] = first, [ d=0, e=0, f=0, d2=1 ] = second;
    if ( a === 0 && b === 0 && c === 0 ) return [ 0, 0, 0, 1 ];
    if ( d === 0 && e === 0 && f === 0 ) return [ 0, 0, 0, 1 ];
    if ( b === 0 && c === 0 ) {
      // integer * heptagon: [a*d, a*e, a*f, d1*d2]
      const r0 = a*d, r1 = a*e, r2 = a*f, dd = d1*d2;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE
        && r2 >= -MAX_SAFE && r2 <= MAX_SAFE && dd >= -MAX_SAFE && dd <= MAX_SAFE )
        return simplify4( r0, r1, r2, dd );
    } else if ( e === 0 && f === 0 ) {
      // heptagon * integer: [a*d, b*d, c*d, d1*d2]
      const r0 = a*d, r1 = b*d, r2 = c*d, dd = d1*d2;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE
        && r2 >= -MAX_SAFE && r2 <= MAX_SAFE && dd >= -MAX_SAFE && dd <= MAX_SAFE )
        return simplify4( r0, r1, r2, dd );
    } else {
      const r0 = a*d + b*e + c*f;
      const r1 = a*e + b*d + b*f + c*e + c*f;
      const r2 = a*f + b*e + b*f + c*d + c*e + c*f;
      const dd = d1*d2;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE
        && r2 >= -MAX_SAFE && r2 <= MAX_SAFE && dd >= -MAX_SAFE && dd <= MAX_SAFE )
        return simplify4( r0, r1, r2, dd );
    }
  }
  const a = BigInt(first[0] || 0), b = BigInt(first[1] || 0), c = BigInt(first[2] || 0), d1 = BigInt(first[3] || 1);
  const d = BigInt(second[0] || 0), e = BigInt(second[1] || 0), f = BigInt(second[2] || 0), d2 = BigInt(second[3] || 1);
  return simplify4( a*d + b*e + c*f, a*e + b*d + b*f + c*e + c*f, a*f + b*e + b*f + c*d + c*e + c*f, d1*d2 )
}

function embed( a )
{
  const a0 = Number(a[0] || 0), a1 = Number(a[1] || 0), a2 = Number(a[2] || 0), ad = Number(a[3] || 1);
  return ( a0 + RHO * a1 + SIGMA * a2 ) / ad
}

const getIrrational = (i) => (i===1)? '𝜌' : '𝜎';

const field = createField( { name: 'heptagon', order: 3, times, embed, reciprocal, getIrrational } );

export default field
