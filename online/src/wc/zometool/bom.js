
const svgMapping = {
  ball: 'ball',

  b0:   '[[0,0,1],[0,0,1]]:[-1,1,1]',
  b1:   '[[0,0,1],[0,0,1]]:[1,0,1]',
  b2:   '[[0,0,1],[0,0,1]]:[0,1,1]',

  y0:   '[[0,0,1],[2,-1,1]]:[-1,1,1]',
  y1:   '[[0,0,1],[2,-1,1]]:[1,0,1]',
  y2:   '[[0,0,1],[2,-1,1]]:[0,1,1]',

  r00:  '[[-1,1,1],[0,0,1]]:[2,-1,1]',
  r0:   '[[-1,1,1],[0,0,1]]:[-1,1,1]',
  r1:   '[[-1,1,1],[0,0,1]]:[1,0,1]',
  r2:   '[[-1,1,1],[0,0,1]]:[0,1,1]',

  g0:   '[[2,-1,1],[5,-3,1]]:[-1,1,1]',
  g1:   '[[2,-1,1],[5,-3,1]]:[1,0,1]',
  g2:   '[[2,-1,1],[5,-3,1]]:[0,1,1]',

  hg0:  '[[2,-1,1],[5,-3,1]]:[-1,1,2]',
  hg1:  '[[2,-1,1],[5,-3,1]]:[1,0,2]',
  hg2:  '[[2,-1,1],[5,-3,1]]:[0,1,2]',
}

// Fallback for catalogs in the original format (e.g. "PST-B0-BLU": { color, orbit, length }),
//  which lack the "shape" field; it is derived from the part ID instead.
const legacyShape = partNum =>
{
  const segment = partNum .split( '-' )[ 1 ];
  return ( segment === 'BAL' )? 'BALL' : segment;
}

// Each catalog part counts only the design parts with its shape code (e.g. "B0", "R00", "BALL",
//  the svgMapping keys uppercased) AND its stated color; design parts that match no catalog entry,
//  including those with a color not stated in the catalog, are ignored.
//  The order of the catalog will be the order in the bom.
export const normalizeBOM = ( rawBoM, { colors = {}, parts = {} } ) =>
{
  const bom = [];
  for ( const [ partNum, part ] of Object.entries( parts ) ) {
    const shape = ( part.shape ?? legacyShape( partNum ) ) ?.toUpperCase();
    const count = rawBoM[ svgMapping[ shape ?.toLowerCase() ] ]?.[ part.color ];
    if ( count )
      bom .push( { partNum, count, color: colors[ part.color ], shape } );
  }
  return bom;
}
