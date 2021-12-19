
import React, { useEffect, useRef } from 'react'
import IconButton from '@material-ui/core/IconButton'
import Tooltip from '@material-ui/core/Tooltip'
import FolderOpenRoundedIcon from '@material-ui/icons/FolderOpenRounded'
import Menu from '@material-ui/core/Menu'
import MenuItem from '@material-ui/core/MenuItem'
import Divider from '@material-ui/core/Divider';
import { connect } from 'react-redux'

import UrlDialog from './webloader.jsx'
import { fetchFileText } from '../bundles/files.js'
import { openDesign } from '../bundles/designs.js'
import { fetchUrlText } from '../../ui/viewer/hooks.js'

const openDesignUrl = url => openDesign( fetchUrlText( url ), url )

const openDesignFile = file => openDesign( fetchFileText( file ), file.name )

const models = [
  {
    key: "vZomeLogo",
    label: "vZome Logo",
    description: "The vZome logo, one tetrahedral cell of the 4D 600-cell (cell-first projection)"
  },
  {
    key: "affineDodec",
    label: "Stretched Dodecahedron",
    description: "A regular dodecahedron stretched by a linear transformation"
  },
  {
    key: "120-cell",
    label: "Hyper-dodecahedron",
    description: "The 4D analogue of a dodecahedron, with 120 dodecahedral cells"
  },
  {
    key: "bluePlaneArches1",
    label: "Arched Rhombic Triacontahedron",
    description: "A sculpture built using just the blue planes"
  },
  {
    key: "C240",
    label: "C-240 Buckyball",
    description: "C-240 Buckyball"
  },
  {
    key: "orangePurpleChiral",
    label: "Orange and Purple Tangle",
    description: "A design by Brian Hall"
  },
  {
    key: "truncated5Cell",
    url: "https://vzome.com/models/2007/04-Apr/5cell/A4_3C.vZome",
    label: "Truncated 5-Cell",
    description: "Truncated 5-Cell"
  },
]

let url = "./models/vZomeLogo.vZome"
const urlParams = new URLSearchParams( window.location.search );
if ( urlParams.has( "url" ) ) {
  url = decodeURI( urlParams.get( "url" ) )
}

const DesignsMenu = ( { openUrl, openFile } ) =>
{
  const [anchorEl, setAnchorEl] = React.useState(null)
  const [showDialog, setShowDialog] = React.useState(false)
  const ref = useRef()
  const chooseFile = () => {
    setAnchorEl(null)
    ref.current.click()
  }
  useEffect( () => openUrl( url ), [openUrl] )

  const handleClickOpen = (event) => {
    setAnchorEl( event.currentTarget )
  }

  const handleSelectModel = model => {
    setAnchorEl(null)
    const { url, key } = model
    openUrl( url || `./models/${key}.vZome`, key )
  }

  const handleClose = () => {
    setAnchorEl(null)
  }

  const handleShowUrlDialog = () => {
    setAnchorEl(null)
    setShowDialog(true)
  }

  return (
    <>
      <Tooltip title="Open a design" aria-label="open">
        <IconButton color="inherit" aria-label="open" onClick={handleClickOpen}>
          <FolderOpenRoundedIcon fontSize="large"/>
        </IconButton>
      </Tooltip>
      <Menu
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        <MenuItem onClick={chooseFile}>Local vZome file
          <input className="FileInput" type="file" ref={ref}
            onChange={ (e) => {
                const selected = e.target.files && e.target.files[0]
                if ( selected )
                  openFile( selected )
              } }
            accept=".vZome" /> 
        </MenuItem>
        <MenuItem onClick={handleShowUrlDialog}>Remote vZome URL</MenuItem>
        <Divider />
        { models.map( (model) => (
          <MenuItem key={model.key} onClick={()=>handleSelectModel(model)}>{model.label}</MenuItem>
        ) ) }
      </Menu>
      <UrlDialog show={showDialog} setShow={setShowDialog} openDesign={openUrl} />
    </>
  )
}

const select = (state) => ({
  // TODO make a real selector!
  enabled: ! state.workingPlane,
})

const boundEventActions = {
  openUrl : openDesignUrl,
  openFile : openDesignFile,
}

export default connect( select, boundEventActions )( DesignsMenu )
