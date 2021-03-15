
import React from 'react'
import { connect } from 'react-redux'

import { selectionToggled } from '../bundles/mesh.js'
import * as planes from '../bundles/planes.js'
import * as designs from '../bundles/designs.js'
import { DesignCanvas, BuildPlane, MeshGeometry } from '@vzome/react-vzome'

const select = ( state ) =>
{
  const { lighting } = state
  const mesh = state.designs && designs.selectMesh( state )
  const camera = ( state.designs && designs.selectCamera( state ) ) || state.camera
  const resolver = state.designs && designs.selectShaper( state )
  // const shown = mesh && new Map( mesh.shown )
  // if ( workingPlane && workingPlane.enabled && workingPlane.endPt ) {
  //   const { position, endPt, buildingStruts } = workingPlane
  //   let previewBall = createInstance( [ endPt ] )
  //   if ( ! shown.has( previewBall.id ) )
  //     shown.set( previewBall.id, previewBall )
  //   if ( buildingStruts ) {
  //     let previewStrut = createInstance( [ position, endPt ] )
  //     if ( ! shown.has( previewStrut.id ) )
  //       shown.set( previewStrut.id, previewStrut )
  //   }
  // }
  return {
    camera,
    lighting,
    resolver,
    mesh,
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

const isLeftMouseButton = e =>
{
  e = e || window.event;
  if ( "which" in e )  // Gecko (Firefox), WebKit (Safari/Chrome) & Opera
    return e.which === 1
  else if ( "button" in e )  // IE, Opera 
    return e.button === 0
  return false
}

const DesignEditor = ( props ) =>
{
  const { startGridHover, stopGridHover, workingPlane } = props
  const { mesh, resolver } = props
  const { shown, selected } = mesh
  const { selectionToggler, shapeClick, bkgdClick, startBallHover, stopBallHover, clickable } = props
  const focus = workingPlane && workingPlane.enabled && workingPlane.buildingStruts && workingPlane.position
  const atFocus = id => focus && ( id === JSON.stringify(focus) )
  const handleClick = clickable && (( id, vectors, selected ) =>
  {
    if ( workingPlane ) {
      if ( vectors.length === 1 )
        shapeClick( focus, vectors[ 0 ] )
    }
    else {
      selectionToggler( id, selected )
    }
  })
  const handleBackgroundClick = ( e ) =>
  {
    workingPlane && isLeftMouseButton( e ) && bkgdClick()
  }
  const onHover = ( vectors, inbound ) =>
  {
    if ( workingPlane && vectors.length === 1 ) {
      const position = vectors[ 0 ]
      inbound? startBallHover( position ) : stopBallHover( position )
    }
  }

  return (
    <DesignCanvas {...props} >
      <MeshGeometry {...{ shown, selected, resolver }} />

      {workingPlane && workingPlane.enabled &&
          <BuildPlane config={workingPlane} {...{ startGridHover, stopGridHover }} />}
    </DesignCanvas>
  )
}

export default connect( select, boundEventActions )( DesignEditor )
