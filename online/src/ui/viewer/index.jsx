
import { REVISION } from '../../revision.js'
export { REVISION } from '../../revision.js'

console.log( `vzome-viewer revision ${REVISION}` );

import React, { useEffect, useRef, useState } from 'react';
import { Provider, useDispatch, useSelector } from 'react-redux';
import { createRoot } from 'react-dom/client';

import { StylesProvider, jssPreset } from '@material-ui/styles';
import IconButton from '@material-ui/core/IconButton'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded';
import FullscreenIcon from '@material-ui/icons/Fullscreen';
import FullscreenExitIcon from '@material-ui/icons/FullscreenExit';
import OpenInBrowserIcon from '@material-ui/icons/OpenInBrowser'
import SettingsIcon from '@material-ui/icons/Settings'
import Link from '@material-ui/core/Link';
import { create } from 'jss';

import { ShapedGeometry } from './geometry.jsx'
import { DesignCanvas } from './designcanvas.jsx'
import { createWorkerStore, defineCamera, fetchDesign, selectEditBefore, whilePerspective } from './store.js';
import { Spinner } from './spinner.jsx'
import { ErrorAlert } from './alert.jsx'
import { SettingsDialog } from './settings.jsx';
import { useVR } from './hooks.js';
import { SceneMenu } from './scenes.jsx';

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

const encodeUrl = url => url .split( '/' ) .map( encodeURIComponent ) .join( '/' );

const normalStyle = {
  display: 'flex',       // flex is for the light dom content, usually an image
  height: '100%',
  width: '100%',
  position: 'relative',
  overflow: 'hidden',    // curiously, this forces Canvas to recompute its size when changing back
};

const fullScreenStyle = {
  height: '100%',
  width: '100%',
  position: 'fixed',
  top: '0px',
  left: '0px',
  zIndex: '1300',
};

export const DesignViewer = ( { children, children3d, config={} } ) =>
{
  const { showScenes=false, useSpinner=false, allowFullViewport=false } = config;
  const source = useSelector( state => state.source );
  const scene = useSelector( state => state.scene );
  const waiting = useSelector( state => !!state.waiting );

  const [ fullScreen, setFullScreen ] = useState( false );
  const [ showSettings, setShowSettings ] = useState( false );
  const vrAvailable = useVR();
  const containerRef = useRef();

  const report = useDispatch();
  const sceneCamera = useSelector( state => state.scene && state.scene.camera );
  const syncCamera = camera => report( { type: 'TRACKBALL_MOVED', payload: camera } );

  const toggleFullScreen = () =>
  {
    const { perspective } = sceneCamera || {};
    // This is a complete hack to work around the issue with resize in OrthographicCamera.
    //  We simply use a thunk to switch to a perspective camera before we toggle fullScreen.
    //  Note that this does NOT help with window resize.
    report( whilePerspective( perspective, () => setFullScreen( !fullScreen ) ) );
  }

  return (
    <div ref={containerRef} style={ fullScreen? fullScreenStyle : normalStyle }>
      { scene?
        <DesignCanvas lighting={scene.lighting} sceneCamera={sceneCamera} syncCamera={syncCamera} >
          { scene.shapes &&
            <ShapedGeometry embedding={scene.embedding} shapes={scene.shapes} />
          }
          {children3d}
        </DesignCanvas>
        : children // This renders the light DOM if the scene couldn't load
      }

      { showScenes && <SceneMenu/> }

      <Spinner visible={useSpinner && waiting} />

      { allowFullViewport &&
        <IconButton color="inherit" aria-label="fullscreen"
            style={ { position: 'absolute', bottom: '5px', right: '5px' } }
            onClick={toggleFullScreen} >
          { fullScreen? <FullscreenExitIcon fontSize='large'/> : <FullscreenIcon fontSize='large'/> }
        </IconButton>
      }
      <IconButton color="inherit" aria-label="settings"
          style={ { position: 'absolute', top: '5px', right: '5px' } }
          onClick={() => setShowSettings(!showSettings)} >
        <SettingsIcon fontSize='large'/>
      </IconButton>

      <SettingsDialog {...{ showSettings, setShowSettings }} container={containerRef.current} />

      {/* I'm using the legacy preview mode just for me, really, when viewing on my Oculus Quest.
          This lets me enjoy designs from web pages that include many viewers, which normally
          limit you to one VR experience.  The preview viewer will open in a new tab, where I can enter VR.
        */}
      { vrAvailable && source && source.url &&
        <IconButton color="inherit" aria-label="preview"
          style={ { position: 'absolute', top: '45px', right: '5px' } }
          component={Link}
          href={`https://www.vzome.com/app/?url=${encodeUrl(source.url)}`} target="_blank" rel="noopener"
        >
          <OpenInBrowserIcon fontSize='large'/>
        </IconButton>
      }

      { source && source.text &&
        <IconButton color="inherit" aria-label="download"
            style={ { position: 'absolute', bottom: '5px', left: '5px' } }
            onClick={() => download( source ) } >
          <GetAppRoundedIcon fontSize='large'/>
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

  const root = createRoot( container );
  root.render( reactElement );

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

export const useVZomeUrl = ( url, config={ debug: false } ) =>
{
  const report = useDispatch();
  // TODO: this should be encapsulated in an API on the store
  useEffect( () =>
  {
    if ( !!url ) 
      report( fetchDesign( url, config ) );
  }, [ url ] );
}

// This component has to be separate from UrlViewer because of the useDispatch hook used in
//  useVZomeUrl above.  I shouldn't really need to export it, but React (or the dev tools)
//  got pissy when I didn't.
export const UrlViewerInner = ({ url, children, config }) =>
{
  useVZomeUrl( url, config );
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
export const UrlViewer = ({ url, store, children, config={ showScenes: false } }) => (
  <WorkerContext store={store} >
    <UrlViewerInner url={url} config={ { ...config, allowFullViewport: true } }>
      {children}
    </UrlViewerInner>
  </WorkerContext>
)
