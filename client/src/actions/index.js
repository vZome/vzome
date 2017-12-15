
import { WEBSOCKET_CONNECT, WEBSOCKET_DISCONNECT, WEBSOCKET_SEND } from 'redux-websocket'

export const OPEN_URL = 'OPEN_URL'
export const CLOSE_VIEW = 'CLOSE_VIEW'
export const SET_BACKGROUND = 'SET_BACKGROUND'
export const ADD_STRUT = 'ADD_STRUT'
export const REMOVE_STRUT = 'REMOVE_STRUT'

const SERVER_URL = 'ws://192.168.1.100:8532/vZome?'

export const openUrl = (url) => ({
  type: OPEN_URL,
  url
})

export const setBackground = (color) => ({
  type: SET_BACKGROUND,
  color
})

export const addStrut = (id, start, end, color) => ({
  type: ADD_STRUT,
  id,
  start,
  end,
  color
})

export const removeStrut = (id) => ({
  type: REMOVE_STRUT,
  id
})

export const closeView = () => ({
  type: CLOSE_VIEW
})

export const connectSocket = (url) => ({
  type: WEBSOCKET_CONNECT,
  payload: { url: SERVER_URL + encodeURIComponent( url ) }
})

export const sendMessage = (msg) => ({
  type: WEBSOCKET_SEND,
  payload: msg
})

export const disconnectSocket = () => ({
  type: WEBSOCKET_DISCONNECT
})

