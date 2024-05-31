
import { createSignal, onMount } from "solid-js";

import TextField from '@suid/material/TextField';
import Dialog from '@suid/material/Dialog';
import DialogActions from '@suid/material/DialogActions';
import DialogContent from '@suid/material/DialogContent';
import DialogContentText from '@suid/material/DialogContentText';
import DialogTitle from '@suid/material/DialogTitle';
import IconButton from '@suid/material/IconButton'
import Button from '@suid/material/Button'
import ShareIcon from "@suid/icons-material/Share";
import Link from '@suid/material/Link';
import { Tooltip } from '../../framework/tooltip.jsx'

import { useEditor } from "../../../viewer/context/editor.jsx";

export const SharingDialog = ( props ) =>
{
  const [ open, setOpen ] = createSignal( false );
  const [ token, setToken ] = createSignal( '' );
  const [ error, setError ] = createSignal( '' );
  const [ uploading, setUploading ] = createSignal( false );
  const { shareToGitHub } = useEditor();

  onMount( () => {
    const pat = localStorage .getItem( "gitHubPersonalAccessToken" );
    if ( !!pat ) setToken( pat );
  })

  const handleClickOpen = () => {
    setUploading( false );
    setOpen( true );
  };
  const handleCancel = () => {
    setOpen( false );
  };
  const handleShare = () =>{
    setUploading( true );
    shareToGitHub( token() )
      .then( url => {
        window.open( url, '_blank' );
        setOpen( false );
      })
      .catch( error => {
        setError( error );
      });
  }
  const handleChange = (event) => {
    setToken( event.target.value );
    localStorage .setItem( "gitHubPersonalAccessToken", event.target.value );
  }

  return (
    <>
      <Tooltip title='Share to GitHub' aria-label="share">
        <IconButton color="primary" aria-label="share" onClick={handleClickOpen}>
          <ShareIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Dialog open={open()} onClose={handleCancel} aria-labelledby="form-dialog-title" maxWidth='sm' fullWidth={true}>
        <DialogTitle id="form-dialog-title">Share to GitHub</DialogTitle>
        <DialogContent>
          <Switch fallback={
              <DialogContentText>
                You are authenticated and authorized to GitHub.
              </DialogContentText>
          }>
            <Match when={error()}>
              <DialogContentText>
                Error during upload: {error()}
              </DialogContentText>
            </Match>
            <Match when={uploading()}>
              <DialogContentText>
                Uploading content to GitHub...
              </DialogContentText>
            </Match>
            <Match when={!token()}>
              <DialogContentText>
                Sharing requires a GitHub account, and you must generate a
                Personal Access Token (classic) for the account.
                GitHub accounts are free of charge. <Link href='https://github.com/settings/tokens' target="_blank" rel="noopener">
                  Click here to sign in (or sign up) and generate the token.
                </Link>
              </DialogContentText>
              <TextField onChange={handleChange}
                autoFocus
                margin="dense"
                id="access-token"
                label="Personal Access Token"
                type="password"
                defaultValue={token()}
                fullWidth
              />
            </Match>
          </Switch>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancel} color="secondary">
            Cancel
          </Button>
          <Button disabled={!token() || uploading()} onClick={handleShare} color="primary">
            Share
          </Button>
        </DialogActions>
      </Dialog>
    </>
  )
} 


