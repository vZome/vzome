
import { For, createEffect } from "solid-js";

import TableContainer from "@suid/material/TableContainer";
import Table          from "@suid/material/Table";
import TableHead      from "@suid/material/TableHead";
import TableRow       from "@suid/material/TableRow";
import TableCell      from "@suid/material/TableCell";
import TableBody      from "@suid/material/TableBody";
import Paper          from "@suid/material/Paper";

import { labelString, resolveOrbits, useCellOrbits } from "./state.jsx";

const coxeterTable = [
  {
    name: 'icosahedron',
    orbits: [ 'A' ],
  },
  {
    name: 'triakis icosahedron',
    orbits: [ 'B' ],
  },
  {
    name: 'regular compound of five octahedra',
    orbits: [ 'C' ],
  },
  {
    name: '',
    orbits: [ 'D' ],
  },
  {
    name: '',
    orbits: [ 'E' ],
  },
  {
    name: '',
    orbits: [ 'F' ],
  },
  {
    name: 'great icosahedron',
    orbits: [ 'G' ],
  },
  {
    name: '',
    orbits: [ 'H' ],
  },
  {
    name: '',
    orbits: [ 'e1' ],
  },
  {
    name: '',
    orbits: [ 'f1' ],
  },
  {
    name: '',
    orbits: [ 'g1' ],
  },
  {
    name: '',
    orbits: [ 'e1', 'f1' ],
  },
  {
    name: '',
    orbits: [ 'e1', 'f1', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'f1', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'e2' ],
  },
  {
    name: '',
    orbits: [ 'f2' ],
  },
  {
    name: '',
    orbits: [ 'g2' ],
  },
  {
    name: '',
    orbits: [ 'e2', 'f2' ],
  },
  {
    name: '',
    orbits: [ 'e2', 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e1' ],
  },
  {
    name: 'regular compound of ten tetrahedra',
    orbits: [ 'E', 'f1' ],
  },
  {
    name: '',
    orbits: [ 'F', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e1', 'f1' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e1', 'f1', 'g1' ],
  },
  {
    name: 'excavated dodecahedron',
    orbits: [ 'E', 'f1', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e2' ],
  },
  {
    name: '',
    orbits: [ 'E', 'f2' ],
  },
  {
    name: '',
    orbits: [ 'F', 'g2' ],
  },
  {
    name: 'medial triambic icosahedron ',
    orbits: [ 'D', 'e2', 'f2' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e2', 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'E', 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'f1R' ],
  },
  {
    name: '',
    orbits: [ 'e1', 'f1R' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e1', 'f1R' ],
  },
  {
    name: '',
    orbits: [ 'f1R', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'e1', 'f1R', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e1', 'f1R', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'f1R', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'e1', 'f1R', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e1', 'f1R', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'f1R', 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'e1', 'f1R', 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e1', 'f1R', 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'e2', 'f1R' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e2', 'f1R' ],
  },
  {
    name: 'regular compound of five tetrahedra',
    orbits: [ 'E', 'f1R' ],
  },
  {
    name: '',
    orbits: [ 'e2', 'f1R', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e2', 'f1R', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'E', 'f1R', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'e2', 'f1R', 'f2' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e2', 'f1R', 'f2' ],
  },
  {
    name: '',
    orbits: [ 'E', 'f1R', 'f2' ],
  },
  {
    name: '',
    orbits: [ 'e2', 'f1R', 'f2', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e2', 'f1R', 'f2', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'E', 'f1R', 'f2', 'g1' ],
  },
  {
    name: '',
    orbits: [ 'e2', 'f1R', 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'D', 'e2', 'f1R', 'f2', 'g2' ],
  },
  {
    name: '',
    orbits: [ 'E', 'f1R', 'f2', 'g2' ],
  },
];

const StellationRow = props =>
{
  const { setOrbitList, enabledOrbits } = useCellOrbits();

  const selectOrbits = () => setOrbitList( resolveOrbits( props.orbits ) );
  const matchesState = () => JSON.stringify( enabledOrbits() ) === JSON.stringify( resolveOrbits( props.orbits ) );
  
  const orbitStr = () => labelString( props.orbits );
  const bkgdColor = () => matchesState() ? 'lightgray' : 'white';

  let domElement;
  createEffect( () => {
    if ( matchesState() ) {
      // console.log( `This is stellation #${props.index}` );
      domElement .scrollIntoView( { behavior: "smooth", block: "center" } );
    }
  });

  return (
    <TableRow ref={domElement} onClick={ () => selectOrbits() }
      sx={{ "&:last-child td, &:last-child th": { border: 0 }, "tr": { backgroundColor: bkgdColor() } }}
    >
      <TableCell align="right">{props.index}</TableCell>
      <TableCell align="right">{orbitStr()}</TableCell>
      <TableCell align="left">{props.name}</TableCell>
    </TableRow>
  )
}

export const CoxeterTable = () =>
{
  return (
    <div id='coxeter-table' class='safe-grid-item' >
      <div class="centered-scroller">
        <div class='scroller-content'>
          <TableContainer component={Paper}>
            <Table size='small' sx={{ minWidth: 450 }} aria-label="simple table">
              <TableHead sx={{ backgroundColor: 'whitesmoke', position:'sticky' }}>
                <TableRow>
                  <TableCell align="right">Number</TableCell>
                  <TableCell align="right">Orbits</TableCell>
                  <TableCell align="left">Name</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                <For each={coxeterTable}>{ ({ orbits, name }, i) => (
                  <StellationRow index={i()+1} name={name} orbits={orbits} />
                ) }</For>
              </TableBody>
            </Table>
          </TableContainer>
        </div>
      </div>
    </div>
  );
}