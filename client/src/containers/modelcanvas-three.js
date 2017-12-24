
import React from 'react';
import { connect } from 'react-redux'
import React3 from 'react-three-renderer';
import * as THREE from 'three';

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
      <line>
        <geometry vertices={ [ this.start, this.end ] } />
        <lineBasicMaterial linewidth={6} color={this.props.color} />
      </line>
    )
  }
}

class ModelCanvasThree extends React.Component {
  
  constructor(props, context) {
    super(props, context);

    // construct the position vector here, because if we use 'new' within render,
    // React will think that things have changed when they have not.
    this.cameraPosition = new THREE.Vector3(0, 0, 65);
  }

  render() {    
		return (
			this.props.enabled?
				<React3
					mainCamera="camera" // this points to the perspectiveCamera below
					antialias
					width={this.props.width}
					height={this.props.height} >
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
							this.props.segments.map( segment =>
								<Strut key={segment.id} start={segment.start} end={segment.end} color={segment.color} />
							)
						}
					</scene>
				</React3>
			:
				<div/>
		)
  }
}

const mapStateToProps = (state) => ({
  enabled: state.connectionLive,
  segments: state.segments
})

const ModelCanvas = connect(mapStateToProps)(ModelCanvasThree)

export default ModelCanvas
