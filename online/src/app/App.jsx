
import React from 'react'

import { DesignEditor } from './components/designeditor.jsx'
import { ErrorAlert } from './components/alert.jsx'
import { VZomeAppBar } from './components/appbar.jsx'
import { DesignViewer, WorkerContext, useVZomeUrl } from '../ui/viewer/index.jsx'

const queryParams = new URLSearchParams( window.location.search );
const debug = queryParams.get( 'debug' ) === 'true';
const url = queryParams.get( 'url' ); // support for legacy viewer usage
const viewOnly = !!url;

const App = () =>
{
  useVZomeUrl( url );

  return (
    <>
      <VZomeAppBar viewOnly={viewOnly} />
      { viewOnly?
        <DesignViewer/>
      :
        <DesignEditor debug={debug} />
      }
      <ErrorAlert/> 
    </>
  );
}

export const Online = () => (
  <WorkerContext>
    <App/>
  </WorkerContext>
)