
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
import { reducer, initialState, doStartGridHover, doStopGridHover } from './planes.js';
import { createStrut } from '../../ui/viewer/store.js';

const App = () =>
{
  useNewDesign(); // has to be nested here, since it needs Redux context

  const [ state, dispatch ] = useReducer( reducer, initialState ); // dedicated local store
  const buildPlanes = useSelector( reduxState => reduxState.buildPlanes ); // from the main Redux store
  const startGridHover = (pt) => dispatch( doStartGridHover( pt ) );
  const stopGridHover = (pt) => dispatch( doStopGridHover( pt ) );

  const report = useDispatch();
  const doCreateStrut = ( origin, plane, zone, index ) => report( createStrut( origin, plane, zone, index ) );

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
      <DesignViewer config={ { useSpinner: true } }
        children3d={ buildPlanes &&
          <BuildPlane {...{ buildPlanes, state, startGridHover, stopGridHover }} createStrut={doCreateStrut} />
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
