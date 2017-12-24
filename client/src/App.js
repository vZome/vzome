import React from 'react'
import ModelUrlControl from './containers/modelurlcontrol.js';
import ModelCanvas from './containers/modelcanvas-three.js';
import EditButtons from './containers/editbuttons.js';
import logo from './logo.svg';
import './App.css';

const App = () => (
  <div className="App">
    <header className="App-header">
      <img src={logo} className="App-logo" alt="logo" />
      <h1 className="App-title">Welcome to vZome-React</h1>
    </header>
    <ModelUrlControl/>
    <ModelCanvas width={650} height={400} scale={5}/>
    <EditButtons/>
  </div>
)

export default App
