
// babel workaround
import "regenerator-runtime/runtime";

import React from 'react';
import { render } from 'react-dom'

import { DesignHistoryInspector } from './components/inspector.jsx'
import { VZomeAppBar } from './components/appbar.jsx'
import { getModelURL } from './components/folder.jsx';
import { DesignViewer, WorkerContext, useVZomeUrl } from '../ui/viewer/index.jsx'

const queryParams = new URLSearchParams( window.location.search );
const relativeUrl = queryParams.get( 'url' ); // support for legacy viewer usage (old vZome shares)
const legacyViewerMode = !!relativeUrl;
// Must make this absolute before the worker tries to, with the wrong base URL
const url = relativeUrl && new URL( relativeUrl, window.location ) .toString();

const App = () =>
{
  useVZomeUrl( url || getModelURL( 'vZomeLogo' ), { preview: legacyViewerMode } );

  return (
    <>
      <VZomeAppBar oneDesign={legacyViewerMode} />
      { legacyViewerMode? <DesignViewer useSpinner showSnapshots /> : <DesignHistoryInspector/> }
    </>
  );
}

const Online = () => (
  <WorkerContext>
    <App/>
  </WorkerContext>
);

render( <Online/>, document.getElementById( 'root' ) );
