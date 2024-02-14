
import { createEffect } from "solid-js";

import Typography from '@suid/material/Typography';

import { InteractionToolProvider, useInteractionTool } from "../../viewer/context/interaction.jsx";
import { useWorkerClient } from "../../viewer/context/worker.jsx";
import { useViewer } from "../../viewer/context/viewer.jsx";
import { CameraProvider, useCamera } from "../../viewer/context/camera.jsx";
import { SceneCanvas } from '../../viewer/scenecanvas.jsx';

import { ALL_ORBITS, ModelWorker, useCellOrbits } from "./state.jsx";

const CellSelectorTool = props =>
{
  const { setState: setOrbit } = useCellOrbits();

  const handlers = {

    allowTrackball: true,

    onClick: ( id, position, type, selected, label ) => {
      if ( !!label ) { // a labeled panel
        setOrbit( label, value => !value );
      }
    },
    
    bkgdClick: () =>
    {
      props.orbits .forEach( orbit => {
        setOrbit( orbit, false );
      });
    },

    onDragStart: ( id, position, type, starting, evt ) => {},
    onDrag: evt => {},
    onDragEnd: evt => {},
    onContextMenu: ( id, position, type, selected ) => {}
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return null;
}

const SelectorCanvas = () =>
{
  const { subscribeFor } = useWorkerClient();
  const { scene, setScene } = useViewer();
  const { state: { camera }, setCamera } = useCamera();
  const orbitPanels = {}; // yes, this is not reactive
  const { state: toggles, initialize } = useCellOrbits();

  subscribeFor( 'SCENE_RENDERED', ( { scene } ) => {
    if ( scene.camera ) {
      const { distance, near, far, width } = camera;  // This looks circular, but it is not reactive code.
      // Use the camera from the loaded scene, except for the zoom.
      setCamera( { ...scene.camera, distance, near, far, width } );
    }
  });

  ALL_ORBITS .forEach( orbit => {
    // select or deselect all panels for this cell orbit
    createEffect( () => {
      const selected = toggles[ orbit ];
      // console.log( `orbit ${orbit} toggled to ${selected}` );
      const panels = orbitPanels[ orbit ];
      !!panels && panels .forEach( ( { id, i } ) => {
        setScene( 'shapes', id, 'instances', i, 'selected', selected );
      });
    });
  } );

  createEffect( () => {
    // index all the panels for later selection toggling by orbit
    if ( !! scene?.shapes ) {
      for ( const [id,shape] of Object.entries( scene.shapes ) ) {
        for ( const [i,instance] of shape.instances.entries() ) {
          if ( !! instance.label ) {
            const orbit = instance.label;
            const panel = { id, i };
            if ( ! orbitPanels[ orbit ] ) {
              orbitPanels[ orbit ] = [ panel ];
            } else {
              orbitPanels[ orbit ] .push( panel );
            }
          }
        }
      }
      initialize();
    }
  });

  return (
    <SceneCanvas rotationOnly={false} panSpeed={0} scene={scene}
      style={{ position: 'relative', height: '100%' }} height='100%' width='100%' />
  )
}

const Selector = props =>
{
  return (
    <div class='selector safe-grid-item' >
      <div class="centered-scroller">
        <div class='scroller-content'>
          <ModelWorker model={props.model} >
            <InteractionToolProvider>
              <CellSelectorTool model={props.model} orbits={props.orbits} />
              <SelectorCanvas/>
            </InteractionToolProvider>
          </ModelWorker>
        </div>
      </div>
    </div>
  )
}

export const Selectors = () =>
{
  return (
    <div id='text-and-selectors' >
      <div id='full-text'>
        <Typography gutterBottom sx={{ margin: '1em' }}>
          To toggle different subsets of the stellation in the main view,
          click on or touch the shapes below.
        </Typography>
      </div>
      <div id='minimal-text'>
        <Typography gutterBottom sx={{ margin: '0.4em' }}>
          Click on or touch the shapes below.
        </Typography>
      </div>
      <CameraProvider distance={500}>
        <div id='selectors' class='safe-grid-item'>
          <div class="centered-scroller">
            <div class='scroller-content'>
              <Selector model='pieces-aceg' orbits={['a', 'c', 'e1', 'e2', 'g1', 'g2']} />
            </div>
          </div>
          <div class="centered-scroller">
            <div class='scroller-content'>
              <Selector model='pieces-bdfh' orbits={['b', 'd', 'f1L', 'f1R', 'f2', 'h']} />
            </div>
          </div>
        </div>
      </CameraProvider>
    </div>
  );
}
