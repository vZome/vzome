
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import { createSignal } from "solid-js";

import { createActionItem } from "../components/actionitem.jsx";

export const ToolsMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  const ActionItem = createActionItem( props.controller, 'editor', doClose );

  return (
    <div>
      <Button id="tools-menu-button"
        aria-controls={open() ? "tools-menu-menu" : undefined} aria-haspopup="true" aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        Tools
      </Button>
      <Menu id="tools-menu-menu" MenuListProps={{ "aria-labelledby": "tools-menu-button" }}
        anchorEl={anchorEl()} open={open()} onClose={doClose}
      >
        <ActionItem label="Icosahedral Symmetry"         action="icosasymm" controller="symmetry" mods="⌘" key="I" disabled={true} />
        <ActionItem label="Cubic / Octahedral Symmetry"  action="octasymm"  controller="symmetry" mods="⌥⌘" key="C" disabled={true} />
        <ActionItem label="Tetrahedral Symmetry"         action="tetrasymm" controller="symmetry" mods="⌥⌘" key="T" disabled={true} />
        <ActionItem label="Point Reflection"             action="pointsymm" />

        <Divider />

        <ActionItem label="Replace With Panels" action="ReplaceWithShape" disabled={true} />

        <Divider />
        
        <ActionItem label="Generate Polytope..." action="showPolytopesDialog" mods="⌥⌘" key="P" disabled={true} />

        <Divider />
        
        <ActionItem label="Validate Paneled Surface" action="Validate2Manifold" disabled={true} />

      </Menu>
    </div>
  );
}
