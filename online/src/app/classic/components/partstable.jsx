

import TableContainer from "@suid/material/TableContainer";
import Table          from "@suid/material/Table";
import TableBody      from "@suid/material/TableBody";
import TableHead      from "@suid/material/TableHead";
import TableRow       from "@suid/material/TableRow";
import TableCell      from "@suid/material/TableCell";
import Paper          from "@suid/material/Paper";
import Button          from "@suid/material/Button";

import { useEditor } from "../../framework/context/editor";
import { createEffect } from "solid-js";

export const PartsPanel = props =>
{
  const { partsList, rootController, controllerAction } = useEditor();  
  const cellStyle = { padding: '3px 0px', border: '0px' };
  const actionsStyle = { ...cellStyle, textAlign: 'right', 'padding-inline-end': '6px' };
  const buttonStyle = { padding: '3px 5px', minWidth: '40px', fontSize: '0.6rem', lineHeight: '1.0', fontWeight: '600' };
  const headerStyle = { ...cellStyle, fontWeight: 'bold', 'vertical-align': 'top' };
  const actionsHeaderStyle = { ...headerStyle, textAlign: 'right', 'padding-inline-end': '6px' };
  const colorStyle = str => ({
    'background-color': `rgb( ${ str?.replaceAll( ',', ' ' ) || '255 255 255'} ) `,
    width: '40px',
    height: '20px'
  });

  const selectAll = partType => () => {
    controllerAction( rootController(), `AdjustSelectionByClass/select${partType}` );
  }
  const deselectAll = partType => () => {
    controllerAction( rootController(), `AdjustSelectionByClass/deselect${partType}` );
  }
  const selectStruts = ( color, length ) => () => {
    controllerAction( rootController(), `AdjustSelectionByOrbitLength/selectSimilarStruts/${color}/${length}` );
  }
  const deselectStruts = ( color, length ) => () => {
    controllerAction( rootController(), `AdjustSelectionByOrbitLength/deselectSimilarStruts/${color}/${length}` );
  }

  return (
    <div>
      <div class="parts-row">Balls: <span class="parts-count">{partsList().balls}</span>
        <div class="parts-buttons">
          <Button size="small" sx={buttonStyle} variant="outlined" color="primary" onClick={selectAll( 'Balls' )}>
            Select
          </Button>
          <Button size="small" sx={buttonStyle} variant="outlined" color="primary" onClick={deselectAll( 'Balls' )}>
            Deselect
          </Button>
        </div>
      </div>
      <div class="parts-row">Panels: <span class="parts-count">{partsList().panels}</span>
        <div class="parts-buttons">
          <Button size="small" sx={buttonStyle} variant="outlined" color="primary" onClick={selectAll( 'Panels' )}>
            Select
          </Button>
          <Button size="small" sx={buttonStyle} variant="outlined" color="primary" onClick={deselectAll( 'Panels' )}>
            Deselect
          </Button>
        </div>
      </div>
      <TableContainer component={Paper} sx={{ 'padding-inline': '12px 2px', 'padding-block': '2px' }}>
        <Table aria-label="measurements table" size="small">
          <TableHead>
            <TableRow>
              <TableCell align="left" component="th" sx={headerStyle}>Struts</TableCell>
              <TableCell align="left" component="th" sx={headerStyle}>Orbit</TableCell>
              <TableCell align="left" component="th" sx={headerStyle}>Length<br></br>(orbit units)</TableCell>
              <TableCell align="left" component="th" sx={actionsHeaderStyle}>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            <For each={partsList().orbitColors}>{ ( { color, length, count, zomic, orbit } ) =>
              <TableRow>
                <TableCell align="left" component="th" scope="row" sx={cellStyle} >{ count }</TableCell>
                <TableCell align="left" component="th" scope="row" sx={cellStyle} >
                  <div style={ colorStyle( color ) }></div>
                </TableCell>
                <TableCell align="left" component="th" scope="row" sx={cellStyle} >{ length }</TableCell>
                <TableCell align="left" component="th" scope="row" sx={actionsStyle} >
                  <Button size="small" sx={buttonStyle} variant="outlined" color="primary" onClick={selectStruts( orbit, zomic )}>
                    Select
                  </Button>
                  <Button size="small" sx={buttonStyle} variant="outlined" color="primary" onClick={deselectStruts( orbit, zomic )}>
                    Deselect
                  </Button>
                </TableCell>
              </TableRow>
            }</For>
            <TableRow>
              <TableCell align="left" component="th" scope="row" sx={cellStyle} >{ partsList().struts }</TableCell>
              <TableCell align="left" component="th" scope="row" sx={cellStyle} >Total</TableCell>
              <TableCell align="left" component="th" scope="row" sx={cellStyle} ></TableCell>
              <TableCell align="left" component="th" scope="row" sx={actionsStyle} >
                  <Button size="small" sx={buttonStyle} variant="outlined" color="primary" onClick={selectAll( 'Struts' )}>
                    Select
                  </Button>
                  <Button size="small" sx={buttonStyle} variant="outlined" color="primary" onClick={deselectAll( 'Struts' )}>
                    Deselect
                  </Button>
                  
                </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}