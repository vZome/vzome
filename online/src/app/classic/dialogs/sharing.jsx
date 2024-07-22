
import { createEffect, createSignal } from "solid-js";
import { unwrap } from "solid-js/store";

import TextField from '@suid/material/TextField';
import Dialog from '@suid/material/Dialog';
import DialogActions from '@suid/material/DialogActions';
import DialogContent from '@suid/material/DialogContent';
import DialogContentText from '@suid/material/DialogContentText';
import DialogTitle from '@suid/material/DialogTitle';
import IconButton from '@suid/material/IconButton';
import Button from '@suid/material/Button';
import FormGroup from "@suid/material/FormGroup";
import FormControl from "@suid/material/FormControl";
import FormControlLabel from "@suid/material/FormControlLabel";
import Checkbox from "@suid/material/Checkbox";
import InputLabel from "@suid/material/InputLabel";
import MenuItem from "@suid/material/MenuItem";
import Select from "@suid/material/Select";
import ShareIcon from "@suid/icons-material/Share";
import Link from '@suid/material/Link';
import { Tooltip } from '../../framework/tooltip.jsx'

import { useViewer } from "../../../viewer/context/viewer.jsx";
import { useEditor } from '../../framework/context/editor.jsx';
import { resumeMenuKeyEvents, suspendMenuKeyEvents } from '../context/commands.jsx';
import { getUserRepos } from "../../../both-contexts.js";

const AUTHENTICATING = 0;
const CHOOSING_REPO = 1;
const CONFIGURING = 2;
const UPLOADING = 3;

const ConfigPage = ( props ) =>
{
  const { state, setState } = useEditor();
  const { scenes } = useViewer();
  const noScenes = () => scenes.length < 2;

  createEffect( () => {
    let indexed = true;
    scenes .slice( 1 ) .map( scene => {
      if ( scene.title ) indexed = false;
    });
    const style = noScenes()? 'none' : indexed? 'indexed' : 'menu (all)';
    setState( 'sharing', { style } );
  });

  const handleTitleEntered = (event) => {
    setState( 'sharing', { title: event.target.value } );
  }
  const handleDescriptionEntered = (event) => {
    setState( 'sharing', { description: event.target.value } );
  }
  const handleStyleChange = (event) => {
    setState( 'sharing', { style: event.target.value } );
  }

  return (
    <>
      <TextField onChange={handleTitleEntered}
        autoFocus
        margin="dense"
        id="title"
        label="title"
        type="text"
        value={ state?.sharing?.title }
        fullWidth
      />
      <TextField onChange={handleDescriptionEntered}
        autoFocus
        margin="normal"
        id="title"
        label="description"
        type="text"
        value={ state?.sharing?.description }
        multiline
        rows={4}
        fullWidth
      />
      <FormControl sx={{ 'padding-top': '20px', m: 1, minWidth: 290 }} size="medium" >
        <InputLabel id="style-label" sx={{ 'padding-top': '20px' }}>scenes style</InputLabel>
        <Select labelId="style-label" label="scenes style"
          disabled={noScenes()}
          value={ state?.sharing?.style }
          onChange={handleStyleChange}
        >
          <MenuItem value='indexed'>Next/Prev Buttons</MenuItem>
          <MenuItem value='indexed (load-camera)'>Next/Prev Buttons (load-camera)</MenuItem>
          <MenuItem value='menu (named)'>Menu (named)</MenuItem>
          <MenuItem value='menu (all)'>Menu (all)</MenuItem>
          <MenuItem value='zometool'>Zometool Instructions</MenuItem>
          <MenuItem value='none'>None</MenuItem>
        </Select>
      </FormControl>
      <FormControl sx={{ 'padding-top': '20px', m: 1, minWidth: 290 }} >
        <FormGroup aria-label="blog" row>
          <FormControlLabel
            control={<Checkbox checked={props.blog} onChange={ () => props.setBlog( v => !v ) } />}
            label="Create blog post"
          />
          <FormControlLabel
            control={<Checkbox disabled={!props.blog} checked={props.publish} onChange={ () => props.setPublish( v => !v ) } />}
            label="Publish immediately"
          />
        </FormGroup>
      </FormControl>
    </>
  );
}

export const SharingDialog = ( props ) =>
{
  const [ open, setOpen ] = createSignal( false );
  const [ target, setTarget ] = createSignal( {} );
  const [ repo, setRepo ] = createSignal( null );
  const [ repos, setRepos ] = createSignal( [] );
  const [ error, setError ] = createSignal( '' );
  const [ stage, setStage ] = createSignal( AUTHENTICATING );
  const [ disabled, setDisabled ] = createSignal( true );
  const [ blog, setBlog ] = createSignal( false );
  const [ publish, setPublish ] = createSignal( false );

  const { shareToGitHub } = useEditor();

  const TARGET_KEY = 'classic-github-target-details';

  const handleClickOpen = () => {
    setStage( CHOOSING_REPO );
    setError( '' );
    suspendMenuKeyEvents();
    setOpen( true );  
    const target = localStorage .getItem( TARGET_KEY );
    if ( !!target ) {
      const stored = JSON.parse( target );
      setTarget( stored );
      getUserRepos( stored )
        .then( repos => {
          if ( repos.length === 0 ) {
            setError( 'You are authenticated to GitHub, but no repositories were found.  Please create a repository.' );
          } else {
            const { orgName, repoName } = stored;
            setRepo( orgName + '/' + repoName );
            setRepos( repos );
            setDisabled( false );
          }
        })
        .catch( error => {
          setError( `Unable to list GitHub repos: ${error.message}` );
        } );
    } else {
      setStage( AUTHENTICATING );
      setDisabled( true );
    }
  };
  const handleCancel = () => {
    setOpen( false );
    resumeMenuKeyEvents();
  };
  const handleShare = () =>{
    if ( stage()===AUTHENTICATING ) {
      setStage( CHOOSING_REPO );
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
      setDisabled( true );
      shareToGitHub( unwrap( target() ), blog(), publish() )
        .then( url => {
          window.open( url, '_blank' );
          setOpen( false );
          resumeMenuKeyEvents();
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
                margin="normal"
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
              <FormControl sx={{ 'padding-top': '20px', m: 1, minWidth: 290 }} size="medium" >
                <Show when={repos().length > 0} fallback={
                  <DialogContentText sx={{ 'font-style': 'italic' }}>loading...</DialogContentText>
                }>
                  <InputLabel id="repository-label" sx={{ 'padding-top': '20px' }}>repository</InputLabel>
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
              <ConfigPage blog={blog()} setBlog={setBlog} publish={publish()} setPublish={setPublish} />
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


