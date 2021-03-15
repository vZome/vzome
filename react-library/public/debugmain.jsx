import React from 'react'
import { render } from 'react-dom'

import { UrlViewer } from '../src/index.js'

// const convertLegacyFormat = rawDesign => ({
//   ...rawDesign,
//   instances: rawDesign.instances.map( instance => ({
//     ...instance,
//     shapeId: instance.shape,
//     position: Object.values( instance.position ),
//     rotation: instance.rotation && Object.values( instance.rotation ),
//   }))
// })

const viewerStyle = {
  height: "700px",
  minHeight: "400px",
  maxHeight: "80vh",
  marginLeft: "15%",
  marginRight: "15%",
  marginTop: "15px",
  marginBottom: "15px",
  borderWidth: "medium",
  borderRadius: "10px",
  border: "solid",
}

export const Demo = () =>
{
  return (
    <div>
      <div style={viewerStyle}>
        <UrlViewer url={"/models/affineDodec.vZome"} />
      </div>
    </div>
  )
}

render(<Demo/>, document.querySelector('#root'))
