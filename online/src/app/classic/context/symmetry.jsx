
import { createContext, createEffect, createSignal, on, useContext } from "solid-js";

import { controllerProperty, subController, useEditor } from '../../framework/context/editor.jsx';
import { useCamera } from "../../../viewer/context/camera.jsx";
import { useWorkerClient } from "../../../viewer/context/worker.jsx";

import { ShapesDialog } from "../dialogs/shapes.jsx";
import { OrbitsDialog } from "../dialogs/orbits.jsx";
import { PolytopesDialog } from "../dialogs/polytopes.jsx";
import { unwrap } from "solid-js/store";
import { snapCamera as snapCameraSetting } from "./rendering.jsx";


const SymmetryContext = createContext();

export const SymmetryProvider = (props) =>
{
  const { subscribeFor } = useWorkerClient();
  const { state, setCamera } = useCamera();
  const { rootController, controllerAction } = useEditor();

  const symmetry = () => controllerProperty( rootController(), 'symmetry' );
  const strutBuilder = () => subController( rootController(), 'strutBuilder' );
  const symmController = () => subController( strutBuilder(), `symmetry.${symmetry()}` );

  const [ showShapesDialog, setShowShapesDialog ] = createSignal( false );
  const [ showOrbitsDialog, setShowOrbitsDialog ] = createSignal( false );
  const [ showPolytopesDialog, setShowPolytopesDialog ] = createSignal( false );

  // For this session; the Rendering setting only chooses where it starts.
  const [ snapping, setSnapping ] = createSignal( snapCameraSetting .value() );
  const snapCamera = () =>
  {
    if ( snapping() ) {
      const { up, lookDir } = state.camera;
      controllerAction( symmController(), 'snapCamera', { up: unwrap(up), lookDir: unwrap(lookDir) } );
    }
  }

  const api = {
    snapping, snapCamera,
    toggleSnapping: () => { setSnapping( v => !v ); snapCamera() },
    symmetryDefined: () => !!symmetry(),
    symmetryController: () => symmController(),
    showShapesDialog: () => setShowShapesDialog( true ),
    showOrbitsDialog: () => setShowOrbitsDialog( true ),
    showPolytopesDialog: () => {
      controllerAction( subController( symmController(), 'polytopes' ), 'setQuaternion' );
      setShowPolytopesDialog( true );
    },
  };

  // Snap whenever the symmetry changes, including the first one on load.  Not on SYMMETRY_CHANGED:
  // the worker sends that before the 'symmetry' property, so symmController() would name the old
  // symmetry, or none at all on load, and the worker would fail to find the controller.
  createEffect( on( symmetry, s => { if ( s ) snapCamera(); } ) );

  subscribeFor( 'CAMERA_SNAPPED', ( data ) => {
    const { up, lookDir } = data;
    setCamera( data );
  })

  return (
    <SymmetryContext.Provider value={api}>

      {props.children}

      <Show when={!!symmetry()}>
        <ShapesDialog controller={symmController()} open={showShapesDialog()} close={ ()=>setShowShapesDialog(false) } />

        <OrbitsDialog controller={symmController()} open={showOrbitsDialog()} close={ ()=>setShowOrbitsDialog(false) } />

        <PolytopesDialog controller={symmController()} open={showPolytopesDialog()} close={ ()=>setShowPolytopesDialog(false) } />
      </Show>

    </SymmetryContext.Provider>
  );
}

export const useSymmetry = () => useContext( SymmetryContext );
