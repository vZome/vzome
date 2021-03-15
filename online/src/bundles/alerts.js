
const ERROR_HAPPENED = 'ERROR_HAPPENED'
const ERROR_CLEARED = 'ERROR_CLEARED'

export const showAlert = ( message ) =>
{
  return { type: ERROR_HAPPENED, payload: message }
}

export const clearAlert = () =>
{
  return { type: ERROR_CLEARED }
}

export const reducer = ( state = '', action ) => {
  switch (action.type) {

    case ERROR_HAPPENED:
      return action.payload

    case ERROR_CLEARED:
      return ''

    default:
      return state
  }
}
