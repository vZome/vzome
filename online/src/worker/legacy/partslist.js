
export const assemblePartsList = ( shapes, colors ) =>
{
  const bom = {};
  for (const id in shapes) {
    const { orbit, length, name, instances } = shapes[ id ];
    if ( !name && !orbit ) continue;
    const row = name || `${orbit}:${length}`
    for (const { color } of instances) {
      const match = Object.entries( colors ) .filter( ([ name, value ]) => value === color.toLowerCase() );
      const colorBin = match[0]? match[0][ 0 ] : 'other';
      if ( ! bom[ row ] )
        bom[ row ] = {};
      if ( ! bom[ row ][ colorBin ] )
        bom[ row ][ colorBin ] = 1;
      else
      bom[ row ][ colorBin ] = bom[ row ][ colorBin ] + 1;
    }
  }
  return bom;
}