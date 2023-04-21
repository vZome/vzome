
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import { createSignal } from 'solid-js';

import { createMenuAction, MenuAction } from "../components/menuaction.jsx";
import { controllerProperty, subController } from "../controllers-solid.js";
import { ShapesDialog } from "../components/shapes.jsx";
import { OrbitsDialog } from "../components/orbits.jsx";

export const SystemMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  const [ showShapesDialog, setShowShapesDialog ] = createSignal( false );
  const openShapesDialog = () =>
  {
    setShowShapesDialog( true );
    doClose();
  }
  const [ showOrbitsDialog, setShowOrbitsDialog ] = createSignal( false );
  const openOrbitsDialog = () =>
  {
    setShowOrbitsDialog( true );
    doClose();
  }

  const symmetries = () => controllerProperty( props.controller, 'symmetryPerspectives', 'symmetryPerspectives', true );
  const hasIcosa = () => symmetries() .includes( 'icosahedral' );
  const currentSymm = () => controllerProperty( props.controller, 'symmetry' );
  const strutBuilder = () => subController( props.controller, 'strutBuilder' );
  const symmController = () => subController( strutBuilder(), `symmetry.${currentSymm()}` );

  const EditAction = createMenuAction( props.controller, doClose );

  return (
    <div>
      <Button id="system-menu-button" sx={{ color: 'white', minWidth: 'auto' }}
        aria-controls={open() ? "system-menu-menu" : undefined} aria-haspopup="true" aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        System
      </Button>
      <Menu id="system-menu-menu" MenuListProps={{ "aria-labelledby": "system-menu-button" }}
        anchorEl={anchorEl()} open={open()} onClose={doClose}
      >
        <Show when={ hasIcosa() }>
          <EditAction label="Icosahedral System" action="setSymmetry.icosahedral" checked={ currentSymm() === 'icosahedral' } />
        </Show>
        <EditAction label="Octahedral System" action="setSymmetry.octahedral" checked={ currentSymm() === 'octahedral' } />

        <Divider />
        
        <MenuAction label="Shapes..." onClick={ () => openShapesDialog(true) } />

        <MenuAction label="Directions..." onClick={ () => openOrbitsDialog(true) } />

        <EditAction label="Show Directions Graphically" action="toggleOrbitViews" disabled="true" />
        <EditAction label="Show Strut Scales" action="toggleStrutScales" disabled="true" />
        <EditAction label="Show Frame Labels" action="toggleFrameLabels" disabled="true" />
        <EditAction label="Show Panel Normals" action="toggleNormals" disabled="true" />

      </Menu>

      <ShapesDialog controller={symmController()} open={showShapesDialog()} close={ ()=>setShowShapesDialog(false) } />
      <OrbitsDialog controller={symmController()} open={showOrbitsDialog()} close={ ()=>setShowOrbitsDialog(false) } />
    </div>
  );
}
