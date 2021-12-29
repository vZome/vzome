import React, { useState } from 'react'
// import logger from 'redux-logger'

import { DesignEditor } from './components/designeditor.jsx'
// import EditMenu from './components/editmenu.jsx'
import { ErrorAlert } from './components/alert.jsx'
import { VZomeAppBar } from './components/appbar.jsx'
import { Spinner } from './components/spinner.jsx';
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
  const [ problem, setProblem ] = useState( null );
  const [ waiting, setWaiting ] = useState( false );

  const openUrl = url => {
    if ( controller && url && url.endsWith( ".vZome" ) ) {
      controller. fetchDesignUrl( url );
      controller. enableView(); // worker won't send the render events without this
      setWaiting( true ); // see "event cycle" in components/folder.jsx
   }
  }
  const openFile = file => {
    if ( controller && file ) {
      controller. fetchDesignFile( file );
      controller. enableView(); // worker won't send the render events without this
      setWaiting( true ); // see "event cycle" in components/folder.jsx
    }
  }
  useEffect( () => {
    console.log( 'Creating the controller --------------------------------------------' );
    const ctrlr = createController( setWaiting, { viewOnly } ); // creates the worker
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
      :
        <DesignEditor controller={controller} debug={debug} />
      }
      <ErrorAlert message={problem} dismissed={() => setProblem(null)} /> 
      {/* <EditMenu/>  */}
      <Spinner visible={waiting} />
    </>
  );
}

export default App
