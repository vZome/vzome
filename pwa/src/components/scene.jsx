import React from 'react'
import { useResource } from 'react-three-fiber'

import Ball from './ball'
import Strut from './strut'

function Scene( {points, segments, ballClick} ) {
  const [geometryRef, geometry] = useResource()
  const [materialRef, material] = useResource()
  return (
    <group>
      <boxBufferGeometry ref={geometryRef} args={[0.5,0.5,0.5]} />
      <meshNormalMaterial ref={materialRef} />

      { Object.keys(points).map( key => (
        <Ball key={key} geom={geometry} material={material} position={points[key]} onClick={ballClick}/>
      ))}
      { Object.keys(segments).map( key => (
        <Strut key={key} material={material} {...segments[key]} />
      ))}
    </group>
  )
}

export default Scene
