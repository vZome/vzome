
import React from 'react'
import { connect } from 'react-redux'
import { fileSelected } from '../action-events'

let FileOpener = ({ selectedFile, loadFile }) => {
		
  return ( 
    <div> 
      <h3> 
      Select a vZome file: 
      </h3> 
      <div> 
        <input type="file" onChange={ (e) => {
            const selected = e.target.files && e.target.files[0]
            if ( selected )
              loadFile( selected )
            }
          }
          accept=".vZome" /> 
      </div> 
      {selectedFile
        ? <div> 
            <p>File Name: {selectedFile.name}</p> 
          </div>
        : <div> 
            <br /> 
            <h4>Choose a vZome file</h4> 
          </div>
      }
    </div> 
  )
} 

const select = (state) => ({
  selectedFile: state.selectedFile
})

const boundEventActions = {
  loadFile : fileSelected
}

export default connect( select, boundEventActions )( FileOpener )
