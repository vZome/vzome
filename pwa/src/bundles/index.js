import { composeBundles, createDebugBundle } from 'redux-bundler'
import planes from './planes'

export default composeBundles( planes, createDebugBundle() )
