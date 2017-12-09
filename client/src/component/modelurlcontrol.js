
import React from 'react';

const ModelUrlForm = ({ value, enabled, onTextChange, onOpen }) =>
  <div>
    <form>
      <input
        type="text" width="400" placeholder="vZome model URL..."
        value={value} disabled={!enabled} onChange={onTextChange}
      />
      <button onClick={onOpen} disabled={!enabled} >Open</button>
    </form>
  </div>

export default class ModelUrlControl extends React.Component {
  
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
      <ModelUrlForm value={this.state.urlText} enabled={this.props.enabled}
        onTextChange={this.handleUrlTextChange} onOpen={this.handleOpen} />
    )
  }
}
