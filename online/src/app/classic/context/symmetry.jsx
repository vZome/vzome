
import { createContext, createSignal, useContext } from "solid-js";

import { controllerProperty, subController, useEditor } from "../../../viewer/context/editor.jsx";
import { useCamera } from "../../../viewer/context/camera.jsx";
import { useWorkerClient } from "../../../viewer/context/worker.jsx";

import { ShapesDialog } from "../dialogs/shapes.jsx";
import { OrbitsDialog } from "../dialogs/orbits.jsx";
import { PolytopesDialog } from "../dialogs/polytopes.jsx";
import { unwrap } from "solid-js/store";


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

  const [ snapping, setSnapping ] = createSignal( false );
  const snapCamera = () =>
  {
    if ( snapping() ) {
      const { up, lookDir } = state.camera;
      controllerAction( symmController(), 'snapCamera', { up: unwrap(up), lookDir: unwrap(lookDir) } );
    }
  }

  const api = {
    snapping, snapCamera,
    toggleSnapping: () => setSnapping( v => !v ),
    symmetryDefined: () => !!symmetry(),
    symmetryController: () => symmController(),
    showShapesDialog: () => setShowShapesDialog( true ),
    showOrbitsDialog: () => setShowOrbitsDialog( true ),
    showPolytopesDialog: () => {
      controllerAction( subController( symmController(), 'polytopes' ), 'setQuaternion' );
      setShowPolytopesDialog( true );
    },
  };

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
