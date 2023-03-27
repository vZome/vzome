
import AppBar from '@suid/material/AppBar'
import Toolbar from '@suid/material/Toolbar'
import Typography from '@suid/material/Typography'
import Box from '@suid/material/Box'
import Link from '@suid/material/Link'

// import { OpenMenu } from './folder.jsx'
import { VZomeLogo } from './logo.jsx'
import { MenuBar } from './menubar.jsx'
import { AboutDialog } from './about.jsx'

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
          {/* { !oneDesign && <OpenMenu pathToRoot={pathToRoot} forDebugger={forDebugger} className={classes.open} /> } */}
          <div style={{ flex: '1 1 auto' }}></div>
          <AboutDialog title='vZome Online Classic' 
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

        </Toolbar>
      </AppBar>
    </div>
  )
}
