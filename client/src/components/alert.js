import React from 'react'
import { connect } from 'react-redux'
import { clearAlert } from '../bundles/alerts'
import Alert from 'react-bootstrap/Alert'
import Modal from 'react-bootstrap/Modal'

const ErrorAlert = ( { message, dismissed } ) =>
{
  return (
    <Modal show={message} size='lg' onEscapeKeyDown={dismissed}>
      <Modal.Body>
        <Alert variant="danger" onClose={dismissed} dismissible>
          <Alert.Heading>There's a problem.</Alert.Heading>
          <p>{message}</p>
        </Alert>
      </Modal.Body>
    </Modal>
  )
}

const select = (state) => ({
  message: state.alerts
})

const boundEventActions = {
  dismissed : clearAlert
}

export default connect( select, boundEventActions )( ErrorAlert )
