
import React, { useState, useEffect } from 'react'
import { MeshGeometry } from './geometry'
import DesignCanvas from './designcanvas'
import Adapter, { createInstance } from '../core/adapter'
import * as vZome from '../core/legacyjava'
import Fab from '@material-ui/core/Fab'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'

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

// from https://www.bitdegree.org/learn/javascript-download
export const download = ( url, xml ) =>
{
  const name = url.split( '\\' ).pop().split( '/' ).pop()
  const blob = new Blob([xml], {type : 'application/xml'});
  const element = document.createElement( 'a' )
  const blobURI = URL.createObjectURL( blob )
  element.setAttribute( 'href', blobURI )
  element.setAttribute( 'download', `${name}` )
  element.style.display = 'none'
  document.body.appendChild( element )
  element.click()
  document.body.removeChild( element )
}

const UrlViewer = props =>
{
  const { url, lighting } = props
  const [ camera, setCamera ] = useState( props.camera )
  const [ mesh, setMesh ] = useState( null )
  const [ resolver, setResolver ] = useState( null )
  const [ xml, setXml ] = useState( null )
  useEffect( () => {
    async function parseUrl() {
      const { parser } = await vZome.coreState  // Must wait for the vZome code to initialize
      const text = await fetchModel( url )
      if ( !text )
        return;
      setXml( text )
      const { edits, camera, field, parseAndPerformEdit, targetEdit, shaper } = parser( text ) || {}
      if ( !edits )
        return;
      setCamera( { ...camera, fov: convertFOV( 0.75 ) } )
      setResolver( { shaper } )
      let meshAdapter = new Adapter( originShown( field ), new Map(), new Map() )
      const record = ( adapter ) => {
        meshAdapter = adapter
      } // yup, overwrite every time
      vZome.interpret( vZome.Step.DONE, parseAndPerformEdit, meshAdapter, edits.firstElementChild, [], record )
      setMesh( meshAdapter )
    }
    parseUrl();
  }, [url] )
  return (
    <div style={ { display: 'flex', height: '100%' } }>
      <DesignCanvas {...{ lighting, camera }} >
        { mesh && <MeshGeometry shown={mesh.shown} selected={mesh.selected} resolver={resolver.shaper} /> }
      </DesignCanvas>
      { xml &&
        <Fab color="primary" size="small" aria-label="download"
            style={ { position: 'absolute' } }
            onClick={() => download( url, xml ) } >
          <GetAppRoundedIcon fontSize='small'/>
        </Fab> }
    </div>
  )
}

export default UrlViewer
