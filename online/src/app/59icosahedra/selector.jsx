
import { createContext, createEffect, useContext } from "solid-js";
import { createStore } from "solid-js/store";

import { useWorkerClient } from "../../workerClient/index.js";
import { useInteractionTool } from "../../viewer/solid/interaction.jsx";
import { controllerAction, subController } from "../../workerClient/controllers-solid.js";

const CellOrbitContext = createContext( {} );

export const CellOrbitProvider = ( props ) =>
{
  const [ state, setState ] = createStore( {} );

  createEffect( () => console.log( JSON.stringify( state, null, 2 ) ) );
  
  return (
    <CellOrbitContext.Provider value={ { state, setState } }>
      {props.children}
    </CellOrbitContext.Provider>
  );
}

export const useCellOrbits = () => { return useContext( CellOrbitContext ); };

export const CellSelectorTool = () =>
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
      setState( () => {} );
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
