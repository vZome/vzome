
import { simplify, createNumberFromPairs } from '../fields/common'

export const algebraicNumberFactory =
{
  zero: () => new JavaBigRational( 0n, 1n ),

  one: () => new JavaBigRational( 1n, 1n ),

  createRational: ( legacyField, num, denom ) =>
    {
      const order = legacyField.getOrder()
      const factors = [ new JavaBigRational( num, denom ) ]
      for ( let index = 1; index < order; index++ ) {
        factors.push( ZERO )
      }
      return new JavaAlgebraicNumber( legacyField, factors )
    },

  createAlgebraicNumber: ( legacyField, numerators, divisor ) =>
    {
      const bigRats = []
      for (let i = 0; i < numerators.length; i++) {
        bigRats.push( new JavaBigRational( numerators[ i ], divisor ) )
      }
      return new JavaAlgebraicNumber( legacyField, bigRats )
    },

  createAlgebraicNumberFromTD: ( legacyField, trailingDivisor ) =>
    {
      const bigRats = []
      const divisor = trailingDivisor[ trailingDivisor.length-1 ]
      for (let i = 0; i < trailingDivisor.length-1; i++) {
        bigRats.push( new JavaBigRational( trailingDivisor[ i ], divisor ) )
      }
      return new JavaAlgebraicNumber( legacyField, bigRats )
    },

  createAlgebraicNumberFromPairs: ( legacyField, pairs ) =>
    {
      const order = pairs.length / 2
      const bigRats = []
      for (let i = 0; i < order; i++) {
        bigRats.push( new JavaBigRational( pairs[ 2*i ], pairs[ 2*i+1 ] ) )
      }
      return new JavaAlgebraicNumber( legacyField, bigRats )
    }
}

class JavaAlgebraicNumber
{
  constructor( legacyField, bigRationals )
  {
    this.legacyField = legacyField
    this.bigRationals = bigRationals
    this.__interfaces = [ "com.vzome.core.algebra.AlgebraicNumber" ]
    this[ 'times$com_vzome_core_algebra_AlgebraicNumber' ] = this.times
    this[ 'plus$com_vzome_core_algebra_AlgebraicNumber' ] = this.plus
    this[ 'minus$com_vzome_core_algebra_AlgebraicNumber' ] = this.minus
  }

  getField()
  {
    return this.legacyField
  }

  evaluate()
  {
    return this.legacyField.evaluateNumber( this.bigRationals )
  }

  isZero()
  {
    return this.bigRationals.reduce( ( a, c ) => a && c.isZero(), true )
  }

  isOne()
  {
    return this.bigRationals[ 0 ].isOne() && this.bigRationals.slice( 1 ).reduce( ( a, c ) => a && c.isZero(), true )
  }

  toTrailingDivisor()
  {
    const pairs = this.bigRationals.reduce( ( a, current ) => a.concat( [ current.getNumerator(), current.getDenominator() ] ), [] )
    return createNumberFromPairs( pairs )
  }

  negate()
  {
    return new JavaAlgebraicNumber( this.legacyField, this.bigRationals.map( br => br.negate() ) )
  }

  reciprocal()
  {
    return new JavaAlgebraicNumber( this.legacyField, this.legacyField.reciprocal( this.bigRationals ) )
  }
  
  minus( that )
  {
    return this.plus( that.negate() )
  }

  plus( that )
  {
    if (this.isZero()) {
      return that
    }
    if (that.isZero()) {
      return this
    }
    const sums = this.bigRationals.map( (v,i) => v.plus( that.bigRationals[ i ] ) )
    return new JavaAlgebraicNumber( this.legacyField, sums )
  }

  times( that )
  {
    if ( this.isZero() || that.isZero() )
        return this.legacyField.zero();
    if ( this.isOne() )
        return that;
    if ( that.isOne() )
        return this;
    const pairs = this.legacyField.multiply( this.bigRationals, that.bigRationals );
    return new JavaAlgebraicNumber( this.legacyField, pairs )
  }

  dividedBy( that )
  {
    return this.times( that.reciprocal() )
  }

  getNumberExpression() {}
}

class JavaBigRational
{
  constructor( num, denom )
  {
    [ num, denom ] = simplify( [ BigInt(num), BigInt(denom) ] )
    this.num = num
    this.denom = denom
  }

  evaluate()
  {
    return Number( this.num ) / Number( this.denom )
  }

  isZero()
  {
    return this.num === 0n
  }

  isOne()
  {
    return this.num === 1n && this.denom === 1n
  }

  negate()
  {
    return new JavaBigRational( -this.num, this.denom )
  }

  reciprocal()
  {
    if ( this.isOne() )
      return this
    return new JavaBigRational( this.denom, this.num )
  }

  getNumerator()
  {
    return this.num
  }

  getDenominator()
  {
    return this.denom
  }

  plus( that )
  {
    if (this.isZero()) {
      return that
    }
    if (that.isZero()) {
      return this
    }
    if (this.denom === that.denom) {
      return new JavaBigRational( this.num + that.num, this.denom )
    }
    // different denominators
    const d = this.denom * that.denom
    return new JavaBigRational( this.num*that.denom + that.num*this.denom, d )
  }

  times( that )
  {
    if ( this.isOne() ) {
        return that;
    }
    if ( that.isOne() ) {
        return this;
    }
    if ( this.isZero() || that.isZero() ) {
      return ZERO
    }
    return new JavaBigRational( this.num*that.num, this.denom*that.denom )
  }

  timesInt( i )
  {
    if ( i === 1 )
      return this
    if ( i === 0 )
      return ZERO
    return new JavaBigRational( this.num * BigInt(i), this.denom )
  }
}

const ZERO = new JavaBigRational( 0n, 1n )

class JavaDomNodeList
{
  constructor( nodeList )
  {
    this.nativeNodeList = nodeList
    this.__interfaces = [ "org.w3c.dom.NodeList" ]
  }

  getLength()
  {
    return this.nativeNodeList.length
  }

  item( i )
  {
    const node = this.nativeNodeList.item( i )
    if ( node.nodeType === 1 )
      return new JavaDomElement( node )
    else
      return node
  }
}

export class JavaDomElement
{
  constructor( element )
  {
    this.nativeElement = element
    this.__interfaces = [ "org.w3c.dom.Element" ]
  }

  getAttribute( name )
  {
    return this.nativeElement.getAttribute && this.nativeElement.getAttribute( name )
  }

  getLocalName()
  {
    return this.nativeElement.localName
  }

  getTextContent()
  {
    return this.nativeElement.textContent
  }

  getChildNodes()
  {
    return new JavaDomNodeList( this.nativeElement.childNodes )
  }

  getChildElement( name )
  {
    let target = this.nativeElement.firstElementChild
    while ( target && name.toLowerCase() !== target.nodeName.toLowerCase() )
      target = target.nextElementSibling
    return target && new JavaDomElement( target )
  }

  getElementsByTagName( name )
  {
    let target = this.nativeElement.firstElementChild
    const results = []
    while ( target ) {
      if ( name.toLowerCase() === target.nodeName.toLowerCase() ) {
        results.push( new JavaDomElement( target ) )
      }
      target = target.nextElementSibling
    }
    return { getLength: () => results.length, item: i => results[ i ] }
  }
}

export class JsProperties
{
  constructor( config )
  {
    this.config = config
  }

  getProperty( key )
  {
    return this.config[ key ]
  }

  get( key )
  {
    return this.config[ key ]
  }
}
