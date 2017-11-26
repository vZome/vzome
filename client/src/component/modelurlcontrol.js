
import React from 'react';

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
