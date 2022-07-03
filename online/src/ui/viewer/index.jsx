
import React, { useEffect, useState } from 'react';
import { Provider, useDispatch, useSelector } from 'react-redux';
import ReactDOM from "react-dom";

import { makeStyles } from '@material-ui/core/styles';
import { StylesProvider, jssPreset } from '@material-ui/styles';
import IconButton from '@material-ui/core/IconButton'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded';
import FullscreenIcon from '@material-ui/icons/Fullscreen';
import FullscreenExitIcon from '@material-ui/icons/FullscreenExit';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import { create } from 'jss';

import { ShapedGeometry } from './geometry.jsx'
import { DesignCanvas } from './designcanvas.jsx'
import { createWorkerStore, defineCamera, fetchDesign, selectEditBefore } from './store.js';
import { Spinner } from './spinner.jsx'
import { ErrorAlert } from './alert.jsx'

// from https://www.bitdegree.org/learn/javascript-download
const download = source =>
{
  const { name, text } = source;
  const blob = new Blob( [ text ], { type : 'application/xml' } );
  const element = document.createElement( 'a' )
  const blobURI = URL.createObjectURL( blob )
  element.setAttribute( 'href', blobURI )
  element.setAttribute( 'download', `${name}` )
  element.style.display = 'none'
  document.body.appendChild( element )
  element.click()
  document.body.removeChild( element )
}

const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
}));

export const SceneMenu = ( { snapshots } ) =>
{
  const classes = useStyles();
  const report = useDispatch();
  const [snapshotIndex, setSnapshotIndex] = React.useState( 0 );
  const handleChange = (event) =>
  {
    const index = event.target.value;
    setSnapshotIndex( index );
    const { nodeId, camera } = snapshots[ index ];
    report( selectEditBefore( nodeId ) );
    report( defineCamera( camera ) );
  }

  return (
    <div style={ { position: 'absolute', background: 'lightgray', top: '0px' } }>
      <FormControl variant="outlined" className={classes.formControl}>
        <InputLabel htmlFor="scene-menu-label">Scene</InputLabel>
        <Select
          native
          value={snapshotIndex}
          onChange={handleChange}
          label="Scene"
          inputProps={{
            name: 'scene',
            id: 'scene-menu-label',
          }}
        >
          {snapshots.map((snapshot, index) => (
            <option key={index} value={index}>{
              snapshot.title || (( index === 0 )? "- none -" : `scene ${index}`)
            }</option>)
          )}
        </Select>
      </FormControl>
    </div>
  );
}

export const DesignViewer = ( { children, children3d, config={} } ) =>
{
  const { showSnapshots=false, useSpinner=false } = config;
  const source = useSelector( state => state.source );
  const scene = useSelector( state => state.scene );
  const waiting = useSelector( state => !!state.waiting );
  const snapshots = useSelector( state => state.snapshots );
  const [ fullScreen, setFullScreen ] = useState( false );
  const normalStyle = { display: 'flex', height: '100%', position: 'relative' };
  const fullScreenStyle = { height: '100%', width: '100%', position: 'fixed', top: '0px', left: '0px', zIndex: '1000' };
  return (
    <div style={ fullScreen? fullScreenStyle : normalStyle }>
      { scene?
        <DesignCanvas {...scene} >
          { scene.shapes &&
            <ShapedGeometry embedding={scene.embedding} shapes={scene.shapes} />
          }
          {children3d}
        </DesignCanvas>
        : children // This renders the light DOM if the scene couldn't load
      }
      { showSnapshots && snapshots && snapshots[1] &&  // There is always >=1 snapshot, and we only want to show 2 or more
        <SceneMenu snapshots={snapshots} />
      }
      <Spinner visible={useSpinner && waiting} />
      <IconButton color="inherit" aria-label="fullscreen"
          style={ { position: 'absolute', bottom: '5px', right: '5px' } }
          onClick={() => setFullScreen(!fullScreen)} >
        { fullScreen? <FullscreenExitIcon fontSize='medium'/> : <FullscreenIcon fontSize='medium'/> }
      </IconButton>
      { source && source.text &&
        <IconButton color="inherit" aria-label="download"
            style={ { position: 'absolute', top: '5px', right: '5px' } }
            onClick={() => download( source ) } >
          <GetAppRoundedIcon fontSize='medium'/>
        </IconButton>
      }
      <ErrorAlert/>
    </div>
  )
}

export const renderViewer = ( store, container, url, config ) =>
{
  if ( url === null || url === "" ) {
    ReactDOM.unmountComponentAtNode( container );
    return null;
  }

  // The URL was prefetched, so we don't pass it here.
  // Note the addition of a slot child element; this lets us potentially render the light dom,
  //   for example if the worker cannot load.
  // LG: Can we handle canvas resizing using `ResizeObserver` without modifying `vZome` or recreating the element constantly?
  const viewerElement = React.createElement( UrlViewer, { store, config }, (<slot></slot>) );

  // We need JSS to inject styles on our shadow root, not on the document head.
  // I found this solution here:
  //   https://stackoverflow.com/questions/51832583/react-components-material-ui-theme-not-scoped-locally-to-shadow-dom
  const jss = create({
      ...jssPreset(),
      insertionPoint: container
  });
  const reactElement = React.createElement( StylesProvider, { jss: jss }, [ viewerElement ] );

  ReactDOM.render( reactElement, container );

  return reactElement;
}

// If the worker-store is not injected (as in the web component), it is created here.
//  This component is used by UrlViewer (below) and by the Online App.
export const WorkerContext = props =>
{
  const [ store ] = useState( props.store || createWorkerStore() );
  return (
    <Provider store={store}>
      {props.children}
    </Provider>
  );
}

export const useVZomeUrl = ( url, preview, forDebugger=false ) =>
{
  const report = useDispatch();
  // TODO: this should be encapsulated in an API on the store
  useEffect( () => !!url && report( fetchDesign( url, preview, forDebugger ) ), [ url ] );
}

// This component has to be separate from UrlViewer because of the useDispatch hook used in
//  useVZomeUrl above.  I shouldn't really need to export it, but React (or the dev tools)
//  got pissy when I didn't.
export const UrlViewerInner = ({ url, children, config }) =>
{
  const { showSnapshots } = config;
  // "preview" means show a preview if you find one.  When "showSnapshots" is true, the
  //   XML will have to be parsed, so a preview JSON is not sufficient.
  useVZomeUrl( url, !showSnapshots );
  return ( <DesignViewer config={config} >
             {children}
           </DesignViewer> );
}

// This is the component to reuse in a React app rather than the web component.
//  In that context, the store property can be null, since the WorkerContext
//  will create a worker-sre when none is injected.
//  It is also used by the web component, but with the worker-store injected so that the
//  worker can get initialized and loaded while the main context is still fetching
//  this module.
export const UrlViewer = ({ url, store, children, config={ showSnapshots: false } }) => (
  <WorkerContext store={store} >
    <UrlViewerInner url={url} config={config}>
      {children}
    </UrlViewerInner>
  </WorkerContext>
)
