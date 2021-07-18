
import defaultShapes from './default/index.js'
import dodecagon3d from './dodecagon3d/index.js'
import dodecs from './dodecs/index.js'
import lifelike from './lifelike/index.js'
import octahedral from './octahedral/index.js'
import octahedralFast from './octahedralFast/index.js'
import octahedralRealistic from './octahedralRealistic/index.js'
import tiny from './tiny/index.js'
import rootTwo from './rootTwo/index.js'
import rootTwoBig from './rootTwoBig/index.js'
import rootTwoSmall from './rootTwoSmall/index.js'
import rootThreeOctaSmall from './rootThreeOctaSmall/index.js'
import heptagonAntiprism from './heptagonAntiprism/index.js'

import sqrtPhiFivefold from './sqrtPhi/fivefold/index.js'
import sqrtPhiOctahedra from './sqrtPhi/octahedra/index.js'
import sqrtPhiTinyIcosahedra from './sqrtPhi/tinyIcosahedra/index.js'
import sqrtPhiZome from './sqrtPhi/zome/index.js'

const shapes = {
  'default': defaultShapes,  // default is a keyword in Javascript
  dodecagon3d,
  dodecs,
  lifelike,
  octahedral,
  octahedralFast,
  octahedralRealistic,
  tiny,
  rootTwo,
  rootTwoBig,
  rootTwoSmall,
  rootThreeOctaSmall,
  'heptagon/antiprism': heptagonAntiprism,  // these must all use the legacy paths
  'sqrtPhi/fivefold': sqrtPhiFivefold,
  'sqrtPhi/octahedra': sqrtPhiOctahedra,
  'sqrtPhi/tinyIcosahedra': sqrtPhiTinyIcosahedra,
  'sqrtPhi/zome': sqrtPhiZome,
}

export default shapes
