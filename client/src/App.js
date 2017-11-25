// Started from create-react-app,
//   then added in https://github.com/mehmetkose/react-websocket/example

import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import PropTypes from 'prop-types';

class Websocket extends React.Component {

    constructor(props) {
      super(props);
      this.state = {
        ws: new WebSocket(this.props.url, this.props.protocol),
        attempts: 1
      };
    }

    logging(logline) {
      if (this.props.debug === true) {
          console.log(logline);
      }
    }

    generateInterval (k) {
        if(this.props.reconnectIntervalInMilliSeconds > 0) {
            return this.props.reconnectIntervalInMilliSeconds;
        }
        return Math.min(30, (Math.pow(2, k) - 1)) * 1000;
    }

    setupWebsocket() {
      let websocket = this.state.ws;

      websocket.onopen = () => {
        this.logging('Websocket connected');
        if (typeof this.props.onOpen === 'function') this.props.onOpen();
      };

      websocket.onmessage = (evt) => {
        this.props.onMessage(evt.data);
      };

      this.shouldReconnect = this.props.reconnect;
      websocket.onclose = () => {
        this.logging('Websocket disconnected');
        if (typeof this.props.onClose === 'function') this.props.onClose();
        if (this.shouldReconnect) {
          let time = this.generateInterval(this.state.attempts);
          this.timeoutID = setTimeout(() => {
            this.setState({attempts: this.state.attempts+1});
            this.setState({ws: new WebSocket(this.props.url, this.props.protocol)});
            this.setupWebsocket();
          }, time);
        }
      }
    }

    componentDidMount() {
      this.setupWebsocket();
    }

    componentWillUnmount() {
      this.shouldReconnect = false;
      clearTimeout(this.timeoutID);
      let websocket = this.state.ws;
      websocket.close();
    }

    sendMessage(message){
      let websocket = this.state.ws;
      websocket.send(message);
    }

    render() {
      return (
        <div></div>
      );
    }
}

Websocket.defaultProps = {
    debug: false,
    reconnect: true
};

Websocket.propTypes = {
    url: PropTypes.string.isRequired,
    onMessage: PropTypes.func.isRequired,
    onOpen: PropTypes.func,
    onClose: PropTypes.func,
    debug: PropTypes.bool,
    reconnect: PropTypes.bool,
    protocol: PropTypes.string,
    reconnectIntervalInMilliSeconds : PropTypes.number
};


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
