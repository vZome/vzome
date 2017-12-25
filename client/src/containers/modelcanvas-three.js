
import React from 'react';
import PureRenderMixin from 'react/lib/ReactComponentWithPureRenderMixin';
import { connect } from 'react-redux'
import React3 from 'react-three-renderer';
import * as THREE from 'three';
import TrackballControls from './trackball';

class Strut extends React.Component {

  constructor(props, context) {
    super(props, context);

    this.start = new THREE.Vector3( this.props.start.x, this.props.start.y, this.props.start.z );
    this.end = new THREE.Vector3( this.props.end.x, this.props.end.y, this.props.end.z );

    // construct these vectors here, because if we use 'new' within render,
    // React will think that things have changed when they have not.
  }

  shouldComponentUpdate = PureRenderMixin.shouldComponentUpdate;
  
  render() {
    return (
      <line>
        <geometry vertices={ [ this.start, this.end ] } />
        <lineBasicMaterial linewidth={6} color={this.props.color} />
      </line>
    )
  }
}

// class Ball extends React.Component {
// 
//   constructor(props, context) {
//     super(props, context);
// 
//     this.center = new THREE.Vector3( this.props.center.x, this.props.center.y, this.props.center.z );
// 
//     // construct these vectors here, because if we use 'new' within render,
//     // React will think that things have changed when they have not.
//   }
// 
//   shouldComponentUpdate = PureRenderMixin.shouldComponentUpdate;
//   
//   render() {
//     return (<group
//       position={this.center}
//     >
//       <mesh ref={this._ref} >
//         <geometryResource
//           resourceId="boxGeometry"
//         />
//         <meshLambertMaterial
//           color={this.props.color}
//         />
//       </mesh>
//     </group>);
//   }
// }

class ModelCanvasThree extends React.Component {
  
  constructor(props, context) {
    super(props, context);

    this.state = {
      cameraPosition: new THREE.Vector3(0, 0, 65),
      cameraRotation: new THREE.Euler()
    };
  }

  componentDidMount() {
    const {
      camera,
    } = this.refs;
    
    const controls = new TrackballControls(camera);

    controls.rotateSpeed = 7.0;
    controls.zoomSpeed = 1.2;
    controls.panSpeed = 0.8;
    controls.noZoom = false;
    controls.noPan = false;
    controls.staticMoving = true;
    controls.dynamicDampingFactor = 0.3;

    this.controls = controls;

    this.controls.addEventListener('change', this._onTrackballChange);
  }

  _onTrackballChange = () => {
    this.setState({
      cameraPosition: this.refs.camera.position.clone(),
      cameraRotation: this.refs.camera.rotation.clone(),
    });
  };

  _onAnimate = () => {
    this._onAnimateInternal();
  };

  _onAnimateInternal() {
    const {
      camera,
    } = this.refs;

    if (this.state.camera !== camera) {
      this.setState({
        camera,
      });
    }

    this.controls.update();
  }

  componentWillUnmount() {
    this.controls.removeEventListener('change', this._onTrackballChange);

    this.controls.dispose();
    delete this.controls;
  }

  render() {

    const {
      cameraPosition,
      cameraRotation,
    } = this.state;

		return (
			<React3
				mainCamera="mainCamera" // this points to the perspectiveCamera below
				antialias
				onAnimate={this._onAnimate}
				width={this.props.width}
				height={this.props.height} >
				<scene>
					<perspectiveCamera
						name="mainCamera"
						ref="camera"
						fov={45}
						aspect={this.props.width / this.props.height}
						near={0.1}
						far={1000}
						position={cameraPosition}
						rotation={cameraRotation}
					/>
					{
						this.props.segments.map( segment =>
							<Strut key={segment.id} start={segment.start} end={segment.end} color={segment.color} />
						)
					}
				</scene>
			</React3>
		)
  }
}
// 						{
// 							this.props.balls.map( ball =>
// 								<Ball key={ball.id} center={ball.center} color={ball.color} />
// 							)
// 						}

const mapStateToProps = (state) => ({
  segments: state.segments,
  balls: state.balls
})

const ModelCanvas = connect(mapStateToProps)(ModelCanvasThree)

export default ModelCanvas
