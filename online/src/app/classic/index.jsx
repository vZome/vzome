import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import { VZomeAppBar } from './components/appbar.jsx';
import { createWorkerStore } from './controllers-solid.js';
import { ClassicEditor } from './classic.jsx';
import { createWorker } from "../../workerClient/client.js";

const worker = createWorker();

const Classic = () =>
{
  const { rootController, getScene, setState } = createWorkerStore( worker );

  return (
    <ErrorBoundary fallback={err => err}>
      <VZomeAppBar getScene={getScene} controller={rootController()} />
      <ClassicEditor getScene={getScene} setState={setState} controller={rootController()} />
    </ErrorBoundary>
  );
}

render( Classic, document.getElementById( 'root' ) );
