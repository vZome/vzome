
import React from 'react'
import { connect } from 'react-redux'

import Backdrop from '@material-ui/core/Backdrop';
import CircularProgress from '@material-ui/core/CircularProgress';
import Typography from '@material-ui/core/Typography'
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: '#fff',
  },
}));

export const Spinner = ( { visible, message } ) =>
{
  const classes = useStyles();
  return (
    <Backdrop className={classes.backdrop} open={visible}>
      <CircularProgress color="inherit" />
      {/* <Typography variant="h2">{message}</Typography> */}
    </Backdrop>
  );
}

const select = ( { progress } ) => ({
  visible: progress && progress.showing,
  message: progress && progress.message
})

export default connect( select )( Spinner )
