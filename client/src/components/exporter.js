
import React from 'react'
import { connect } from 'react-redux'
import { actionTriggered } from '../bundles/vzomejava'

let Exporter = ({ enabled, doAction }) => {
  return ( 
    <button id="exporter" onClick={ () => doAction("export.dae") }
        title="Export as Collada (DAE)"
        style={{ cursor: enabled ? 'pointer' : 'default' }} >
        <img alt="Export a DAE file" className="Icon" src="/app/export.svg" />
    </button>
  )
} 

const select = (state) => ({
  enabled: state.jre.javaReady
})

const boundEventActions = {
  doAction : actionTriggered
}

export default connect( select, boundEventActions )( Exporter )
