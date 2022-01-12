
import { simplify3, createField } from './common.js'

const SQRT2 = Math.sqrt(2)

function reciprocal( x )
{
  const [ a=0n, b=0n, c=1n ] = x;
  return simplify3( a*c, 0n - b*c, a*a - 2n*b*b )
}

function times( a, b )
{
  const [ a0=0n, a1=0n, ad=1n ] = a, [ b0=0n, b1=0n, bd=1n ] = b
  return simplify3( a0*b0 + 2n*a1*b1, a0*b1 + a1*b0, ad*bd )
}

function embed( a )
{
  const [ a0=0n, a1=0n, ad=1n ] = a
  return ( Number(a0) + SQRT2 * Number(a1) ) / Number(ad)
}

const field = createField( { name: 'rootTwo', order: 2, times, embed, reciprocal } )

export default field
