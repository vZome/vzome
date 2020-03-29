import React from 'react'
import ReactDOM from 'react-dom'
import App from './App'
import { Provider } from 'redux-bundler-react'
import getStore from './bundles'

ReactDOM.render(
  <Provider store={getStore()}>
    <App />
  </Provider>,
  document.getElementById('root')
);
