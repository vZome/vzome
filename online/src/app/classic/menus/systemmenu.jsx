
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import { createSignal } from "solid-js";

import { createMenuAction } from "../components/menuaction.jsx";
import { controllerProperty, subController } from "../controllers-solid.js";

export const SystemMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  const symmetries = () => controllerProperty( props.controller, 'symmetryPerspectives', 'symmetryPerspectives', true );
  const hasIcosa = () => symmetries() .includes( 'icosahedral' );
  const initSystem = () => controllerProperty( props.controller, 'symmetry', 'symmetry', false );

  const EditAction = createMenuAction( props.controller, doClose );

  return (
    <div>
      <Button id="system-menu-button"
        aria-controls={open() ? "system-menu-menu" : undefined} aria-haspopup="true" aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        System
      </Button>
      <Menu id="system-menu-menu" MenuListProps={{ "aria-labelledby": "system-menu-button" }}
        anchorEl={anchorEl()} open={open()} onClose={doClose}
      >
        <Show when={ hasIcosa() }>
          <EditAction label="Icosahedral System" action="setSymmetry.icosahedral" checked={ initSystem() === 'icosahedral' } disabled="true" />
        </Show>
        <EditAction label="Octahedral System" action="setSymmetry.octahedral" checked={ initSystem() === 'octahedral' } disabled="true" />

        <Divider />
        
        <EditAction label="Shapes..." action="configureShapes" disabled="true" />
        <EditAction label="Directions..." action="configureDirections" disabled="true" />
        <EditAction label="Show Directions Graphically" action="toggleOrbitViews" disabled="true" />
        <EditAction label="Show Strut Scales" action="toggleStrutScales" disabled="true" />
        <EditAction label="Show Frame Labels" action="toggleFrameLabels" disabled="true" />
        <EditAction label="Show Panel Normals" action="toggleNormals" disabled="true" />

      </Menu>
    </div>
  );
}
