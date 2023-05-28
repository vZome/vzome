
import AppBar from '@suid/material/AppBar'
import Toolbar from '@suid/material/Toolbar'
import Typography from '@suid/material/Typography'
import Box from '@suid/material/Box'

// import { OpenMenu } from './folder.jsx'
import { VZomeLogo } from './logo.jsx'
import { FileMenu } from '../menus/filemenu.jsx';
import { EditMenu } from '../menus/editmenu.jsx';
import { ConstructMenu } from '../menus/constructmenu.jsx';
import { ToolsMenu } from '../menus/toolsmenu.jsx';
import { SystemMenu } from '../menus/systemmenu.jsx';
import { HelpMenu } from '../menus/help.jsx';
import { AboutDialog } from './about.jsx'
import { useWorkerClient } from '../../../workerClient/index.js'

// export const VZomeAppBar = ( { oneDesign, pathToRoot='.', forDebugger=false, title, about } ) =>
export const VZomeAppBar = ( props ) =>
{
  // const classes = useStyles()
  const { getScene, rootController } = useWorkerClient();

  return (
    <div id="appbar" >
      <AppBar position="static" sx={{ backgroundColor: '#01203d' }}>
        <Toolbar>
          <VZomeLogo/>
          <Typography variant="h5" sx={{ paddingLeft: '12px', paddingRight: '40px' }}>
            vZome Online <Box component="span" fontStyle="oblique">{props.title}</Box>
          </Typography>
          <Show when={props.menuBar}>
            <FileMenu/>
            <EditMenu/>
            <ConstructMenu/>
            <ToolsMenu/>
            <SystemMenu/>
            <HelpMenu/>
          </Show>
          {/* { !oneDesign && <OpenMenu pathToRoot={pathToRoot} forDebugger={forDebugger} className={classes.open} /> } */}
          <div style={{ flex: '1 1 auto' }}></div>
          <AboutDialog title={props.title} about={props.about} />
        </Toolbar>
      </AppBar>
    </div>
  )
}
