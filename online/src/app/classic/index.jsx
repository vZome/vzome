// babel workaround
import "regenerator-runtime/runtime";

import { render } from 'solid-js/web';
import { solidify } from './solid-react.jsx';

import { ClassicAppBar } from './appbar.jsx';
import { ClassicEditor } from './classic.jsx';
import { createWorker } from "../../workerClient/client.js";

const AppBar = solidify( ClassicAppBar );

const worker = createWorker();

const Classic = () => (
  <>
    <AppBar worker={worker} />
    <ClassicEditor worker={worker} />
  </>
)

render( Classic, document.getElementById( 'root' ) );
