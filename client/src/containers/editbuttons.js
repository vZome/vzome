
import React from 'react';
import { connect } from 'react-redux'
import { sendDataToWebSocketAction } from "redux-simple-websocket"

let EditButtons = ({ enabled, endpoint, dispatch }) => {
  
  return (
    enabled?
      <div>
        <button disabled={!enabled} onClick={() => {
          dispatch( sendDataToWebSocketAction( endpoint, { action: 'undoRedo/undo' } ) )
        }}>
          Undo
        </button>
        <button disabled={!enabled} onClick={() => {
          dispatch( sendDataToWebSocketAction( endpoint, { action: 'undoRedo/redo' } ) )
        }}>
          Redo
        </button>
      </div>
    :
      <div/>
  )
}

const select = (state) => ({
  enabled: state.connectionLive,
  endpoint: state.modelUrl
})

export default connect(select)(EditButtons)
