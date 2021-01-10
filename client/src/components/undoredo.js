
import React from 'react'
import { connect } from 'react-redux'
import { ActionCreators as UndoActionCreators } from 'redux-undo'

import IconButton from '@material-ui/core/IconButton'
import Tooltip from '@material-ui/core/Tooltip'
import FastForwardRoundedIcon from '@material-ui/icons/FastForwardRounded'
import FastRewindRoundedIcon from '@material-ui/icons/FastRewindRounded'
import UndoRoundedIcon from '@material-ui/icons/UndoRounded'
import RedoRoundedIcon from '@material-ui/icons/RedoRounded'

import * as designFns from '../bundles/designs'

const UndoRedoButtons = ({ doUndoAll, doUndo, doRedo, doRedoAll, canUndo, canRedo, redoAllIndex }) =>
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
    <>
      <Tooltip title="Undo all" aria-label="undo-all">
        <IconButton color="inherit" aria-label="undo-all" onClick={handleUndoAll} disabled={!canUndo}>
          <FastRewindRoundedIcon fontSize="large"/>
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
          <FastForwardRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
    </>
  )
}

const select = ( state ) =>
{
  const { designs } = state
  const history = designs && designFns.selectDesign( state ).mesh
  return {
    canUndo: history && history.past.length > 0,
    canRedo: history && history.future.length > 0,
    redoAllIndex: history && history.future.length - 1
  }
}

const boundEventActions = {
  doUndoAll : UndoActionCreators.jumpToPast,
  doUndo : UndoActionCreators.undo,
  doRedo : UndoActionCreators.redo,
  doRedoAll : UndoActionCreators.jumpToFuture,
}

export default connect( select, boundEventActions )( UndoRedoButtons )
