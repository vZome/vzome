
import React, { useState } from 'react'
import { connect } from 'react-redux'
import { fetchModel } from '../bundles/files'
import Modal from 'react-bootstrap/Modal'
import Button from 'react-bootstrap/Button'
import Tab from 'react-bootstrap/Tab'
import Row from 'react-bootstrap/Row'
import Col from 'react-bootstrap/Col'
import Nav from 'react-bootstrap/Nav'
import OverlayTrigger from 'react-bootstrap/OverlayTrigger'
import Tooltip from 'react-bootstrap/Tooltip'

const models = [
  {
    key: "vZomeLogo",
    label: "vZome Logo",
    description: "The vZome logo, one tetrahedral cell of the 4D 600-cell (cell-first projection)"
  },
  {
    key: "affineDodec",
    label: "Stretched Dodecahedron",
    description: "A regular dodecahedron stretched by a linear transformation"
  },
  {
    key: "120-cell",
    label: "Hyper-dodecahedron",
    description: "The 4D analogue of a dodecahedron, with 120 dodecahedral cells"
  }
]

const Models = ({ enabled, openModel }) =>
{
  const [show, setShow] = useState( false )
  const [model, setModel] = useState( models[0].key )

  const handleCancel = () =>{
    setShow( false )
  }
  const handleOpen = () =>{
    setShow( false )
    openModel( `/app/models/${model}.vZome` )
  }
  const handleShow = () => setShow( true )

  return (
    <>
      <OverlayTrigger placement="bottom" overlay={<Tooltip>Built-in models</Tooltip>} >
        <Button id="models" variant="link" onClick={handleShow}
          style={{ cursor: enabled ? 'pointer' : 'default' }} >
          <img alt="select model" className="Icon" src="/app/dodecFaces.svg" />
        </Button>
      </OverlayTrigger>

      <Modal centered show={show} size='lg' onHide={handleCancel}>
        <Modal.Header closeButton>
          <Modal.Title>Built-in Models</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Tab.Container id="model-tabs" defaultActiveKey={model} onSelect={setModel}>
            <Row>
              <Col sm={4}>
                <Nav variant="pills" className="flex-column">
                  { models.map( (model) => (
                    <Nav.Item>
                      <Nav.Link key={model.key} eventKey={model.key}>{model.label}</Nav.Link>
                    </Nav.Item>
                  ) ) }
                </Nav>
              </Col>
              <Col sm={8}>
                <Tab.Content>
                  { models.map( (model) => (
                    <Tab.Pane key={model.key} eventKey={model.key}>{model.description}</Tab.Pane>
                  ) ) }
                </Tab.Content>
              </Col>
            </Row>
          </Tab.Container>
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
  enabled: state.jre.javaReady
})

const boundEventActions = {
  openModel : fetchModel
}

export default connect( select, boundEventActions )( Models )
