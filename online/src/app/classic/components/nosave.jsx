
import Dialog from '@suid/material/Dialog';
import DialogActions from '@suid/material/DialogActions';
import DialogContent from '@suid/material/DialogContent';
import DialogContentText from '@suid/material/DialogContentText';
import DialogTitle from '@suid/material/DialogTitle';
import Button from '@suid/material/Button';

export const NoSave = ( props ) =>
{
  const handleCancel = () =>
  {
    props.close();
  }

  return (
    <Dialog open={props.show} onClose={handleCancel} aria-labelledby="form-dialog-title" >
      <DialogTitle id="form-dialog-title">Save Not Supported</DialogTitle>
      <DialogContent>
        <DialogContentText>
          There is a bug in file saving, causing garbling of the command history.
          Save is therefore disabled.  The bug will be fixed soon.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleCancel} color="primary">
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  )
} 


