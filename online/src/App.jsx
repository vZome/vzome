import React from 'react'
import { Provider } from 'react-redux'
// import logger from 'redux-logger'
import thunk from 'redux-thunk'
import { makeStyles } from '@material-ui/core/styles'

import './App.css'

import DesignEditor from './components/designeditor.jsx'
import ErrorAlert from './components/alert.jsx'
import Spinner from './components/spinner.jsx'
import VZomeAppBar from './components/appbar.jsx'
import Debugger from './components/debugger.jsx'
import createBundleStore from './bundles/index.js'

const queryParams = new URLSearchParams( window.location.search );
const profile = queryParams.get( "profile" ) || queryParams.get( "editMode" )
const debug = queryParams.get( 'debug' ) === 'true'

const store = createBundleStore( profile, [ thunk ] );

const useStyles = makeStyles((theme) => ({
  content: {
    flexGrow: 1,
    display: "contents",
  },
}));

const App = () =>
{
  const classes = useStyles();
  return (
    <Provider store={store}>
      <VZomeAppBar/>
      { debug && <Debugger/> }
      <main className={classes.content}>
        <DesignEditor/>
      </main>
      <ErrorAlert/> 
      <Spinner/>
    </Provider>
  );
}

export default App;
