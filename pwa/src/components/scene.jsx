import React from 'react'
import { useResource } from 'react-three-fiber'

import Ball from './ball'
import Strut from './strut'

function Scene( { points, segments, ballClick, setFocus, clearFocus, focus } )
{
  const [geometryRef, geometry] = useResource()
  const [materialRef, material] = useResource()
  const [material2Ref, material2] = useResource()
  const atFocus = pos => JSON.stringify(pos) === JSON.stringify(focus)
  const handleClick = ( position ) => {
    if ( ! focus )
      setFocus( position )
    else if ( atFocus( position ) )
      clearFocus()
    else
      ballClick( focus, position )
  }
  return (
    <group>
      <boxBufferGeometry ref={geometryRef} args={[0.5,0.5,0.5]} />
      <meshNormalMaterial ref={materialRef} />
      <meshLambertMaterial ref={material2Ref} emissive={"#ff8888"}/>

      { Object.keys(points).map( key => (
        <Ball key={key} geom={geometry} material={atFocus(points[key])? material2 : material} position={points[key]} onClick={handleClick}/>
      ))}
      { Object.keys(segments).map( key => (
        <Strut key={key} material={material} {...segments[key]} radius={0.1} />
      ))}
    </group>
  )
}

export default Scene
