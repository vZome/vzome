

export const OPEN_URL = 'OPEN_URL'
export const CLOSE_VIEW = 'CLOSE_VIEW'
export const SET_BACKGROUND = 'SET_BACKGROUND'
export const ADD_STRUT = 'ADD_STRUT'
export const REMOVE_STRUT = 'REMOVE_STRUT'

export const openUrl = (url) => ({
  type: OPEN_URL,
  url
})

export const setBackground = (color) => ({
  type: SET_BACKGROUND,
  color
})

export const closeView = () => ({
  type: CLOSE_VIEW
})

export const undo = () => ({
  type: CLOSE_VIEW
})
