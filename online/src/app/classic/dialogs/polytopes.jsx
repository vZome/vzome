
import { For, createEffect } from 'solid-js';
import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"
import FormControl from "@suid/material/FormControl";
import InputLabel from "@suid/material/InputLabel";
import MenuItem from "@suid/material/MenuItem";
import Select from "@suid/material/Select";

import { controllerProperty, subController, useEditor } from '../../../viewer/context/editor.jsx';

const indices = [ 0, 1, 2, 3 ];
const alt = Math.sqrt( 3 ) / 2;
const x_D4 = [ 2, 2, 2.5, 3.5 ];
const y_D4 = [ 1-alt, 1+alt, 1, 1 ];
const x_others = [ 1, 2, 3, 4 ];
const y_others = [ 1, 1, 1, 1 ];

const CoxeterDiagram = props =>
{
  const { controllerAction } = useEditor();
  const useMirror = (i) => () => controllerProperty( props.controller, `edge.${i}` ) === 'true';
  const toggleMirror = (i) => (evt) =>
  {
    controllerAction( props.controller, `edge.${i}` );
  }
  const isD4 = () => props.group === 'D4';
  const x = (i) => isD4()? x_D4[i] : x_others[i];
  const y = (i) => isD4()? y_D4[i] : y_others[i];

  return (
    <div style={{ position: 'relative', 'background-color': 'white', cursor: 'pointer' }}>
      <svg viewBox='0 -0.5 5 3' stroke="black" stroke-width={0.03} >
        <style>
            { ".label { font: .35px sans-serif; }" }
        </style>
        <g>
          <text x="1.4" y="1.5" class="label" stroke-width={0.03}>{
            props.group==='H4' ? '5' :
            props.group==='B4/C4' ? '4' :
            ''
          }</text>
          <text x="2.4" y="1.5" class="label" stroke-width={0.03}>{
            props.group==='F4' ? '4' : ''
          }</text>
          <For each={indices}>{ i =>
            <g>
              <line x1={x(i)} y1={y(i)} x2={x(2)} y2={y(2)} stroke="black" stroke-width={0.05} />
              <circle cx={x(i)} cy={y(i)} r={0.1} fill="black" />
              { useMirror(3-i)() && // reversed
                <circle cx={x(i)} cy={y(i)} r={0.3} fill="none" />
              }
              <circle cx={x(i)} cy={y(i)} r={0.5} fill="none" stroke="none" onClick={ toggleMirror(3-i) } pointer-events="all" />
            </g>
          }</For>
        </g>
      </svg>
    </div>

  )
}

export const PolytopesDialog = props =>
{
  const { controllerAction } = useEditor();
  const controller = () => subController( props.controller, 'polytopes' );
  const groups = () => controllerProperty( controller(), 'groups', 'groups', true );
  const group = () => controllerProperty( controller(), 'group', 'group', false ) || 'H4'; // must not be undefined, so select is controlled

  // The Select shows empty initially, rendering before the groups list is populated.
  //   It heals after dismissing and reopening the dialog.
  //   To fix this, I added the Show guard.

  const changeGroup = (event) =>
  {
    controllerAction( controller(), `setGroup.${event.target.value}` );
  };
  const generate = event =>
  {
    props.close();
    controllerAction( controller(), 'generate' );
  }

  return (
    <Dialog onClose={ () => props.close() } open={props.open}>
      <DialogTitle id="polytopes-dialog">Generate a 4D Polytope</DialogTitle>
      <DialogContent sx={{ 'padding-bottom': '0px' }}>
        <FormControl fullWidth sx={{ 'padding-top': '20px' }}>
          <InputLabel id="symmetry-group-label" sx={{ 'padding-top': '20px' }}>group</InputLabel>
          <Show when={groups().length > 0}>
            <Select labelId="symmetry-group-label" id="symmetry-group" label="group"
              value={group()}
              onChange={changeGroup}
            >
              <For each={groups()}>{ g =>
                <MenuItem value={g}>{g}</MenuItem>
              }</For>
            </Select>
            <CoxeterDiagram controller={controller()} group={group()}/>
          </Show>
        </FormControl>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" size="medium" onClick={ ()=>props.close() } color="secondary">
          Close
        </Button>
        <Button variant="contained" size="medium" onClick={ generate } color="primary">
          Apply
        </Button>
      </DialogActions>
    </Dialog>
  );
}
