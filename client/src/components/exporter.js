
import React, { useState } from 'react'
import { connect } from 'react-redux'
import { exportTriggered } from '../bundles/vzomejava'
import Modal from 'react-bootstrap/Modal'
import Button from 'react-bootstrap/Button'
import Dropdown from 'react-bootstrap/Dropdown'

const Exporter = ({ enabled, doExport }) =>
{
  const [show, setShow] = useState(false);

  const handleCancel = () =>{
    setShow( false )
  }
  const handleExport = () =>{
    setShow( false )
    doExport( "dae", "Exporting Collada (DAE)..." )
  }
  const handleShow = () => setShow(true);

  return (
    <>
      <Button id="exporter" variant="link" onClick={handleShow}
          style={{ cursor: enabled ? 'pointer' : 'default' }} >
          <img alt="export .dae" className="Icon" src="/app/export.svg" />
      </Button>

      <Modal centered show={show} onHide={handleCancel}>
        <Modal.Header closeButton>
          <Modal.Title>Export as COLLADA (dae)</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <a href="https://en.wikipedia.org/wiki/COLLADA" target="_blank" rel="noopener noreferrer">COLLADA (COLLAborative Design Activity) </a>
          is an interchange file format for interactive 3D applications.
          COLLADA documents are XML files, usually identified with a .dae (digital asset exchange) filename extension.
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCancel}>
            Cancel
          </Button>
          <Button variant="primary" onClick={handleExport}>
            Export
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  )
} 

const select = (state) => ({
  enabled: state.jre.javaReady
})

const boundEventActions = {
  doExport : exportTriggered
}

export default connect( select, boundEventActions )( Exporter )
