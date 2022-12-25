
// babel workaround
import "regenerator-runtime/runtime";

import React, { useReducer } from 'react';
import { render } from 'react-dom'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'

import { VZomeAppBar } from '../components/appbar.jsx'
import { BuildPlane } from './buildplane.jsx'
import { DesignViewer, WorkerContext } from '../../ui/viewer/index.jsx'
import { reducer, initialState, doToggleDisk, doSetCenter, doStrutPreview, doSelectPlane, doSelectHinge, doToggleBuild } from './planes.js';
import { createStrut, joinBalls, newDesign } from '../../ui/viewer/store.js';
import { useEffect } from "react";
import { createWorker } from "../../workerClient/client.js";

const isLeftMouseButton = e =>
{
  e = e || window.event;
  if ( "which" in e )  // Gecko (Firefox), WebKit (Safari/Chrome) & Opera
    return e.which === 1
  else if ( "button" in e )  // IE, Opera 
    return e.button === 0
  return false
}

const worker = createWorker();

const App = () =>
{
  const { sendToWorker, subscribe } = worker;

  // TODO: encapsulate the build plane as a "tool", including a UI
  const [ state, dispatch ] = useReducer( reducer, initialState ); // dedicated local store
  useEffect( () => {
    // Connect the worker store to the local store, to listen to worker events
    subscribe( {
      onWorkerError: error => console.log( error ), // TODO show the user!
      onWorkerMessage: msg => dispatch( msg ),
    } );
    sendToWorker( newDesign() );
  }, [] );

  const ballCallbacks = {
    bkgdClick: ( e ) => isLeftMouseButton( e ) && dispatch( doToggleDisk() ),
    onClick: ( id, position, type, selected ) => {
      if ( type === 'ball' ) {
        if ( state.endPt ) {
          sendToWorker( joinBalls( state.center.id, id ) );
        }
        dispatch( doSetCenter( id, position ) );
      }
    },
    onHover: ( id, position, type, starting ) => {
      if ( starting && state.buildingStruts && state.center ?.position ) {
        if ( type === 'ball' ) {
          const [ x0, y0, z0 ] = state.center.position;
          const [ x1, y1, z1 ] = position;
          const vector = [ x1-x0, y1-y0, z1-z0 ];
          dispatch( doStrutPreview( vector ) );
        }
      } else
        dispatch( doStrutPreview() );
    },
  }
  const actions = {
    createStrut: ( plane, zone, index, orientation ) => sendToWorker( createStrut( state.center.id, plane, zone, index, orientation ) ),
    previewStrut: endPt => dispatch( doStrutPreview( endPt ) ),
    changePlane: ( name, orientation ) => dispatch( doSelectPlane( name, orientation ) ),
    changeHinge: ( name, orientation ) => dispatch( doSelectHinge( name, orientation ) ),
    toggleBuild: endPt => dispatch( doToggleBuild( endPt ) ),
  }

  return (
    <>
      <VZomeAppBar oneDesign={false} pathToRoot='..' forDebugger={false} title='vZome Online'
        about={ <>
          <Typography gutterBottom>
            This is an experimental web-based modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>,
            based on <Link target="_blank" href="https://vzome.com" rel="noopener" >vZome</Link> technology.
          </Typography>
        </> }
      />
      <DesignViewer config={ { useSpinner: true, undoRedo: true } } callbacks={ballCallbacks}
        children3d={ state.buildPlanes && state.enabled && state.center &&
          <BuildPlane {...{ state }} actions={actions} />
        } />
    </>
  );
}

const WorkerApp = () => (
  <WorkerContext worker={worker} >
    <App/>
  </WorkerContext>
);

render( <WorkerApp/>, document.getElementById( 'root' ) );
