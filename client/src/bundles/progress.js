
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

export const reducer = ( state = false, action ) => {
  switch (action.type) {

    case PROGRESS_STARTED:
      return true
  
    case PROGRESS_STOPPED:
      return false
  
    default:
      return state
  }
}
