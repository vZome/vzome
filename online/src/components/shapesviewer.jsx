
import React from 'react'
import { connect } from 'react-redux'

import * as designs from '../bundles/designs.js'
import { DesignCanvas, ShapedGeometry } from '@vzome/react-vzome'

const select = ( state ) =>
{
  const lighting = ( state.designs && designs.selectLighting( state ) ) || state.lighting
  const camera = ( state.designs && designs.selectCamera( state ) ) || state.camera
  const preview = ( state.designs && designs.selectPreview( state ) ) || {}

  return {
    camera,
    lighting,
    preview,
  }
}

const ShapesViewer = ( props ) =>
{
  let { preview={} } = props
  const { shapes={}, instances=[] } = preview

  return (
    <DesignCanvas {...props} >
      { preview && <ShapedGeometry {...{ shapes, instances }} /> }
    </DesignCanvas>
  )
}

export default connect( select )( ShapesViewer )
