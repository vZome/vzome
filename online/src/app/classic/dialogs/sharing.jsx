
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

import { CameraProvider, DesignViewer } from '../../../viewer/index.jsx'

import { useViewer } from "../../../viewer/context/viewer.jsx";
import { useEditor } from '../../framework/context/editor.jsx';
import { resumeMenuKeyEvents, suspendMenuKeyEvents } from '../context/commands.jsx';
import { getUserRepos } from "../../../both-contexts.js";
import { useCamera } from "../../../viewer/context/camera.jsx";
import { ZometoolInstructions } from "../../../wc/zometool/index.jsx";
import { instructionsCSS } from "../../../wc/zometool/zometool.css.js";
import { SceneIndexingProvider, SceneProvider, useSceneIndexing } from "../../../viewer/context/scene.jsx";
import { urlViewerCSS } from "../../../viewer/urlviewer.css.js";

const CONFIGURING = 1;
const AUTHENTICATING = 2;
const CHOOSING_REPO = 3;
const UPLOADING = 4;

const IndexButtons = (props) =>
{
  const { scenes } = useViewer();
  const { showIndexedScene } = useSceneIndexing();
  const [ index, setIndex ] = createSignal( 1 );
  createEffect( () => {
    showIndexedScene( index(), { camera: props.loadCamera } );
  } );

  const changeScene = delta => evt => setIndex( i => i + delta );

  return (
    <div class="vzome-viewer-buttons">
      <button class="vzome-viewer-prev-button" onClick={ changeScene( -1 ) } disabled={ index() === 1 } >Previous</button>
      <button class="vzome-viewer-next-button" onClick={ changeScene( +1 ) } disabled={ index() === scenes.length-1 } >Next</button>
    </div>
  );
}

const ViewerPreview = ( props ) =>
{
  const normalizedStyle = () =>
    ( props.scenesStyle === 'indexed-camera' )? 'indexed' :
    ( props.scenesStyle === 'zometool' )? 'none' : props.scenesStyle;

  const viewerConfig = () => ({
    allowFullViewport: true,
    showScenes: normalizedStyle(),
  });
  
  let zometoolRef;
  createEffect( () => { if (props.scenesStyle === 'zometool') zometoolRef.textContent = urlViewerCSS + instructionsCSS; } );

  return (
    <SceneIndexingProvider>              
      <Show when={ props.scenesStyle !== 'zometool' } fallback={
        <div style={{ height: '100%' }}>
          <style ref={zometoolRef}></style>
          <ZometoolInstructions style={{ position: 'relative', height: '100%' }} height='100%' width='100%'
              config={ viewerConfig() }/>
        </div>
      } >
        <Show when={ props.scenesStyle === "indexed" } >
          <IndexButtons loadCamera={false}/>
        </Show>
        {/* Best to have two of these, so changing the style reinitializes the index */}
        <Show when={ props.scenesStyle === "indexed-camera" } >
          <IndexButtons loadCamera={true}/>
        </Show>
        <DesignViewer config={ viewerConfig() } style={{ position: 'relative', height: '100%' }} height='100%' width='100%' />
      </Show>
    </SceneIndexingProvider>
  );
}

const ConfigPage = () =>
{
  const { state, setState, sceneIndex } = useEditor();
  const { scenes, setScenes } = useViewer();
  const { state: { camera: mainCamera } } = useCamera();
  const noScenes = () => scenes.length < 2;

  createEffect( () => {
    // console.log( 'ConfigPage created; syncing default scene camera' );
    setScenes( 0, 'camera', unwrap( mainCamera ) );
  } );

  const handleTitleEntered = (event) => {
    setState( 'sharing', 'title', event.target.value );
  }
  const handleDescriptionEntered = (event) => {
    setState( 'sharing', 'description', event.target.value );
  }
  const handleStyleChange = (event) => {
    setState( 'sharing', 'style', event.target.value );
  }

  return (
    <div class="sharing-config">
    <div class="sharing-config-controls">
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
          <MenuItem value='indexed-camera'>Next/Prev Buttons (load-camera)</MenuItem>
          <MenuItem value='titled'>Menu (titled)</MenuItem>
          <MenuItem value='all'>Menu (all)</MenuItem>
          <MenuItem value='zometool'>Zometool Instructions</MenuItem>
          <MenuItem value='none'>None</MenuItem>
        </Select>
      </FormControl>
    </div>
      <fieldset class="viewer-preview">
        <legend><span>viewer preview</span></legend>
        <div class="viewer-preview-inner">
          <CameraProvider>
            <SceneProvider index={ sceneIndex() } passive={true} config={{ preview: true, debug: false, labels: true, source: false }}>
              {/* <ViewerPreview scenesStyle={ state.sharing.style } camera={ copyOfCamera( mainCamera ) } /> */}
              <ViewerPreview scenesStyle={ state.sharing.style } />
            </SceneProvider>
          </CameraProvider>
        </div>
      </fieldset>
    </div>
  );
}

export const SharingDialog = ( props ) =>
{
  const [ open, setOpen ] = createSignal( false );
  const [ target, setTarget ] = createSignal( {} );
  const [ repo, setRepo ] = createSignal( null );
  const [ repos, setRepos ] = createSignal( [] );
  const [ error, setError ] = createSignal( '' );
  const [ stage, setStage ] = createSignal( CONFIGURING );
  const [ disabled, setDisabled ] = createSignal( true );
  const [ blog, setBlog ] = createSignal( false );
  const [ publish, setPublish ] = createSignal( false );

  const { shareToGitHub } = useEditor();

  const TARGET_KEY = 'classic-github-target-details';

  const handleClickOpen = () => {
    setError( '' );
    suspendMenuKeyEvents();
    setDisabled( false );
    setStage( CONFIGURING );
    setOpen( true );
    return;
  };

  const handleCancel = () => {
    setOpen( false );
    resumeMenuKeyEvents();
  };
  
  const nextState = () =>
  {
    if ( stage()===CONFIGURING ) {
      const target = localStorage .getItem( TARGET_KEY );
      if ( !!target ) {
        setStage( CHOOSING_REPO );
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
      return;
    }
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
      <Dialog open={open()} onClose={handleCancel} aria-labelledby="form-dialog-title" maxWidth='lg' fullWidth={false}>
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
              <FormControl sx={{ 'padding-block': '20px', m: 1, minWidth: 290 }} >
                <FormGroup aria-label="blog" row>
                  <FormControlLabel
                    control={<Checkbox checked={blog()} onChange={ () => setBlog( v => !v ) } />}
                    label="Create blog post"
                  />
                  <FormControlLabel
                    control={<Checkbox disabled={!blog()} checked={publish()} onChange={ () => setPublish( v => !v ) } />}
                    label="Publish immediately"
                  />
                </FormGroup>
              </FormControl>
              <DialogContentText>
                Select a GitHub repository:
              </DialogContentText>
              <FormControl sx={{ 'padding-block': '20px', m: 1, minWidth: 290 }} size="medium" >
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
              <ConfigPage/>
            </Match>
          </Switch>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCancel} color="secondary">
            Cancel
          </Button>
          <Button disabled={disabled() || error()} onClick={nextState} color="primary">
            { stage()===AUTHENTICATING? 'Authenticate' : stage()===CONFIGURING? 'Confirm' : 'Share' }
          </Button>
        </DialogActions>
      </Dialog>
    </>
  )
} 


