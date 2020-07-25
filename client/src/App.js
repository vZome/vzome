import React from 'react';
import logo from './logo.svg';
import './App.css';

// import ModelUrlControl from './containers/modelurlcontrol.js';
import ModelCanvas from './containers/modelcanvas-three.js';
import EditButtons from './containers/editbuttons.js';
import FileOpener from './containers/fileopener.js';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <h1 className="App-title">Welcome to vZome-React</h1>
      </header>
      {/* <ModelUrlControl/> */}
      <ModelCanvas/>
      <EditButtons/>
      <FileOpener/>
    </div>
  );
}

export default App;
