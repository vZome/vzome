
import { For, createContext, createEffect, createSignal, useContext } from "solid-js";

import FormControlLabel from '@suid/material/FormControlLabel';
import Switch from '@suid/material/Switch';

// Reusing app and viewer infrastructure
import { LightedTrackballCanvas } from '../../viewer/ltcanvas.jsx';
import { ShapedGeometry } from '../../viewer/geometry.jsx';
import { CameraProvider } from "../../viewer/context/camera.jsx";

import { ALL_ORBITS, ModelWorker, labelString, useCellOrbits } from './state.jsx';
import { useScene, useSceneTitles } from "../../viewer/context/scene.jsx";


const CellOrbitScene = props =>
{
  const { scene } = useScene();
  const { showTitledScene } = useSceneTitles();
  const { state: toggles } = useCellOrbits();
  const showCell = () => toggles[ props.cell ];
  const { showCutaway } = useContext( ViewOptions );

  createEffect( () => {
    showTitledScene( showCutaway()? 'cutaway' : 'full', { camera: false } );
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

const ViewOptionsProvider = ( props ) =>
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
  const { enabledOrbits } = useCellOrbits();
  const label = () => labelString( enabledOrbits() );
  const { showCutaway, setShowCutaway } = useContext( ViewOptions );
  const toggleCutaway = () => setShowCutaway( value => !value );

  return (
    <>
      <h1 id='stellation-label'>
        {label()}
      </h1>
      <FormControlLabel label="Cutaway View" sx={{ margin: 'auto', position: 'absolute', bottom: '0.5rem', right: '1.5rem', 'z-index': '50' }}
        control={
          <Switch checked={showCutaway()} onChange={ toggleCutaway } size='medium' inputProps={{ "aria-label": "cutaway" }} />
        }/>
      <LightedTrackballCanvas height='100%' width='100%' rotationOnly={false} rotateSpeed={4.5} >
        {props.children}
      </LightedTrackballCanvas>
    </>
  );
}

export const Stellation = () =>
{
  return (
    <CameraProvider distance={200}>
      <ViewOptionsProvider>
        <StellationCanvas>
          <For each={ ALL_ORBITS } >{ orbit =>
            <CellOrbit cell={orbit}/>
          }</For>
        </StellationCanvas>
      </ViewOptionsProvider>
    </CameraProvider>
  );
}