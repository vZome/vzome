
// babel workaround
import "regenerator-runtime/runtime";

import React, { useRef } from 'react';
import { render } from 'react-dom'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'

import { VZomeAppBar } from '../components/appbar.jsx'
import { BuildPlaneTool } from './buildplane.jsx'
import { DesignViewer, WorkerContext } from '../../ui/viewer/index.jsx'
import { createWorker } from "../../workerClient/client.js";

const worker = createWorker();

const App = () =>
{
  const toolRef = useRef();

  return (
    <>
      <VZomeAppBar oneDesign={false} pathToRoot='..' forDebugger={false} title='vZome Online'
        about={ <>
          <Typography gutterBottom>
            This is an experimental web-based modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>,
            based on <Link target="_blank" href="https://vzome.com" rel="noopener" >vZome</Link> technology.
          </Typography>
        </> }
      />
      <DesignViewer config={ { useSpinner: true, undoRedo: true } } toolRef={toolRef}
        children3d={ <BuildPlaneTool worker={worker} ref={toolRef} />
        } />
    </>
  );
}

const WorkerApp = () => (
  <WorkerContext worker={worker} >
    <App/>
  </WorkerContext>
);

render( <WorkerApp/>, document.getElementById( 'root' ) );
