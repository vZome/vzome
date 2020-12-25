
import React from 'react'
import { connect } from 'react-redux'
import { commandTriggered } from '../commands'
import * as designs from '../bundles/models'
import { ActionCreators as UndoActionCreators } from 'redux-undo'
import IconButton from '@material-ui/core/IconButton';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import MoreVertIcon from '@material-ui/icons/MoreVert';
import Divider from '@material-ui/core/Divider';

const ITEM_HEIGHT = 48;

const EditMenu = ({ visible, edits, modelNames, doEdit, canUndo, canRedo, doUndo, doRedo, doSelectModel }) =>
{
  const [anchorEl, setAnchorEl] = React.useState(null);
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };
  const h4_c = { groupName: "H4", renderGroupName: "H4", index: 12, edgesToRender: 15 }
  if ( visible )
    return (
      <div>
        <IconButton id="editmenu"
          aria-label="more"
          aria-controls="long-menu"
          aria-haspopup="true"
          onClick={handleClick}
        >
          <MoreVertIcon />
        </IconButton>
        <Menu id="long-menu"
          anchorEl={anchorEl} keepMounted
          open={open} onClose={handleClose}
          PaperProps={{
            style: {
              maxHeight: ITEM_HEIGHT * 20,
              width: '30ch',
            },
          }}
        >
          { modelNames.map( modelName =>
            <MenuItem key={modelName} onClick={ () => doSelectModel( modelName ) } >
              {modelName}
            </MenuItem>
          ) }
          <Divider />
          <MenuItem disabled={!canUndo} onClick={doUndo}>Undo</MenuItem>
          <MenuItem disabled={!canRedo} onClick={doRedo}>Redo</MenuItem>
          <Divider />
          <MenuItem onClick={ e => doEdit( 'allSelected' ) }>Select All</MenuItem>
          <MenuItem onClick={ e => doEdit( 'allDeselected' ) }>Deselect All</MenuItem>
          <MenuItem onClick={ e => doEdit( 'centroid' ) }>Centroid 1</MenuItem>
          <MenuItem onClick={ e => doEdit( 'shortred' ) }>Short Red 0</MenuItem>
          <MenuItem onClick={ e => doEdit( 'ShowPoint', { mode: 'origin' } ) }>Show Origin</MenuItem>
          <MenuItem onClick={ e => doEdit( 'Delete' ) }>Delete</MenuItem>
          <Divider />
          <MenuItem onClick={ e => doEdit( 'Polytope4d', h4_c ) }>H4 Polytope</MenuItem>
          <Divider />
          { edits.map( edit =>
            <MenuItem key={edit} onClick={ e => doEdit( edit ) } >
              {edit}
            </MenuItem>
          ) }
        </Menu>
      </div>
    )
  else
    return null
} 

const select = ( { models, commands } ) => {
  const undoable = models && models.models[ models.current ]
  return {
    modelNames: models && Object.keys( models.models ),
    canUndo: undoable && undoable.past.length > 0,
    canRedo: undoable && undoable.future.length > 0,
    visible: !!commands,
    edits: commands && Object.getOwnPropertyNames( commands )
  }
}

const boundEventActions = {
  doEdit : commandTriggered,
  doUndo : UndoActionCreators.undo,
  doRedo : UndoActionCreators.redo,
  doSelectModel: designs.switchModel,
}

export default connect( select, boundEventActions )( EditMenu )
