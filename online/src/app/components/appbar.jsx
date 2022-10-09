
import AppBar from '@suid/material/AppBar'
import Toolbar from '@suid/material/Toolbar'
import Typography from '@suid/material/Typography'
import Box from '@suid/material/Box'

// import { AboutDialog } from './about.jsx'
// import { OpenMenu } from './folder.jsx'
import { VZomeLogo } from './logo.jsx'

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

export const VZomeAppBar = props =>
{
  const classes = { root: {}, title: {}, open: {} };

  return (
    <div id="appbar" class={classes.root}>
      <AppBar position="static">
        <Toolbar>
          <VZomeLogo/>
          <Typography variant="h5" class={classes.title}>
            vZome <Box component="span" fontStyle="oblique">Online</Box>
          </Typography>
          {/* <Show when={ ! props.oneDesign }>
            <OpenMenu pathToRoot={props.pathToRoot} forDebugger={props.forDebugger} class={classes.open} />
          </Show> */}
          {/* <AboutDialog title={props.title} about={props.about} /> */}
        </Toolbar>
      </AppBar>
    </div>
  )
}
