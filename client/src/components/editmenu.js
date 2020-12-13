
import React from 'react'
import { connect } from 'react-redux'
import { commandTriggered } from '../bundles/mesh'
import IconButton from '@material-ui/core/IconButton';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import MoreVertIcon from '@material-ui/icons/MoreVert';
import Divider from '@material-ui/core/Divider';

const ITEM_HEIGHT = 48;

const EditMenu = ({ visible, edits, doEdit }) =>
{
  const [anchorEl, setAnchorEl] = React.useState(null);
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };
  const h4_8 = { groupName: "H4", renderGroupName: "H4", index: 8, edgesToRender: 15 }
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
          <MenuItem onClick={ e => doEdit( 'random' ) }>New Random</MenuItem>
          <MenuItem onClick={ e => doEdit( 'allSelected' ) }>Select All</MenuItem>
          <MenuItem onClick={ e => doEdit( 'allDeselected' ) }>Deselect All</MenuItem>
          <MenuItem onClick={ e => doEdit( 'centroid' ) }>Centroid 1</MenuItem>
          <MenuItem onClick={ e => doEdit( 'shortred' ) }>Short Red 0</MenuItem>
          <MenuItem onClick={ e => doEdit( 'ShowPoint', { mode: 'origin' } ) }>Show Origin</MenuItem>
          <MenuItem onClick={ e => doEdit( 'Delete' ) }>Delete</MenuItem>
          <Divider />
          <MenuItem onClick={ e => doEdit( 'Polytope4d', h4_8 ) }>120-Cell</MenuItem>
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

const select = ( { java, mesh, workingPlane } ) => ({
  visible: !java.readOnly && ! workingPlane,
  edits: mesh && Object.getOwnPropertyNames( mesh.commands )
})

const boundEventActions = {
  doEdit : commandTriggered,
}

export default connect( select, boundEventActions )( EditMenu )
