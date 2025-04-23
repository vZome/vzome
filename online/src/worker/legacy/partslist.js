
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

// For the Zometool component
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


// For online vZome, not the Zometool component
export const createPartsList = ( shapes, controller ) =>
{
  if ( !controller )
    return { balls: 0, struts: 0, panels: 0, orbitColors: [] };

  const field = controller .getSymmetry() .getField();
  const orbitNames = controller .getCommandList( 'orbitNames' );
  const orbitOrder = orbit => {
    const index = orbitNames .indexOf( orbit );
    return index === -1 ? 1000 : index;
  }
  let balls = 0;
  let struts = 0;
  let panels = 0;
  const unsortedOrbits = Object.values( shapes ).reduce( ( acc, shape ) =>
  {
    const { name, orbit, length, instances } = shape;
    const count = instances.length;
    if ( count === 0 )
      return acc;
    if ( name === 'ball' )
      balls = count;
    else if ( ! orbit )
      panels += count;
    else {
      const len = field .createAlgebraicNumberFromTD( JSON.parse( length ) );
      acc.push( { orbit, length: len .toString( 5 ), count }); // 5 is "MATH" format
      struts += count;
    }
    return acc;
  }, [] );

  const orbits = unsortedOrbits .toSorted( ( a, b ) => orbitOrder( a.orbit ) - orbitOrder( b.orbit ) );
  const orbitColors = orbits.map( ( { orbit, length, count } ) =>
    ({ length, count, color: controller .getProperty( `orbitColor.${orbit}` ) || '128,128,128' }) );
  return { balls, struts, panels, orbitColors };
}
