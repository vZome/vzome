
export const URL_PROVIDED = 'URL_PROVIDED'
export const VIEW_CLOSED = 'VIEW_CLOSED'
export const BACKGROUND_SET = 'BACKGROUND_SET'
export const JAVA_MESSAGE_RECEIVED = 'JAVA_MESSAGE'

export const urlProvided = (url) => ({
  type: URL_PROVIDED,
  payload: url
})

export const backgroundSet = (color) => ({
  type: BACKGROUND_SET,
  payload: color
})

export const viewClosed = () => ({
  type: VIEW_CLOSED
})

export const javaMessageReceived = (message) => ({
  type: JAVA_MESSAGE_RECEIVED,
  payload: message
})