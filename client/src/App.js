import React from 'react';
import './App.css';

// import ModelUrlControl from './containers/modelurlcontrol.js';
import ModelCanvas from './containers/modelcanvas-three.js';
import EditButtons from './containers/editbuttons.js';
import FileOpener from './containers/fileopener.js';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1 className="App-title">vZome Online Viewer</h1>
      </header>
      {/* <ModelUrlControl/> */}
      <ModelCanvas/>
      <EditButtons/>
      <FileOpener/>
    </div>
  );
}

export default App;
