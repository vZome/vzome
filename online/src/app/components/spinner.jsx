
import React from 'react'

import Backdrop from '@material-ui/core/Backdrop';
import CircularProgress from '@material-ui/core/CircularProgress';
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
