import React from 'react'
import { useDispatch, useSelector } from 'react-redux';

import { Alert, AlertTitle } from '@material-ui/lab';
import Backdrop from '@material-ui/core/Backdrop';
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
  backdrop: {
    zIndex: theme.zIndex.drawer - 1,
    position: "absolute",
    color: '#fff',
  },
}));

export const ErrorAlert = () =>
{
  const classes = useStyles();
  const report = useDispatch();
  const message = useSelector( state => state.problem );

  const dismissed = () => report( { type: 'ALERT_DISMISSED' } );

  return (
    <Backdrop className={classes.backdrop} open={!!message}
        anchororigin={{ vertical: 'top', horizontal: 'center' }}>
      <Alert variant='filled' severity="error" onClose={dismissed}>
        <AlertTitle>There's a problem</AlertTitle>
        {message}
      </Alert>
    </Backdrop>
  )
}

