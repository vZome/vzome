import React from 'react'
import { connect } from 'react-redux'
import * as designs from '../bundles/designs.js'

import Tooltip from '@material-ui/core/Tooltip'
import IconButton from '@material-ui/core/IconButton'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'

const select = ( state ) =>
{
  const text = state.designs && designs.selectText( state )
  const name = state.designs && designs.selectDesignName( state )
  const design = state.designs && designs.selectDesign( state )
  return {
    text,
    name,
    design,
  }
}

// from https://www.bitdegree.org/learn/javascript-download
export const download = ( name, text ) =>
{
  const blob = new Blob( [text], {type : 'application/xml'} );
  const element = document.createElement( 'a' )
  const blobURI = URL.createObjectURL( blob )
  element.setAttribute( 'href', blobURI )
  element.setAttribute( 'download', name )
  element.style.display = 'none'
  document.body.appendChild( element )
  element.click()
  document.body.removeChild( element )
}

function ShareButton( { text, name, design } )
{
  const handleClick = event =>
  {
    if ( event.getModifierState( "Alt" ) ) {
      if ( ! name.endsWith( '.json' ) ) { name += '.json' }
      const jsonText = designs.serialize( design )
      download( name, jsonText )
    } else {
      download( name, text )
    }
  }

  return (
    <>
      <Tooltip title="Download vZome file" aria-label="download">
        <IconButton color="inherit" aria-label="download" onClick={handleClick} disabled={!text}>
          <GetAppRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
    </>
  )
}


export default connect( select )( ShareButton )
