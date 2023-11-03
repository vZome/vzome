
import { createContext, createEffect, useContext } from "solid-js";
import { createStore } from "solid-js/store";

import { useWorkerClient } from "../../workerClient/index.js";
import { useInteractionTool } from "../../viewer/solid/interaction.jsx";
import { controllerAction, subController } from "../../workerClient/controllers-solid.js";

const CellOrbitContext = createContext( {} );

export const CellOrbitProvider = ( props ) =>
{
  const [ state, setState ] = createStore( {} );
  
  return (
    <CellOrbitContext.Provider value={ { state, setState } }>
      {props.children}
    </CellOrbitContext.Provider>
  );
}

export const useCellOrbits = () => { return useContext( CellOrbitContext ); };

export const CellSelectorTool = props =>
{
  const { rootController } = useWorkerClient();
  const pickingController  = () => subController( rootController(), 'picking' );
  const { setState } = useCellOrbits();

  const handlers = {

    allowTrackball: true,

    onClick: ( id, position, type, selected, label ) => {
      if ( !!label ) { // a labeled panel
        controllerAction( pickingController(), 'SelectManifestation', { id } );
        setState( label, !selected );
      }
    },
    
    bkgdClick: () =>
    {
      controllerAction( rootController(), 'DeselectAll' );
      if ( props.model === 'pieces-aceg' ) {
        setState( 'a', false );
        setState( 'c', false );
        setState( 'e1', false );
        setState( 'e2', false );
        setState( 'g1', false );
        setState( 'g2', false );
      } else {
        setState( 'b', false );
        setState( 'd', false );
        setState( 'f1L', false );
        setState( 'f1R', false );
        setState( 'f2', false );
        setState( 'h', false );  
      }
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
