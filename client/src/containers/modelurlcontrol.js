
import React from 'react'
import { connect } from 'react-redux'
import { urlProvided, viewClosed } from '../action-events'

let ModelUrlControl = ({ enabled, openUrl, closeUrl }) => {
  let input
  return (
    <div>
      <form onSubmit={e => {
        e.preventDefault()
        if (!input.value.trim()) {
          return
        }
        openUrl( input.value )
      }}>
        <input ref={node => {
          input = node
        }} disabled={!enabled} type="text" placeholder="vZome model URL..." />
        <button type="submit" disabled={!enabled}>
          Open
        </button>
        <button disabled={enabled} onClick={() => closeUrl( input.value )}>
          Close
        </button>
      </form>
    </div>
  )
}

const select = (state) => ({
  enabled: !state.connectionLive
})

const boundEventActions = {
  openUrl : urlProvided,
  closeUrl : viewClosed
}

export default connect( select, boundEventActions )( ModelUrlControl )
