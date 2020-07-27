
import React from 'react'
import { connect } from 'react-redux'
import { urlProvided, viewClosed } from '../action-events'
import { connectWebSocketAction, closeWebSocketAction } from "redux-simple-websocket"

const BASE_URL = 'wss://vzome-websocket.herokuapp.com'
// const BASE_URL = 'ws://' + window.location.hostname + ':8532'
const SERVER_URL = BASE_URL + '/vZome?'

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
        dispatch( urlProvided( encodeUrl( input.value ) ) )
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
          dispatch( viewClosed() )
        }}>
          Close
        </button>
      </form>
    </div>
  )
}

const select = (state) => ({
  enabled: !state.connectionLive
})

export default connect(select)(ModelUrlControl)
