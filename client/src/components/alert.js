import React from 'react'
import { connect } from 'react-redux'
import { clearAlert } from '../bundles/alerts'
import { Alert, AlertTitle } from '@material-ui/lab';
import Snackbar from '@material-ui/core/Snackbar';


const ErrorAlert = ( { message, dismissed } ) =>
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

const select = (state) => ({
  message: state.alerts
})

const boundEventActions = {
  dismissed : clearAlert
}

export default connect( select, boundEventActions )( ErrorAlert )
