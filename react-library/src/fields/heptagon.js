

import { simplify4, createField } from './common.js'

const RHO = 1.8019377358
const SIGMA = 2.2469796037

// Thanks to Nan Ma for getting the matrix inverse of
//     A     B     C
//     B    A+C   B+C
//     C    B+C  A+B+C
function reciprocal( x )
{
  const [ A=0, B=0, C=0, D=1 ] = x
  const A1 = A*A + A*B + 2*A*C - B*B - B*C
  const B1 = C*C - A*B - B*B
  const C1 = B*B + B*C - A*C - C*C
  const determinant = A*A1 + B*B1 + C*C1
  return simplify4( D*A1, D*B1, D*C1, determinant )
}

function times( first, second )
{
  const [ a=0, b=0, c=0, d1=1 ] = first, [ d=0, e=0, f=0, d2=1 ] = second
  return simplify4( a*d + b*e + c*f, a*e + b*d + b*f + c*e + c*f, a*f + b*e + b*f + c*d + c*e + c*f, d1*d2 )
}

function embed( a )
{
  const [ a0=0, a1=0, a2=0, ad=1 ] = a
  return ( a0 + RHO * a1 + SIGMA * a2 ) / ad
}

const field = createField( { name: 'heptagon', order: 3, times, embed, reciprocal } )

export default field
