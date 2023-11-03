
import { render } from 'solid-js/web';
import { ErrorBoundary, createEffect } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

import { VZomeAppBar } from '../classic/components/appbar.jsx';
import { WorkerStateProvider, useWorkerClient } from '../../workerClient/context.jsx';
import { SceneCanvas } from '../../viewer/solid/index.jsx'
import { getModelURL } from '../classic/components/folder.jsx';
import { CameraStateProvider, RotationProvider, useCameraState } from "../../viewer/solid/camera.jsx";
import { InteractionToolProvider } from '../../viewer/solid/interaction.jsx';
import { CellOrbitProvider, CellSelectorTool, useCellOrbits } from './selector.jsx';
import { LightedTrackballCanvas } from '../../viewer/solid/ltcanvas.jsx';
import { ShapedGeometry } from '../../viewer/solid/geometry.jsx';

const ModelCanvas = () =>
{
  const { state } = useWorkerClient();

  const scene = () => {
    let { camera, lighting, ...other } = state.scene;
    const backgroundColor = 'lightblue';
    lighting = { ...lighting, backgroundColor }; // override just the background
    return ( { ...other, camera, lighting } );
  }

  return (
    <SceneCanvas rotationOnly={true} scene={scene()}
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
      <CameraStateProvider>
        <ModelWorker model={props.model} >
          <InteractionToolProvider>
            <CellSelectorTool/>
            <ModelCanvas/>
          </InteractionToolProvider>
        </ModelWorker>
      </CameraStateProvider>
    </div>
  )
}

const CellOrbitScene = props =>
{
  const { state: geometry } = useWorkerClient();
  const { state: toggles } = useCellOrbits();
  const showCell = () => toggles[ props.cell ];

  return (
    <group>
      <Show when={ showCell() }>
        <ShapedGeometry embedding={geometry.scene?.embedding} shapes={geometry.scene?.shapes} />
      </Show>
    </group>
  );
}

const CellOrbit = props =>
{
  return (
    <ModelWorker model={props.cell} >
      <CellOrbitScene cell={props.cell} />
    </ModelWorker>
  );
}

const StellationCanvas = props =>
{
  const { state } = useCameraState();
  return (
    <LightedTrackballCanvas sceneCamera={state.scene?.camera} lighting={state.scene?.lighting}
        style={{ position: 'relative', height: '100%' }} height='100%' width='100%'
        rotationOnly={false} rotateSpeed={4.5} >
      {props.children}
    </LightedTrackballCanvas>
  );
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
      <CellOrbitProvider>
        <div id='text-and-selectors' style={{ display: 'grid', 'grid-template-rows': '15% 85%', height: '100%',
            'border-right': '2px solid darkgrey' }}>
          <Typography gutterBottom sx={{ margin: '1em' }}>
            To toggle different shapes in the stellation shown on the right,
            click on the shapes below.
          </Typography>
          <RotationProvider>
            <div id='selectors' style={{ display: 'grid', 'grid-template-rows': '1fr 1fr', margin: '1em' }}>
              <Selector model='pieces-aceg' />
              <Selector model='pieces-bdfh' />
            </div>
          </RotationProvider>
        </div>
        <CameraStateProvider>
          <StellationCanvas>
            <CellOrbit cell='a'/>
            <CellOrbit cell='b'/>
            <CellOrbit cell='c'/>
            <CellOrbit cell='d'/>
            <CellOrbit cell='e1'/>
            <CellOrbit cell='e2'/>
            <CellOrbit cell='f1L'/>
            <CellOrbit cell='f1R'/>
            <CellOrbit cell='f2'/>
            <CellOrbit cell='g1'/>
            <CellOrbit cell='g2'/>
            <CellOrbit cell='h'/>
          </StellationCanvas>
        </CameraStateProvider>
      </CellOrbitProvider>
    </div>
  </ErrorBoundary>
)

render( App, document.getElementById( 'root' ) );
