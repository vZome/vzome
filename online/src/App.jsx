import React from 'react'
import { Provider } from 'react-redux'
// import logger from 'redux-logger'
import thunk from 'redux-thunk'
import { makeStyles } from '@material-ui/core/styles'
import Grid from '@material-ui/core/Grid'

import './App.css'

import DesignEditor from './components/designeditor.jsx'
import ErrorAlert from './components/alert.jsx'
import VZomeAppBar from './components/appbar.jsx'
import Debugger from './components/debugger.jsx'
import createBundleStore from './bundles/index.js'

const queryParams = new URLSearchParams( window.location.search );
const profile = queryParams.get( "profile" ) || queryParams.get( "editMode" )
const debug = queryParams.get( 'debug' ) === 'true'

const store = createBundleStore( profile, [ thunk ] );

/* CSS GRID DEBUGGING TIP:
  The DesignEditor (or the r3f Canvas inside it) will lock in an absolute height
  on initial render.  This means that adjusting CSS layout attributes
  on the Debugger component within the browser's debug tools may not
  have the expected result.  For tweaking the Debugger layout, try
  setting the 2nd Grid item to height:700px, so it won't flex initially.
*/
const useStyles = makeStyles((theme) => ({
  debugDebuggerGrid: {
    height: '750px',  // uncomment this to debug the Debugger grid layout; see above
  },
}))

const App = () =>
{
  const classes = useStyles();
  return (
    <Provider store={store}>
        <VZomeAppBar/>
        { debug ?
          <div style={{ flex: '1', height: '100%' }}>
            <Grid id='debug-main' container spacing={0}>
              <Grid id='debugger-grid-item' item xs={4}>
                <Debugger/>
              </Grid>
              <Grid item xs={8} className={classes.debugDebuggerGrid}>
                <DesignEditor/>
              </Grid>
            </Grid>
          </div>
        : <DesignEditor/>}
        <ErrorAlert/> 
        {/* <Spinner/> */}
    </Provider>
  );
}

export default App
