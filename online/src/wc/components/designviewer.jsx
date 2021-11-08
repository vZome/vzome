
import React from 'react'
import { ShapedGeometry } from './geometry.jsx'
import { DesignCanvas } from './designcanvas.jsx'
import { useInstanceShaper } from './legacyhooks.js'

export const MeshGeometry = ({ shown, selected, renderer, highlightBall, handleClick, onHover }) =>
{
  const { shaper, embedding } = renderer || {}
  const { shapes, instances } = useInstanceShaper( shown, selected, shaper )
  return ( instances &&
    <ShapedGeometry {...{ shapes, instances, embedding, highlightBall, handleClick, onHover }} />
  ) || null
}

export const DesignViewer = props =>
{
  const { scene, mesh, renderer, children } = props
  return (
    <DesignCanvas {...scene} >
      { (scene && scene.shapes)? <ShapedGeometry {...scene} />
        : ( mesh && <MeshGeometry {...{ shown: mesh.shown, selected: mesh.selected, renderer }} /> ) }
      {children}
    </DesignCanvas>
  )
}
