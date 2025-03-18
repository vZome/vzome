
import TableContainer from "@suid/material/TableContainer";
import Table          from "@suid/material/Table";
import TableBody      from "@suid/material/TableBody";
import TableHead      from "@suid/material/TableHead";
import TableRow       from "@suid/material/TableRow";
import TableCell      from "@suid/material/TableCell";
import Paper          from "@suid/material/Paper";

import { controllerProperty, subController, useEditor } from '../../framework/context/editor.jsx';

export const MeasurePanel = () =>
{
  const { rootController } = useEditor();
  const measureController = () => subController( rootController(), 'measure' );
  const measures = () => controllerProperty( measureController(), 'measures', 'measures', true );
  const value = measure => controllerProperty( measureController(), measure );
  
  const cellStyle = { padding: '6px 0px', border: '0px' };
  const headerStyle = { ...cellStyle, borderBlockEnd: '1px solid darkgray', fontWeight: 'bold' };
  return (
    <TableContainer component={Paper}>
      <Table aria-label="measurements table" size="small">
        <TableHead>
          <TableRow>
            <TableCell align="left" component="th" sx={headerStyle}>Measurement</TableCell>
            <TableCell align="left" component="th" sx={headerStyle}>Value</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          <For each={measures()}>{ ( measure ) =>
            <TableRow>
              <TableCell align="left" component="th" scope="row" sx={cellStyle} >{ measure }</TableCell>
              <TableCell align="left" component="th" scope="row" sx={cellStyle} >{ value( measure ) }</TableCell>
            </TableRow>
          }</For>
        </TableBody>
      </Table>
    </TableContainer>
  );  
}