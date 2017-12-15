
import React from 'react'
import { connect } from 'react-redux'
import { openUrl, closeView, connectSocket, disconnectSocket } from '../actions'

let ModelUrlControl = ({ enabled, dispatch }) => {
  let input

  return (
    <div>
      <form onSubmit={e => {
        e.preventDefault()
        if (!input.value.trim()) {
          return
        }
        dispatch( openUrl( input.value ) )
        dispatch( connectSocket( input.value ) )
      }}>
        <input ref={node => {
          input = node
        }} disabled={!enabled} type="text" placeholder="vZome model URL..." />
        <button type="submit" disabled={!enabled} >
          Open
        </button>
        <button disabled={enabled} onClick={() => {
          dispatch( disconnectSocket() )
          dispatch( closeView() )
        }}>
          Close
        </button>
      </form>
    </div>
  )
}

const mapStateToProps = (state) => ({
  enabled: !state.connectionLive
})

ModelUrlControl = connect(mapStateToProps)(ModelUrlControl)

export default ModelUrlControl
