// Started from create-react-app,
//   then added in https://github.com/mehmetkose/react-websocket/example

import React from 'react';
import Websocket from './websocket.js';
import logo from './logo.svg';
import './App.css';

class ServerConnection  extends React.Component {
  
  constructor(props) {
    super(props);
    this.handleClose = this.handleClose.bind(this);
    this.handleOpen = this.handleOpen.bind(this);
    this.handleData = this.handleData.bind(this);
  }
  
  handleOpen() {
    this.props.onOpen();
  }

  handleData(data) {
    this.props.onMessage( data );
  }

  handleClose() {
    this.props.onClose();
  }

  render() {
  
    const socketUrl = "ws://192.168.1.100:8532/vZome?" + encodeURIComponent( this.props.url )
    return (
      <div>
        <Websocket url={socketUrl} onMessage={this.handleData}
          onOpen={this.handleOpen} onClose={this.handleClose}
          reconnect={true} debug={true}
          ref={Websocket => {
            this.refWebSocket = Websocket;
          }}/>
        <button onClick={() => this.handleClose()} >Close</button>
      </div>
    )
  }
}

class ModelCanvas extends React.Component {

  componentDidMount() {
    console.log( "ModelCanvas mounted" );
    const ctx = this.refs.canvas.getContext("2d");
    ctx.strokeStyle = 'red';
    ctx.strokeRect(0, 0, this.props.width, this.props.height);
  }
  
  renderSegment(segment, style) {
    console.log( "segment: " + JSON.stringify(segment) );
    const ctx = this.refs.canvas.getContext("2d");
    const center = { x: this.props.width/2, y: this.props.height/2 };
    ctx.strokeStyle = style;
    ctx.beginPath();
    ctx.moveTo( this.props.scale*segment.start.x + center.x, this.props.scale*segment.start.y + center.y );
    ctx.lineTo( this.props.scale*segment.end.x + center.x, this.props.scale*segment.end.y + center.y );
    ctx.stroke();
  }
  
  render() {
    return(
      <canvas ref="canvas" width={this.props.width} height={this.props.height} />
    )
  }
}

class ModelUrlControl extends React.Component {
  
  constructor(props) {
    super(props);
    this.state = {
      urlText: this.props.url
    };
    
    this.handleUrlTextChange = this.handleUrlTextChange.bind(this);
    this.handleOpen = this.handleOpen.bind(this);
  }
  
  handleUrlTextChange(e) {
    this.setState({
      urlText: e.target.value
    });
  }

  handleOpen() {
    this.props.onOpen(this.state.urlText);
  }

  render() {
    return (
      <div>
        <form>
          <input
            type="text" width="300" placeholder="vZome model URL..."
            value={this.state.urlText}
            disabled={!this.props.enabled}
            onChange={this.handleUrlTextChange}
          />
          <button onClick={() => this.handleOpen()} disabled={!this.props.enabled} >Open</button>
        </form>
      </div>
    )
  }
}

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
      this.refs.display.renderSegment( parsed,'green');
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
