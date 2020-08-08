
export const PROGRESS_STARTED = 'PROGRESS_STARTED'
export const PROGRESS_STOPPED = 'PROGRESS_STOPPED'

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
