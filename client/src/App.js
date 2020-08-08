import React from 'react';
import './App.css';

import ModelCanvas from './components/modelcanvas-three.js';
import FileOpener from './components/fileopener.js';
import Exporter from './components/exporter.js';
import Spinner from './components/spinner.js';

function App() {
  return (
    <div>
      <ModelCanvas/>
      <header className="App-header">
        <h1>vZome Online Viewer (beta)</h1>
      </header>
      <FileOpener/>
      <Exporter/>
      <Spinner/>
      {/* <div>Export icon made from <a href="http://www.onlinewebfonts.com/icon">Icon Fonts</a> is licensed by CC BY 3.0</div> */}
      {/* <a href="https://github.com/mhnpd/react-loader-spinner">react-loader-spinner</a> */}
      {/* <a href="http://simpleicon.com/folder-2.html">folder icon</a> */}
    </div>
  );
}

export default App;
