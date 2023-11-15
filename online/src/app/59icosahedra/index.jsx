
import { render } from 'solid-js/web';
import { ErrorBoundary, createContext, createEffect, createSignal, useContext } from "solid-js";

import Typography from '@suid/material/Typography';
import Link from '@suid/material/Link';
import FormControlLabel from '@suid/material/FormControlLabel';
import Switch from '@suid/material/Switch';

import { VZomeAppBar } from '../classic/components/appbar.jsx';
import { WorkerStateProvider, useWorkerClient } from '../../workerClient/context.jsx';
import { SceneCanvas } from '../../viewer/solid/index.jsx'
import { getModelURL } from '../classic/components/folder.jsx';
import { CameraStateProvider, RotationProvider, useCameraState } from "../../viewer/solid/camera.jsx";
import { InteractionToolProvider } from '../../viewer/solid/interaction.jsx';
import { CellOrbitProvider, CellSelectorTool, useCellOrbits } from './selector.jsx';
import { LightedTrackballCanvas } from '../../viewer/solid/ltcanvas.jsx';
import { ShapedGeometry } from '../../viewer/solid/geometry.jsx';
import { selectScene } from '../../workerClient/actions.js';

const SelectorCanvas = () =>
{
  const { state } = useWorkerClient();

  const scene = () => {
    let { camera, lighting, ...other } = state.scene;
    const backgroundColor = 'lightblue';
    lighting = { ...lighting, backgroundColor }; // override just the background
    return ( { ...other, camera, lighting } );
  }

  return (
    <SceneCanvas rotationOnly={false} panSpeed={0} scene={scene()}
      style={{ position: 'relative', height: '100%' }} height='100%' width='100%' />
  )
}

const ModelWorker = props =>
{
  const config = { url: getModelURL( props.model ), preview: props.preview, debug: false, sceneTitle: props.sceneTitle };

  return (
    <WorkerStateProvider config={config} >
      {props.children}
    </WorkerStateProvider>
  )
}

const Selector = props =>
{
  return (
    <div class='selector' >
      <CameraStateProvider>
        <ModelWorker model={props.model} preview={true} >
          <InteractionToolProvider>
            <CellSelectorTool model={props.model}/>
            <SelectorCanvas/>
          </InteractionToolProvider>
        </ModelWorker>
      </CameraStateProvider>
    </div>
  )
}

const CellOrbitScene = props =>
{
  const { state: geometry, postMessage } = useWorkerClient();
  const { state: toggles } = useCellOrbits();
  const showCell = () => toggles[ props.cell ];
  const { showCutaway } = useContext( ViewOptions );

  createEffect( () => {
    postMessage( selectScene( showCutaway()? 'cutaway' : 'full' ) );
  });

  // The group is necessary due to a defect in solid-three regarding conditional components
  return (
    <group>
      <Show when={ showCell() }>
        <ShapedGeometry embedding={geometry.scene?.embedding} shapes={geometry.scene?.shapes} />
      </Show>
    </group>
  );
}

const ViewOptions = createContext( { showCutaway: () => true } );

export const ViewOptionsProvider = ( props ) =>
{
  const [ showCutaway, setShowCutaway ] = createSignal( false );
  
  return (
    <ViewOptions.Provider value={ { showCutaway, setShowCutaway } }>
      {props.children}
    </ViewOptions.Provider>
  );
}

const CellOrbit = props =>
{
  const { showCutaway } = useContext( ViewOptions );


  return (
    <ModelWorker model={props.cell} preview={true} sceneTitle={ showCutaway()? 'cutaway' : 'full' } >
      <CellOrbitScene cell={props.cell} />
    </ModelWorker>
  );
}

const StellationCanvas = props =>
{
  const { state } = useCameraState();
  const { showCutaway, setShowCutaway } = useContext( ViewOptions );
  const toggleCutaway = () => setShowCutaway( value => !value );

  const scene = () => {
    let { camera, lighting, ...other } = state.scene;
    const backgroundColor = 'lightblue';
    lighting = { ...lighting, backgroundColor }; // override just the background
    return ( { ...other, camera, lighting } );
  }

  return (
    <>
      <FormControlLabel label="Cutaway View" sx={{ margin: 'auto', position: 'absolute', bottom: '0.2rem', right: '1rem', 'z-index': '50' }}
        control={
          <Switch checked={showCutaway()} onChange={ toggleCutaway } size='medium' inputProps={{ "aria-label": "cutaway" }} />
        }/>
      <LightedTrackballCanvas sceneCamera={scene().camera} lighting={scene().lighting}
          height='100%' width='100%'
          rotationOnly={false} rotateSpeed={4.5} >
        {props.children}
      </LightedTrackballCanvas>
    </>
  );
}

const App = () => (
  <ErrorBoundary fallback={err => <div>{err.toString()}</div>} >
    <VZomeAppBar title='The 59 Icosahedra' customTitle={true}
      about={ <>
        <Typography gutterBottom>
          This application lets you explore
          the <Link target="_blank" rel="noopener" href="https://en.wikipedia.org/wiki/The_Fifty-Nine_Icosahedra">
            59 stellations of the icosahedron
          </Link> identified by H. S. M. Coxeter.
          It was inspired by <Link target="_blank" rel="noopener" href="https://www.instructables.com/The-Magnetic-59-Icosahedra/">
            Bob Hearn's physical magnetic model
          </Link>.
        </Typography>
        <Typography gutterBottom>
          This application is a PROTOTYPE; please be patient with defects.
          It is built using <Link target="_blank" rel="noopener" href='https://vzome.com'>vZome</Link> technology.
          To share feedback, please join
          the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">
            vZome Discord server
          </Link>.
        </Typography>
      </> } />
    <div id='below-appbar' >
      <CellOrbitProvider>
        <div id='stellation' >
          <CameraStateProvider>
            <ViewOptionsProvider>
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
            </ViewOptionsProvider>
          </CameraStateProvider>
        </div>
        <div id='text-and-selectors' >
          <div id='full-text'>
            <Typography gutterBottom sx={{ margin: '1em' }}>
              To toggle different subsets of the stellation in the main view,
              click on or touch the shapes below.
            </Typography>
          </div>
          <div id='minimal-text'>
            <Typography gutterBottom sx={{ margin: '1em' }}>
              Click on or touch the shapes below.
            </Typography>
          </div>
          <RotationProvider>
            <div id='selectors' >
              <Selector model='pieces-aceg' />
              <Selector model='pieces-bdfh' />
            </div>
          </RotationProvider>
        </div>
      </CellOrbitProvider>
    </div>
  </ErrorBoundary>
)

render( App, document.getElementById( 'root' ) );
