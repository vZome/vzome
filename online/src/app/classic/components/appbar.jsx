
import AppBar from '@suid/material/AppBar'
import Toolbar from '@suid/material/Toolbar'
import Typography from '@suid/material/Typography'
import Box from '@suid/material/Box'

// import { AboutDialog } from './about.jsx'
// import { OpenMenu } from './folder.jsx'
import { VZomeLogo } from './logo.jsx'
import { MenuBar } from './menubar.jsx'

// const useStyles = makeStyles((theme) => ({
//   root: {
//     zIndex: theme.zIndex.drawer + 1,
//   },
//   title: {
//     marginLeft: theme.spacing(2),
//     flexGrow: 1,
//   },
//   open: {
//     marginRight: theme.spacing(5),
//   },
// }))

// export const VZomeAppBar = ( { oneDesign, pathToRoot='.', forDebugger=false, title, about } ) =>
export const VZomeAppBar = ( props ) =>
{
  // const classes = useStyles()

  return (
    <div id="appbar" >
      <AppBar position="static" sx={{ backgroundColor: '#01203d' }}>
        <Toolbar>
          <VZomeLogo/>
          <Typography variant="h5" sx={{ paddingLeft: '12px', paddingRight: '40px' }}>
            vZome Online <Box component="span" fontStyle="oblique">Classic</Box>
          </Typography>
          <MenuBar controller={props.controller} scene={props.getScene()} />
          {/* { !oneDesign && <OpenMenu pathToRoot={pathToRoot} forDebugger={forDebugger} className={classes.open} /> }
          <AboutDialog title={title} about={about} /> */}
        </Toolbar>
      </AppBar>
    </div>
  )
}
