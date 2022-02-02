
// These imports work because we are using @rollup/plugin-url.
// See package.json and rollup.config.js

import connectorUrl from './connector.vef'
import yellowUrl from './yellow.vef'

const shapes = {
  connector: connectorUrl,
  yellow: yellowUrl,
}

export default shapes