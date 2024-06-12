
import TextField from '@suid/material/TextField';
import Dialog from '@suid/material/Dialog';
import DialogActions from '@suid/material/DialogActions';
import DialogContent from '@suid/material/DialogContent';
import DialogContentText from '@suid/material/DialogContentText';
import DialogTitle from '@suid/material/DialogTitle';
import Button from '@suid/material/Button';

import { createSignal } from "solid-js";
import { resumeMenuKeyEvents } from '../../framework/context/editor.jsx';

export const UrlDialog = ( props ) =>
{
  const [ url, setUrl ] = createSignal( '' );

  const handleCancel = () =>{
    resumeMenuKeyEvents();
    props.setShow( false );
  }
  const handleOpen = () =>{
    resumeMenuKeyEvents();
    props.setShow( false );
    props.openDesign( url() );
  }
  const handleChange = (event) => setUrl( event.target.value )

  return (
    <Dialog open={props.show} onClose={handleCancel} aria-labelledby="form-dialog-title" maxWidth='lg' fullWidth={true}>
      <DialogTitle id="form-dialog-title">Load a Remote vZome Design</DialogTitle>
      <DialogContent>
        <DialogContentText>
          The URL must be open to public access.
        </DialogContentText>
        <TextField onChange={handleChange}
          autoFocus
          margin="dense"
          id="name"
          label="vZome design URL"
          type="url"
          fullWidth
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleCancel} color="secondary">
          Cancel
        </Button>
        <Button onClick={handleOpen} color="primary">
          Open
        </Button>
      </DialogActions>
    </Dialog>
  )
} 


