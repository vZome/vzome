
import { com } from "./core-java.js";
import { toWebColor } from "./scenes.js";

class VertexSet
{
  constructor()
  {
    this.map = new Map();
  }

  add( vertex )
  {
    const key = vertex .toString();
    if ( this.map .has( key ) ) return;
    this.map .set( key, vertex );
  }

  indexOf( vertex )
  {
    const key = vertex .toString();
    let index = 0;
    for (const item of this.map .keys()) {
      if ( item === key ) return index;
      ++index;
    }
    return -1;
  }

  asArray()
  {
    return Array.from( this.map .values() );
  }
}

export const modelToJS = ( manifestations, withColors=true ) =>
{
  const sortedVertices = new VertexSet();
  const origin = com.vzome.core.editor.api.Manifestations.sortVertices( manifestations, sortedVertices );
  const field = origin .getField() .getName();

  const balls = [];
  const struts = [];
  const panels = [];
  const iterator = manifestations .iterator();
  while ( iterator.hasNext() ) {
    const man = iterator.next();
    const color = toWebColor( man .getColor() );
    if ( withColors && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0 )
    {
      const vertex = sortedVertices .indexOf( man .getLocation() );
      balls .push( { vertex, color } );
    }
    else if ( man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0 )
    {
      const vertices = [ sortedVertices .indexOf( man .getLocation() ), sortedVertices .indexOf( man .getEnd() ) ];
      struts .push( withColors? { vertices, color } : vertices );
    }
    else if ( man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0 )
    {
      const vertices = [];
      const panel = man .iterator();
      while ( panel .hasNext() ) vertices .push( sortedVertices .indexOf( panel .next() ) );
      panels .push( withColors? { vertices, color } : vertices );
    }
  }

  const vertices = sortedVertices .asArray() .map( vertex => {
    const xyzANs = vertex .minus( origin ) .getComponents();
    return xyzANs .map( an => an.toTrailingDivisor() .map( big => Number( big ) ) );
  } );

  if ( withColors )
    return { field, vertices, balls, struts, panels }
  else
    return { field, vertices, edges: struts, faces: panels }
}