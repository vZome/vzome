
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import { createSignal } from "solid-js";

import { createActionItem } from "../components/actionitem.jsx";

export const EditMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  const ActionItem = createActionItem( props.controller, 'editor', doClose );

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
        <ActionItem label="Undo"     action="undo"    mods="⌘" key="Z" controller="undoRedo" />
        <ActionItem label="Redo"     action="redo"    mods="⌘" key="Y" controller="undoRedo" />
        <ActionItem label="Undo All" action="undoAll" mods="⌥⌘" key="Z" controller="undoRedo" />
        <ActionItem label="Redo All" action="redoAll" mods="⌥⌘" key="Y" controller="undoRedo" />

        <Divider />

        <ActionItem label="Cut"    action="cut"    mods="⌘" key="X" disabled={true} />
        <ActionItem label="Copy"   action="copy"   mods="⌘" key="C" disabled={true} />
        <ActionItem label="Paste"  action="paste"  mods="⌘" key="V" disabled={true} />
        <ActionItem label="Delete" action="Delete" code="Delete|Backspace" />

        <Divider />

        <ActionItem label="Select All"       action="SelectAll"       mods="⌘" key="A" />
        <ActionItem label="Select Neighbors" action="SelectNeighbors" mods="⌥⌘" key="A" />
        <ActionItem label="Invert Selection" action="InvertSelection" />

        <Divider />

        <ActionItem label="Group"   action="GroupSelection/group" mods="⌘" key="G" />
        <ActionItem label="Ungroup" action="GroupSelection/ungroup" mods="⌥⌘" key="G" />

        <Divider />

        <ActionItem label="Hide"            action="hideball"   mods="⌃" key="H" />
        <ActionItem label="Show All Hidden" action="ShowHidden" mods="⌥⌃" key="H" />

      </Menu>
    </div>
  );
}
