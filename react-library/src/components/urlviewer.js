
import React, { useState, useEffect } from 'react'
import { MeshGeometry } from './geometry'
import DesignCanvas from './designcanvas'
import Adapter, { createInstance } from '../core/adapter'
import * as vZome from '../core/legacyjava'

const aspectRatio = window.innerWidth / window.innerHeight // TODO: this is totally static!
const convertFOV = (fovX) => ( fovX / aspectRatio ) * 180 / Math.PI  // converting radians to degrees
export const defaultInitialCamera = {
  fov: convertFOV( 0.75 ), // 0.44 in vZome
  position: [ 0, 0, 75 ],
  lookAt: [ 0, 0, 0 ],
  up: [ 0, 1, 0 ],
  far: 217.46,
  near: 0.271,
}

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

const UrlViewer = props =>
{
  const { url, lighting } = props
  const [ camera, setCamera ] = useState( props.camera )
  const [ mesh, setMesh ] = useState( null )
  const [ resolver, setResolver ] = useState( null )
  useEffect( () => {
    async function parseUrl() {
      const { parser } = await vZome.coreState  // Must wait for the vZome code to initialize
      const text = await fetchModel( url )
      if ( !text )
        return;
      const { edits, camera, field, parseAndPerformEdit, targetEdit, shaper } = parser( text ) || {}
      if ( !edits )
        return;
      setCamera( { ...camera, fov: convertFOV( 0.75 ) } )
      setResolver( { shaper } )
      let meshAdapter = new Adapter( originShown( field ), new Map(), new Map() )
      const record = ( adapter ) => {
        meshAdapter = adapter
      } // yup, overwrite every time
      vZome.parse( vZome.Step.DONE, parseAndPerformEdit, meshAdapter, edits.firstElementChild, [], record )
      setMesh( meshAdapter )
    }
    parseUrl();
  }, [url] )
  return (
    <DesignCanvas {...{ lighting, camera }} >
      { mesh && <MeshGeometry shown={mesh.shown} selected={mesh.selected} resolver={resolver.shaper} />}
    </DesignCanvas>
  )
}

export default UrlViewer
