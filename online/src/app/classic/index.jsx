// babel workaround
import "regenerator-runtime/runtime";

import React from 'react';
import { render } from 'react-dom'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'

import { VZomeAppBar } from '../components/appbar.jsx'
import { WorkerContext } from '../../ui/viewer/index.jsx'
import { ClassicEditor } from './classic.jsx';

const Classic = () => (
  <WorkerContext>
    <VZomeAppBar oneDesign={false} pathToRoot='..' title='vZome Online Classic'
      about={ <>
        <Typography gutterBottom>
          vZome Online Classic is a work in progress, still at the proof-of-concept stage.  It will be part of an in-browser modeling tool
          for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>.
          The intention is for it to be a complete replacement for
          the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
        </Typography>
        <Typography gutterBottom>
          If you want to stay informed about progress on this app, or better yet offer feedback,
          join the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">Discord server</Link>.
        </Typography>
      </> } />
    <ClassicEditor />
  </WorkerContext>
)

render( <Classic />, document.getElementById( 'root' ) )
