
import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography';
import Link from '@suid/material/Link';

// Reusing app and viewer infrastructure
import { VZomeAppBar } from '../classic/components/appbar.jsx';

import { CellOrbitProvider } from './state.jsx';
import { CoxeterTable } from './table.jsx';
import { Selectors } from './selectors.jsx';
import { Stellation } from './stellation.jsx';

const App = () =>
{
  return (
    <ErrorBoundary fallback={err => <div>{err.toString()}</div>} >
      <VZomeAppBar title='The 59 Icosahedra' customTitle={true}
        about={ <>
          <Typography gutterBottom>
            This application lets you explore
            the <Link target="_blank" rel="noopener" href="https://en.wikipedia.org/wiki/The_Fifty-Nine_Icosahedra">
              59 stellations of the icosahedron
            </Link> identified by H. S. M. Coxeter.
            It was inspired by <Link target="_blank" rel="noopener" href="https://www.instructables.com/The-Magnetic-59-Icosahedra/">
              Bob Hearn's physical magnetic model
            </Link>.
          </Typography>
          <Typography gutterBottom>
            This application is a PROTOTYPE; please be patient with defects.
            It is built using <Link target="_blank" rel="noopener" href='https://vzome.com'>vZome</Link> technology.
            To share feedback, please join
            the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">
              vZome Discord server
            </Link>.
          </Typography>
        </> } />
      <div id='below-appbar' class='safe-grid-item' >
        <CellOrbitProvider>
          <div id='stellation' >
            <Stellation/>
          </div>
          <div id='alternatives' class='safe-grid-item' >
            <Selectors/>
            <CoxeterTable/>
          </div>
        </CellOrbitProvider>
      </div>
    </ErrorBoundary>
  )
}

render( App, document.getElementById( 'root' ) );
