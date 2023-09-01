
import Dialog from '@suid/material/Dialog';
import DialogActions from '@suid/material/DialogActions';
import DialogContent from '@suid/material/DialogContent';
import DialogContentText from '@suid/material/DialogContentText';
import DialogTitle from '@suid/material/DialogTitle';
import Button from '@suid/material/Button';

export const Beta = ( props ) =>
{
  const handleCancel = () =>
  {
    props.close();
  }

  return (
    <Dialog open={props.show} onClose={handleCancel} aria-labelledby="form-dialog-title" >
      <DialogTitle id="form-dialog-title">Beta Version</DialogTitle>
      <DialogContent>
        <DialogContentText>
          vZome Online is still a beta release.
          You can open most existing vZome design files, but not all of them.
          If you open an existing file, never save back to the same file, to avoid corrupting it.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleCancel} color="primary">
          Continue
        </Button>
      </DialogActions>
    </Dialog>
  )
} 


