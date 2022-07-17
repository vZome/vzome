

import React from 'react'
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Button from '@material-ui/core/Button';
import Checkbox from '@material-ui/core/Checkbox';

export const SettingsDialog = ({ showSettings, setShowSettings, perspective, setPerspective, container }) =>
{
  const handleClose = () => setShowSettings( false )
  const togglePerspective = () => setPerspective( !perspective )

  return (
    <Dialog open={showSettings} onClose={handleClose} aria-labelledby="form-dialog-title"
        container={container} style={{ position: 'absolute'}} BackdropProps={{ style: { position: 'absolute' }}} >
      <DialogTitle id="form-dialog-title">Settings</DialogTitle>
      <DialogContent>
        <FormGroup row={false}>
          <FormControlLabel
            control={<Checkbox checked={perspective} onChange={togglePerspective} name="perspective" />}
            label="Perspective"
          />
        </FormGroup>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">
          Close
        </Button>
      </DialogActions>
    </Dialog>
  )
} 


