
import React from 'react'
import { connect } from 'react-redux'
import { selectionToggled } from '../bundles/mesh'
import * as planes from '../bundles/planes'
import { createInstance } from '../bundles/mesh'
import * as designs from '../bundles/designs'
import { DesignCanvas } from 'vzome'

const select = ( state, ownProps ) =>
{
  const { design } = ownProps
  const { lighting, workingPlane, java } = state
  const mesh = state.designs && designs.selectMesh( state, design )
  const field = state.designs && designs.selectField( state, design )
  const camera = state.camera || designs.selectCamera( state, design )
  const resolver = state.designs && designs.selectShaper( state, design )
  const preResolved = java.shapes && { shapes: java.shapes, instances: java.renderingOn? java.instances : java.previous }
  const shown = mesh && new Map( mesh.shown )
  if ( workingPlane && workingPlane.enabled && workingPlane.endPt ) {
    const { position, endPt, buildingStruts } = workingPlane
    let previewBall = createInstance( [ endPt ] )
    if ( ! shown.has( previewBall.id ) )
      shown.set( previewBall.id, previewBall )
    if ( buildingStruts ) {
      let previewStrut = createInstance( [ position, endPt ] )
      if ( ! shown.has( previewStrut.id ) )
        shown.set( previewStrut.id, previewStrut )
    }
  }
  return {
    workingPlane,
    camera,
    lighting,
    resolver,
    preResolved,
    mesh: mesh && { ...mesh, shown },
    clickable: !!state.designs,
  }
}

const boundEventActions = {
  selectionToggler : selectionToggled,
  startGridHover: planes.doStartGridHover,
  stopGridHover: planes.doStopGridHover,
  startBallHover: planes.doStartBallHover,
  stopBallHover: planes.doStopBallHover,
  shapeClick: planes.doBallClick,
  bkgdClick: planes.doBackgroundClick,
}

export default connect( select, boundEventActions )( DesignCanvas )
