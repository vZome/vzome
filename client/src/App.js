import React from 'react'

import Fab from '@material-ui/core/Fab'
import UndoRoundedIcon from '@material-ui/icons/UndoRounded'
import RedoRoundedIcon from '@material-ui/icons/RedoRounded'

import './App.css'

import ModelCanvas from './components/modelcanvas-three.js'
import WebLoader from './components/webloader.js'
import Exporter from './components/exporter.js'
import ErrorAlert from './components/alert.js'
import EditMenu from './components/editmenu.js'
import Spinner from './components/spinner.js'
import VZomeAppBar from './components/appbar.js'

function App() {
  return (
    <>
      <VZomeAppBar/>
      <ModelCanvas/>
      <ErrorAlert/> 
      {/* <Fab color="primary" aria-label="undo">
        <UndoRoundedIcon />
      </Fab>
      <Fab color="primary" aria-label="redo">
        <RedoRoundedIcon />
      </Fab> */}

      {/* <WebLoader/>
      <Models/>
      <FileOpener/>
      <Exporter/>*/}
      {/* <EditMenu/> */}
      {/* <Spinner/> */}
      {/* <div>Export icon made from <a href="http://www.onlinewebfonts.com/icon">Icon Fonts</a> is licensed by CC BY 3.0</div> */}
      {/* <a href="https://github.com/mhnpd/react-loader-spinner">react-loader-spinner</a> */}
      {/* <a href="http://simpleicon.com/folder-2.html">folder icon</a> */}
    </>
  );
}

export default App;
