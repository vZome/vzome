
// babel workaround
import "regenerator-runtime/runtime";

import React from 'react';
import { render } from 'react-dom'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'

import { VZomeAppBar } from './components/appbar.jsx'
import { BuildPlane } from './components/buildplane.jsx'
import { getModelURL } from './components/folder.jsx';
import { DesignViewer, WorkerContext, useVZomeUrl } from '../../ui/viewer/index.jsx'

const App = () =>
{
  useVZomeUrl( url || getModelURL( 'vZomeLogo' ), { preview: legacyViewerMode, debug: forDebugger } );

  return (
    <>
      <VZomeAppBar oneDesign={legacyViewerMode} forDebugger={forDebugger} title='vZome Online'
        about={ <>
          <Typography gutterBottom>
            This is an experimental in-browser modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>.
          </Typography>
        </> }
      />
      <DesignViewer config={ { useSpinner: true } } >
        <BuildPlane config={workingPlane} {...{ startGridHover, stopGridHover }} />
      </DesignViewer>
    </>
  );
}

const Online = () => (
  <WorkerContext>
    <App/>
  </WorkerContext>
);

render( <Online/>, document.getElementById( 'root' ) );
