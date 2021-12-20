
import React from 'react'
import ReactDOM from "react-dom";
import { StylesProvider, jssPreset } from '@material-ui/styles';
import { create } from 'jss';
import { ShapedGeometry } from './geometry.jsx'
import { DesignCanvas } from './designcanvas.jsx'
import { useVZomeDesign } from './hooks.js'
import IconButton from '@material-ui/core/IconButton'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'

// from https://www.bitdegree.org/learn/javascript-download
const download = source =>
{
  const { url, text } = source;
  const name = url.split( '\\' ).pop().split( '/' ).pop()
  const blob = new Blob( [ text ], { type : 'application/xml' } );
  const element = document.createElement( 'a' )
  const blobURI = URL.createObjectURL( blob )
  element.setAttribute( 'href', blobURI )
  element.setAttribute( 'download', `${name}` )
  element.style.display = 'none'
  document.body.appendChild( element )
  element.click()
  document.body.removeChild( element )
}

export const DesignViewer = ( { design } ) =>
{
  const { source, scene } = useVZomeDesign( design );
  return (
    <div style={ { display: 'flex', height: '100%', position: 'relative' } }>
      { scene &&
        <DesignCanvas {...scene} >
          { scene.shapes &&
            <ShapedGeometry embedding={scene.embedding} shapes={scene.shapes} />
          }
        </DesignCanvas>
      }
      { source && source.text &&
        <IconButton color="inherit" aria-label="download"
            style={ { position: 'absolute', top: '5px', right: '5px' } }
            onClick={() => download( source ) } >
          <GetAppRoundedIcon fontSize='medium'/>
        </IconButton>
      }
    </div>
  )
}

export const render = ( design, container, stylesMount, url ) =>
{
  if ( url === null || url === "" ) {
    ReactDOM.unmountComponentAtNode( container );
    return null;
  }

  // TODO: Can we handle canvas resizing using `ResizeObserver` without modifying `vZome` or recreating the element constantly?
  const viewerElement = React.createElement( DesignViewer, { design } );

  // We need JSS to inject styles on our shadow root, not on the document head.
  // I found this solution here:
  //   https://stackoverflow.com/questions/51832583/react-components-material-ui-theme-not-scoped-locally-to-shadow-dom
  const jss = create({
      ...jssPreset(),
      insertionPoint: container
  });
  const reactElement = React.createElement( StylesProvider, { jss: jss }, [ viewerElement ] );

  ReactDOM.render( reactElement, stylesMount );

  return reactElement;
}