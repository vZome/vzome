
import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

import { VZomeAppBar } from '../classic/components/appbar.jsx';
import { WorkerProvider } from '../../viewer/context/worker.jsx';
import { ViewerProvider } from '../../viewer/context/viewer.jsx';
import { UndoRedoButtons } from './undoredo.jsx';

import { BuildPlaneTool } from './buildplane.jsx'
import { DesignViewer } from '../../viewer/index.jsx';
import { CameraProvider } from '../../viewer/context/camera.jsx';
import { EditorProvider } from '../framework/context/editor.jsx';

const WorkerApp = () => (
  <ErrorBoundary fallback={err => <div>{err.toString()}</div>} >
    <CameraProvider>
    <WorkerProvider>
    <ViewerProvider>
    <EditorProvider>
      <VZomeAppBar showOpen pathToRoot='../models' forDebugger={false} title='Buildplane'
        about={ <>
          <Typography gutterBottom>
            This is an experimental web-based modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>,
            based on <Link target="_blank" href="https://vzome.com" rel="noopener" >vZome</Link> technology.
            It is a proof-of-concept for building in 3D using a 2D "build plane".
          </Typography>
          <Typography gutterBottom>
            You can drag to rotate the view, right-drag to pan it, and zoom it using the mouse scroll gesture.
            To interact with the shapes, hover and click.
            Try clicking on different types of objects.
          </Typography>
          <Typography gutterBottom>
            There are many choices to make for an interaction like this, and there are probably
            better choices to be made.
            Please share your feedback by <Link target="_blank" href="mailto:info@vzome.com" rel="noopener" >email</Link> or
            join the <Link target="_blank" href="https://discord.gg/vhyFsNAFPS" rel="noopener" >Discord server</Link>.
          </Typography>
          <Typography gutterBottom>
            For the full suite of vZome web applications and technology,
            see <Link target="_blank" href="https://docs.vzome.com/online.html" rel="noopener" >this page</Link>.
          </Typography>
        </> } />
      <div id='viewer-and-undoredo'>
        <DesignViewer height="100%" width="100%" config={ { useSpinner: true } }
          children3d={ <BuildPlaneTool/> } />
        <UndoRedoButtons/>
      </div>
    </EditorProvider>
    </ViewerProvider>
    </WorkerProvider>
    </CameraProvider>
  </ErrorBoundary>
);

render( WorkerApp, document.getElementById( 'root' ) );
