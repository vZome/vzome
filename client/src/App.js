// Started from create-react-app,
//   then added in https://github.com/mehmetkose/react-websocket/example

import React, { Component } from 'react';
import Websocket from './websocket.js';
import logo from './logo.svg';
import './App.css';


class App extends Component {

  handleData(data) {
    console.log( data );
  }
  handleOpen()  {
    alert("connected:)");
  }
  handleClose() {
    alert("disconnected:(");
  }

  sendMessage(message){
    this.refWebSocket.sendMessage(message);
  }

  render() {
  
    var vZomeURL = "http://vzome.com/models/2007/07-Jul/affine120-bop/purpleBlueOrange-affine120cell.vZome";
    var URL = "ws://192.168.1.100:8532/vZome?" + encodeURIComponent( vZomeURL );
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to vZome-React</h1>
        </header>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>
        <button onClick={() => this.sendMessage("Hello")} >Send Message</button>
        <Websocket url={URL} onMessage={this.handleData}
                onOpen={this.handleOpen} onClose={this.handleClose}
                reconnect={true} debug={true}
                ref={Websocket => {
                  this.refWebSocket = Websocket;
                }}/>
      </div>
    );
  }
}

export default App;
