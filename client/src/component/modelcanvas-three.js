
import React from 'react';
import React3 from 'react-three-renderer';
import * as THREE from 'three';
import PropTypes from 'prop-types';

export default class ModelCanvas extends React.Component {

  static defaultProps = { width: 800, height: 600 };

  renderSegment(segment) {
    console.log( "segment: " + JSON.stringify(segment) );
    const renderable = {
      color: segment.color,
      endpoints: [
        new THREE.Vector3( segment.start.x, segment.start.y, segment.start.z ),
        new THREE.Vector3( segment.end.x, segment.end.y, segment.end.z )
      ]
    };
    this.setState({ 
      segments: this.state.segments.concat( [ renderable ] )
    })
  }
  
  static propTypes = {
    width: PropTypes.number.isRequired,
    height: PropTypes.number.isRequired,
  };

  constructor(props, context) {
    super(props, context);

    this.cameraPosition = new THREE.Vector3(0, 0, 65);

    // construct the position vector here, because if we use 'new' within render,
    // React will think that things have changed when they have not.

    this.state = {
      segments: []
    };
  }

  render() {
    const {
      width,
      height,
    } = this.props;

    // or you can use:
    // width = window.innerWidth
    // height = window.innerHeight
    
    const lines = this.state.segments.map((segment, i) =>
          <line>
            <geometry vertices={segment.endpoints} />
            <lineBasicMaterial linewidth={6} color={segment.color} />
          </line>
        )

    return (<React3
      mainCamera="camera" // this points to the perspectiveCamera below
      width={width}
      height={height}

//       onAnimate={this._onAnimate}
    >
      <scene>
        <perspectiveCamera
          name="camera"
          fov={75}
          aspect={width / height}
          near={0.1}
          far={1000}

          position={this.cameraPosition}
        />
        {lines}
      </scene>
    </React3>);
  }
}
