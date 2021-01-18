
import React from 'react';
import { withStyles } from '@material-ui/core/styles'
import Dialog from '@material-ui/core/Dialog'
import MuiDialogTitle from '@material-ui/core/DialogTitle'
import MuiDialogContent from '@material-ui/core/DialogContent'
import IconButton from '@material-ui/core/IconButton'
import CloseIcon from '@material-ui/icons/Close'
import Typography from '@material-ui/core/Typography'
import InfoRoundedIcon from '@material-ui/icons/InfoRounded'
import Link from '@material-ui/core/Link'
import Tooltip from '@material-ui/core/Tooltip'

const styles = (theme) => ({
  root: {
    margin: 0,
    padding: theme.spacing(2),
  },
  closeButton: {
    position: 'absolute',
    right: theme.spacing(1),
    top: theme.spacing(1),
    color: theme.palette.grey[500],
  },
})

const DialogTitle = withStyles(styles)((props) => {
  const { children, classes, onClose, ...other } = props;
  return (
    <MuiDialogTitle disableTypography className={classes.root} {...other}>
      <Typography variant="h6">{children}</Typography>
      {onClose ? (
        <IconButton aria-label="close" className={classes.closeButton} onClick={onClose}>
          <CloseIcon />
        </IconButton>
      ) : null}
    </MuiDialogTitle>
  )
})

const DialogContent = withStyles((theme) => ({
  root: {
    padding: theme.spacing(2),
  },
}))(MuiDialogContent)

export default function AboutDialog() {
  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true)
  }
  const handleClose = () => {
    setOpen(false)
  }

  return (
    <>
      <Tooltip title="About vZome Online" aria-label="about">
        <IconButton color="inherit" aria-label="about" onClick={handleClickOpen}>
          <InfoRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Dialog onClose={handleClose} aria-labelledby="customized-dialog-title" open={open}>
        <DialogTitle id="customized-dialog-title" onClose={handleClose}>
          About vZome Online
        </DialogTitle>
        <DialogContent dividers>
          <Typography gutterBottom>
            vZome Online is the world's first in-browser modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>
            ... or it will be soon.
          </Typography>
          <Typography gutterBottom>
            Right now, you can load and view existing vZome designs, created using
            the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>.
            Click on the folder icon try out some of the built-in designs, or load one of your own!
          </Typography>
          <Typography gutterBottom>
            At the moment, you cannot modify designs or create new designs.  I'm working to complete those features
            (and all the other features of desktop vZome)
            as soon as possible;
            my top priority so far has been to support loading existing designs.
            If you want to stay informed about my progress, follow vZome
            on <Link target="_blank" rel="noopener" href="https://www.facebook.com/vZome">Facebook</Link> or <Link target="_blank" rel="noopener" href="https://twitter.com/vZome">Twitter</Link>.
          </Typography>
          <Typography gutterBottom>
            Not all vZome designs will load successfully yet, unfortunately.
            It turns out to be a long and painstaking process to port all of the vZome
            code to the web, and I still have some work to do.
            If you have a vZome design (in the golden field) that does not load
            here, <Link target="_blank" rel="noopener" href="mailto:info@vzome.com">send me the vZome file</Link>,
            and I can prioritize the necessary fixes.
          </Typography>
        </DialogContent>
        {/* <DialogActions>
          <Button autoFocus onClick={handleClose} color="primary">
            Download vZome Desktop App
          </Button>
        </DialogActions> */}
      </Dialog>
    </>
  )
}
