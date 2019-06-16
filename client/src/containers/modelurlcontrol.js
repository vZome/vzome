
import React from 'react'
import { connect } from 'react-redux'
import { openUrl, closeView } from '../actions'
import { connectWebSocketAction, closeWebSocketAction } from "redux-simple-websocket"

const ws_port = 8532  // TODO: allow override for dev case
const SERVER_URL = 'ws://' + window.location.hostname + ':' + ws_port + '/vZome?'

const encodeUrl = url => {
	 return SERVER_URL + encodeURIComponent( url )
}

let ModelUrlControl = ({ enabled, dispatch }) => {
  let input

  return (
    <div>
      <form onSubmit={e => {
        e.preventDefault()
        if (!input.value.trim()) {
          return
        }
        dispatch( openUrl( encodeUrl( input.value ) ) )
        dispatch( connectWebSocketAction( encodeUrl( input.value ) ) )
      }}>
        <input ref={node => {
          input = node
        }} disabled={!enabled} type="text" placeholder="vZome model URL..." />
        <button type="submit" disabled={!enabled} >
          Open
        </button>
        <button disabled={enabled} onClick={() => {
          dispatch( closeWebSocketAction( encodeUrl( input.value ) ) )
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
