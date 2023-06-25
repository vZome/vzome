
import { createSignal } from 'solid-js'

import Dialog from '@suid/material/Dialog'
import DialogActions from '@suid/material/DialogActions'
import DialogTitle from '@suid/material/DialogTitle'
import DialogContent from '@suid/material/DialogContent'
import DialogContentText from '@suid/material/DialogContentText'
import IconButton from '@suid/material/IconButton'
import Button from '@suid/material/Button'
import InfoRoundedIcon from '@suid/icons-material/InfoRounded'
import { Tooltip } from '../suidstub/tooltip.jsx'

import { REVISION } from '../../../revision.js'

export const AboutDialog = ( { title, about } ) =>
{
  const [open, setOpen] = createSignal(false);

  const handleClickOpen = () => {
    setOpen( true );
  };
  const handleClose = () => {
    setOpen( false );
  };

  return (
    <>
      <Tooltip title={`About ${title}`} aria-label="about">
        <IconButton color="inherit" aria-label="about" onClick={handleClickOpen}>
          <InfoRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Dialog onClose={handleClose} aria-labelledby="about-dialog-title" open={open()}>
        <DialogTitle id="customized-dialog-title" >
          About vZome Online {title} (rev {REVISION})
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="about-dialog-description">
            {about}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Close</Button>
        </DialogActions>
      </Dialog>
    </>
  )
}
