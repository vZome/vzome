
import * as vzomejava from './vzomejava'
import * as mesh from './mesh'

const implementations = { mesh, vzomejava }

const currentImplementation = vzomejava

const IMPLEMENTATION_CHOSEN = 'IMPLEMENTATION_CHOSEN'

const initialState = {
  instanceSelector: currentImplementation.instanceSelector,
  supportsEdits: currentImplementation.supportsEdits
}

export const reducer = ( state = initialState, action ) =>
{
  switch (action.type) {

    case IMPLEMENTATION_CHOSEN: {
      return { instanceSelector: implementations[ action.payload ].instanceSelector }
    }

    default:
      return state
  }
}
