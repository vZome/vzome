import React, {Component} from 'react'
import {render} from 'react-dom'

import DesignCanvas from '../../src/designcanvas'
import rawDesign from './models/dodecahedron'

const design = {
  ...rawDesign,
  instances: rawDesign.instances.map( instance => ({
    ...instance,
    shapeId: instance.shape,
    position: Object.values( instance.position ),
    rotation: instance.rotation && Object.values( instance.rotation ),
  }))
}

const aspectRatio = window.innerWidth / window.innerHeight
const convertFOV = (fovX) => ( fovX / aspectRatio ) * 180 / Math.PI  // converting radians to degrees

const defaultLighting = {
  backgroundColor: '#BBDAED',
  ambientColor: '#292929',
  directionalLights: [ // These are the vZome defaults, for consistency
    { direction: [ 1, -1, -1 ], color: '#EBEBE4' },
    { direction: [ -1, 0, 0 ], color: '#E4E4EB' },
    { direction: [ 0, 0, -1 ], color: '#1E1E1E' }
  ]
}

const logoInitialCamera = {  // These values match the camera in the default vZomeLogo model file
  fov: convertFOV( 0.442 ),
  position: [ -23.6819, 12.3843, -46.8956 ],
  lookAt: [ 0, -3.4270, 5.5450 ],
  up: [ -0.8263, 0.3136, 0.4677 ],
  far: 119.34,
  near: 0.1491,
}

export const defaultInitialCamera = {
  fov: convertFOV( 0.75 ), // 0.44 in vZome
  position: [ 0, 0, 75 ],
  lookAt: [ 0, 0, 0 ],
  up: [ 0, 1, 0 ],
  far: 217.46,
  near: 0.271,
}

const viewerStyle = {
  height: "400px",
  minHeight: "400px",
  maxHeight: "60vh",
  marginLeft: "15%",
  marginRight: "15%",
  marginTop: "15px",
  marginBottom: "15px",
  borderWidth: "medium",
  borderRadius: "10px",
  border: "solid",
}

export default class Demo extends Component
{
  render() {
    return <div>
      <div style={viewerStyle}>
        <DesignCanvas
          preResolved={design}
          lighting={defaultLighting}
          camera={defaultInitialCamera} />
      </div>
    </div>
  }
}

render(<Demo/>, document.querySelector('#demo'))
