
import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

import { VZomeAppBar } from '../classic/components/appbar.jsx';
import { WorkerStateProvider } from '../../workerClient/context.jsx';

import { BuildPlaneTool } from './buildplane.jsx'
import { DesignViewer } from '../../viewer/solid/index.jsx';

const WorkerApp = () => (
  <ErrorBoundary fallback={err => <div>{err.toString()}</div>} >
    <WorkerStateProvider>
      <VZomeAppBar oneDesign={false} pathToRoot='..' forDebugger={false} title='Browser'
        about={ <>
          <Typography gutterBottom>
            This is an experimental web-based modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>,
            based on <Link target="_blank" href="https://vzome.com" rel="noopener" >vZome</Link> technology.
          </Typography>
        </> } />
      <DesignViewer height="100%" width="100%" config={ { useSpinner: true, undoRedo: true } }
        children3d={ <BuildPlaneTool/> } />
    </WorkerStateProvider>
  </ErrorBoundary>
);

render( WorkerApp, document.getElementById( 'root' ) );
