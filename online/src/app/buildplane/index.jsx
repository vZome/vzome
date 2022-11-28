
// babel workaround
import "regenerator-runtime/runtime";

import React, { useReducer } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { render } from 'react-dom'
import Typography from '@material-ui/core/Typography'
import Link from '@material-ui/core/Link'

import { VZomeAppBar } from '../components/appbar.jsx'
import { BuildPlane } from './buildplane.jsx'
import { useNewDesign } from '../classic/controller-hooks.js';
import { DesignViewer, WorkerContext } from '../../ui/viewer/index.jsx'
import { reducer, initialState, doBackgroundClick, doBallClick, doStrutPreview } from './planes.js';
import { createStrut, joinBalls } from '../../ui/viewer/store.js';
import { useEffect } from "react";

const isLeftMouseButton = e =>
{
  e = e || window.event;
  if ( "which" in e )  // Gecko (Firefox), WebKit (Safari/Chrome) & Opera
    return e.which === 1
  else if ( "button" in e )  // IE, Opera 
    return e.button === 0
  return false
}

const App = () =>
{
  useNewDesign(); // has to be nested here, since it needs Redux context
  const sendToWorker = useDispatch();
  const doCreateStrut = ( plane, zone, index ) => sendToWorker( createStrut( state.center.id, plane, zone, index ) );
  const doJoinBalls = ( i1, i2 ) => sendToWorker( joinBalls( i1, i2 ) );
  const buildPlanes = useSelector( reduxState => reduxState.buildPlanes ); // from the main Redux store
  const lastInstance = useSelector( reduxState => reduxState.lastInstance );

  // TODO: encapsulate the build plane as a "tool", including a UI
  const [ state, dispatch ] = useReducer( reducer, initialState ); // dedicated local store
  const previewStrut = endPt => dispatch( doStrutPreview( endPt ) );

  // The last ball created always gets to be the new plane center
  useEffect( () => {
    if ( lastInstance ) {
      const { type, id, position } = lastInstance;
      if ( type === 'ball' )
        dispatch( doBallClick( id, position ) );
    }
  }, [ lastInstance ] );

  const ballCallbacks = {
    bkgdClick: ( e ) => isLeftMouseButton( e ) && dispatch( doBackgroundClick() ),
    onClick: ( id, position, type, selected ) => {
      if ( type === 'ball' ) {
        if ( state.endPt ) {
          doJoinBalls( state.center.id, id );
        }
        dispatch( doBallClick( id, position ) );
      }
    },
    onHover: ( id, position, type, value ) => {
      if ( value && state.buildingStruts && state.center.position ) {
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

  return (
    <>
      <VZomeAppBar oneDesign={true} forDebugger={false} title='vZome Online'
        about={ <>
          <Typography gutterBottom>
            This is an experimental in-browser modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>,
            based on <Link target="_blank" href="https://vzome.com" rel="noopener" >vZome</Link> technology.
          </Typography>
        </> }
      />
      <DesignViewer config={ { useSpinner: true } } callbacks={ballCallbacks}
        children3d={ buildPlanes && state.enabled &&
          <BuildPlane {...{ buildPlanes, state }}  previewStrut={previewStrut} createStrut={doCreateStrut} />
        } />
    </>
  );
}

const WorkerApp = () => (
  <WorkerContext>
    <App/>
  </WorkerContext>
);

render( <WorkerApp/>, document.getElementById( 'root' ) );
