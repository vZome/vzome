
import React from 'react';
import Grid from '@material-ui/core/Grid'
import Button from '@material-ui/core/Button';

import { DesignViewer } from '../../ui/viewer/index.jsx'
import { useNewDesign } from './controller-hooks.js';
import { OrbitPanel } from './orbit-panel.jsx';

export const ClassicEditor = () =>
{
  useNewDesign();
  // const makeHyperdo = useControllerAction( '', 'Polytope4d' );

  const rightColumns = 2;
  const canvasColumns = 12 - rightColumns;

  return (
    <div style={{ flex: '1', height: '100%' }}>
      <Grid id='editor-main' container spacing={0} style={{ height: '100%' }}>        
        <Grid id='editor-canvas' item xs={canvasColumns}>
          <DesignViewer config={ { useSpinner: true } } />
        </Grid>
        <Grid id='editor-drawer' item xs={rightColumns} style={{ position: 'relative' }}>
          <div id="camera-control" style={{ minHeight: '280px' }}></div>
          <OrbitPanel symmController='strutBuilder/symmetry' orbitSet='buildOrbits' />
          {/* <Button variant="contained" color="primary" >
            Hyperdo
          </Button> */}
        </Grid>
      </Grid>
    </div>
  )
}
