import React from 'react';
import { render } from 'react-dom'
// import * as serviceWorker from './serviceWorker';
import {
  BrowserRouter as Router,
  Switch,
  Route,
} from "react-router-dom"

import App from './App.jsx';
import Article from './Article.jsx';
import BHallBasic from './BHallBasic.jsx';

render(
  <Router>
    <Switch>
      <Route path="/app/fivecell">
        <Article />
      </Route>
      <Route path="/app/bhall/basic">
        <BHallBasic />
      </Route>
      <Route path="/">
        <App />
      </Route>
    </Switch>
  </Router>,
  document.getElementById( 'root' )
)

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
// serviceWorker.unregister();
