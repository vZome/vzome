import React from 'react';
import { Provider } from 'react-redux'
import { render } from 'react-dom'
import * as serviceWorker from './serviceWorker';

import { createStore, applyMiddleware, combineReducers } from 'redux'
import logger from 'redux-logger'
import thunk from 'redux-thunk'

import './index.css';
import App from './App';
import * as jre from './bundles/jre'
import * as vzomejava from './bundles/vzomejava'
import * as camera from './bundles/camera'
import * as lighting from './bundles/lighting'
import * as progress from './bundles/progress'

const rootReducer = combineReducers( {
  jre: jre.reducer,
  vzomejava: vzomejava.reducer,
  lighting: lighting.reducer,
  camera: camera.reducer,
  progress: progress.reducer
} )

const store = createStore( rootReducer, applyMiddleware( logger, thunk, vzomejava.middleware ) );

jre.init( window, store )

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
)

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
