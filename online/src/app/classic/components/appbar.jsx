
import { Show, mergeProps } from 'solid-js'
import AppBar from '@suid/material/AppBar'
import Toolbar from '@suid/material/Toolbar'
import Typography from '@suid/material/Typography'
import Box from '@suid/material/Box'

import { OpenMenu } from './folder.jsx'
import { VZomeLogo } from './logo.jsx'
import { AboutDialog } from '../dialogs/about.jsx';
import { SharingDialog } from '../dialogs/sharing.jsx';

export const Spacer = () => <div style={{ flex: '1 1 auto' }}></div>

export const VZomeAppBar = ( props ) =>
{
  const spacer = <Spacer/>;
  const merged = mergeProps( {
    spacer,
    showOpen: false,
    pathToRoot: './models',
    forDebugger: false,
    customTitle: false,
  }, props );

  return (
    <div id="appbar" >
      <AppBar position="static" sx={{ backgroundColor: '#01203d' }}>
        <Toolbar>
          <Show when={ !merged.customTitle } fallback={
            <Typography variant="h5" sx={{ paddingLeft: '12px', paddingRight: '40px' }}>{props.title}</Typography>
          }>
            <VZomeLogo/>
            <Typography variant="h5" sx={{ paddingLeft: '12px', paddingRight: '40px' }}>
              vZome Online <Box component="span" fontStyle="oblique">{props.title}</Box>
            </Typography>
          </Show>
          {merged.spacer}
          <Show when={merged.showOpen} >
            <OpenMenu pathToRoot={merged.pathToRoot} forDebugger={merged.forDebugger} />
          </Show>
          <Show when={!!merged.spacer} >
            <SharingDialog/>
          </Show>
          <AboutDialog title={props.customTitle? props.title : 'vZome Online '+ props.title} about={props.about} />
        </Toolbar>
      </AppBar>
    </div>
  )
}
