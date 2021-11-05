import { useMemo, useState, useEffect, useRef } from 'react'
import { Matrix4 } from 'three'

console.log( `import.meta.url = ${import.meta.url}` );

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

const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]

const convertPreview = preview =>
{
  let i = 0
  let { lights, camera, shapes, instances, embedding, orientations } = preview
  instances = instances.map( ({ position, orientation, color, shape }) => {
    const id = "id_" + i++
    const { x, y, z } = position
    const rotation = orientations[ orientation ] || IDENTITY_MATRIX
    return { id, position: [ x, y, z ], rotation, color, shapeId: shape }
  })
  const dlights = lights.directionalLights.map( ({ direction, color }) => {
    const { x, y, z } = direction
    return { direction: [ x, y, z ], color }
  })
  const { lookAtPoint, upDirection, lookDirection, viewDistance, fieldOfView, near, far } = camera
  const lookAt = [ ...Object.values( lookAtPoint ) ]
  const up = [ ...Object.values( upDirection ) ]
  const lookDir = [ ...Object.values( lookDirection ) ]
  camera = {
    near, far, up, lookAt,
    fov: fieldOfView,
    position: lookAt.map( (e,i) => e - viewDistance * lookDir[ i ] ),
  }
  return { lighting: { ...lights, directionalLights: dlights }, camera, shapes, instances, embedding }
}

export const fetchPreview = async ( url ) =>
{
  const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" );
  const previewText = await fetchUrlText( previewUrl );
  return convertPreview( JSON.parse( previewText ) );
}

export const fetchScene = async ( url, text ) =>
{
  try {
    return await fetchPreview( url );
  } catch (error) {
    const legacy = await import( '../legacy/dynamic.js' );
    return await legacy.parseAndRender( text ); // TODO run this in a worker!
  }
}

export const useVZomeUrl = ( url, defaultScene ) =>
{
  const [ scene, setScene ] = useState( defaultScene )
  const [ text, setText ] = useState( null )
  useEffect( () => {
    async function parseUrl() {
      try {
        const text = await fetchUrlText( url );
        if ( ! url.endsWith( ".vZome" ) ) {
          // This is the only case in which we don't resolve the promise with text,
          //  since there is no point in allowing download of non-vZome text.
          alert( `Unrecognized file name: ${url}` );
          return;
        }
        setText( text )
        const scene = await fetchScene( url, text );
        setScene( scene )
      } catch (error) {
        console.log( error.message );
        alert( error.message )  // TODO: these alerts should just be rejections?  or state?
      }
    }
    parseUrl()
  }, [ url ] )
  // We have text if we could find the vZome file,
  //  and a scene, either because there was a 3D preview JSON next to it,
  //  or because we parsed, interpreted, and rendered the vZome file.
  return { text, scene }
}

export const useInstanceSorter = ( shapes, instances ) =>
{
  const filterInstances = ( shape, instances ) => instances.filter( instance => instance.shapeId === shape.id )
  const sortByShape = () => Object.values( shapes ).map( shape => ( { shape, instances: filterInstances( shape, instances ) } ) )
  return useMemo( sortByShape, [ shapes, instances ] )
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
