import { useMemo, useState, useEffect } from 'react'
import { createInstance } from '../core/adapter.js'
import { parse, interpret, Step } from '../core/api.js'

const aspectRatio = window.innerWidth / window.innerHeight // TODO: this is totally static!
const convertFOV = (fovX) => ( fovX / aspectRatio ) * 180 / Math.PI  // converting radians to degrees

const originShown = field =>
{
  const originBall = createInstance( [ field.origin( 3 ) ] )
  return new Map().set( originBall.id, originBall )
}

export const fetchModel = ( path ) =>
{
  // TODO: I should really deploy my own copy of this proxy on Heroku
  const fetchWithCORS = url => fetch ( url ).catch ( _ => fetch( 'https://cors-anywhere.herokuapp.com/' + url ) )

  return fetchWithCORS( path )
    .then( response =>
    {
      if ( !response.ok ) {
        throw new Error( 'Network response was not ok' );
      }
      return response.text()
    })
    .catch( error =>
    {
      console.error( 'There has been a problem with your fetch operation:', error );
      return null
    });
}

export const useVZomeUrl = ( url, defaultCamera ) =>
{
  const [ camera, setCamera ] = useState( defaultCamera )
  const [ mesh, setMesh ] = useState( null )
  const [ renderer, setRenderer ] = useState( null )
  const [ text, setText ] = useState( null )
  useEffect( () => {
    async function parseUrl() {
      const text = await fetchModel( url )
      if ( !text ) {
        console.log( `Unable to fetch model file from ${url}`)
        return
      }
      setText( text )
      const { firstEdit, camera, field, targetEdit, renderer } = await parse( text ) || {}
      if ( !firstEdit ) {
        console.log( `Unable to parse XML from ${url}`)
        return
      }
      if ( field.unknown ) {
        console.log( `Field ${field.name} is not implemented.`)
        return
      }
      setCamera( { ...camera, fov: convertFOV( 0.75 ) } )
      setRenderer( renderer )
      let latestMesh = { shown: originShown( field ), selected: new Map(), hidden: new Map(), groups: [] }
      let targetMesh = null
      const record = ( mesh, id ) => {
        if ( !targetMesh && id === targetEdit ) {
          targetMesh = latestMesh // record the prior state
        }
        latestMesh = mesh // will record where we failed, if we don't reach targetEdit
      } // yup, overwrite every time
      interpret( Step.DONE, latestMesh, firstEdit, [], record )
      setMesh( targetMesh || latestMesh )
    }
    parseUrl();
  }, [url] )
  return [ mesh, camera, renderer, text ]
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
