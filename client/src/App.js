import React from 'react';
import './App.css';

import ModelCanvas from './components/modelcanvas-three.js';
import FileOpener from './components/fileopener.js';

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
