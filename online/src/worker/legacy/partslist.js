
const countPartsForSnapshot = ( instances, shapes, colors ) =>
{
  const bom = {};
  for ( const { color, shapeId } of instances ) {
    const { orbit, length, name } = shapes[ shapeId ];
    if ( !name && !orbit ) continue;
    const row = name || `${orbit}:${length}`
    const match = Object.entries( colors ) .filter( ([ name, value ]) => value === color.toLowerCase() );
    const colorBin = match[0]? match[0][ 0 ] : 'other';
    if ( ! bom[ row ] )
      bom[ row ] = {};
    if ( ! bom[ row ][ colorBin ] )
      bom[ row ][ colorBin ] = 1;
    else
    bom[ row ][ colorBin ] = bom[ row ][ colorBin ] + 1;
  }
  return bom;
}

export const assemblePartsList = ( rendered, colors ) =>
{
  const bom = {};
  // union the BOMs for all scenes
  const { shapes, scenes, snapshots } = rendered;
  for ( const { snapshot } of scenes .slice( 1 ) ) {
    const snapshotBom = countPartsForSnapshot( snapshots[ snapshot ], shapes, colors );
    for ( const [ row, bins ] of Object.entries( snapshotBom ) ) {
      if ( ! bom[ row ] ) {
        bom[ row ] = bins;
      } else {
        for ( const [ colorBin, count ] of Object.entries( bins ) ) {
          if ( ! bom[ row ][ colorBin ] ) {
            bom[ row ][ colorBin ] = count;
          } else {
            bom[ row ][ colorBin ] = Math.max( bom[ row ][ colorBin ], count );
          }
        }
      }
    }
  }

  return bom;
}