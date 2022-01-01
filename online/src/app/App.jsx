
import React from 'react'

import { DesignHistoryInspector } from './components/inspector.jsx'
import { ErrorAlert } from './components/alert.jsx'
import { VZomeAppBar } from './components/appbar.jsx'
import { WorkerContext, useVZomeUrl } from '../ui/viewer/index.jsx'

const queryParams = new URLSearchParams( window.location.search );
const url = queryParams.get( 'url' ); // support for legacy inspector usage
const oneDesign = !!url;

const App = () =>
{
  useVZomeUrl( url, { preview: false } );

  return (
    <>
      <VZomeAppBar oneDesign={oneDesign} />
      <DesignHistoryInspector/>
      <ErrorAlert/> 
    </>
  );
}

export const Online = () => (
  <WorkerContext>
    <App/>
  </WorkerContext>
)