
const reducer = (state = {
  modelUrl: "",
  connectionLive: false,
  segments: []
}, action) => {
  switch (action.type) {

    case 'OPEN_URL':
      return {
        modelUrl: action.url,
        connectionLive: true,
        segments: [
          { id: 1,
            start: { x:25, y:25, z:25 },
            end: { x:-25, y:-25, z:-25 },
            color: '#DD0000'
          },
          { id: 2,
            start: { x:-25, y:25, z:25 },
            end: { x:25, y:-25, z:-25 },
            color: '#0077CC'
          }
        ]
      }

    case 'CLOSE_VIEW':
      return {
        ...state,
        connectionLive: false,
        segments: []
      }

    default:
      return state
  }
}

export default reducer

