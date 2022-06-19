
import React, { useEffect } from 'react'
import { useDispatch } from 'react-redux';
import { fetchDesign } from '../../ui/viewer/store.js';

import { DesignViewer } from '../../ui/viewer/index.jsx'

import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid'
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';

const useStyles = makeStyles((theme) => ({
  root: {
    width: '100%',
    maxWidth: 360,
    position: 'relative',
    overflow: 'auto'
  },
}));

const queryParams = new URLSearchParams( window.location.search );
const githubUser = queryParams.get( 'user' ) || "vorth";

const BASE_URL = `https://raw.githubusercontent.com/${githubUser}/vzome-sharing/main/`;

const DesignList = ( { setUrl } ) =>
{
  const classes = useStyles();
  const [ designs, setDesigns ] = React.useState( [] );
  const [ selectedIndex, setSelectedIndex ] = React.useState( 0 );

  useEffect( () => {
    fetch( `https://api.github.com/repos/${githubUser}/vzome-sharing/git/trees/main?recursive=1` )
      .then( response => response.json() )
      .then( json => {
        const allPaths = json.tree.filter( entry => entry.type==="blob" && entry.path.endsWith( '.vZome' ) )
          .filter( entry => entry.path > "2021/07/31" )
          .map( entry => entry.path );
        console.log( 'Repo has', allPaths.length, 'entries.' );
        setDesigns( allPaths );
      } );
  }, [] );

  const handleListItemClick = (event, index) => {
    setSelectedIndex(index);
    const path = designs[ index ].split( '/' ).map( encodeURIComponent ).join( '/' );
    setUrl( BASE_URL + path );
  };

  return (
    <div style={{ width: '100%', height: '100%', position: 'relative' }}>
      <List component="nav" aria-label="vzome designs" style={{ overflow: 'auto', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }} >
      { designs.map( ( path, i ) => (
        <ListItem key={i}
          button
          selected={selectedIndex === i}
          onClick={(event) => handleListItemClick( event, i )}
        >
          <ListItemText primary={path} />
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

  const drawerColumns = 5;
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
