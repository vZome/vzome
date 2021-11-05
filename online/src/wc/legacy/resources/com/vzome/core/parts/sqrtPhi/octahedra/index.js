
// These imports work because we are using @rollup/plugin-url.
// See package.json and rollup.config.js

import blueUrl from './blue.vef'
import mauveUrl from './mauve.vef'
import ivoryUrl from './ivory.vef'
import slateUrl from './slate.vef'
import yellowUrl from './yellow.vef'
import greenUrl from './green.vef'

const shapes = {
  blue: blueUrl,
  mauve: mauveUrl,
  ivory: ivoryUrl,
  slate: slateUrl,
  yellow: yellowUrl,
  green: greenUrl,
}

export default shapes