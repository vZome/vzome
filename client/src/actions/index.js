
export const openUrl = (url) => ({
  type: 'OPEN_URL',
  url
})

export const setBackground = (color) => ({
  type: 'SET_BACKGROUND',
  color
})

export const addStrut = (id, start, end, color) => ({
  type: 'ADD_STRUT',
  id,
  start,
  end,
  color
})

export const removeStrut = (id) => ({
  type: 'REMOVE_STRUT',
  id
})

export const closeView = () => ({
  type: 'CLOSE_VIEW'
})

