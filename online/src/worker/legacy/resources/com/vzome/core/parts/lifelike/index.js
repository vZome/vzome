
// These imports work because we are using @rollup/plugin-url.
// See package.json and rollup.config.js

import connectorUrl from './connector.vef'
import blueUrl from './blue.vef'
import yellowUrl from './yellow.vef'
import redUrl from './red.vef'
import yellowShortUrl from './yellow-short.vef'
import redShortUrl from './red-short.vef'
import greenUrl from './green.vef'
import greenShortUrl from './green-short.vef'

const shapes = {
  connector: connectorUrl,
  blue: blueUrl,
  yellow: yellowUrl,
  red: redUrl,
  green: greenUrl,
  'red-short': redShortUrl,
  'yellow-short': yellowShortUrl,
  'green-short': greenShortUrl,
}

export default shapes