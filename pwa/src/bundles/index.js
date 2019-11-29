import { composeBundles } from 'redux-bundler'
import planes from './planes'
import points from './points'

export default composeBundles( points, planes )
