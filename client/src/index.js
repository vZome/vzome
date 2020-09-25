import React from 'react';
import { Provider } from 'react-redux'
import { render } from 'react-dom'
import * as serviceWorker from './serviceWorker';

import logger from 'redux-logger'
import thunk from 'redux-thunk'

import './index.css';
import App from './App';
import createBundleStore from './bundles'

const store = createBundleStore( [ thunk ] );

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById( 'root' )
)

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
