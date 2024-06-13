
import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"

import { controllerProperty, subController } from '../../framework/context/editor.jsx';
import { OrbitPanel } from "../components/orbitpanel.jsx";

const OrbitsDialog = props =>
{
  const availableOrbits = () => subController( props.controller, 'availableOrbits' );
  const snapOrbits      = () => subController( props.controller, 'snapOrbits' );

  const allOrbits    = () => controllerProperty( props.controller, 'orbits', 'orbits', true );
  const orbits       = () => controllerProperty( availableOrbits(), 'orbits', 'orbits', true );
  const lastAvailable = () => controllerProperty( availableOrbits(), 'selectedOrbit', 'orbits', false );
  const lastSnap = () => controllerProperty( snapOrbits(), 'selectedOrbit', 'orbits', false );

  return (
    <Dialog onClose={ () => props.close() } open={props.open} maxWidth='md' fullWidth='true'>
      <DialogTitle id="orbits-dialog">Direction Configuration</DialogTitle>
      <DialogContent>
        <div style={{ display: 'grid', 'grid-template-columns': '1fr 1fr', 'min-width': '550px' }}>
          <OrbitPanel orbits={allOrbits()} controller={availableOrbits()} lastSelected={lastAvailable()}
            label="available directions" style={{ height: '100%' }} />
          <OrbitPanel orbits={orbits()} controller={snapOrbits()} lastSelected={lastSnap()}
            label="snap directions" style={{ height: '100%' }} />
        </div>
      </DialogContent>
      <DialogActions>
        <Button size="small" onClick={ ()=>props.close() } color="primary">
          Close
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export { OrbitsDialog };
