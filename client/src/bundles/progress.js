
const PROGRESS_STARTED = 'PROGRESS_STARTED'
const PROGRESS_STOPPED = 'PROGRESS_STOPPED'

export const startProgress = () =>
{
  return { type: PROGRESS_STARTED }
}

export const stopProgress = () =>
{
  return { type: PROGRESS_STOPPED }
}

// With the initial state as true, we get a grey flash on page load,
//  because the canvas component is not initialized yet.  TODO

export const reducer = ( state = true, action ) => {
  switch (action.type) {

    case PROGRESS_STARTED:
      return true
  
    case PROGRESS_STOPPED:
      return false
  
    default:
      return state
  }
}
