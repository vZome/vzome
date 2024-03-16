
import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"
import TextField from "@suid/material/TextField"

import { subController, useEditor } from '../../../viewer/context/editor.jsx';
import { createEffect, createSignal } from "solid-js"

export const LabelDialog = props =>
{
  const { rootController, controllerAction } = useEditor();
  const controller  = () => subController( rootController(), 'picking' );
  const [ value, setValue ] = createSignal( '' );
  createEffect( () => {
    if ( !! props.label )
      setValue( props.label );
  })

  const perform = event =>
  {
    controllerAction( controller(), 'Label', { id: props.id, text: value() } );
    props.close();
  }

  return (
    <Dialog onClose={ () => props.close() } open={props.open}>
      <DialogTitle id="label-dialog">Label an Object</DialogTitle>
      <DialogContent sx={{ 'padding-bottom': '0px' }}>
        <TextField autoFocus id="label-value" label="value" variant="outlined" fullWidth value={value()} sx={{ margin: '5px' }}
            onChange={(event, value) => setValue( value )}
          />
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" size="medium" onClick={ ()=>props.close() } color="secondary">
          Close
        </Button>
        <Button variant="contained" size="medium" onClick={ perform } color="primary">
          Apply
        </Button>
      </DialogActions>
    </Dialog>
  );
}
