import React from 'react'
import { connect } from 'redux-bundler-react'

import { useResource } from 'react-three-fiber';

import Ball from './ball'
import Plane from './plane'

function Scene( {points} ) {
  const [geometryRef, geometry] = useResource()
  const [materialRef, material] = useResource()
  return (
    <>
      <Plane/>

      <dodecahedronBufferGeometry ref={geometryRef} args={[0.4, 0]} />
      <meshNormalMaterial ref={materialRef} />

      { points.map( point => (
        <Ball geom={geometry} material={material} position={point} />
      ))}
    </>
  );
};

// export default connect(
//   'selectPoints',
//   // 'selectWorkingPlaneEnabled',
//   // 'doToggleWorkingPlane',
//   Scene
// )
export default Scene
