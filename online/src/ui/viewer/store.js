
import { wrapStore } from 'redux-in-worker';

export const createWorkerStore = () => wrapStore(
  new Worker( new URL( '/modules/vzome-worker-static.js', import.meta.url ), { type: 'module' } ),
  {}, // initial state
  window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__(),
);

