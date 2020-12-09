
import React, { useState } from 'react'
import { connect } from 'react-redux'
import { fetchModel } from '../bundles/files'
import Modal from 'react-bootstrap/Modal'
import Button from 'react-bootstrap/Button'
import Form from 'react-bootstrap/Form'
import OverlayTrigger from 'react-bootstrap/OverlayTrigger'
import Tooltip from 'react-bootstrap/Tooltip'

const WebLoader = ({ enabled, openModel }) =>
{
  const [show, setShow] = useState( false )
  const [url, setUrl] = useState( '' )

  const handleCancel = () =>{
    setShow( false )
  }
  const handleOpen = () =>{
    setShow( false )
    openModel( url )
  }
  const handleShow = () => setShow( true )
  const handleChange = (event) => setUrl( event.target.value )

  if ( !enabled )
    return null;
    
  return (
    <>
      <OverlayTrigger placement="bottom" overlay={<Tooltip>Open a remote vZome model</Tooltip>} >
        <Button id="web" variant="link" onClick={handleShow}
          style={{ cursor: enabled ? 'pointer' : 'default' }} >
          <img alt="web model" className="Icon" src="/app/dodecFaces.svg" />
        </Button>
      </OverlayTrigger>

      <Modal centered show={show} size='lg' onHide={handleCancel}>
        <Modal.Header closeButton>
          <Modal.Title>Load a Remote vZome Model</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group controlId="formUrl">
              <Form.Control type="url" placeholder="Enter URL" value={url} onChange={handleChange} />
              <Form.Text className="text-muted">
                The server must allow cross-origin access.
              </Form.Text>
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCancel}>
            Cancel
          </Button>
          <Button variant="primary" onClick={handleOpen}>
            Open
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  )
} 

const select = (state) => ({
  // TODO make a real selector!
  enabled: ( state.jre && state.jre.javaReady ) 
            || ( !state.jre && state.alerts !== undefined ) // there is no state for files, but this correlates
})

const boundEventActions = {
  openModel : fetchModel
}

export default connect( select, boundEventActions )( WebLoader )
