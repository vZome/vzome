import React from 'react'
import { Provider } from 'react-redux'
import logger from 'redux-logger'
import thunk from 'redux-thunk'
import { makeStyles } from '@material-ui/core/styles'

import './App.css'

import DesignEditor from './components/designeditor.js'
import ErrorAlert from './components/alert.js'
import Spinner from './components/spinner.js'
import VZomeAppBar from './components/appbar.js'
import Debugger from './components/debugger.js'
import createBundleStore from './bundles'

const store = createBundleStore( [ thunk, logger ] );

const useStyles = makeStyles((theme) => ({
  content: {
    flexGrow: 1,
    display: "contents",
  },
}));

function App() {
  const classes = useStyles();
  return (
    <Provider store={store}>
      <VZomeAppBar/>
      <Debugger/>
      <main className={classes.content}>
        <DesignEditor/>
      </main>
      <ErrorAlert/> 
      <Spinner/>
    </Provider>
  );
}

export default App;
