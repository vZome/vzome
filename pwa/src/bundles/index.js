import { composeBundles } from 'redux-bundler'
import planes from './planes'
import structure from './structure'

export default composeBundles( structure, planes )
