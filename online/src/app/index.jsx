

import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

// import { DesignHistoryInspector } from './components/inspector.jsx'
import { getModelURL } from './classic/components/folder.jsx';
import { VZomeAppBar } from './classic/components/appbar.jsx';
import { WorkerStateProvider } from '../workerClient/index.js';
import { DesignViewer } from '../viewer/solid/index.jsx';
import { CameraProvider } from '../workerClient/camera.jsx';

const queryParams = new URLSearchParams( window.location.search );
const relativeUrl = queryParams.get( 'url' ); // support for legacy viewer usage (old vZome shares)

// Must make this absolute before the worker tries to, with the wrong base URL
const url = ( relativeUrl && new URL( relativeUrl, window.location ) .toString() )
              || getModelURL( '120-cell' );   // TODO REMOVE THIS WHEN DesignHistoryInspector is ready

const legacyViewerMode = !!relativeUrl;
              
const forDebugger = !legacyViewerMode && queryParams.get( 'debug' ) === 'true';

const config = { url, preview: legacyViewerMode, debug: forDebugger };

const Online = () =>
{
  const para_1 = legacyViewerMode?
      <Typography gutterBottom>
        vZome Online Viewer is a viewer for existing vZome designs, created using
        the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
      </Typography>
    :
      <Typography gutterBottom>
        vZome Online Inspector is an inspector for existing vZome designs, created using
        the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
        The left-side drawer shows the complete edit history for the design,
        so you can explore how it was created.
        Click on the folder icon try out some of the built-in designs, or load one of your own!
      </Typography>
    ;
  
  return (
    <ErrorBoundary fallback={err => err}>
      <CameraProvider>
      <WorkerStateProvider config={config}>
        <VZomeAppBar title={ 'Viewer' } showOpen={true}
          about={
            <>
              {para_1}
              <Typography gutterBottom>
                You cannot modify designs or create new designs.  I'm working to complete those features
                (and all the other features of desktop vZome) in the other apps of
                the <Link target="_blank" rel="noopener" href="https://docs.vzome.com//online.html">vZome Online suite</Link>.
                If you want to stay informed about my progress, follow vZome
                on <Link target="_blank" rel="noopener" href="https://www.facebook.com/vZome">Facebook</Link> or <Link target="_blank" rel="noopener" href="https://twitter.com/vZome">Twitter</Link>,
                or join the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">Discord server</Link>.
              </Typography>
              <Typography gutterBottom>
                If you have a vZome design that does not load
                here, <Link target="_blank" rel="noopener" href="mailto:info@vzome.com">send me the vZome file</Link>,
                and I can prioritize the necessary fixes.
              </Typography>
            </>
          } />
        {/* { legacyViewerMode?
          <DesignViewer config={ { useSpinner: true } } />
        : <DesignHistoryInspector/> } */}
        <DesignViewer height="100%" width="100%" config={ { useSpinner: true, showScenes: true } } />
      </WorkerStateProvider>
      </CameraProvider>
    </ErrorBoundary>
  );
}

render( Online, document.getElementById( 'root' ) );
