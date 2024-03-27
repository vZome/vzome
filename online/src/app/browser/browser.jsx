
import { Show, createEffect, createResource, createSignal } from 'solid-js';

import { fetchGitHubShares, getEmbeddingHtml, getAssetUrl } from './github.js';
import { useViewer } from '../../viewer/context/viewer.jsx';

import { DesignViewer } from '../../viewer/index.jsx'

import List from '@suid/material/List';
import ListItem from '@suid/material/ListItem';
import ListItemText from '@suid/material/ListItemText';
import Typography from '@suid/material/Typography'
import Button from '@suid/material/Button';
import { UsersMenu } from './users.jsx';

const DesignList = (props) =>
{
  const [ selectedIndex, setSelectedIndex ] = createSignal( 0 );

  const handleListItemClick = (url, path, index) =>
  {
    setSelectedIndex( index );
    props.setUrl( url, path );
  };

  return (
    <div style={{ width: '100%', height: '100%', position: 'relative' }}>
      <List dense component="nav" aria-label="vzome designs" style={{ overflow: 'auto', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }} >
        <For each={props.designs} >{ ( { title, details, url, path }, i ) =>
          <ListItem button
            selected={selectedIndex() === i}
            onClick={() => handleListItemClick( url, path, i )}
          >
            <ListItemText primary={title} secondary={details} />
          </ListItem>
        }</For>
      </List>
    </div>
  );
}

const classicURL = new URL( '../classic/index.html', window.location ) .toString() + "?design=";

const DesignActions = (props) =>
{
  const copyHtml = path =>
  {
    const html = getEmbeddingHtml( props.githubUser, path );
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

  return (
    <Show when={ props.url } fallback={
      <Typography variant='h6' gutterBottom style={{ 'text-align': 'center', 'margin-top': '13px' }}>
        <em>Select any design from the list on the left</em>
      </Typography>
    }>
      <div style={{ display: 'flex', gap: '1rem', margin: '12px', 'justify-content': 'space-evenly' }}>
        <Button variant="contained" color="primary" onClick={() => copyHtml( props.path )}>
          Copy Embeddable HTML
        </Button>
        <Button variant="contained" color="secondary" onClick={() => copyUrl( props.url )}>
          Copy Raw vZome URL
        </Button>
        <Button variant="contained" target="_blank" rel="noopener" href={ getAssetUrl( props.githubUser, props.path ) }>
          Show GitHub Assets
        </Button>
        <Button variant="contained" color="secondary" target="_blank" rel="noopener" href={ classicURL + props.url }>
          Open in vZome Online
        </Button>
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
const defaultGithubUser = queryParams.get( 'user' ) || localStorage.getItem( 'vzome-github-user' ) || "vorth";
console.log( "defaultGithubUser ", defaultGithubUser );
const storedUsers = JSON.parse( localStorage.getItem( 'vzome-github-users' ) || '[ "david-hall", "john-kostick", "thynstyx", "vorth" ]' );
let knownUsers = filterUniqueUsers( [ defaultGithubUser, ...storedUsers ] );

export const DesignBrowser = () =>
{
  const { fetchPreview } = useViewer();
  const [ url, setUrl ] = createSignal( null );
  const [ path, setPath ] = createSignal( null );
  const selectUrl = ( newUrl, path ) =>
  {
    if ( newUrl === url() )
      return;
    fetchPreview( newUrl, { preview: true } );
    setPath( path );
    setUrl( newUrl );
  }
  const [ githubUser, setGithubUser ] = createSignal( defaultGithubUser );
  const [ options, setOptions ] = createSignal( [ ...knownUsers ] );

  // const handleCreate = (inputValue) =>
  // {
  //   const newOption = createOption( inputValue );
  //   setGithubUser( newOption );
  //   // This will trigger an attempt to load designs, below
  // };

  const [ designs ] = createResource( githubUser, fetchGitHubShares );

  createEffect( () =>
  {
    if ( designs()?.length > 0 ) {
      // current githubUser is a valid one
      const validUser = githubUser() .toLowerCase();
      console.log( "storing vzome-github-user ", validUser );
      localStorage .setItem( 'vzome-github-user', validUser );
      // prepend it to the list
      const newList = [ validUser, ...knownUsers ];
      // then filter the list for uniqueness, preserving the order
      knownUsers = filterUniqueUsers( newList );
      // and finally store it
      const value = JSON.stringify( knownUsers );
      console.log( "storing vzome-github-users ", value );
      localStorage .setItem( 'vzome-github-users', value );
      // and update the UI
      setOptions( [ ...knownUsers ] );
    }
    else
      setUrl( null );
  });

  return (
    <div id='github-browser' style={{ display: 'grid', 'grid-template-columns': '20% 80%', height: '100%' }}>
      <div id='users-designs' style={{ display: 'grid', 'grid-template-rows': 'min-content 1fr' }}>
        <UsersMenu users={options()} currentUser={githubUser()} setUser={setGithubUser} />
        <DesignList githubUser={githubUser()} designs={designs()} setUrl={selectUrl}/>
      </div>
      {/* This 'min-content 1fr' triggers an infinite loop involving the solid-three ResizeObserver,
           unless DesignViewer and LightedTrackballCanvas both have height=100%, display=flex, and overflow=hidden. */}
      <div id='github-browser' style={{ display: 'grid', 'grid-template-rows': 'min-content 1fr' }}>
        <div id='details' style={{ 'min-height': '60px', 'border-bottom': '1px solid gray', 'background-color': 'whitesmoke' }}>
          <DesignActions githubUser={githubUser()} url={url()} path={path()} />
        </div>
        <DesignViewer config={ { useSpinner: true, showScenes: 'all' } } style={{ position: 'relative', height: '100%' }} height='100%' width='100%' />
      </div>
    </div>
  )
}
