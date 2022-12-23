// babel workaround
import "regenerator-runtime/runtime";

import { render } from 'solid-js/web';
import { solidify } from './solid-react.jsx';

import { ClassicAppBar } from './appbar.jsx';
import { ClassicEditor } from './classic.jsx';

const AppBar = solidify( ClassicAppBar );

const Classic = () => (
  <>
    <AppBar />
    <ClassicEditor />
  </>
)

render( Classic, document.getElementById( 'root' ) );
