
import { useState, useEffect, useRef } from 'react'
import { Matrix4 } from 'three'

export const fetchUrlText = async ( url ) =>
{
  let response
  try {
    response = await fetch( url )
  } catch ( error ) {
    console.log( `Fetching ${url} failed with "${error}"; trying cors-anywhere` )
    // TODO: I should really deploy my own copy of this proxy on Heroku
    response = await fetch( 'https://cors-anywhere.herokuapp.com/' + url )
  }
  if ( !response.ok ) {
    throw new Error( `Failed to fetch "${url}": ${response.statusText}` )
  }
  return response.text()
}

export const useVZomeDesign = ( design ) =>
{
  const [ scene, setScene ] = useState( null )
  const [ source, setSource ] = useState( null )
  useEffect( () => {
    design.connectListeners( setSource, setScene );
  }, [] )
  // We have text if we could find the vZome file,
  //  and a scene, either because there was a 3D preview JSON next to it,
  //  or because we parsed, interpreted, and rendered the vZome file.
  return { source, scene }
}

export const useEmbedding = embedding =>
{
  const ref = useRef()
  useEffect( () => {
    if ( embedding && ref.current && ref.current.matrix ) {
      const m = new Matrix4()
      m.set( ...embedding )
      m.transpose()
      ref.current.matrix.identity()  // Required, or applyMatrix4() changes will accumulate
      // This imperative approach is required because I was unable to decompose the
      //   embedding matrix (a shear) into a scale and rotation.
      ref.current.applyMatrix4( m )
    }
  }, [embedding] )
  return ref
}
