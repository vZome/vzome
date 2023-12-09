
import { Show, createSignal } from "solid-js";

import IconButton from '@suid/material/IconButton'
import SettingsIcon from '@suid/icons-material/Settings'
import Menu from '@suid/material/Menu';
import MenuItem from '@suid/material/MenuItem';

import { StrutLengthPanel } from './length.jsx';
import { OrbitPanel } from './orbitpanel.jsx';
import { controllerAction, controllerProperty, subController } from '../../../viewer/util/controllers-solid.js';
import { useSymmetry } from "../classic.jsx";

export const StrutBuildPanel = () =>
{
  const { showOrbitsDialog, symmetryController, symmetryDefined } = useSymmetry();
  const availableOrbits = () => subController( symmetryController(), 'availableOrbits' );
  const buildOrbits = () => subController( symmetryController(), 'buildOrbits' );
  const orbits = () => controllerProperty( availableOrbits(), 'orbits', 'orbits', true );

  const setOrbitNames = key => controllerAction( availableOrbits(), key );

  const lastSelected = () => controllerProperty( buildOrbits(), 'selectedOrbit', 'orbits', false );

  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const revealSettings = evt => {
    setAnchorEl( evt.currentTarget );
  }
  const setAvailableOrbits = action => () => {
    setAnchorEl( null );
    setOrbitNames( action );
  }
  const showConfiguration = evt => {
    setAnchorEl( null );
    showOrbitsDialog();
  }

  return(
    <div id="build" style={{ display: 'grid', 'grid-template-rows': '1fr min-content', height: '100%' }}>
      <Show when={symmetryDefined()}>
      <OrbitPanel orbits={orbits()} controller={buildOrbits()} lastSelected={lastSelected()}
          label="build directions" style={{ height: '100%' }} >
        <IconButton color="inherit" aria-label="settings"
            style={ { position: 'absolute', top: '8px', right: '8px' } }
            onClick={revealSettings} >
          <SettingsIcon fontSize='medium'/>
        </IconButton>
        <Menu
          id="orbit-settings-menu"
          anchorEl={anchorEl()}
          open={Boolean(anchorEl())}
          onClose={ () => setAnchorEl( null ) }
        >
          <MenuItem onClick={ setAvailableOrbits( 'rZomeOrbits' ) }>real Zome</MenuItem>
          <MenuItem onClick={ setAvailableOrbits( 'predefinedOrbits' ) }>predefined</MenuItem>
          <MenuItem onClick={ setAvailableOrbits( 'setAllDirections' ) }>all</MenuItem>
          <MenuItem onClick={ showConfiguration }>configure...</MenuItem>
        </Menu>
      </OrbitPanel>
      <StrutLengthPanel controller={buildOrbits()} />
      </Show>
    </div>
  );
}