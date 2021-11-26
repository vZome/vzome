
import React from 'react'
import { ShapedGeometry } from './components/geometry.jsx'
import { DesignCanvas } from './components/designcanvas.jsx'
import { useVZomeUrl } from './components/hooks.js'

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
  const { text, scene } = useVZomeUrl( props.url, props.camera )
  return (
    <div style={ { display: 'flex', height: '100%' } }>
      <DesignCanvas {...scene} >
        { (scene && scene.shapes)? <ShapedGeometry {...scene} /> : null }
      </DesignCanvas>
      { text &&
        <button className='muiButton' onClick={ () => download( props.url, text ) }>
          {/* Material-UI was not building correctly, leaving the styles behind when using esbuild,
              so I have simply hand-coded this, copying their styles and SVG.  See vzome-viewer.css.ts. */}
          <svg focusable="false" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M16.59 9H15V4c0-.55-.45-1-1-1h-4c-.55 0-1 .45-1 1v5H7.41c-.89 0-1.34 1.08-.71 1.71l4.59 4.59c.39.39 1.02.39 1.41 0l4.59-4.59c.63-.63.19-1.71-.7-1.71zM5 19c0 .55.45 1 1 1h12c.55 0 1-.45 1-1s-.45-1-1-1H6c-.55 0-1 .45-1 1z"></path>
          </svg>
        </button> }
    </div>
  )
}
