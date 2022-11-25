
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
import { reducer, initialState, doBackgroundClick, doBallClick } from './planes.js';
import { createStrut } from '../../ui/viewer/store.js';

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
  const doCreateStrut = ( plane, zone, index ) => sendToWorker( createStrut( state.focusId, plane, zone, index ) );
  const buildPlanes = useSelector( reduxState => reduxState.buildPlanes ); // from the main Redux store

  // TODO: encapsulate the build plane as a "tool", including a UI
  const [ state, dispatch ] = useReducer( reducer, initialState ); // dedicated local store

  const designCallbacks = {
    bkgdClick: ( e ) => isLeftMouseButton( e ) && dispatch( doBackgroundClick() ),
    onClick: ( id, position, selected ) => dispatch( doBallClick( id, position ) ),
    onHover: ( id, position, value ) => { },
  }

  return (
    <>
      <VZomeAppBar oneDesign={true} forDebugger={false} title='vZome Online'
        about={ <>
          <Typography gutterBottom>
            This is an experimental in-browser modeling tool
            for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link>.
          </Typography>
        </> }
      />
      <DesignViewer config={ { useSpinner: true } } callbacks={designCallbacks}
        children3d={ buildPlanes && state.enabled &&
          <BuildPlane {...{ buildPlanes, state }} createStrut={doCreateStrut} />
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
