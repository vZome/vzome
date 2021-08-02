import React from 'react'
import { Provider } from 'react-redux'
// import logger from 'redux-logger'
import thunk from 'redux-thunk'
import Grid from '@material-ui/core/Grid'

import './App.css'

import DesignEditor from './components/designeditor.jsx'
// import EditMenu from './components/editmenu.jsx'
import ErrorAlert from './components/alert.jsx'
import VZomeAppBar from './components/appbar.jsx'
import Debugger from './components/debugger.jsx'
import createBundleStore from './bundles/index.js'

const queryParams = new URLSearchParams( window.location.search );
const editor = queryParams.get( 'editor' ) === 'true'
const profile = queryParams.get( "profile" ) || queryParams.get( "editMode" )
const debug = queryParams.get( 'debug' ) === 'true'

const store = createBundleStore( profile, [ thunk ] );

const App = () =>
{
  return (
    <Provider store={store}>
        <VZomeAppBar/>
        { debug ?
          <div style={{ flex: '1', height: '100%' }}>
            <Grid id='debug-main' container spacing={0} style={{ height: '100%' }}>
              <Grid id='debugger-grid-item' item xs={4}>
                <Debugger/>
              </Grid>
              <Grid item xs={8}>
                <DesignEditor/>
              </Grid>
            </Grid>
          </div>
        : <DesignEditor/> }
        <ErrorAlert/> 
        {/* <EditMenu/>  */}
        {/* <Spinner/> */}
    </Provider>
  );
}

export default App
