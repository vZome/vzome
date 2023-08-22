
import Dialog from '@suid/material/Dialog';
import DialogActions from '@suid/material/DialogActions';
import DialogContent from '@suid/material/DialogContent';
import DialogContentText from '@suid/material/DialogContentText';
import DialogTitle from '@suid/material/DialogTitle';
import Button from '@suid/material/Button';

export const Guardrail = ( props ) =>
{
  const handleCancel = () =>
  {
    props.close(false);
  }
  const handleContinue = () =>
  {
    props.close(true);
  }

  return (
    <Dialog open={props.show} onClose={handleCancel} aria-labelledby="form-dialog-title" >
      <DialogTitle id="form-dialog-title">Unsaved Changes</DialogTitle>
      <DialogContent>
        <DialogContentText>
          You have unsaved changes, and you will lose that work if you continue.
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleContinue} color="secondary">
          Continue
        </Button>
        <Button onClick={handleCancel} color="primary">
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  )
} 


