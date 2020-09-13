
import * as vzomejava from './vzomejava'
import * as jsweet from './jsweet'

const implementations = { jsweet, vzomejava }

const currentImplementation = jsweet

const IMPLEMENTATION_CHOSEN = 'IMPLEMENTATION_CHOSEN'

const initialState = {
  instanceSelector: currentImplementation.instanceSelector,
  supportsEdits: currentImplementation.supportsEdits
}

export const reducer = ( state = initialState, action ) =>
{
  switch (action.type) {

    case IMPLEMENTATION_CHOSEN:
    {
      const impl = implementations[ action.payload ]
      return {
        instanceSelector: impl.instanceSelector,
        supportsEdits: impl.supportsEdits,
      }
    }

    default:
      return state
  }
}
