import { useMemo, useState, useEffect, useRef } from 'react'
import { Matrix4 } from 'three'
import { createInstance } from '../core/adapter.js'
import { parse, interpret, Step } from '../core/api.js'

const aspectRatio = window.innerWidth / window.innerHeight // TODO: this is totally static!
const convertFOV = (fovX) => ( fovX / aspectRatio ) * 180 / Math.PI  // converting radians to degrees

const originShown = field =>
{
  const originBall = createInstance( [ field.origin( 3 ) ] )
  return new Map().set( originBall.id, originBall )
}

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
    fov: convertFOV( fieldOfView ),
    position: lookAt.map( (e,i) => e - viewDistance * lookDir[ i ] ),
  }
  return { lighting: { ...lights, directionalLights: dlights }, camera, shapes, instances, embedding }
}

export const fetchUrlDesign = async ( textPromise, url ) =>
{
  const text = await textPromise
  if ( ! url.endsWith( ".vZome" ) ) {
    // This is the only case in which we don't resolve the promise with text,
    //  since there is no point in allowing download of non-vZome text.
    throw new Error( `Unrecognized file name: ${url}` )
  }
  const previewUrl = url.substring( 0, url.length-6 ).concat( ".shapes.json" )
  try {
    const previewText = await fetchUrlText( previewUrl )
    const scene = convertPreview( JSON.parse( previewText ) )
    return { text, scene }
  } catch (error) {
    console.log( `Preview load of "${previewUrl}" failed due to error: ${error}` )
    try {
      const parsed = await parse( text ) // TODO run this in a worker!
      if ( ! parsed.firstEdit ) {
        return { text, error: `Unable to parse XML from ${url}` }
      }
      if ( parsed.field.unknown ) {
        return { text, error: `Field ${parsed.field.name} is not implemented.` }
      }
      return { text, parsed }
    } catch (error) {
      return { text, error: error.message }
    }
  }
}

export const useVZomeUrl = ( url, defaultScene ) =>
{
  const [ scene, setScene ] = useState( defaultScene )
  const [ mesh, setMesh ] = useState( null )
  const [ renderer, setRenderer ] = useState( null )
  const [ text, setText ] = useState( null )
  useEffect( () => {
    async function parseUrl() {
      try {
        const fetched = await fetchUrlDesign( fetchUrlText( url ), url )
        setText( fetched.text )
        if ( fetched.error ) {
          alert( fetched.error )  // TODO: these alerts should just be rejections?  or state?
        } else if ( fetched.scene ) {
          setScene( fetched.scene )
        } else {
          const { field, targetEdit, firstEdit, renderer, camera, lighting } = fetched.parsed
          let latestMesh = { shown: originShown( field ), selected: new Map(), hidden: new Map(), groups: [] }
          let targetMesh = null
          const record = ( mesh, id ) => {
            if ( !targetMesh && id === targetEdit ) {
              targetMesh = latestMesh // record the prior state
            }
            latestMesh = mesh // will record where we failed, if we don't reach targetEdit
          } // yup, overwrite every time
          interpret( Step.DONE, latestMesh, firstEdit, [], record )
          setScene( { camera: { ...camera, fov: convertFOV( 0.75 ) }, lighting, embedding: renderer.embedding } )
          setMesh( targetMesh || latestMesh )
          setRenderer( renderer )
        }
      } catch (error) {
        alert( error.message )  // TODO: these alerts should just be rejections?  or state?
      }
    }
    parseUrl()
  }, [ url ] )
  // We have text if we could find the vZome file,
  //  and either a scene if there was a 3D preview JSON next to it,
  //  or mesh and renderer if the XML was parsed successfully
  //  (in which case we still have a scene, just with no shapes and instances).
  return { text, scene, mesh, renderer }
}

export const useInstanceShaper = ( shown, selected, shaper ) =>
{
  const [ shapes ] = useState( {} )
  const [ shapedInstances ] = useState( {} )
  const cachingShaper = shaper && shaper( shapes )
  const shapeInstance = ( instance ) =>
  {
    // TODO: handle undefined result from resolve
    let shapedInstance = shapedInstances[ instance.id ]
    if ( shapedInstance && ( instance.color === shapedInstance.color ) ) {
      return shapedInstance
    }
    shapedInstance = cachingShaper( instance )
    // everything except selected state will go into shapedInstances
    shapedInstance = { ...shapedInstance, vectors: instance.vectors }
    shapedInstances[ instance.id ] = shapedInstance
    return shapedInstance
  }
  const shapeInstances = () =>
  {
    if ( shaper ) {
      try {
        const instances = []
        const tryToShape = ( instance, selected ) => {
          try {
            instance && instances.push( { ...shapeInstance( instance ), selected } )
          } catch (error) {
            console.log( `Failed to shape instance: ${instance.id}` );
          }
        }
        shown.forEach( instance => tryToShape( instance, false ) )
        selected.forEach( instance => tryToShape( instance, true ) )
        return { shapes, instances }
      } catch (error) {
        console.log( 'Caught an odd error while shaping' )
        return {}
      }
    } else
      return {}
  }
  return useMemo( shapeInstances, [ shown, selected, shaper ] )
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
