
import { For, Show, Switch, Match, createEffect, createMemo, createResource, createSignal } from 'solid-js';

import { reconcile } from 'solid-js/store';

import { fetchGitHubShares, getEmbeddingHtml, getAssetUrl } from './github.js';
import { useViewer } from '../../viewer/context/viewer.jsx';
import { useScene } from '../../viewer/context/scene.jsx';

import { DesignViewer } from '../../viewer/index.jsx'

import List from '@suid/material/List';
import ListItemButton from '@suid/material/ListItemButton';
import ListItemText from '@suid/material/ListItemText';
import Typography from '@suid/material/Typography'
import Button from '@suid/material/Button';
import IconButton from '@suid/material/IconButton';
import Menu from '@suid/material/Menu';
import MenuItem from '@suid/material/MenuItem';
import MoreVert from '@suid/icons-material/MoreVert';
import { UsersMenu } from './users.jsx';

const MONTH_NAMES = [ 'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December' ];

// Groups dated designs (gists and repo "dated" entries) into year > month sections,
// most recent first, and nonstandard repo paths into an "Other" section that mirrors
// their folder hierarchy, sorted alphabetically.
const groupDesigns = designs =>
{
  const years = new Map(); // year -> month -> items[]
  const otherRoot = { folders: new Map(), items: [] };

  for ( const design of designs ) {
    if ( design.kind === 'other' ) {
      // tokens: [ ...folderNames, filename ]
      const folderTokens = design.tokens .slice( 0, -1 );
      let node = otherRoot;
      for ( const folder of folderTokens ) {
        if ( ! node.folders .has( folder ) )
          node.folders .set( folder, { folders: new Map(), items: [] } );
        node = node.folders .get( folder );
      }
      node.items .push( design );
    }
    else {
      const year = design.date .getFullYear();
      const month = design.date .getMonth(); // 0-11
      if ( ! years .has( year ) )
        years .set( year, new Map() );
      const months = years .get( year );
      if ( ! months .has( month ) )
        months .set( month, [] );
      months .get( month ) .push( design );
    }
  }

  const sortByDateDesc = items => [ ...items ] .sort( ( a, b ) => b.date - a.date );

  const yearSections = [ ...years .entries() ]
    .sort( ( [ y1 ], [ y2 ] ) => y2 - y1 )
    .map( ( [ year, months ] ) => ({
      year,
      months: [ ...months .entries() ]
        .sort( ( [ m1 ], [ m2 ] ) => m2 - m1 )
        .map( ( [ month, items ] ) => ({ month, items: sortByDateDesc( items ) }) ),
    }) );

  const sortFolder = node => ({
    folders: [ ...node.folders .entries() ]
      .sort( ( [ n1 ], [ n2 ] ) => n1 .localeCompare( n2 ) )
      .map( ( [ name, child ] ) => ({ name, ...sortFolder( child ) }) ),
    items: [ ...node.items ] .sort( ( a, b ) => a.title .localeCompare( b.title ) ),
  });

  return { yearSections, other: sortFolder( otherRoot ) };
}

const Section = (props) =>
{
  return (
    <details open={ !! props.startExpanded } style={{ 'padding-left': '8px' }}>
      <summary style={{ cursor: 'pointer', 'font-weight': props.folder ? 'normal' : 'bold', padding: '4px 0' }}>
        { props.label }
      </summary>
      <div style={{ 'padding-left': '16px' }}>
        { props.children }
      </div>
    </details>
  );
}

const DesignItem = (props) =>
{
  const { title, details, url, path } = props.design;
  return (
    <ListItemButton
      dense
      selected={props.selectedUrl === url}
      onClick={() => props.onSelect( url, path )}
    >
      <ListItemText primary={title} secondary={details} />
    </ListItemButton>
  );
}

const FolderSection = (props) =>
{
  return (
    <>
      <For each={ props.node.folders }>{ folder =>
        <Section label={ folder.name } folder>
          <FolderSection node={ folder } selectedUrl={props.selectedUrl} onSelect={props.onSelect} />
        </Section>
      }</For>
      <For each={ props.node.items }>{ design =>
        <DesignItem design={ design } selectedUrl={props.selectedUrl} onSelect={props.onSelect} />
      }</For>
    </>
  );
}

