
import React from 'react';
import PureRenderMixin from 'react/lib/ReactComponentWithPureRenderMixin';
import { connect } from 'react-redux'
import React3 from 'react-three-renderer';
import * as THREE from 'three';
import TrackballControls from './trackball';

class Geometry extends React.Component {

  constructor(props, context) {
    super(props, context);

    // construct these THREE objects here, because if we use 'new' within render,
    // React will think that things have changed when they have not.

    this.vertices = [];
		this.props.vertices.map( vertex => ( this.vertices.push( new THREE.Vector3( vertex.x, vertex.y, vertex.z ) ) ) );

		this.faces = [];
		this.props.faces.map( face => {
			let normal = new THREE.Vector3( face.normal.x, face.normal.y, face.normal.z );
			this.faces.push( new THREE.Face3( face.vertices[0], face.vertices[1], face.vertices[2], normal ) );
			return null;
		} );

		this.id = this.props.id;
  }

  shouldComponentUpdate = PureRenderMixin.shouldComponentUpdate;
  
  render() {
    return (
			<geometry
				resourceId={this.id}
				vertices={this.vertices}
				faces={this.faces}
			/>
    )
  }
}

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
    return (<group
      position={this.start}
      quaternion={this.props.rotation}
    >
      <mesh ref={this._ref} >
        <geometryResource
          resourceId={this.props.shape}
        />
        <meshLambertMaterial
          color={this.props.color}
        />
      </mesh>
    </group>);
  }
}

class Ball extends React.Component {

  constructor(props, context) {
    super(props, context);

    this.center = new THREE.Vector3( this.props.center.x, this.props.center.y, this.props.center.z );

    // construct these vectors here, because if we use 'new' within render,
    // React will think that things have changed when they have not.
  }

  shouldComponentUpdate = PureRenderMixin.shouldComponentUpdate;
  
  render() {
    return (<group
      position={this.center}
    >
      <mesh ref={this._ref} >
        <geometryResource
          resourceId={this.props.shape}
        />
        <meshLambertMaterial
          color={this.props.color}
        />
      </mesh>
    </group>);
  }
}

class ModelCanvasThree extends React.Component {
  
  constructor(props, context) {
    super(props, context);

    this.state = {
      cameraPosition: new THREE.Vector3(0, 0, 65),
      cameraRotation: new THREE.Euler()
    };

    this.lightPosition = new THREE.Vector3(0, 500, 2000);
    this.lightTarget = new THREE.Vector3(0, 0, 0);
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
        <resources>
					{
						this.props.shapes.map( shape =>
							<Geometry key={shape.id} id={shape.id} vertices={shape.vertices} faces={shape.faces} />
						)
					}
        </resources>
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
          <ambientLight
            color={0x808080}
          />
          <directionalLight
            color={0xffffff}
            intensity={0.5}
            position={cameraPosition}
            lookAt={this.lightTarget}
          />
					{
						this.props.segments.map( segment =>
							<Strut key={segment.id} start={segment.start} end={segment.end} color={segment.color} rotation={segment.rotation} shape={segment.shape} />
						)
					}
					{
						this.props.balls.map( ball =>
							<Ball key={ball.id} center={ball.center} color={ball.color} shape={ball.shape} />
						)
					}
				</scene>
			</React3>
		)
  }
}

const mapStateToProps = (state) => ({
  shapes: state.shapes,
  segments: state.segments,
  balls: state.balls
})

const ModelCanvas = connect(mapStateToProps)(ModelCanvasThree)

export default ModelCanvas
