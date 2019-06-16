import React from 'react'
import { render } from 'react-dom'
import { createStore, applyMiddleware } from 'redux'
import { Provider } from 'react-redux'
import logger from 'redux-logger'
import { createSimpleWebSocketMiddleware } from "redux-simple-websocket"
import './index.css';
import App from './App';
import reducer from './reducers'

const store = createStore( reducer,
								applyMiddleware( createSimpleWebSocketMiddleware(), logger ) );

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
)
