import React from 'react';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

import { createStore, applyMiddleware } from 'redux'
import { Provider } from 'react-redux'
import { render } from 'react-dom'
import reducer from './reducers'
import logger from 'redux-logger'
import { createSimpleWebSocketMiddleware } from "redux-simple-websocket"

const store = createStore( reducer,
								applyMiddleware( createSimpleWebSocketMiddleware(), logger ) );

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
