
import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"
import FormControl from "@suid/material/FormControl";
import FormControlLabel from "@suid/material/FormControlLabel";
import RadioGroup from "@suid/material/RadioGroup";
import Radio from "@suid/material/Radio";
import { For } from 'solid-js';

import { controllerAction, controllerProperty } from "../controllers-solid.js";

export const ShapesDialog = props =>
{
  const styles = () => controllerProperty( props.controller, 'styles', 'styles', true );
  const currStyle = () => controllerProperty( props.controller, 'renderingStyle', 'renderingStyle', false );

  const handleChange = event =>{
    controllerAction( props.controller, `setStyle.${event.target.value}` );
  }

  return (
    <Dialog onClose={ () => props.close() } open={props.open}>
      <DialogTitle id="shapes-dialog">Select Shapes</DialogTitle>
      <DialogContent>
        <FormControl>
          <RadioGroup aria-labelledby="shapes-dialog" name="shapes-dialog-radio-buttons-group"
            value={currStyle()} onChange={handleChange}
          >
            <For each={styles()}>{ style =>
              <FormControlLabel value={style} control={<Radio />} label={style} />
            }</For>
          </RadioGroup>
        </FormControl>
      </DialogContent>
      <DialogActions>
        <Button size="small" onClick={ ()=>props.close() } color="primary">
          Close
        </Button>
      </DialogActions>
    </Dialog>
  );
}
