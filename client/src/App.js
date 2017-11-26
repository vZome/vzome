// Started from create-react-app,
//   then added in https://github.com/mehmetkose/react-websocket/example

import React from 'react';
import ServerConnection from './component/serverconnection.js';
import ModelCanvas from './component/modelcanvas.js';
import ModelUrlControl from './component/modelurlcontrol.js';
import logo from './logo.svg';
import './App.css';


class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {
      modelUrl: "",
      connectionLive: false
    };
    this.handleOpen = this.handleOpen.bind(this);
    this.dispatchMessage = this.dispatchMessage.bind(this);
    this.connectionClosed = this.connectionClosed.bind(this);
    this.connectionOpened = this.connectionOpened.bind(this);
  }
  
  handleOpen(url) {
    console.log( "opening " + url );
    this.setState({
      modelUrl: url
    })
  }
  
  dispatchMessage(message) {
    const parsed = JSON.parse(message);
    if ( parsed.render ) {
      this.refs.display.renderSegment( parsed );
    } else {
      console.log( "server info: " + parsed.info );
    }
  }
  
  connectionOpened() {
    console.log( "connection opened." );
    this.setState({
      connectionLive: true
    })
  }
  
  connectionClosed() {
    console.log( "connection closed." );
    this.setState({
      modelUrl: "",
      connectionLive: false
    })
  }

  render() {
  
    const connection = this.state.modelUrl ?
      <ServerConnection url={this.state.modelUrl}
        onOpen={this.connectionOpened}
        onClose={this.connectionClosed}
        onMessage={this.dispatchMessage}/>:
      <div/>
    
    const display = this.state.connectionLive ?
      <ModelCanvas ref="display" scale={8} width={this.props.width} height={this.props.height}/> :
      <div/>

    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to vZome-React</h1>
        </header>
        <ModelUrlControl onOpen={this.handleOpen} url={this.state.modelUrl} enabled={!this.state.modelUrl}/>
        {connection}
        {display}
      </div>
    );
  }
}

export default App;
