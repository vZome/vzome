
// These imports work because we are using @rollup/plugin-url.
// See package.json and rollup.config.js

import connectorUrl from './connector.vef'
import blueUrl from './blue.vef'
import greenUrl from './green.vef'

const shapes = {
  connector: connectorUrl,
  blue: blueUrl,
  green: greenUrl,
}

export default shapes