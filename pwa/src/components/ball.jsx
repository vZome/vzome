import React from 'react'

function Ball( { geom, material, position=[0,0,0] } ) {
  return (
    <mesh
      position={position}
      geometry={geom}
      material={material}
      onClick={e => console.log('click')}
      onPointerOver={e => console.log('hover')}
      onPointerOut={e => console.log('unhover')}>
    </mesh>
  )
}

export default Ball
