import React from 'react'
import { connect } from 'react-redux'
import ModelUrlControl from './containers/modelurlcontrol.js';
import ModelCanvas from './containers/modelcanvas-three.js';
import EditButtons from './containers/editbuttons.js';
import logo from './logo.svg';
import './App.css';

let App = ({ enabled }) => (
  <div className="App">
    <header className="App-header">
      <img src={logo} className="App-logo" alt="logo" />
      <h1 className="App-title">Welcome to vZome-React</h1>
    </header>
    <ModelUrlControl/>
		{ enabled? <ModelCanvas width={950} height={500} scale={5}/> : <div/> }
    <EditButtons/>
  </div>
)

const mapStateToProps = (state) => ({
  enabled: state.connectionLive
})

App = connect(mapStateToProps)(App)

export default App
