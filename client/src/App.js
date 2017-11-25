// Started from create-react-app,
//   then added in https://github.com/mehmetkose/react-websocket/example

import React from 'react';
import Websocket from './websocket.js';
import logo from './logo.svg';
import './App.css';

class VZomeViewer extends React.Component {
  
  constructor(props) {
    super(props);
    this.state = {
      fileUrl: "",
      socketUrl: ""
    };
    
    this.openWebSocket = this.openWebSocket.bind(this);
    this.closeWebSocket = this.closeWebSocket.bind(this);
    this.handleUrlTextChange = this.handleUrlTextChange.bind(this);
  }
  
  handleUrlTextChange(e) {
    this.setState({
      fileUrl: e.target.value,
      socketUrl: ""
    });
  }

  openWebSocket(url) {
    this.setState({
      socketUrl: "ws://192.168.1.100:8532/vZome?" + encodeURIComponent( url )
    });
  }
  
  closeWebSocket() {
    this.setState({
      fileUrl: "",
      socketUrl: ""
    })
  }
  
  handleData(data) {
    console.log( data );
  }

  handleOpen()  {
    alert("connected:)");
  }
  
  handleClose() {
    alert("disconnected:(");
  }
  
  render() {
    
    const view = this.state.socketUrl ?
      <Websocket url={this.state.socketUrl} onMessage={this.handleData}
        onOpen={this.handleOpen} onClose={this.handleClose}
        reconnect={true} debug={true}
        ref={Websocket => {
          this.refWebSocket = Websocket;
        }}/>:
      <div/>
    const openEnabled = this.state.fileUrl && !this.state.socketUrl;

    return (
      <div className="vZome-viewer">
        <form>
          <input
            type="text"
            placeholder="vZome URL..."
            value={this.state.fileUrl}
            disabled={this.state.socketUrl}
            onChange={this.handleUrlTextChange}
          />
        </form>
        <button onClick={() => this.openWebSocket(this.state.fileUrl)} disabled={!openEnabled} >Open</button>
        <button onClick={() => this.closeWebSocket()} disabled={!this.state.socketUrl} >Close</button>
        {view}
      </div>
    )
  }
}

class App extends React.Component {

  render() {
  
//     var vZomeURL = "http://vzome.com/models/2007/07-Jul/affine120-bop/purpleBlueOrange-affine120cell.vZome";
//     var URL = "ws://192.168.1.100:8532/vZome?" + encodeURIComponent( vZomeURL );

    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to vZome-React</h1>
        </header>
        <VZomeViewer/>
      </div>
    );
  }
}

export default App;
