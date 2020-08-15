
import React from 'react'
import { connect } from 'react-redux'
import { exportTriggered } from '../bundles/vzomejava'

let Exporter = ({ enabled, doExport }) => {
  return ( 
    <button id="exporter" onClick={ () => doExport( "dae", "Exporting Collada (DAE)..." ) }
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
  doExport : exportTriggered
}

export default connect( select, boundEventActions )( Exporter )
