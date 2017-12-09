
import React from 'react';

export default class ModelCanvas extends React.Component {

  componentDidMount() {
    console.log( "ModelCanvas mounted" );
    const ctx = this.refs.canvas.getContext("2d");
    ctx.strokeStyle = 'red';
    ctx.strokeRect(0, 0, this.props.width, this.props.height);
  }
  
  renderEvent(segment) {
    console.log( "segment: " + JSON.stringify(segment) );
    const ctx = this.refs.canvas.getContext("2d");
    const center = { x: this.props.width/2, y: this.props.height/2 };
    ctx.strokeStyle = segment.color;
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
