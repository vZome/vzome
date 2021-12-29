import React from 'react'
import { Alert, AlertTitle } from '@material-ui/lab';
import Snackbar from '@material-ui/core/Snackbar';


export const ErrorAlert = ( { message, dismissed } ) =>
{
  return (
    <Snackbar open={!!message} onClose={dismissed}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}>
      <Alert variant='filled' severity="error" onClose={dismissed}>
        <AlertTitle>There's a problem</AlertTitle>
        {message}
      </Alert>
    </Snackbar>
  )
}

