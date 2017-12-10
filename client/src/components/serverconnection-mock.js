
import React from 'react';

export default class ServerConnection  extends React.Component {
  
  constructor(props) {
    super(props);
    this.close = this.close.bind(this);
    this.fakeStruts = this.fakeStruts.bind(this);
  }
  
  componentDidMount() {
    this.props.onOpen();
  }

  close() {
    this.props.onClose();
  }

  fakeStruts() {
    this.props.onMessage( JSON.stringify( { render: "segment", color: 'purple', start: { x: -50, y: -50 }, end: { x:300, y:200 }} ) );
    this.props.onMessage( JSON.stringify( { render: "segment", color: 'orange', start: { x: -100, y: 100 }, end: { x:140, y:-90 }} ) );
  }

  render() {
    return (
      <div>
        <div>Server Connection</div>
        <button onClick={() => this.fakeStruts()} >Struts</button>
        <button onClick={() => this.close()} >Close</button>
      </div>
    )
  }
}
