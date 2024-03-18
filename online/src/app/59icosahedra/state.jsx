
import { createContext, createEffect, createSignal, useContext } from "solid-js";
import { createStore } from "solid-js/store";

import { getModelURL } from "../classic/components/folder.jsx";
import { WorkerStateProvider } from "../../viewer/context/worker.jsx";
import { ViewerProvider } from "../../viewer/context/viewer.jsx";

const CHIRAL_PAIRS = { f1: [ 'f1L', 'f1R' ] };

const resolveOrbit = key => CHIRAL_PAIRS[ key ] || [ key ];

const LABELS = {
  e1: 'e₁',
  e2: 'e₂',
  f1: 'f₁',
  f2: 'f₂',
  g1: 'g₁',
  g2: 'g₂',
  f1R: "f₁ᴿ",
  f1L: "f₁ᴸ",
}

export const resolveLabel = key => LABELS[ key ] || key;

export const SHELLS = {
  a: [ 'a' ],
  b: [ 'b' ],
  c: [ 'c' ],
  d: [ 'd' ],
  e: [ 'e1', 'e2' ],
  f: [ 'f1', 'f2' ],
  g: [ 'g1', 'g2' ],
  h: [ 'h' ]
}

export const CORES = [];
let allOrbits = [];
for ( const [key, value] of Object.entries( SHELLS ) ) {
  const coreKey = key .toUpperCase();
  const coreValue = [ ...value ];
  coreValue .forEach( (v,i,a) => a.splice( i, 1, ...resolveOrbit( v ) ) );
  CORES[ coreKey ] = [ ...allOrbits, ...coreValue ];
  allOrbits = [ ...CORES[ coreKey ] ];
}
export const ALL_ORBITS = allOrbits;

export const resolveOrbits = inList =>
{
  let outList = [];
  inList .forEach( shorthand => {
    outList = outList .concat( CORES[ shorthand ] || SHELLS[ shorthand ] || resolveOrbit( shorthand ) )
  } );
  return outList;
}

export const labelString = orbits => orbits .map( k => resolveLabel( k ) ) .join( '' );

const CellOrbitContext = createContext( {} );

export const CellOrbitProvider = ( props ) =>
{
  const [ state, setState ] = createStore( {} );
  const enabledOrbits = () => Object.entries( state ) .filter( ([ k, v ]) => v ) .map( ([k,v]) => k );

  const setOrbitList = orbits => {
    setState( Object.fromEntries( ALL_ORBITS .map( orbit => [ orbit, false ] ) ) );
    setState( Object.fromEntries( orbits .map( orbit => [ orbit, true ] ) ) );
  }

  const initialize = () => setOrbitList( resolveOrbits( [ 'E', 'f1R' ] ) ); // default to 5 tetra compound
  
  return (
    <CellOrbitContext.Provider value={ { state, setState, enabledOrbits, setOrbitList, initialize } }>
      {props.children}
    </CellOrbitContext.Provider>
  );
}

export const useCellOrbits = () => { return useContext( CellOrbitContext ); };

export const ModelWorker = props =>
{
  const config = { url: getModelURL( props.model ), preview: true, debug: false, sceneTitle: props.sceneTitle, labels: props.labels };

  return (
    <WorkerStateProvider>
      <ViewerProvider config={config} >
        {props.children}
      </ViewerProvider>
    </WorkerStateProvider>
  )
}
