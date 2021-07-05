

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
