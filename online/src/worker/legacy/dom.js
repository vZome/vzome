

class JavaDomNodeList
{
  constructor( nodeList, owner=null )
  {
    this.nativeNodeList = nodeList
    this.document = owner || new JavaDomDocument();
  }

  getLength()
  {
    return this.nativeNodeList.length
  }

  item( i )
  {
    const node = this.nativeNodeList[ i ];
    if ( node.tagName )
      return new JavaDomElement( node, this.document )
    else
      return node
  }
}
JavaDomNodeList.__interfaces = [ "org.w3c.dom.NodeList" ]

function sortObj(obj) {
  return Object.keys(obj).sort().reduce(function (result, key) {
    result[key] = obj[key];
    return result;
  }, {});
}

// JavaDomAttributes, JavaDomDocument, and the setters on JavaDomElement
//  are all required just for the edit .getDetailXml() function we need when
//  doing checkSideEffects() during debugging.
//
// The sorting in JavaDomAttributes is required to match the sorting behavior
//  that happens while serializing XML in the desktop implementation, since
//  checkSideEffects() completely bypasses XML serialization and parsing.

export class JavaDomAttributes
{
  toJSON( key )
  {
    return sortObj( this );
  }
}

export class JavaDomDocument
{
  constructor()
  {
    this.child = null;
  }

  createElement( tagName )
  {
    return new JavaDomElement( { tagName, attributes: new JavaDomAttributes(), children: [] }, this );
  }

  createTextNode( text )
  {
    return text;
  }
}
JavaDomDocument.__interfaces = [ "org.w3c.dom.Document" ]

export class JavaDomElement
{
  constructor( element, owner=null )
  {
    this.nativeElement = element
    this.document = owner || new JavaDomDocument();
  }

  appendChild( child )
  {
    if ( typeof( child ) === 'string' )
      this.nativeElement.children.push( child );
    else
      this.nativeElement.children.push( child.nativeElement );
  }

  setTextContent( value )
  {
    this.nativeElement.children.push( value );
  }

  setAttribute( name, value )
  {
    this.nativeElement.attributes[ name ] = value;
  }

  getOwnerDocument()
  {
    return this.document;
  }

  getAttribute( name )
  {
    return this.nativeElement.attributes && this.nativeElement.attributes[ name ];
  }

  getLocalName()
  {
    return this.nativeElement.tagName
  }

  getTextContent()
  {
    const kids = this.nativeElement.children .filter( kid => kid.tagName !== 'effects' ); // 'effects' appear when parsing a history export
    if ( kids.length === 1 && ( typeof kids[ 0 ] === 'string' ) )
      return kids[ 0 ];
    return null;
  }

  getChildNodes()
  {
    const kids = this.nativeElement.children .filter( kid => kid.tagName !== 'effects' ); // 'effects' appear when parsing a history export
    if ( kids.length === 1 && ( typeof kids[ 0 ] === 'string' ) )
      return null;
    return new JavaDomNodeList( kids, this.document )
  }

  getChildElement( name )
  {
    const nativeChild = this.nativeElement.children.filter( n => n.tagName === name )[ 0 ];
    return nativeChild && new JavaDomElement( nativeChild, this.document );
  }

  getElementsByTagName( name )
  {
    const results = this.nativeElement.children.filter( n => n.tagName === name );
    return {
      getLength: () => results.length,
      item: i => (i < results.length)? new JavaDomElement( results[ i ], this.document ) : null
    }
  }

  // This is not DOM, just my quick-and-dirty serializer
  toIndentedString( indentation )
  {
    let result = indentation + "<" + this.getLocalName();
    for ( const [ name, value ] of Object.entries( this.nativeElement.attributes ) ) {
      result += " " + name + "=\"" + value + "\"";
    }
    // add attributes
    if ( this.nativeElement.children.length === 0 )
      return result + "/>\n";
    else
      result += ">\n";
    const text = this .getTextContent();
    if ( text ) {
      result += indentation + "  " + text + "\n";
    } else {
      const kids = this .getChildNodes();
      for (let index = 0; index < kids.getLength(); index++) {
        const child = kids.item( index );
        result += child.toIndentedString( indentation+"  " );
      }
    }
    result += indentation + "</" + this.getLocalName() + ">\n";
    return result;
  }
}
JavaDomElement.__interfaces = [ "org.w3c.dom.Element" ]
