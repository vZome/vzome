
import React from 'react';
import Websocket from './websocket.js';

export default class ServerConnection  extends React.Component {
  
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