const DesignList = (props) =>
{
  const [ selectedUrl, setSelectedUrl ] = createSignal( null );

  const handleSelect = (url, path) =>
  {
    setSelectedUrl( url );
    props.setUrl( url, path );
  };

  const grouped = createMemo( () => groupDesigns( props.designs || [] ) );

  const rateLimitMessage = () =>
  {
    const reset = props.resetAt;
    const when = reset ? ` Try again after ${ reset .toLocaleTimeString() }.` : '';
    return `GitHub API rate limit reached.${when}`;
  }

  return (
    <div style={{ width: '100%', height: '100%', position: 'relative' }}>
      <Switch fallback={
        <List dense component="nav" aria-label="vzome designs" style={{ overflow: 'auto', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }} >
          <For each={ grouped().yearSections }>{ ( { year, months }, yi ) =>
            <Section label={ year } startExpanded={ yi() === 0 }>
              <For each={ months }>{ ( { month, items }, mi ) =>
                <Section label={ MONTH_NAMES[ month ] } startExpanded={ yi() === 0 && mi() === 0 }>
                  <For each={ items }>{ design =>
                    <DesignItem design={ design } selectedUrl={selectedUrl()} onSelect={handleSelect} />
                  }</For>
                </Section>
              }</For>
            </Section>
          }</For>
          <Show when={ grouped().other.folders.length > 0 || grouped().other.items.length > 0 }>
            <Section label="Other">
              <FolderSection node={ grouped().other } selectedUrl={selectedUrl()} onSelect={handleSelect} />
            </Section>
          </Show>
        </List>
      }>
        <Match when={ props.rateLimited }>
          <Typography variant='body1' color='error' style={{ 'text-align': 'center', 'margin-top': '13px', padding: '0 12px' }}>
            <em>{ rateLimitMessage() }</em>
          </Typography>
        </Match>
        <Match when={ props.loading }>
          <Typography variant='body1' style={{ 'text-align': 'center', 'margin-top': '13px' }}>
            <em>indexing designs...</em>
          </Typography>
        </Match>
      </Switch>
    </div>
  );
}

const classicURL = new URL( '../classic/index.html', window.location ) .toString() + "?design=";

const DesignActions = (props) =>
{
  const copyHtml = ( path, url ) =>
  {
    const html = getEmbeddingHtml( props.githubUser, path, url );
    navigator.clipboard.writeText( html ) .then( () => {
      console.log( `HTML copied to the clipboard: ${props.url}` );
    }, () => {
      console.log( `HTML copy FAILED: ${props.url}` );
    });
  }

  const copyUrl = url =>
  {
    navigator.clipboard.writeText( url ) .then( () => {
      console.log( `URL copied to the clipboard: ${url}` );
    }, () => {
      console.log( `URL copy FAILED: ${url}` );
    });
  }

  const isGist = () => !! props.path && props.path .startsWith( 'https://gist.github.com/' );

  // Shared action descriptors, rendered as buttons on desktop and as menu items on mobile.
  const actions = () => {
    if ( ! props.path || ! props.url )
      return [];
    return [
      { label: 'Copy Embeddable HTML', color: 'primary', onClick: () => copyHtml( props.path, props.url ) },
      { label: 'Copy Raw vZome URL', color: 'secondary', onClick: () => copyUrl( props.url ) },
      isGist()
        ? { label: 'Show Gist', color: 'primary', href: props.path }
        : { label: 'Show GitHub Assets', color: 'primary', href: getAssetUrl( props.githubUser, props.path ) },
      { label: 'Open in vZome online', color: 'secondary', href: classicURL + props.url },
    ];
  };

  const [ menuAnchor, setMenuAnchor ] = createSignal( null );

  const runAction = action =>
  {
    setMenuAnchor( null );
    if ( action.onClick )
      action.onClick();
  }

  return (
    <Show when={ props.url } fallback={
      <Typography class='design-select-prompt' variant='h6' gutterBottom style={{ 'text-align': 'center', 'margin-top': '13px' }}>
        <em>Select any design from the list on the left</em>
      </Typography>
    }>
      <div class='design-actions-desktop' style={{ gap: '1rem', margin: '12px', 'justify-content': 'space-evenly' }}>
        <For each={ actions() }>{ action =>
          <Button variant="contained" color={ action.color } onClick={ action.onClick } target={ action.href && "_blank" } rel={ action.href && "noopener" } href={ action.href }>
            { action.label }
          </Button>
        }</For>
      </div>
      <div class='design-actions-mobile' style={{ 'justify-content': 'center' }}>
        <IconButton aria-label="design actions" size="small" style={{ padding: '4px' }} onClick={ e => setMenuAnchor( e.currentTarget ) }>
          <MoreVert />
        </IconButton>
        <Menu anchorEl={ menuAnchor() } open={ !! menuAnchor() } onClose={ () => setMenuAnchor( null ) }>
          <For each={ actions() }>{ action =>
            <MenuItem onClick={ () => runAction( action ) } target={ action.href && "_blank" } rel={ action.href && "noopener" } href={ action.href } component={ action.href ? "a" : "li" }>
              { action.label }
            </MenuItem>
          }</For>
        </Menu>
      </div>
    </Show>
  );
}

