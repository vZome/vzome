import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

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
      <VZomeAppBar getScene={getScene} controller={rootController()} 
        about={ <>
          <Typography gutterBottom>
            vZome Online Classic is a work in progress, still at the proof-of-concept stage.  It will be part of a web-based modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>.
            The intention is for it to be a complete replacement for
            the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
          </Typography>
          <Typography gutterBottom>
            If you want to stay informed about progress on this app, or better yet offer feedback,
            join the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">Discord server</Link>.
          </Typography>
        </> }
      />
      <ClassicEditor getScene={getScene} setState={setState} controller={rootController()} />
    </ErrorBoundary>
  );
}

render( Classic, document.getElementById( 'root' ) );
