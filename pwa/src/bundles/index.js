import { composeBundles, createDebugBundle } from 'redux-bundler'
import planes from './planes'
import points from './points'

export default composeBundles( planes, points, createDebugBundle() )
