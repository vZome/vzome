
import React from 'react'
import { useDispatch } from 'react-redux';
import { doControllerAction, newDesign } from '../../ui/viewer/store.js';

import Grid from '@material-ui/core/Grid'
import Button from '@material-ui/core/Button';
import { useEffect } from 'react';

export const ClassicEditor = ( props ) =>
{
  const report = useDispatch();
  const doAction = () => report( doControllerAction( "polytopes", "Polytope4d" ) );

  useEffect( () => report( newDesign() ), [] );

  const drawerColumns = 5;
  const canvasColumns = 12 - drawerColumns;

  return (
    <div style={{ flex: '1', height: '100%' }}>
      <Grid id='editor-main' container spacing={0} style={{ height: '100%' }}>        
        <Grid id='editor-drawer' item xs={drawerColumns}>
          <Button variant="contained" color="primary" onClick={doAction}>
            Controller Action
          </Button>
        </Grid>
        <Grid id='editor-canvas' item xs={canvasColumns} >
          <div id='swing-root'></div>
        </Grid>
      </Grid>
    </div>
  )
}
