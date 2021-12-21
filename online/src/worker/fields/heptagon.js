

import { simplify4, createField } from './common.js'

const RHO = 1.8019377358
const SIGMA = 2.2469796037

// Thanks to Nan Ma for getting the matrix inverse of
//     A     B     C
//     B    A+C   B+C
//     C    B+C  A+B+C
function reciprocal( x )
{
  const [ A=0n, B=0n, C=0n, D=1n ] = x
  const A1 = A*A + A*B + 2n*A*C - B*B - B*C
  const B1 = C*C - A*B - B*B
  const C1 = B*B + B*C - A*C - C*C
  const determinant = A*A1 + B*B1 + C*C1
  return simplify4( D*A1, D*B1, D*C1, determinant )
}

function times( first, second )
{
  const [ a=0n, b=0n, c=0n, d1=1n ] = first, [ d=0n, e=0n, f=0n, d2=1n ] = second
  return simplify4( a*d + b*e + c*f, a*e + b*d + b*f + c*e + c*f, a*f + b*e + b*f + c*d + c*e + c*f, d1*d2 )
}

function embed( a )
{
  const [ a0=0n, a1=0n, a2=0n, ad=1n ] = a
  return ( Number(a0) + RHO * Number(a1) + SIGMA * Number(a2) ) / Number(ad)
}

const field = createField( { name: 'heptagon', order: 3, times, embed, reciprocal } )

export default field
