
import { createContext, createEffect, useContext } from "solid-js";
import { createStore } from "solid-js/store";

import { useScene } from "../../workerClient/context.jsx";
import { useInteractionTool } from "../../viewer/solid/interaction.jsx";

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
  const { setState: setOrbit } = useCellOrbits();
  const { scene, setScene } = useScene();

  const updateOrbit = ( label, value ) =>
  {
    setOrbit( label, value );
    // Toggle all the panels labeled the same.
    //   This could be more performant, but we don't have too many objects to loop over.
    for ( const [id,shape] of Object.entries( scene.shapes ) ) {
      for ( const [i,instance] of shape.instances.entries() ) {
        if ( instance.label === label ) {
          setScene( 'shapes', id, 'instances', i, 'selected', value );
        }
      }
    }
  }

  const handlers = {

    allowTrackball: true,

    onClick: ( id, position, type, selected, label ) => {
      if ( !!label ) { // a labeled panel
        updateOrbit( label, !selected );

      }
    },
    
    bkgdClick: () =>
    {
      if ( props.model === 'pieces-aceg' ) {
        updateOrbit( 'a', false );
        updateOrbit( 'c', false );
        updateOrbit( 'e1', false );
        updateOrbit( 'e2', false );
        updateOrbit( 'g1', false );
        updateOrbit( 'g2', false );
      } else {
        updateOrbit( 'b', false );
        updateOrbit( 'd', false );
        updateOrbit( 'f1L', false );
        updateOrbit( 'f1R', false );
        updateOrbit( 'f2', false );
        updateOrbit( 'h', false );  
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
