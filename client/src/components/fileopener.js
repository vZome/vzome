
import React, { useRef } from 'react'
import { connect } from 'react-redux'
import { fileSelected } from '../bundles/files'

let FileOpener = ({ enabled, loadFile }) => {
	const ref = useRef()
  return ( 
    <div id="fileopener" onClick={ () => ref.current.click() }
        style={{ cursor: enabled ? 'pointer' : 'default' }} >
      <input className="FileInput" type="file" ref={ref}
        onChange={ (e) => {
            const selected = e.target.files && e.target.files[0]
            if ( selected )
              loadFile( selected )
          } }
        accept=".vZome" disabled={!enabled} /> 
      <img alt="Open your vZome file"  title="Open your vZome file" className="Icon" src="/app/folder-2.svg" />
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
