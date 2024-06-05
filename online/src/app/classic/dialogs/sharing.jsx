
import { createSignal, onMount } from "solid-js";
import { unwrap } from "solid-js/store";

import TextField from '@suid/material/TextField';
import Dialog from '@suid/material/Dialog';
import DialogActions from '@suid/material/DialogActions';
import DialogContent from '@suid/material/DialogContent';
import DialogContentText from '@suid/material/DialogContentText';
import DialogTitle from '@suid/material/DialogTitle';
import IconButton from '@suid/material/IconButton';
import Button from '@suid/material/Button';
import FormControl from "@suid/material/FormControl";
import InputLabel from "@suid/material/InputLabel";
import MenuItem from "@suid/material/MenuItem";
import Select from "@suid/material/Select";
import ShareIcon from "@suid/icons-material/Share";
import Link from '@suid/material/Link';
import { Tooltip } from '../../framework/tooltip.jsx'

import { useEditor } from "../../../viewer/context/editor.jsx";
import { getUserRepos } from "../../../worker/legacy/gitcommit.js";

const AUTHENTICATING = 0;
const CHOOSING_REPO = 1;
const CONFIGURING = 2;
const UPLOADING = 3;

export const SharingDialog = ( props ) =>
{
  const [ open, setOpen ] = createSignal( false );
  const [ target, setTarget ] = createSignal( {} );
  const [ repo, setRepo ] = createSignal( null );
  const [ repos, setRepos ] = createSignal( [] );
  const [ error, setError ] = createSignal( '' );
  const [ stage, setStage ] = createSignal( AUTHENTICATING );
  const [ disabled, setDisabled ] = createSignal( true );

  const { shareToGitHub } = useEditor();

  const TARGET_KEY = 'github-target-details';

  const handleClickOpen = () => {
    const target = localStorage .getItem( TARGET_KEY );
    if ( !!target ) {
      const stored = JSON.parse( target );
      setTarget( stored );
      getUserRepos( stored )
        .then( repos => {
          setStage( CHOOSING_REPO );
          if ( repos.length === 0 ) {
            setError( 'You are authenticated to GitHub, but no repositories were found.  Please create a repository.' );
          } else {
            const { orgName, repoName } = stored;
            setRepo( orgName + '/' + repoName );
            setRepos( repos );
            setDisabled( false );
          }
          setOpen( true );    
        })
        .catch( error => {
          setError( `Unable to list GitHub repos: ${error.message}` );
        } );
    } else {
      setStage( AUTHENTICATING );
      setError( '' );
      setDisabled( true );
      setOpen( true );  
    }
  };
  const handleCancel = () => {
    setOpen( false );
  };
  const handleShare = () =>{
    if ( stage()===AUTHENTICATING ) {
      getUserRepos( target() )
        .then( repos => {
          setRepo( repos[ 0 ] );
          setRepos( repos );
          setStage( CHOOSING_REPO );
        })
        .catch( error => setError( `GitHub authentication failed: ${error.message}` ) );
      return;
    }
    if ( stage()===CHOOSING_REPO ) {
      const [ orgName, repoName ] = repo() .split( '/' );
      const { token, branchName } = target();
      const newTarget = { token, orgName, repoName, branchName };
      setTarget( newTarget );
      localStorage .setItem( TARGET_KEY, JSON.stringify( newTarget ) );
      setStage( CONFIGURING );
      return;
    }
    if ( stage()===CONFIGURING ) {
      setStage( UPLOADING );
      const config = {}; // TODO
      shareToGitHub( unwrap( target() ), config )
        .then( url => {
          window.open( url, '_blank' );
          setOpen( false );
        })
        .catch( error => {
          setError( error );
        });
    }
  }
  const handleTokenEntered = (event) => {
    setTarget( { token: event.target.value, branchName: 'main' } );
    setDisabled( false );
  }
  const handleRepoChange = (event) => {
    setRepo( event.target.value );
  };
  
  return (
    <>
      <Tooltip title='Share to GitHub' aria-label="share">
        <IconButton color="primary" aria-label="share" onClick={handleClickOpen}>
          <ShareIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Dialog open={open()} onClose={handleCancel} aria-labelledby="form-dialog-title" maxWidth='sm' fullWidth={true}>
        <DialogTitle id="form-dialog-title">{error()? 'GitHub Sharing Error' : 'Share to GitHub' }</DialogTitle>
        <DialogContent>
          <Switch fallback={
              <DialogContentText>
                Uploading content to GitHub...
              </DialogContentText>
          }>
            <Match when={error()}>
              <DialogContentText>
                {error()}
              </DialogContentText>
            </Match>
            <Match when={stage()===AUTHENTICATING}>
              <DialogContentText>
                Sharing requires a GitHub account, and you must generate a
                Personal Access Token (classic) for the account.
                GitHub accounts are free of charge. <Link href='https://github.com/settings/tokens' target="_blank" rel="noopener">
                  Click here to sign in (or sign up) and generate the token.
                </Link>
              </DialogContentText>
              <TextField onChange={handleTokenEntered}
                autoFocus
                margin="dense"
                id="access-token"
                label="Personal Access Token"
                type="password"
                fullWidth
              />
            </Match>
            <Match when={stage()===CHOOSING_REPO}>
              <DialogContentText>
                Select a GitHub repository:
              </DialogContentText>
              <FormControl fullWidth sx={{ 'padding-top': '20px' }}>
                <InputLabel id="repository-label" sx={{ 'padding-top': '20px' }}>repository</InputLabel>
                <Show when={repos().length > 0}>
                  <Select labelId="repository-label" id="repository" label="repository"
                    value={repo()}
                    onChange={handleRepoChange}
                  >
                    <For each={repos()}>{ r =>
                      <MenuItem value={r}>{r}</MenuItem>
                    }</For>
                  </Select>
                </Show>
              </FormControl>
            </Match>
            <Match when={stage()===CONFIGURING}>
              <DialogContentText>
                Ready to share default content to GitHub.
              </DialogContentText>
              {/* TODO %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                  Configure what to share.
              */}
            </Match>

          </Switch>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancel} color="secondary">
            Cancel
          </Button>
          <Button disabled={disabled() || error()} onClick={handleShare} color="primary">
            { stage()===AUTHENTICATING? 'Authenticate' : stage()===CHOOSING_REPO? 'Confirm' : 'Share' }
          </Button>
        </DialogActions>
      </Dialog>
    </>
  )
} 


