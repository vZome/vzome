
// babel workaround
import "regenerator-runtime/runtime";

import React from 'react';
import { render } from 'react-dom'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'

import { DesignHistoryInspector } from './components/inspector.jsx'
import { VZomeAppBar } from './components/appbar.jsx'
import { getModelURL } from './components/folder.jsx';
import { DesignViewer, WorkerContext, useVZomeUrl } from '../ui/viewer/index.jsx'

const queryParams = new URLSearchParams( window.location.search );
const relativeUrl = queryParams.get( 'url' ); // support for legacy viewer usage (old vZome shares)
const legacyViewerMode = !!relativeUrl;
// Must make this absolute before the worker tries to, with the wrong base URL
const url = relativeUrl && new URL( relativeUrl, window.location ) .toString();

const forDebugger = !legacyViewerMode && queryParams.get( 'debug' ) === 'true';

const App = () =>
{
  useVZomeUrl( url || getModelURL( 'vZomeLogo' ), legacyViewerMode, forDebugger );

  return (
    <>
      <VZomeAppBar oneDesign={legacyViewerMode} forDebugger={forDebugger} title='vZome Online'
        about={ <>
          <Typography gutterBottom>
            vZome Online is the world's first in-browser modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>
            ... or it will be soon.
          </Typography>
          <Typography gutterBottom>
            Right now, you can load and view existing vZome designs, created using
            the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
            The left-side drawer shows the complete edit history for the design,
            so you can explore how it was created.
            Click on the folder icon try out some of the built-in designs, or load one of your own!
          </Typography>
          <Typography gutterBottom>
            At the moment, you cannot modify designs or create new designs.  I'm working to complete those features
            (and all the other features of desktop vZome)
            as soon as possible.
            If you want to stay informed about my progress, follow vZome
            on <Link target="_blank" rel="noopener" href="https://www.facebook.com/vZome">Facebook</Link> or <Link target="_blank" rel="noopener" href="https://twitter.com/vZome">Twitter</Link>,
            or join the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">Discord server</Link>.
          </Typography>
          <Typography gutterBottom>
            If you have a vZome design that does not load
            here, <Link target="_blank" rel="noopener" href="mailto:info@vzome.com">send me the vZome file</Link>,
            and I can prioritize the necessary fixes.
          </Typography>
        </> }
      />
      { legacyViewerMode?
        <DesignViewer config={ { useSpinner: true } } />
      : <DesignHistoryInspector/> }
    </>
  );
}

const Online = () => (
  <WorkerContext>
    <App/>
  </WorkerContext>
);

render( <Online/>, document.getElementById( 'root' ) );
