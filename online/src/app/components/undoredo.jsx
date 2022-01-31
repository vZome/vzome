
import React from 'react'
// import { ActionCreators as UndoActionCreators } from 'redux-undo'

import IconButton from '@material-ui/core/IconButton'
import Tooltip from '@material-ui/core/Tooltip'
import SkipNextRoundedIcon from '@material-ui/icons/SkipNextRounded'
import SkipPreviousRoundedIcon from '@material-ui/icons/SkipPreviousRounded'
import UndoRoundedIcon from '@material-ui/icons/UndoRounded'
import RedoRoundedIcon from '@material-ui/icons/RedoRounded'

export const UndoRedoButtons = ({ doUndoAll, doUndo, doRedo, doRedoAll, canUndo, canRedo, redoAllIndex }) =>
{
  const handleUndoAll = () =>
  {
    doUndoAll( 0 )
  }

  const handleRedoAll = () =>
  {
    doRedoAll( redoAllIndex )
  }

  return (
    <div color="inherit" style={ { position: 'absolute', top: '5px', left: '5px' } }>
      <Tooltip title="Undo all" aria-label="undo-all">
        <IconButton color="inherit" aria-label="undo-all" onClick={handleUndoAll} disabled={!canUndo}>
          <SkipPreviousRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Tooltip title="Undo" aria-label="undo">
        <IconButton color="inherit" aria-label="undo" onClick={doUndo} disabled={!canUndo}>
          <UndoRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Tooltip title="Redo" aria-label="redo">
        <IconButton color="inherit" aria-label="redo" onClick={doRedo} disabled={!canRedo}>
          <RedoRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Tooltip title="Redo all" aria-label="redo-all">
        <IconButton color="inherit" aria-label="redo-all" onClick={handleRedoAll} disabled={!canRedo}>
          <SkipNextRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
    </div>
  )
}

// const select = ( state ) =>
// {
//   const { designs } = state
//   const history = designs && designFns.selectDesign( state ).mesh
//   return {
//     canUndo: history && history.past.length > 0,
//     canRedo: history && history.future.length > 0,
//     redoAllIndex: history && history.future.length - 1
//   }
// }

// const boundEventActions = {
//   doUndoAll : UndoActionCreators.jumpToPast,
//   doUndo : UndoActionCreators.undo,
//   doRedo : UndoActionCreators.redo,
//   doRedoAll : UndoActionCreators.jumpToFuture,
// }

