
const PROGRESS_STARTED = 'PROGRESS_STARTED'
const PROGRESS_STOPPED = 'PROGRESS_STOPPED'

export const startProgress = ( message ) =>
{
  return { type: PROGRESS_STARTED, payload: message }
}

export const stopProgress = () =>
{
  return { type: PROGRESS_STOPPED }
}

// With the initial state as true, we get a grey flash on page load,
//  because the canvas component is not initialized yet.  TODO
const initialState = {
  message: "",
  showing: true
}

export const reducer = ( state = initialState, action ) => {
  switch (action.type) {

    case PROGRESS_STARTED:
      return {
        showing: true,
        message: action.payload
      }
  
    case PROGRESS_STOPPED:
      return {
        showing: false,
        message: ""
      }
  
    default:
      return state
  }
}
