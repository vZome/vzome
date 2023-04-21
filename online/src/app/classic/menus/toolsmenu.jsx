
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import { createSignal } from "solid-js";

import { createMenuAction } from "../components/menuaction.jsx";
import { subController } from "../controllers-solid.js";

export const ToolsMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  const EditAction = createMenuAction( props.controller, doClose );
  const buildController = () => subController( props.controller, 'strutBuilder' );
  const symmetryController = () => subController( buildController(), 'symmetry' );
  const SymmetryAction = createMenuAction( symmetryController(), doClose );

  return (
    <div>
      <Button id="tools-menu-button" sx={{ color: 'white', minWidth: 'auto' }}
        aria-controls={open() ? "tools-menu-menu" : undefined} aria-haspopup="true" aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        Tools
      </Button>
      <Menu id="tools-menu-menu" MenuListProps={{ "aria-labelledby": "tools-menu-button" }}
        anchorEl={anchorEl()} open={open()} onClose={doClose}
      >
        <SymmetryAction label="Icosahedral Symmetry"         action="icosasymm" mods="⌘" key="I" />
        <SymmetryAction label="Cubic / Octahedral Symmetry"  action="octasymm"  mods="⌥⌘" key="C" />
        <SymmetryAction label="Tetrahedral Symmetry"         action="tetrasymm" mods="⌥⌘" key="T" />

        <EditAction label="Point Reflection" action="pointsymm" />

        <Divider />

        <SymmetryAction label="Replace With Panels" action="ReplaceWithShape" disabled={true} />

        <Divider />
        
        <SymmetryAction label="Generate Polytope..." action="showPolytopesDialog" mods="⌥⌘" key="P" disabled={true} />

        <Divider />
        
        <EditAction label="Validate Paneled Surface" action="Validate2Manifold" />

      </Menu>
    </div>
  );
}
