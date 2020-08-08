import React from 'react';
import './App.css';

import ModelCanvas from './components/modelcanvas-three.js';
import FileOpener from './components/fileopener.js';
import Spinner from './components/spinner.js';

function App() {
  return (
    <div>
      <ModelCanvas/>
      <header className="App-header">
        <h1 className="App-title">vZome Online Viewer (beta)</h1>
      </header>
      <FileOpener/>
      <Spinner/>
      {/* Icons made by <a href="https://www.flaticon.com/authors/kirill-kazachek" title="Kirill Kazachek">Kirill Kazachek</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a> */}
    </div>
  );
}

export default App;
