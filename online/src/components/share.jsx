import React from 'react'
import { connect } from 'react-redux'
import * as designs from '../bundles/designs.js'

import Tooltip from '@material-ui/core/Tooltip'
import IconButton from '@material-ui/core/IconButton'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'

const select = ( state ) =>
{
  const xml = state.designs && designs.selectText( state )
  const name = state.designs && designs.selectDesignName( state )
  return {
    xml,
    name,
  }
}

// from https://www.bitdegree.org/learn/javascript-download
export const download = ( name, xml ) =>
{
  const blob = new Blob([xml], {type : 'application/xml'});
  const element = document.createElement( 'a' )
  const blobURI = URL.createObjectURL( blob )
  element.setAttribute( 'href', blobURI )
  element.setAttribute( 'download', name )
  element.style.display = 'none'
  document.body.appendChild( element )
  element.click()
  document.body.removeChild( element )
}

function ShareButton( { xml, name } )
{
  return (
    <>
      <Tooltip title="Download vZome file" aria-label="download">
        <IconButton color="inherit" aria-label="download" onClick={()=>download( name, xml )} disabled={!xml}>
          <GetAppRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
    </>
  )
}


export default connect( select )( ShareButton )
