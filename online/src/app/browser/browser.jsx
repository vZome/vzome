
import React, { useState } from 'react'
import { useDispatch } from 'react-redux';
import { fetchDesign } from '../../ui/viewer/store.js';
import { useGitHubShares, getEmbeddingHtml, getAssetUrl } from './github.js';

import { DesignViewer } from '../../ui/viewer/index.jsx'

import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Typography from '@material-ui/core/Typography'
import Button from '@material-ui/core/Button';

const queryParams = new URLSearchParams( window.location.search );
const githubUser = queryParams.get( 'user' ) || "vorth";

const DesignList = ( { setUrl } ) =>
{
  const designs = useGitHubShares( githubUser );
  const [ selectedIndex, setSelectedIndex ] = React.useState( 0 );

  const handleListItemClick = (url, path, index) =>
  {
    setSelectedIndex( index );
    setUrl( url, path );
  };

  return (
    <div style={{ width: '100%', height: '100%', position: 'relative' }}>
      <List dense component="nav" aria-label="vzome designs" style={{ overflow: 'auto', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }} >
      { designs.map( ( { title, details, url, path }, i ) => (
        <ListItem key={i}
          button
          selected={selectedIndex === i}
          onClick={() => handleListItemClick( url, path, i )}
        >
          <ListItemText primary={title} secondary={details} />
        </ListItem>
      ))}
      </List>
    </div>
  );
}

const DesignActions = ( { url, path } ) =>
{
  const copyHtml = path =>
  {
    const html = getEmbeddingHtml( githubUser, path );
    navigator.clipboard.writeText( html ) .then( () => {
      console.log( `HTML copied to the clipboard: ${url}` );
    }, () => {
      console.log( `HTML copy FAILED: ${url}` );
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

  if ( url ) {
    return (
      <div style={{ display: 'flex', gap: '1rem', margin: '12px', justifyContent: 'space-evenly' }}>
        <Button variant="contained" color="primary" onClick={() => copyHtml( path )}>
          Copy Embeddable HTML
        </Button>
        <Button variant="contained" color="secondary" onClick={() => copyUrl( url )}>
          Copy Raw vZome URL
        </Button>
        <Button variant="contained" target="_blank" rel="noopener" href={ getAssetUrl( githubUser, path ) }>
          Show GitHub Assets
        </Button>
      </div>
    );
  } else {
    return (
      <Typography variant='h6' gutterBottom style={{ textAlign: 'center', marginTop: '13px' }}>
        <em>Select any design from the list on the left</em>
      </Typography>
    );
  }
}

export const DesignBrowser = ( { debug } ) =>
{
  const report = useDispatch();
  const [ url, setUrl ] = useState( null );
  const [ path, setPath ] = useState( null );
  const selectUrl = ( url, path ) =>
  {
    report( fetchDesign( url, true ) );
    setUrl( url );
    setPath( path );
  }

  return (
    <div id='github-browser' style={{ display: 'grid', gridTemplateColumns: '20% 80%', height: '100%' }}>
      <DesignList setUrl={selectUrl}/>
      <div id='github-browser' style={{ display: 'grid', gridTemplateRows: 'min-content 1fr' }}>
        <div id='details' style={{ minHeight: '60px', borderBottom: '1px solid gray', backgroundColor: 'whitesmoke' }}>
          <DesignActions url={url} path={path} />
        </div>
        <DesignViewer config={ { useSpinner: true } } />
      </div>
    </div>
  )
}
