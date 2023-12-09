
import { render } from 'solid-js/web';
import { ErrorBoundary, createContext, createEffect, createSignal, useContext } from "solid-js";

import Typography from '@suid/material/Typography';
import Link from '@suid/material/Link';
import FormControlLabel from '@suid/material/FormControlLabel';
import Switch from '@suid/material/Switch';

import { WorkerStateProvider, useWorkerClient } from '../../viewer/context/worker.jsx';
import { CameraProvider, useCamera } from "../../viewer/context/camera.jsx";
import { InteractionToolProvider } from '../../viewer/context/interaction.jsx';
import { SceneProvider, useScene } from '../../viewer/context/scene.jsx';

import { VZomeAppBar } from '../classic/components/appbar.jsx';
import { SceneCanvas } from '../../viewer/index.jsx'
import { getModelURL } from '../classic/components/folder.jsx';
import { CellOrbitProvider, CellSelectorTool, useCellOrbits } from './selector.jsx';
import { LightedTrackballCanvas } from '../../viewer/ltcanvas.jsx';
import { ShapedGeometry } from '../../viewer/geometry.jsx';

const SelectorCanvas = () =>
{
  const { subscribeFor } = useWorkerClient();
  const { scene } = useScene();
  const { state: { camera }, setCamera } = useCamera();

  subscribeFor( 'SCENE_RENDERED', ( { scene } ) => {
    if ( scene.camera ) {
      const { distance, near, far, width } = camera;  // This looks circular, but it is not reactive code.
      // Use the camera from the loaded scene, except for the zoom.
      setCamera( { ...scene.camera, distance, near, far, width } );
    }
  });

  return (
    <SceneCanvas rotationOnly={false} panSpeed={0} scene={scene}
      style={{ position: 'relative', height: '100%' }} height='100%' width='100%' />
  )
}

const ModelWorker = props =>
{
  const config = { url: getModelURL( props.model ), preview: true, debug: false, sceneTitle: props.sceneTitle };

  return (
    <WorkerStateProvider config={config} >
      <SceneProvider>
        {props.children}
      </SceneProvider>
    </WorkerStateProvider>
  )
}

const Selector = props =>
{
  return (
    <div class='selector' >
      <ModelWorker model={props.model} >
        <InteractionToolProvider>
          <CellSelectorTool model={props.model}/>
          <SelectorCanvas/>
        </InteractionToolProvider>
      </ModelWorker>
    </div>
  )
}

const CellOrbitScene = props =>
{
  const { scene, requestScene } = useScene();
  const { state: toggles } = useCellOrbits();
  const showCell = () => toggles[ props.cell ];
  const { showCutaway } = useContext( ViewOptions );

  createEffect( () => {
    requestScene( showCutaway()? 'cutaway' : 'full', { camera: false, lighting: false } );
  });

  // The group is necessary due to a defect in solid-three regarding conditional components
  return (
    <group>
      <Show when={ showCell() }>
        <ShapedGeometry embedding={scene?.embedding} shapes={scene?.shapes} />
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
    <ModelWorker model={props.cell} sceneTitle={ showCutaway()? 'cutaway' : 'full' } >
      <CellOrbitScene cell={props.cell} />
    </ModelWorker>
  );
}

const StellationCanvas = props =>
{
  const { showCutaway, setShowCutaway } = useContext( ViewOptions );
  const toggleCutaway = () => setShowCutaway( value => !value );

  return (
    <>
      <FormControlLabel label="Cutaway View" sx={{ margin: 'auto', position: 'absolute', bottom: '0.2rem', right: '1rem', 'z-index': '50' }}
        control={
          <Switch checked={showCutaway()} onChange={ toggleCutaway } size='medium' inputProps={{ "aria-label": "cutaway" }} />
        }/>
      <LightedTrackballCanvas height='100%' width='100%' rotationOnly={false} rotateSpeed={4.5} >
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
          <CameraProvider distance={500}>
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
          </CameraProvider>
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
          <CameraProvider distance={500}>
            <div id='selectors' >
              <Selector model='pieces-aceg' />
              <Selector model='pieces-bdfh' />
            </div>
          </CameraProvider>
        </div>
      </CellOrbitProvider>
    </div>
  </ErrorBoundary>
)

render( App, document.getElementById( 'root' ) );
