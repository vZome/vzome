import React from 'react'

// import Fab from '@material-ui/core/Fab'
// import UndoRoundedIcon from '@material-ui/icons/UndoRounded'
// import RedoRoundedIcon from '@material-ui/icons/RedoRounded'

import './App.css'

import ModelCanvas from './components/modelcanvas-three.js'
import ErrorAlert from './components/alert.js'
import Spinner from './components/spinner.js'
import VZomeAppBar from './components/appbar.js'
// import Exporter from './components/exporter.js'
// import EditMenu from './components/editmenu.js'

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

      {/* <Exporter/> */}
      {/* <EditMenu/> */}
      <Spinner/>
      {/* <div>Export icon made from <a href="http://www.onlinewebfonts.com/icon">Icon Fonts</a> is licensed by CC BY 3.0</div> */}
    </>
  );
}

export default App;
