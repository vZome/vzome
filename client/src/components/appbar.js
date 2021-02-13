import React from 'react'
import { makeStyles } from '@material-ui/core/styles'
import AppBar from '@material-ui/core/AppBar'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'
import Box from '@material-ui/core/Box'

import AboutButton from './about'
import OpenButton from './folder'
import VZomeLogo from './logo'
import DesignsSelect from './designs'
import UndoRedoButtons from './undoredo'

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    zIndex: theme.zIndex.drawer + 1,
  },
  title: {
    marginLeft: theme.spacing(2),
    flexGrow: 1,
  },
  open: {
    marginRight: theme.spacing(5),
  },
}))

const VZomeAppBar = ( { article=false } ) =>
{
  const classes = useStyles()

  return (
    <div className={classes.root}>
      <AppBar position="static">
        <Toolbar>
          <VZomeLogo/>
          <Typography variant="h5" className={classes.title}>
            vZome <Box component="span" fontStyle="oblique">Online</Box>
          </Typography>
          { article? null :
            <>
              <UndoRedoButtons/>
              <DesignsSelect/>
              <OpenButton className={classes.open}/>
              <AboutButton/>
            </> }
        </Toolbar>
      </AppBar>
    </div>
  )
}

export default VZomeAppBar