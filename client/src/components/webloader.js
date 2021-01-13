
import React, { useState } from 'react'
import TextField from '@material-ui/core/TextField';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Button from '@material-ui/core/Button';

export default ({ show, setShow, openDesign }) =>
{
  const [url, setUrl] = useState( '' )

  const handleCancel = () =>{
    setShow( false )
  }
  const handleOpen = () =>{
    setShow( false )
    openDesign( url )
  }
  const handleChange = (event) => setUrl( event.target.value )

  return (
    <Dialog open={show} onClose={handleCancel} aria-labelledby="form-dialog-title" maxWidth='lg' fullWidth={true}>
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


