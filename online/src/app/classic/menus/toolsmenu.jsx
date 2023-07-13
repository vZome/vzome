
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import { createSignal } from "solid-js";

import { MenuAction, createMenuAction } from "../components/menuaction.jsx";
import { useWorkerClient } from "../../../workerClient/index.js";
import { createSymmetryAction, useSymmetry } from "../classic.jsx";

export const ToolsMenu = () =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  const { rootController } = useWorkerClient();
  const EditAction = createMenuAction( rootController(), doClose );
  const SymmetryAction = createSymmetryAction( doClose );
  const { showPolytopesDialog } = useSymmetry();

  const openPolytopes = () =>
  {
    doClose();
    showPolytopesDialog();
  }

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
        
        <MenuAction label="Generate Polytope..." onClick={ openPolytopes } mods="⌥⌘" key="P" />

        <Divider />
        
        <EditAction label="Validate Paneled Surface" action="Validate2Manifold" />

      </Menu>
    </div>
  );
}
