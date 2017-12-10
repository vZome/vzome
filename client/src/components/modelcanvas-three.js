
import React from 'react';
import React3 from 'react-three-renderer';
import * as THREE from 'three';
import PropTypes from 'prop-types';

class Strut extends React.Component {

  constructor(props, context) {
    super(props, context);

    this.start = new THREE.Vector3( this.props.start.x, this.props.start.y, this.props.start.z );
    this.end = new THREE.Vector3( this.props.end.x, this.props.end.y, this.props.end.z );

    // construct these vectors here, because if we use 'new' within render,
    // React will think that things have changed when they have not.
  }
  
  render() {
    return (
      <line key="this.props.key">
        <geometry vertices={ [ this.start, this.end ] } />
        <lineBasicMaterial linewidth={6} color={this.props.color} />
      </line>
    )
  }
}

export default class ModelCanvas extends React.Component {

  static defaultProps = { width: 800, height: 600, scale: 5, render: true, segments: {} };
  
  static propTypes = {
    width: PropTypes.number.isRequired,
    height: PropTypes.number.isRequired,
  };

  constructor(props, context) {
    super(props, context);

    this.cameraPosition = new THREE.Vector3(0, 0, 65);

    // construct the position vector here, because if we use 'new' within render,
    // React will think that things have changed when they have not.
  }

  render() {
    console.log( "ModelCanvas render" );

    // or you can use:
    // width = window.innerWidth
    // height = window.innerHeight
    
    return (
      <React3
        mainCamera="camera" // this points to the perspectiveCamera below
        antialias
        width={this.props.width}
        height={this.props.height}
        >
        <scene>
          <perspectiveCamera
            name="camera"
            fov={75}
            aspect={this.props.width / this.props.height}
            near={0.1}
            far={1000}
            position={this.cameraPosition}
          />
          {
            Object.keys( this.props.segments ).map( (key) => {
              const segment = this.props.segments[ key ];
              return ( 
                <Strut key={segment.id} start={segment.start} end={segment.end} color={segment.color} /> )
            } )
          }
        </scene>
      </React3>
    );
  }
}
