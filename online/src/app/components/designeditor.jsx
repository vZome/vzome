
import React, { useState, useEffect } from 'react'
import Grid from '@material-ui/core/Grid'

import { Debugger } from './debugger.jsx'
import { DesignViewer } from '../../ui/viewer/index.jsx'
// import BuildPlane from './buildplane.jsx'
import { UndoRedoButtons } from './undoredo.jsx'
// import { selectionToggled } from '../bundles/mesh.js'
// import * as planes from '../bundles/planes.js'

// const select = ( state ) =>
// {
//   const scene = ( state.designs && designs.selectScene( state ) ) || state.scene
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
  //   return {
  //     scene,
  //     clickable: !!state.designs,
  //   }
  // }

// const isLeftMouseButton = e =>
// {
//   e = e || window.event;
//   if ( "which" in e )  // Gecko (Firefox), WebKit (Safari/Chrome) & Opera
//     return e.which === 1
//   else if ( "button" in e )  // IE, Opera 
//     return e.button === 0
//   return false
// }

export const DesignEditor = ( props ) =>
{
  // const { startGridHover, stopGridHover, workingPlane } = props;
  const { controller, debug } = props;
  const [ canUndo, setCanUndo ] = useState( true );
  const [ canRedo, setCanRedo ] = useState( false );
  useEffect( () => {
    controller && controller .connectHistory( setCanUndo, setCanRedo );
  }, [controller] )

  const doAction = action =>
  {
    if ( controller ) {
      controller .doAction( action );
    }
  }

  // const { selectionToggler, shapeClick, bkgdClick, startBallHover, stopBallHover, clickable } = props
  // const focus = workingPlane && workingPlane.enabled && workingPlane.buildingStruts && workingPlane.position
  // const atFocus = id => focus && ( id === JSON.stringify(focus) )
  // const handleClick = clickable && (( id, vectors, selected ) =>
  // {
  //   if ( workingPlane ) {
  //     if ( vectors.length === 1 )
  //       shapeClick( focus, vectors[ 0 ] )
  //   }
  //   else {
  //     selectionToggler( id, selected )
  //   }
  // })
  // const handleBackgroundClick = ( e ) =>
  // {
  //   workingPlane && isLeftMouseButton( e ) && bkgdClick()
  // }
  // const onHover = ( vectors, inbound ) =>
  // {
  //   if ( workingPlane && vectors.length === 1 ) {
  //     const position = vectors[ 0 ]
  //     inbound? startBallHover( position ) : stopBallHover( position )
  //   }
  // }

  const drawerColumns = debug? 4 : 0;
  const canvasColumns = 12 - drawerColumns;

  return (
    <div style={{ flex: '1', height: '100%' }}>
      <Grid id='editor-main' container spacing={0} style={{ height: '100%' }}>        
        <Grid id='editor-drawer' item xs={drawerColumns}>
          { debug && <Debugger controller={controller}/> }
        </Grid>
        <Grid id='editor-canvas' item xs={canvasColumns}>
          <DesignViewer controller={controller} >
            {/* { workingPlane && workingPlane.enabled &&
                <BuildPlane config={workingPlane} {...{ startGridHover, stopGridHover }} /> } */}
            <UndoRedoButtons {...{ canRedo, canUndo,
              doRedo: () => doAction( "redo" ), 
              doUndo: () => doAction( "undo" ), 
              doRedoAll: () => doAction( "redoAll" ), 
              doUndoAll: () => doAction( "undoAll" ) }} />
          </DesignViewer>
        </Grid>
      </Grid>
    </div>
  )
}
