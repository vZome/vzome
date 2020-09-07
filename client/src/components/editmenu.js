
import React from 'react'
import { connect } from 'react-redux'
import { commandTriggered } from '../bundles/mesh'
import Dropdown from 'react-bootstrap/Dropdown'

const EditMenu = ({ doEdit }) =>
{
  return (
    <Dropdown>
      <Dropdown.Toggle variant="success" id="dropdown-basic">
        Edits
      </Dropdown.Toggle>

      <Dropdown.Menu>
        <Dropdown.Item onClick={ e => doEdit( 'random' ) }>New Random</Dropdown.Item>
        <Dropdown.Item onClick={ e => doEdit( 'allSelected' ) }>Select All</Dropdown.Item>
        <Dropdown.Item onClick={ e => doEdit( 'allDeselected' ) }>Deselect All</Dropdown.Item>
        <Dropdown.Item onClick={ e => doEdit( 'centroid' ) }>Centroid 1</Dropdown.Item>
        <Dropdown.Item onClick={ e => doEdit( 'NewCentroid' ) }>Centroid 2</Dropdown.Item>
        <Dropdown.Item onClick={ e => doEdit( 'ShowPoint', { mode: 'origin' } ) }>Show Origin</Dropdown.Item>
        <Dropdown.Item onClick={ e => doEdit( 'Hide' ) }>Hide</Dropdown.Item>
        <Dropdown.Item onClick={ e => doEdit( 'ShowHidden' ) }>Show Hidden</Dropdown.Item>
        <Dropdown.Item onClick={ e => doEdit( 'Delete' ) }>Delete</Dropdown.Item>
      </Dropdown.Menu>
    </Dropdown>
  )
} 

const select = (state) => ({
  enabled: state.jre.javaReady
})

const boundEventActions = {
  doEdit : commandTriggered,
}

export default connect( select, boundEventActions )( EditMenu )
