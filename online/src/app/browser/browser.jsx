
import React from 'react'
import { useDispatch } from 'react-redux';
import { fetchDesign } from '../../ui/viewer/store.js';
import { useGitHubShares } from './github.js';

import { DesignViewer } from '../../ui/viewer/index.jsx'

import Grid from '@material-ui/core/Grid'
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import IconButton from '@material-ui/core/IconButton';
import CodeIcon from '@material-ui/icons/Code';
import LinkIcon from '@material-ui/icons/Link';

const queryParams = new URLSearchParams( window.location.search );
const githubUser = queryParams.get( 'user' ) || "vorth";

const DesignList = ( { setUrl } ) =>
{
  const designs = useGitHubShares( githubUser );
  const [ selectedIndex, setSelectedIndex ] = React.useState( 0 );

  const handleListItemClick = (url, index) =>
  {
    setSelectedIndex( index );
    setUrl( url );
  };

  const copyUrl = url =>
  {
    navigator.clipboard.writeText( url ) .then( () => {
      console.log( `URL copied to the clipboard: ${url}` );
    }, () => {
      console.log( `URL copy FAILED: ${url}` );
    });
  }

  return (
    <div style={{ width: '100%', height: '100%', position: 'relative' }}>
      <List dense component="nav" aria-label="vzome designs" style={{ overflow: 'auto', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }} >
      { designs.map( ( { title, details, url }, i ) => (
        <ListItem key={i}
          button
          selected={selectedIndex === i}
          onClick={() => handleListItemClick( url, i )}
        >
          {/* <ListItemSecondaryAction>
            <IconButton edge="start" aria-label="get-html">
              <CodeIcon />
            </IconButton>
          </ListItemSecondaryAction> */}

          <ListItemText primary={title} secondary={details} />

          <ListItemSecondaryAction>
            <IconButton edge="end" aria-label="get-url" onClick={() => copyUrl( url )}>
              <LinkIcon />
            </IconButton>
          </ListItemSecondaryAction>
        </ListItem>
      ))}
      </List>
    </div>
  );
}

export const DesignBrowser = ( { debug } ) =>
{
  const report = useDispatch();
  const setUrl = url => report( fetchDesign( url, true ) );

  const drawerColumns = 2;
  const canvasColumns = 12 - drawerColumns;

  return (
    <div style={{ flex: '1', height: '100%' }}>
      <Grid id='editor-main' container spacing={0} style={{ height: '100%' }}>        
        <Grid id='editor-drawer' item xs={drawerColumns}>
          <DesignList setUrl={setUrl}/>
        </Grid>
        <Grid id='editor-canvas' item xs={canvasColumns} >
          <DesignViewer config={ { useSpinner: true } } />
        </Grid>
      </Grid>
    </div>
  )
}
