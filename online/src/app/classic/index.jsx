import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

import { VZomeAppBar } from './components/appbar.jsx';
import { ClassicEditor } from './classic.jsx';
import { WorkerStateProvider } from '../../workerClient/index.js';

const Classic = () =>
{
  return (
    <ErrorBoundary fallback={ err => <div>{err.toString()}</div> } >
      <WorkerStateProvider>
        <VZomeAppBar menuBar={true}
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
        <ClassicEditor/>
      </WorkerStateProvider>
    </ErrorBoundary>
  );
}

render( Classic, document.getElementById( 'root' ) );
