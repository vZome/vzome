import React from 'react'
import { makeStyles } from '@material-ui/core/styles'

import './App.css'

import ModelCanvas from './components/modelcanvas-three.js'
import ErrorAlert from './components/alert.js'
import Spinner from './components/spinner.js'
import VZomeAppBar from './components/appbar.js'
import Debugger from './components/debugger.js'

const useStyles = makeStyles((theme) => ({
  content: {
    flexGrow: 1,
    display: "contents",
  },
}));

function App() {
  const classes = useStyles();
  return (
    <>
      <VZomeAppBar/>
      <Debugger/>
      <main className={classes.content}>
        <ModelCanvas/>
      </main>
      <ErrorAlert/> 
      <Spinner/>
    </>
  );
}

export default App;
