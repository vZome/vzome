

import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

// import { DesignHistoryInspector } from './components/inspector.jsx'
import { getModelURL } from './classic/components/folder.jsx';
import { VZomeAppBar } from './classic/components/appbar.jsx';
import { WorkerProvider } from '../viewer/context/worker.jsx';
import { ViewerProvider } from '../viewer/context/viewer.jsx';
import { DesignViewer } from '../viewer/index.jsx';
import { CameraProvider } from '../viewer/context/camera.jsx';
import { EditorProvider } from './framework/context/editor.jsx';
import { ImageCaptureProvider } from '../viewer/context/export.jsx';
import { ClassicApp } from './classic/index.jsx';

const queryParams = new URLSearchParams( window.location.search );
const relativeUrl = queryParams.get( 'url' ); // support for legacy viewer usage (old vZome shares)

// Must make this absolute before the worker tries to, with the wrong base URL
const url = ( relativeUrl && new URL( relativeUrl, window.location ) .toString() );

const legacyViewerMode = !!relativeUrl;
              
const forDebugger = !legacyViewerMode && queryParams.get( 'debug' ) === 'true';

const config = { url, preview: legacyViewerMode, labels: true, debug: forDebugger };

const LegacyViewer = () =>
{  
  return (
    <>
      <VZomeAppBar title={ legacyViewerMode? 'Viewer' : 'Inspector' } showOpen={true}
      about={legacyViewerMode?
        <>
          <Typography gutterBottom>
            vZome Viewer is an interactive viewer for designs created 
            using <Link target="_blank" rel="noopener" href="https://www.vzome.com/app">vZome online</Link> or
            the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
          </Typography>
          <Typography gutterBottom>
            Note that <Link target="_blank" rel="noopener" href="https://vzome.github.io/vzome/sharing.html">vZome GitHub sharing</Link> and
            the <Link target="_blank" rel="noopener" href="https://vzome.github.io/vzome/web-component.html">vzome-viewer web component</Link> are
            better solutions for sharing your vZome designs online.
          </Typography>
        </>
        :
        <Typography gutterBottom>
          vZome Inspector is an inspector for existing vZome designs, created using
          <Link target="_blank" rel="noopener" href="https://www.vzome.com/app">vZome online</Link> or
          the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
          The left-side drawer shows the complete edit history for the design,
          so you can explore how it was created.
          Click on the folder icon try out some of the built-in designs, or load one of your own!
        </Typography>
      } />
      <DesignViewer height="100%" width="100%" config={ { useSpinner: true, showScenes: 'named' } } />
    </>
  );
}

const Online = () =>
{
  const reportError = err =>
  {
    console.log( 'ErrorBoundary:', err.toString() );
    return <div>{err.toString()}</div>
  }

  return (
    <ErrorBoundary fallback={ reportError } >
      <CameraProvider name='main' >
        <WorkerProvider>
          <ImageCaptureProvider>
            <ViewerProvider config={config}>
              <EditorProvider>
                { legacyViewerMode? <LegacyViewer/> : <ClassicApp/>}
              </EditorProvider>
            </ViewerProvider>
          </ImageCaptureProvider>
        </WorkerProvider>
      </CameraProvider>
    </ErrorBoundary>
  );
}

render( Online, document.getElementById( 'root' ) );
