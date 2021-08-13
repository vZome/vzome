
// These imports work because we are using @rollup/plugin-url.
// See package.json and rollup.config.js

import connectorUrl from './connector.vef'
import snubVertexUrl from './snubVertex.vef'

const shapes = {
  connector: connectorUrl,
  snubVertex: snubVertexUrl,
}

export default shapes