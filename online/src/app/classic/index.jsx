// babel workaround
import "regenerator-runtime/runtime";

import { render } from 'solid-js/web';
import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'

import { VZomeAppBar } from '../components/appbar.jsx'
import { ClassicEditor } from './classic.jsx';

const Classic = () => (
  <>
    <VZomeAppBar oneDesign={true} title='vZome Online Classic'
      about={ <>
        <Typography gutterBottom>
          vZome Online Classic is a work in progress.  It will be part of the world's first in-browser modeling tool
          for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>.
          The intention is for it to be a complete replacement for
          the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
        </Typography>
        <Typography gutterBottom>
          If you want to stay informed about my progress, follow vZome
          on <Link target="_blank" rel="noopener" href="https://www.facebook.com/vZome">Facebook</Link> or <Link target="_blank" rel="noopener" href="https://twitter.com/vZome">Twitter</Link>,
          or join the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">Discord server</Link>.
        </Typography>
      </> } />
    <ClassicEditor />
  </>
)

render( Classic, document.getElementById( 'root' ) );
