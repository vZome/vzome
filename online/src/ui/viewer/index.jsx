
import React, { useEffect, useState } from 'react';
import { Provider, useDispatch, useSelector } from 'react-redux';
import ReactDOM from "react-dom";

import { StylesProvider, jssPreset } from '@material-ui/styles';
import IconButton from '@material-ui/core/IconButton'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'
import { create } from 'jss';

import { ShapedGeometry } from './geometry.jsx'
import { DesignCanvas } from './designcanvas.jsx'
import { createWorkerStore } from './store.js';
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

export const DesignViewer = ( { children, children3d, useSpinner=false } ) =>
{
  const source = useSelector( state => state.source );
  const scene = useSelector( state => state.scene );
  const waiting = useSelector( state => !!state.waiting );
  return (
    <div style={ { display: 'flex', height: '100%', position: 'relative' } }>
      { scene?
        <DesignCanvas {...scene} >
          { scene.shapes &&
            <ShapedGeometry embedding={scene.embedding} shapes={scene.shapes} />
          }
          {children3d}
        </DesignCanvas>
        : children // This renders the light DOM if the scene couldn't load
      }
      <Spinner visible={useSpinner && waiting} />
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

export const renderViewer = ( store, container, url ) =>
{
  if ( url === null || url === "" ) {
    ReactDOM.unmountComponentAtNode( container );
    return null;
  }

  // The URL was prefetched, so we don't pass it here.
  // Note the addition of a slot child element; this lets us potentially render the light dom,
  //   for example if the worker cannot load.
  // LG: Can we handle canvas resizing using `ResizeObserver` without modifying `vZome` or recreating the element constantly?
  const viewerElement = React.createElement( UrlViewer, { store }, (<slot></slot>) );

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

export const useVZomeUrl = ( url, config ) =>
{
  const { preview } = config;
  const report = useDispatch();
  useEffect( () => !!url && report( { type: 'URL_PROVIDED', payload: { url, viewOnly: preview } } ), [] );
}

// This component has to be separate from UrlViewer because of the useDispatch hook used in
//  useVZomeUrl above.  I shouldn't really need to export it, but React (or the dev tools)
//  got pissy when I didn't.
export const UrlViewerInner = ({ url, children }) =>
{
  useVZomeUrl( url, { preview: true } );
  return ( <DesignViewer>
             {children}
           </DesignViewer> );
}

// This is the component to reuse in a React app rather than the web component.
//  In that context, the store property can be null, since the WorkerContext
//  will create a worker-store when none is injected.
//  It is also used by the web component, but with the worker-store injected so that the
//  worker can get initialized and loaded while the main context is still fetching
//  this module.
export const UrlViewer = ({ url, store, children }) => (
  <WorkerContext store={store} >
    <UrlViewerInner url={url}>
      {children}
    </UrlViewerInner>
  </WorkerContext>
)
