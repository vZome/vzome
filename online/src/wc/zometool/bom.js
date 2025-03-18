
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
  ball: 'PZB-BAL-',
  b0:   'PST-B0-',
  b1:   'PST-B1-',
  b2:   'PST-B2-',
  y0:   'PST-Y0-',
  y1:   'PST-Y1-',
  y2:   'PST-Y2-',
  r00:  'PST-R00-',
  r0:   'PST-R0-',
  r1:   'PST-R1-',
  r2:   'PST-R2-',
  g0:   'PST-G0-',
  g1:   'PST-G1-',
  g2:   'PST-G2-',
  hg0:  'PST-HG0-',
  hg1:  'PST-HG1-',
  hg2:  'PST-HG2-',
}

const colorMapping = {
  "blue":      "BLU",
  "yellow":    "YEL",
  "red":       "RED",
  "green":     "GRN",
  "turquoise": "TEA",
  "orange":    "ORG",
  "purple":    "PUR",
  "white":     "W",
  "black":     "BLK",
  "gray":      "GRY",
}


export const normalizeBOM = ( rawBoM, { colors, parts } ) =>
{
  const bom = [];
  const zometoolCodes = Object.keys( parts ); // not using the values at the moment
  // the order in partcodeMapping will be the order in bom
  for (const key in partcodeMapping) {
    const rawKey = svgMapping[ key ];
    if ( Object.hasOwnProperty.call( rawBoM, rawKey ) ) {
      // There is a rawBoM entry for this rawKey (e.g. svgMapping["b0"]), itself a histogram by color
      const partNumPrefix = partcodeMapping[ key ];
      for (const [ colorName, count ] of Object.entries( rawBoM[ rawKey ]) ) {
        const partNum = partNumPrefix + colorMapping[ colorName ];
        if ( zometoolCodes .includes( partNum ) ) {
          const color = colors[ colorName ];
          bom .push( { key, partNum, count, color } )
        }
      }
    }
  }
  return bom;
}