// Invariant:
//   - storedUsers in MRU order
//   - storedUsers are unique

const filterUniqueUsers = users =>
{
  const uniqueUsers = new Set();
  return users .filter( user => {
    const lower = user .toLowerCase();
    return ! uniqueUsers .has( lower ) && uniqueUsers .add( lower ) && true;
  });
}

const queryParams = new URLSearchParams( window.location.search );
const defaultGithubUser = queryParams.get( 'user' ) ?.toLowerCase() || localStorage.getItem( 'vzome-github-user' ) || "vorth";
console.log( "defaultGithubUser ", defaultGithubUser );
const storedUsers = JSON.parse( localStorage.getItem( 'vzome-github-users' ) || '[ "david-hall", "john-kostick", "thynstyx", "vorth" ]' );
let knownUsers = filterUniqueUsers( [ defaultGithubUser, ...storedUsers ] );

export const DesignBrowser = () =>
{
  const { requestDesign, resetScenes } = useViewer();
  const { setScene } = useScene();
  const clearViewer = () => setScene( reconcile( {} ) );
  const [ url, setUrl ] = createSignal( null );
  const [ path, setPath ] = createSignal( null );
  const selectUrl = ( newUrl, path ) =>
  {
    if ( newUrl === url() )
      return;
    resetScenes();
    clearViewer();
    requestDesign( newUrl, { preview: true } );
    setPath( path );
    setUrl( newUrl );
  }
  const [ githubUser, setGithubUser ] = createSignal( defaultGithubUser );
  const [ options, setOptions ] = createSignal( [ ...knownUsers ] );

  const selectGithubUser = newUser =>
  {
    resetScenes();
    clearViewer();
    setPath( null );
    setUrl( null );
    setGithubUser( newUser );
  }

  const persistKnownUsers = () =>
  {
    const value = JSON.stringify( knownUsers );
    localStorage .setItem( 'vzome-github-users', value );
    setOptions( [ ...knownUsers ] );
  }

  const addUser = newUser =>
  {
    // explicit adds go at the end, unlike the MRU reordering that happens on successful load
    knownUsers = filterUniqueUsers( [ ...knownUsers, newUser ] );
    persistKnownUsers();
  }

  const removeUser = user =>
  {
    knownUsers = knownUsers .filter( u => u .toLowerCase() !== user .toLowerCase() );
    persistKnownUsers();
    if ( githubUser() .toLowerCase() === user .toLowerCase() )
      selectGithubUser( knownUsers[0] || defaultGithubUser );
  }

  // const handleCreate = (inputValue) =>
  // {
  //   const newOption = createOption( inputValue );
  //   setGithubUser( newOption );
  //   // This will trigger an attempt to load designs, below
  // };

  const [ designs ] = createResource( githubUser, fetchGitHubShares );

  createEffect( () =>
  {
    if ( designs() ?.designs ?.length > 0 ) {
      // current githubUser is a valid one
      const validUser = githubUser() .toLowerCase();
      localStorage .setItem( 'vzome-github-user', validUser );
      // prepend it to the list (MRU), then filter for uniqueness, preserving order
      knownUsers = filterUniqueUsers( [ validUser, ...knownUsers ] );
      persistKnownUsers();
    }
    else
      setUrl( null );
  });

  return (
    <div class='design-browser'>
      <div id='users-designs' style={{ display: 'grid', 'grid-template-rows': 'min-content 1fr' }}>
        <UsersMenu users={options()} currentUser={githubUser()} setUser={selectGithubUser} onAddUser={addUser} onRemoveUser={removeUser} />
        <DesignList githubUser={githubUser()} designs={designs() ?.designs} loading={designs.loading} rateLimited={designs() ?.rateLimited} resetAt={designs() ?.resetAt} setUrl={selectUrl}/>
      </div>
      {/* This 'min-content 1fr' triggers an infinite loop involving the solid-three ResizeObserver,
           unless DesignViewer and LightedTrackballCanvas both have height=100%, display=flex, and overflow=hidden. */}
      <div class='design-viewer-panel' style={{ display: 'grid', 'grid-template-rows': 'min-content 1fr' }}>
        <div id='details' style={{ 'border-bottom': '1px solid gray', 'background-color': 'whitesmoke' }}>
          <DesignActions githubUser={githubUser()} url={url()} path={path()} />
        </div>
        <div class='relative-h100'>
          <div class='absolute-0'>
            <DesignViewer config={ { useSpinner: true, showScenes: 'all' } } style={{ position: 'relative', height: '100%' }} height='100%' width='100%' />
          </div>
        </div>
      </div>
    </div>
  )
}
