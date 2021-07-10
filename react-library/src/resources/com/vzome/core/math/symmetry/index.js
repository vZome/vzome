
// These imports work because we are using @rollup/plugin-url.
// See package.json and rollup.config.js

import binaryTetrahedralGroupUrl from './binaryTetrahedralGroup.vef'
import H4rootsRotationalSubgroupUrl from './H4roots-rotationalSubgroup.vef'
import H4rootsUrl from './H4roots.vef'

const resources = {
  'binaryTetrahedralGroup': binaryTetrahedralGroupUrl,
  'H4roots-rotationalSubgroup': H4rootsRotationalSubgroupUrl,
  'H4roots': H4rootsUrl,
}

export default resources