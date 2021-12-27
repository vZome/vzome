import React, { useState } from 'react'
// import logger from 'redux-logger'
import Grid from '@material-ui/core/Grid'

import { DesignEditor } from './components/designeditor.jsx'
// import EditMenu from './components/editmenu.jsx'
import { ErrorAlert } from './components/alert.jsx'
import { VZomeAppBar } from './components/appbar.jsx'
import { Debugger } from './components/debugger.jsx'
import { useEffect } from 'react'
import { createController } from '../ui/viewer/controller.js';
import { DesignViewer } from '../ui/viewer/index.jsx'

const queryParams = new URLSearchParams( window.location.search );
const debug = queryParams.get( 'debug' ) === 'true';
const url = queryParams.get( 'url' ); // support for legacy viewer usage
const viewOnly = !!url;

const App = () =>
{
  const [ controller, setController ] = useState( null );

  const openUrl = url => {
    if ( controller && url && url.endsWith( ".vZome" ) ) {
      controller. fetchDesignUrl( url );
      controller. enableView(); // worker won't send the render events without this
    }
  }
  const openFile = file => {
    console.log( JSON.stringify( file, null, 2 ) );
    if ( controller && file ) {
      controller. fetchDesignFile( file );
      controller. enableView(); // worker won't send the render events without this
    }
  }
  useEffect( () => {
    const ctrlr = createController( { viewOnly } ); // creates the worker
    setController( ctrlr );
    if ( url && url.endsWith( ".vZome" ) ) {
      ctrlr .fetchDesignUrl( url ); // gets the worker started on fetching
      // We don't enable the view yet, since it will only get connected during the initial render.
      //  See useDesignController.
    }
  }, [] );

  return (
    <>
      <VZomeAppBar openUrl={ !viewOnly && openUrl } openFile={ !viewOnly && openFile } />
      { viewOnly?
        <DesignViewer controller={controller} />
      : debug ?
        <div style={{ flex: '1', height: '100%' }}>
          <Grid id='debug-main' container spacing={0} style={{ height: '100%' }}>
            <Grid id='debugger-grid-item' item xs={4}>
              <Debugger controller={controller}/>
            </Grid>
            <Grid item xs={8}>
              <DesignEditor controller={controller}/>
            </Grid>
          </Grid>
        </div>
      :
        <DesignEditor controller={controller}/>
      }
      <ErrorAlert/> 
      {/* <EditMenu/>  */}
      {/* <Spinner/> */}
    </>
  );
}

export default App
