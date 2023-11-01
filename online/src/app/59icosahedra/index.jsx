
import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

import { VZomeAppBar } from '../classic/components/appbar.jsx';
import { WorkerStateProvider, useWorkerClient } from '../../workerClient/context.jsx';
import { SceneCanvas } from '../../viewer/solid/index.jsx'
import { getModelURL } from '../classic/components/folder.jsx';
import { RotationProvider } from "../../viewer/solid/rotation.jsx";
import { InteractionToolProvider } from '../../viewer/solid/interaction.jsx';
import { CellSelectorTool, MacroTriggerTool } from './selector.jsx';

// We must have this split using WorkerStateProvider and useWorkerClient because
//  LightedCameraControls and TrackballControls require the WorkerStateProvider anyway.
const ModelScene = props =>
{
  const { state } = useWorkerClient();

  const scene = () => {
    let { camera, lighting, ...other } = state.scene;
    const backgroundColor = 'lightblue';
    lighting = { ...lighting, backgroundColor }; // override just the background
    return ( { ...other, camera, lighting } );
  }

  return (
    <SceneCanvas rotationOnly={!props.freeCamera} scene={scene()}
      style={{ position: 'relative', height: '100%' }} height='100%' width='100%' />
  )
}

const ModelWorker = props =>
{
  const config = { url: getModelURL( props.model, '..' ), preview: false, debug: false };

  return (
    <WorkerStateProvider config={config} >
      {props.children}
    </WorkerStateProvider>
  )
}

const Selector = props =>
{
  return (
    <div style={{
      border: '2px solid darkgrey', 'border-radius' : '5px',
    }}>
      <ModelWorker model={props.model} >
        <InteractionToolProvider>
          <CellSelectorTool/>
          <ModelScene freeCamera={false} />
        </InteractionToolProvider>
      </ModelWorker>
    </div>
  )
}

const App = () => (
  <ErrorBoundary fallback={err => <div>{err.toString()}</div>} >
    <VZomeAppBar title='59 Icosahedra'
      about={ <>
        <Typography gutterBottom>
          This application is a work-in-progress.  When completed, it will let you explore
          the <Link target="_blank" rel="noopener" href="https://en.wikipedia.org/wiki/The_Fifty-Nine_Icosahedra/">
            59 stellations of the icosahedron
          </Link> identified by H. S. M. Coxeter.
          It is built using <Link target="_blank" rel="noopener" href='https://vzome.com'>vZome</Link> technology.
        </Typography>
      </> } />
    <div id='selectors-and-model' style={{ position: 'relative', display: 'grid', 'grid-template-columns': '2fr 3fr', height: '100%' }}>
      <div id='text-and-selectors' style={{ display: 'grid', 'grid-template-rows': '20% 80%', height: '100%',
          'border-right': '2px solid darkgrey' }}>
        <Typography gutterBottom sx={{ margin: '1em' }}>
          To toggle different shapes in the stellation shown on the right,
          click on the shapes below. (Stellation changes are not yet implemented.)
        </Typography>
        <RotationProvider>
          <div id='selectors' style={{ display: 'grid', 'grid-template-rows': '1fr 1fr', margin: '1em' }}>
            <Selector model='pieces-aceg' />
            <Selector model='pieces-bdfh' />
          </div>
        </RotationProvider>
      </div>
      <ModelWorker model='icosahedron-stellation-faces' >
        <InteractionToolProvider>
          <MacroTriggerTool/>
          <ModelScene freeCamera={true} />
        </InteractionToolProvider>
      </ModelWorker>
    </div>
  </ErrorBoundary>
)

render( App, document.getElementById( 'root' ) );
