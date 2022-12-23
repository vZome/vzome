
import React from 'react'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'

import { VZomeAppBar } from '../components/appbar.jsx';
import { WorkerContext } from '../../ui/viewer/index.jsx';

export const ClassicAppBar = () => (
  <WorkerContext>
    <VZomeAppBar oneDesign={true} pathToRoot='..' title='vZome Online Classic'
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
      </> } />
  </WorkerContext>
)
