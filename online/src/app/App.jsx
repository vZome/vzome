import React, { useState } from 'react'
// import logger from 'redux-logger'
import Grid from '@material-ui/core/Grid'

import { DesignEditor } from './components/designeditor.jsx'
// import EditMenu from './components/editmenu.jsx'
import { ErrorAlert } from './components/alert.jsx'
import { VZomeAppBar } from './components/appbar.jsx'
import Debugger from './components/debugger.jsx'
import { useEffect } from 'react'
import { loadDesign } from '../ui/viewer/design.js';
import { DesignViewer } from '../ui/viewer/index.jsx'

const queryParams = new URLSearchParams( window.location.search );
const editor = queryParams.get( 'editor' ) === 'true';
const profile = queryParams.get( "profile" ) || queryParams.get( "editMode" );
const debug = queryParams.get( 'debug' ) === 'true';

const App = () =>
{
  const [ designToView, setDesignToView ] = useState( null );
  useEffect( () => {
    const url = queryParams.get( 'url' );
    if ( url && url.endsWith( ".vZome" ) ) {
      setDesignToView( loadDesign( url ) );
    }
  }, [] );

  return (
    <>
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
      : designToView? <DesignViewer design={designToView} /> : <DesignEditor/> }
      <ErrorAlert/> 
      {/* <EditMenu/>  */}
      {/* <Spinner/> */}
    </>
  );
}

export default App
