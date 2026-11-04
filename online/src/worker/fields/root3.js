
import { simplify3, createField } from './common.js'

const SQRT3 = Math.sqrt(3)

const MAX_SAFE = Number.MAX_SAFE_INTEGER;

function reciprocal( x )
{
  if ( typeof x[0] !== 'bigint' ) {
    const [ a=0, b=0, c=1 ] = x;
    const r0 = a*c, r1 = -b*c, det = a*a - 3*b*b;
    if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE && det >= -MAX_SAFE && det <= MAX_SAFE )
      return simplify3( r0, r1, det );
  }
  const a = BigInt(x[0] || 0), b = BigInt(x[1] || 0), c = BigInt(x[2] || 1);
  return simplify3( a*c, 0n - b*c, a*a - 3n*b*b )
}

function times( a, b )
{
  if ( typeof a[0] !== 'bigint' && typeof b[0] !== 'bigint' ) {
    const [ a0=0, a1=0, ad=1 ] = a, [ b0=0, b1=0, bd=1 ] = b;
    if ( a0 === 0 && a1 === 0 ) return [ 0, 0, 1 ];
    if ( b0 === 0 && b1 === 0 ) return [ 0, 0, 1 ];
    if ( a1 === 0 ) {
      const r0 = a0*b0, r1 = a0*b1, d = ad*bd;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE && d >= -MAX_SAFE && d <= MAX_SAFE )
        return simplify3( r0, r1, d );
    } else if ( b1 === 0 ) {
      const r0 = a0*b0, r1 = a1*b0, d = ad*bd;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE && d >= -MAX_SAFE && d <= MAX_SAFE )
        return simplify3( r0, r1, d );
    } else {
      const t0a = a0*b0, t0b = 3*a1*b1;
      const r0 = t0a + t0b, r1 = a0*b1 + a1*b0, d = ad*bd;
      if ( r0 >= -MAX_SAFE && r0 <= MAX_SAFE && r1 >= -MAX_SAFE && r1 <= MAX_SAFE && d >= -MAX_SAFE && d <= MAX_SAFE
        && t0a >= -MAX_SAFE && t0a <= MAX_SAFE && t0b >= -MAX_SAFE && t0b <= MAX_SAFE )
        return simplify3( r0, r1, d );
    }
  }
  const a0 = BigInt(a[0] || 0), a1 = BigInt(a[1] || 0), ad = BigInt(a[2] || 1);
  const b0 = BigInt(b[0] || 0), b1 = BigInt(b[1] || 0), bd = BigInt(b[2] || 1);
  return simplify3( a0*b0 + 3n*a1*b1, a0*b1 + a1*b0, ad*bd )
}

function embed( a )
{
  const a0 = Number(a[0] || 0), a1 = Number(a[1] || 0), ad = Number(a[2] || 1);
  return ( a0 + SQRT3 * a1 ) / ad
}

const getIrrational = () => '√3';

const field = createField( { name: 'rootThree', order: 2, times, embed, reciprocal, getIrrational } );

export default field
