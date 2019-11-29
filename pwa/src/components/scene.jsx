import React from 'react'
import { useResource } from 'react-three-fiber'

import Ball from './ball'

function Scene( {points, ballClick} ) {
  const [geometryRef, geometry] = useResource()
  const [materialRef, material] = useResource()
  return (
    <group>
      <boxBufferGeometry ref={geometryRef} args={[0.5,0.5,0.5]} />
      <meshNormalMaterial ref={materialRef} />

      { Object.keys(points).map( key => (
        <Ball key={key} geom={geometry} material={material} position={points[key]} onClick={ballClick}/>
      ))}
    </group>
  )
}

export default Scene
