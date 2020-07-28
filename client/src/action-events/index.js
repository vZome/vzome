import { connectWebSocketAction, closeWebSocketAction } from "redux-simple-websocket"

const BASE_URL = 'wss://vzome-websocket.herokuapp.com'
// const BASE_URL = 'ws://' + window.location.hostname + ':8532'
const SERVER_URL = BASE_URL + '/vZome?'

const encodeUrl = url => {
	 return SERVER_URL + encodeURIComponent( url )
}

export const URL_PROVIDED = 'URL_PROVIDED'
export const FILE_SELECTED = 'FILE_SELECTED'
export const VIEW_CLOSED = 'VIEW_CLOSED'
export const BACKGROUND_SET = 'BACKGROUND_SET'
export const JAVA_MESSAGE_RECEIVED = 'JAVA_MESSAGE'

export const urlProvided = (url) => dispatch => {
  const encodedUrl = encodeUrl( url )
  dispatch( {
    type: URL_PROVIDED,
    payload: encodedUrl
  } )
  dispatch( connectWebSocketAction( encodedUrl ) )
}

export const viewClosed = (url) => dispatch => {
  const encodedUrl = encodeUrl( url )
  dispatch( closeWebSocketAction( encodedUrl ) )
  dispatch( {
    type: VIEW_CLOSED
  } )
}

export const backgroundSet = (color) => ({
  type: BACKGROUND_SET,
  payload: color
})

export const javaMessageReceived = (message) => ({
  type: JAVA_MESSAGE_RECEIVED,
  payload: message
})

export const fileSelected = (selected) => dispatch => {

  console.log( selected )

  dispatch( {
    type: FILE_SELECTED,
    payload: selected
  } )
  
  // read the file
  const reader = new FileReader();

  // file reading finished successfully
  reader.onload = () => {
    var text = reader.result
    const path = "/str/" + selected.name
    window.cheerpjAddStringFile( path, text )
    console.log( "Loaded " + path )

    window.cjCall( "com.vzome.cheerpj.RemoteClientShim", "openFile", path )
  }

  // file reading failed
  reader.onerror = () => alert('Error : Failed to read file')

  // read as text file
  reader.readAsText( selected )
}
