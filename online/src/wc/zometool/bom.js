
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

const partcodeMapping = {
  ball: 'PZB-BAL-W',
  b0:   'PST-B0-BLU',
  b1:   'PST-B1-BLU',
  b2:   'PST-B2-BLU',
  y0:   'PST-Y0-YEL',
  y1:   'PST-Y1-YEL',
  y2:   'PST-Y2-YEL',
  r00:  'PST-R00-RED',
  r0:   'PST-R0-RED',
  r1:   'PST-R1-RED',
  r2:   'PST-R2-RED',
  g0:   'PST-G0-GRN',
  g1:   'PST-G1-GRN',
  g2:   'PST-G2-GRN',
  hg0:  'PST-HG0-GRN',
  hg1:  'PST-HG1-GRN',
  hg2:  'PST-HG2-GRN',
}


export const normalizeBOM = rawBoM =>
{
  const bom = [];
  // the order in partcodeMapping will be the order in bom
  for (const key in partcodeMapping) {
    const rawKey = svgMapping[ key ];
    if ( Object.hasOwnProperty.call( rawBoM, rawKey ) ) {
      const partNum = partcodeMapping[ key ];
      const count = rawBoM[ rawKey ];
      bom .push( { key, partNum, count } )
    }
  }
  return bom;
}