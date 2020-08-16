
import React, { useRef } from 'react'
import { connect } from 'react-redux'
import { fileSelected } from '../bundles/files'
import Button from 'react-bootstrap/Button'

let FileOpener = ({ enabled, loadFile }) =>
{
  const ref = useRef()
  const handleOpen = () => ref.current.click()

  return (
    <Button id="fileopener" variant="link" onClick={handleOpen}
      style={{ cursor: enabled ? 'pointer' : 'default' }} >
      <input className="FileInput" type="file" ref={ref}
        onChange={ (e) => {
            const selected = e.target.files && e.target.files[0]
            if ( selected )
              loadFile( selected )
          } }
        accept=".vZome" disabled={!enabled} /> 
      <img alt="Open your vZome file"  title="Open your vZome file" className="Icon" src="/app/folder-2.svg" />
    </Button>
  )
} 

const select = (state) => ({
  enabled: state.jre.javaReady
})

const boundEventActions = {
  loadFile : fileSelected
}

export default connect( select, boundEventActions )( FileOpener )
