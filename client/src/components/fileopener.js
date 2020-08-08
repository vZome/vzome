
import React from 'react'
import { connect } from 'react-redux'
import { fileSelected } from '../bundles/files'

let FileOpener = ({ enabled, loadFile }) => {
		
  return ( 
    <div id="fileopener">
      <h3> 
      Select a vZome file: 
      </h3> 
      <div> 
        <input type="file" onChange={ (e) => {
            const selected = e.target.files && e.target.files[0]
            if ( selected )
              loadFile( selected )
          } }
          accept=".vZome" disabled={!enabled} /> 
      </div> 
    </div> 
  )
} 

const select = (state) => ({
  enabled: state.jre.javaReady
})

const boundEventActions = {
  loadFile : fileSelected
}

export default connect( select, boundEventActions )( FileOpener )
