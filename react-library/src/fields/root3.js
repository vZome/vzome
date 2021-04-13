
import { simplify3, createField } from './common.js'

const SQRT3 = Math.sqrt(3)

function reciprocal( x )
{
  const [ a=0, b=0, c=1 ] = x;
  return simplify3( a*c, 0 - b*c, a*a - 3*b*b )
}

function times( a, b )
{
  const [ a0=0, a1=0, ad=1] = a, [b0=0, b1=0, bd=1 ] = b
  return simplify3( a0*b0 + 3*a1*b1, a0*b1 + a1*b0, ad*bd )
}

function embed( a )
{
  const [ a0=0, a1=0, ad=1 ] = a
  return ( a0 + SQRT3 * a1 ) / ad
}

const field = createField( { name: 'rootThree', order: 2, times, embed, reciprocal } )

export default field
