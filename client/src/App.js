import React from 'react'
import ModelUrlControl from './containers/modelurlcontrol.js';
import ModelCanvas from './containers/modelcanvas-svg.js';
import logo from './logo.svg';
import './App.css';

const App = () => (
  <div className="App">
    <header className="App-header">
      <img src={logo} className="App-logo" alt="logo" />
      <h1 className="App-title">Welcome to vZome-React</h1>
    </header>
    <ModelUrlControl/>
    <ModelCanvas width={650} height={400} scale={1}/>
  </div>
)

export default App
