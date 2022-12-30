
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import MenuItem from "@suid/material/MenuItem"
import Divider from "@suid/material/Divider";
import Typography from "@suid/material/Typography";
import ListItemText from "@suid/material/ListItemText";
import { createSignal } from "solid-js";
import { controllerAction, subController } from "../controllers-solid";

export const EditMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  const ActionItem = ( innerProps ) =>
  {
    const doAction = () => 
    {
      doClose();
      controllerAction( subController( props.controller, innerProps.controller || 'editor' ), innerProps.action );
    }
    if ( innerProps.key && !innerProps.disabled ) {
      document .addEventListener( "keydown", evt => {
        if ( evt .metaKey && evt.code===("Key"+innerProps.key) ) {
          doAction();
          evt .preventDefault();
        }
      } );
    }
    return (
      <MenuItem disabled={innerProps.disabled} onClick={doAction}>
        <ListItemText>{innerProps.label}</ListItemText>
        <Show when={innerProps.key} >
          <Typography variant="body2" color="text.secondary">
            ⌘{innerProps.key}
          </Typography>
        </Show>
      </MenuItem>
    );
  }

  return (
    <div>
      <Button id="edit-menu-button"
        aria-controls={open() ? "edit-menu-menu" : undefined} aria-haspopup="true" aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        Edit
      </Button>
      <Menu id="edit-menu-menu" MenuListProps={{ "aria-labelledby": "edit-menu-button" }}
        anchorEl={anchorEl()} open={open()} onClose={doClose}
      >
        <ActionItem label="Undo"     action="undo"    key="Z" controller="undoRedo" />
        <ActionItem label="Redo"     action="redo"    key="Y" controller="undoRedo" />
        <ActionItem label="Undo All" action="undoAll" controller="undoRedo" />
        <ActionItem label="Redo All" action="redoAll" controller="undoRedo" />

        <Divider />

        <ActionItem label="Cut"    action="cut"    key="X" disabled={true} />
        <ActionItem label="Copy"   action="copy"   key="C" disabled={true} />
        <ActionItem label="Paste"  action="paste"  key="V" disabled={true} />
        <ActionItem label="Delete" action="delete" disabled={true} />

        <Divider />

        <ActionItem label="Select All"       action="SelectAll" key="A" />
        <ActionItem label="Select Neighbors" action="SelectNeighbors" />
        <ActionItem label="Invert Selection" action="InvertSelection" />

        <Divider />

        <ActionItem label="Group"   action="GroupSelection/group" key="G" />
        <ActionItem label="Ungroup" action="GroupSelection/ungroup" />

        <Divider />

        <ActionItem label="Hide" action="hideball" />
        <ActionItem label="Show All Hidden" action="ShowHidden" />

      </Menu>
    </div>
  );
}
