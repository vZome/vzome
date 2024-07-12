
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

const lengthMapping = {
  ' :2 -phi'        : 'r00',
  " :1/2"           : 'hg1',
  " :1/2*phi"       : 'hg2',
  " :-1/2 +1/2*phi" : 'hg0',
}

export const normalizeBOM = rawBoM =>
{
  for (const key in lengthMapping) {
    if ( Object.hasOwnProperty.call( rawBoM, key )) {
      const count = rawBoM[ key ];
      rawBoM[ lengthMapping[ key ] ] = count;
    }
  }

  const bom = [];
  // the order in partcodeMapping will be the order in bom
  for (const key in partcodeMapping) {
    if ( Object.hasOwnProperty.call( rawBoM, key ) ) {
      const partNum = partcodeMapping[ key ];
      const count = rawBoM[ key ];
      bom .push( { key, partNum, count } )
    }
  }
  return bom;
}