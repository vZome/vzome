import React from 'react';
import './App.css';

// import ModelUrlControl from './containers/modelurlcontrol.js';
import ModelCanvas from './containers/modelcanvas-three.js';
import FileOpener from './containers/fileopener.js';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1 className="App-title">vZome Online Viewer (beta)</h1>
      </header>
      <ModelCanvas/>
      <FileOpener/>
    </div>
  );
}

export default App;
