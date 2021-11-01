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
  minHeight: "200px",
  maxHeight: "80vh",
  marginLeft: "15%",
  marginRight: "15%",
  marginTop: "15px",
  marginBottom: "15px",
  borderWidth: "medium",
  borderRadius: "10px",
  border: "solid",
}

export const Demo = ( {url, height} ) =>
{
  const style = { ...viewerStyle, height }
  return (
    <div style={style}>
      <UrlViewer url={url} />
    </div>
  )
}

const roots = document.getElementsByClassName( 'vzome' )
for (let i = 0; i < roots.length; i++ ) {
  const el = roots[i]
  render( React.createElement( Demo, { url: el.getAttribute( 'data-url' ), height: el.getAttribute( 'data-height' ) } ), el )
}
