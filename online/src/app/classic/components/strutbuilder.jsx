
import React, { useState } from 'react';
import IconButton from '@material-ui/core/IconButton'
import SettingsIcon from '@material-ui/icons/Settings'
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import { convertToReactComponent } from 'react-solid-bridge';

import { useControllerProperty, useControllerAction } from '../controller-hooks.js';
import { subcontroller } from '../../../ui/viewer/store.js';
import { StrutLengthPanel } from './length.jsx';
import { OrbitPanel } from './orbitpanel.jsx';

const OrbitPanelSolid = convertToReactComponent( OrbitPanel );

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI.
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.log(error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return <h1>ERROR OCCURRED, CHECK THE CONSOLE</h1>;
    }

    return this.props.children; 
  }
}

export const StrutBuildPanel = ( { symmController } ) =>
{
  const availableOrbits = subcontroller( symmController, 'availableOrbits' );
  const orbitNames = useControllerProperty( availableOrbits, 'orbits', 'orbits', true );
  const setOrbitNames = useControllerAction( availableOrbits );

  const buildOrbits = subcontroller( symmController, 'buildOrbits' );
  const lastSelected = useControllerProperty( buildOrbits, 'selectedOrbit', 'orbits', false );

  const [ anchorEl, setAnchorEl ] = useState( null );
  const revealSettings = evt => setAnchorEl( evt.currentTarget );
  const setAvailableOrbits = action => () => {
    setAnchorEl( null );
    setOrbitNames( action );
  }

  return(
    <div id="build" style={{ display: 'grid', gridTemplateRows: '1fr min-content', height: '100%' }}>
    <ErrorBoundary>
      <OrbitPanelSolid lastSelected={lastSelected} style={{ height: '100%' }} >
        <IconButton color="inherit" aria-label="settings"
            style={ { position: 'absolute', top: '8px', right: '8px' } }
            onClick={revealSettings} >
          <SettingsIcon fontSize='medium'/>
        </IconButton>
        <Menu
          id="orbit-settings-menu"
          anchorEl={anchorEl}
          keepMounted
          open={Boolean(anchorEl)}
          onClose={ () => setAnchorEl( null ) }
        >
          <MenuItem onClick={ setAvailableOrbits( 'rZomeOrbits' ) }>real Zome</MenuItem>
          <MenuItem onClick={ setAvailableOrbits( 'predefinedOrbits' ) }>predefined</MenuItem>
          <MenuItem onClick={ setAvailableOrbits( 'setAllDirections' ) }>all</MenuItem>
        </Menu>
      </OrbitPanelSolid>
      <StrutLengthPanel controller={buildOrbits} />
    </ErrorBoundary>
    </div>
  );
}