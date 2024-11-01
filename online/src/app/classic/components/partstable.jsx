
import { createSignal } from "solid-js";

import TableContainer from "@suid/material/TableContainer";
import Table          from "@suid/material/Table";
import TableBody      from "@suid/material/TableBody";
import TableHead      from "@suid/material/TableHead";
import TableRow       from "@suid/material/TableRow";
import TableCell      from "@suid/material/TableCell";
import Paper          from "@suid/material/Paper";

import { useEditor } from "../../framework/context/editor";

export const PartsPanel = props =>
{
  const { partsList } = useEditor();  
  const cellStyle = { padding: '3px 0px', border: '0px' };
  const headerStyle = { ...cellStyle, fontWeight: 'bold' };
  const colorStyle = str => ({
    'background-color': `rgb( ${ str?.replaceAll( ',', ' ' ) || '255 255 255'} ) `,
    width: '40px',
    height: '20px'
  });

  return (
    <div>
      <div class="parts-row">Balls: <span class="parts-count">{partsList().balls}</span></div>
      <div class="parts-row">Panels: <span class="parts-count">{partsList().panels}</span></div>
      <TableContainer component={Paper} sx={{ 'padding-inline': '12px', 'padding-block': '2px' }}>
        <Table aria-label="measurements table" size="small">
          <TableHead>
            <TableRow>
              <TableCell align="left" component="th" sx={headerStyle}>Struts</TableCell>
              <TableCell align="left" component="th" sx={headerStyle}>Orbit</TableCell>
              <TableCell align="left" component="th" sx={headerStyle}>Length (orbit units)</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            <For each={partsList().orbitColors}>{ ( { color, length, count } ) =>
              <TableRow>
                <TableCell align="left" component="th" scope="row" sx={cellStyle} >{ count }</TableCell>
                <TableCell align="left" component="th" scope="row" sx={cellStyle} >
                  <div style={ colorStyle( color ) }></div>
                </TableCell>
                <TableCell align="left" component="th" scope="row" sx={cellStyle} >{ length }</TableCell>
              </TableRow>
            }</For>
            <TableRow>
              <TableCell align="left" component="th" scope="row" sx={cellStyle} >{ partsList().struts }</TableCell>
              <TableCell align="left" component="th" scope="row" sx={cellStyle} >Total</TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}