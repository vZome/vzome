
import React from 'react'
import { ShapedGeometry } from './geometry.jsx'
import { DesignCanvas } from './designcanvas.jsx'
import { useVZomeUrl } from './hooks.js'
import IconButton from '@material-ui/core/IconButton'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'

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

export const UrlViewer = props =>
{
  const { text, scene } = useVZomeUrl( props.url, props.camera, props.worker )
  return (
    scene?
      <div style={ { display: 'flex', height: '100%', position: 'relative' } }>
        <DesignCanvas {...scene} >
          { scene.shapes? 
            <ShapedGeometry embedding={scene.embedding} shapes={scene.shapes} />
          : null }
        </DesignCanvas>
        { text &&
          <IconButton color="inherit" aria-label="download"
              style={ { position: 'absolute', top: '5px', right: '5px' } }
              onClick={() => download( props.url, text ) } >
            <GetAppRoundedIcon fontSize='medium'/>
          </IconButton> }
      </div>
    : null
  )
}
