
import React, { useRef } from 'react'
import { connect } from 'react-redux'
import { fileSelected } from '../bundles/files'
import Button from 'react-bootstrap/Button'
import OverlayTrigger from 'react-bootstrap/OverlayTrigger'
import Tooltip from 'react-bootstrap/Tooltip'

let FileOpener = ({ enabled, loadFile }) =>
{
  const ref = useRef()
  const handleOpen = () => ref.current.click()

  if ( ! enabled )
    return null

    return (
    <OverlayTrigger placement="bottom" overlay={<Tooltip>Open a local vZome file</Tooltip>} >
      <Button id="fileopener" variant="link" onClick={handleOpen}
        style={{ cursor: enabled ? 'pointer' : 'default' }} >
        <input className="FileInput" type="file" ref={ref}
          onChange={ (e) => {
              const selected = e.target.files && e.target.files[0]
              if ( selected )
                loadFile( selected )
            } }
          accept=".vZome" disabled={!enabled} /> 
        <img alt="Open your vZome file" className="Icon" src="/app/folder-2.svg" />
      </Button>
    </OverlayTrigger>
  )
} 

const select = (state) => ({
  // TODO make a real selector!
  enabled: ( state.jre && state.jre.javaReady ) 
            || ( !state.jre && state.alerts !== undefined ) // there is no state for files, but this correlates
})

const boundEventActions = {
  loadFile : fileSelected
}

export default connect( select, boundEventActions )( FileOpener )
