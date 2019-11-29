import React, { useRef } from 'react'
import { useFrame } from 'react-three-fiber';

function Ball( { onClick, geom, material, position=[0,0,0] } ) {
  const ref = useRef()
  useFrame(() => (ref.current.rotation.x = ref.current.rotation.y += 0.01))
  return (
    <mesh
      ref={ref}
      position={position}
      geometry={geom}
      material={material}
      onClick={onClick}
      onPointerOver={e => console.log('hover')}
      onPointerOut={e => console.log('unhover')}>
    </mesh>
  )
}

export default Ball
