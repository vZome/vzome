
export class ParsedEdit
{
  constructor( txmlElement, parent, interpretEdit )
  {
    this.nativeElement = txmlElement;
    this.parentEdit = parent;
    this.interpret = interpretEdit;
    this.legacyEdit = undefined;
    const kids = txmlElement.children .filter( kid => kid.tagName !== 'effects' ); // 'effects' appear when parsing a history export
    if ( kids.length === 1 && ( typeof kids[ 0 ] === 'string' ) )
      this.children = [];
    else
      this.children = kids .map( child => new ParsedEdit( child, this, interpretEdit ) );
  }

  // these are for the interpreter
 
  isBranch()
  {
    return this.nativeElement.tagName && this.nativeElement.tagName === "Branch";
  }

  id()
  {
    return this.nativeElement.id;
  }

  firstChild()
  {
    return ( this.children.length > 0 ) && this.children[ 0 ];
  }

  nextSibling()
  {
    if ( !this.parentEdit ) return null;
    const next = this.nativeElement.index + 1;
    if ( next >= this.parentEdit.children.length ) return null;
    return this.parentEdit.children[ next ];
  }

  perform( context )
  {
    console.log( 'perform', this.nativeElement.id, this.nativeElement.tagName );
    this.legacyEdit = this.interpret.call( this, this.nativeElement, context );
  }

  // these are for the debugger UI

  name()
  {
    return this.nativeElement.tagName;
  }

  toString()
  {
    return this.name();
  }

  getAttributeNames()
  {
    return this.nativeElement.attributes.keys();
  }

  getAttribute( name )
  {
    return this.nativeElement.attributes[ name ];
  }

  // const serialize = () => serializeLegacyEdit( txmlElement );
}