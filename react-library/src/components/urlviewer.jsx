
import React from 'react'
import { ShapedGeometry } from './geometry.jsx'
import DesignCanvas from './designcanvas.jsx'
import { useInstanceShaper, useVZomeUrl } from './hooks.js'
import Fab from '@material-ui/core/Fab'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'

export const MeshGeometry = ({ shown, selected, resolver, highlightBall, handleClick, onHover }) =>
{
  const { shapes, instances } = useInstanceShaper( shown, selected, resolver )
  return ( instances && <ShapedGeometry {...{ shapes, instances, highlightBall, handleClick, onHover }} /> ) || null
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

export const UrlViewer = props =>
{
  const { url, lighting } = props
  const [ mesh, camera, resolver, xml ] = useVZomeUrl( url, props.camera )
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
