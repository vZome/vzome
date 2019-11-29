import React from 'react'
import { useResource } from 'react-three-fiber'

import Ball from './ball'

function Scene( {points, ballClick} ) {
  const [geometryRef, geometry] = useResource()
  const [materialRef, material] = useResource()
  return (
    <group>
      <dodecahedronBufferGeometry ref={geometryRef} args={[0.4, 0]} />
      <meshNormalMaterial ref={materialRef} />

      { points.map( point => (
        <Ball geom={geometry} material={material} position={point} onClick={ballClick}/>
      ))}
    </group>
  )
}

export default Scene
