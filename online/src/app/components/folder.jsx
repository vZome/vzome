
import React, { useRef, useState } from 'react'
import { useDispatch } from 'react-redux';

import { fetchDesign, openDesignFile } from '../../workerClient/index.js';

import IconButton from '@material-ui/core/IconButton'
import Tooltip from '@material-ui/core/Tooltip'
import FolderOpenRoundedIcon from '@material-ui/icons/FolderOpenRounded'
import Menu from '@material-ui/core/Menu'
import MenuItem from '@material-ui/core/MenuItem'
import Divider from '@material-ui/core/Divider';

import UrlDialog from './webloader.jsx'

const queryParams = new URLSearchParams( window.location.search );
const enableDropbox = !! queryParams .get( 'enableDropbox' );

if ( enableDropbox ) {
  window.localStorage.setItem( 'vzome.enable.dropbox', true );
}

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
    url: "https://www.vzome.com/models/2007/04-Apr/5cell/A4_3C.vZome",
    label: "Truncated 5-Cell",
    description: "Truncated 5-Cell"
  },
]

export const getModelURL = ( key, pathToRoot='.' ) => new URL( `${pathToRoot}/models/${key}.vZome`, window.location ) .toString();

export const OpenMenu = props =>
{
  const { pathToRoot, forDebugger=false } = props;
  const [anchorEl, setAnchorEl] = useState(null)
  const [showDialog, setShowDialog] = useState(false)
  const acceptFiles = ".vZome";
  const inputRef = useRef()
  const report = useDispatch();

  const dropboxEnabled = window.Dropbox && window.localStorage.getItem( 'vzome.enable.dropbox' );

  const chooseFile = () => {
    setAnchorEl(null)
    inputRef.current.click();
  }
  const showDropboxChooser = () => {
    setAnchorEl(null)
    window.Dropbox.choose( {
      linkType: 'direct',
      extensions: ['.vzome'],
      success: (files) => {
        report( fetchDesign( files[0].link, { preview: false, debug: forDebugger } ) );
      },
    } );
  }
  const onFileSelected = e => {
    const selected = e.target.files && e.target.files[0]
    if ( selected )
      report( openDesignFile( selected, forDebugger ) );
    inputRef.current.value = null;
  }

  const openUrl = url => {
    if ( url && url.endsWith( ".vZome" ) ) {
      report( fetchDesign( url, { preview: false, debug: forDebugger } ) );
    }
  }

  // This can trigger an event cycle involving the "waiting" state,
  //  if we change to "[openUrl]", causing an endless repetition
  //  of this effect.
  // However, as-is, this does not have the desired effect of opening
  // the default URL, since openUrl will be a no-op until the controller
  // is created.
  // I'm taking it out, since an editor really should come up with a new document, anyway.
  //
  // useEffect( () => openUrl( url ), [] )

  const handleClickOpen = (event) => {
    setAnchorEl( event.currentTarget )
  }

  const handleSelectModel = model => {
    setAnchorEl(null)
    const { url, key } = model
    openUrl( url || getModelURL( key, pathToRoot ), key );
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
        <MenuItem onClick={chooseFile} >Local vZome file
          <input className="FileInput" type="file" ref={inputRef}
            onChange={onFileSelected} accept={acceptFiles} /> 
        </MenuItem>
        <MenuItem onClick={handleShowUrlDialog} >Remote vZome URL</MenuItem>
        { dropboxEnabled && <MenuItem onClick={showDropboxChooser} >Choose from Dropbox</MenuItem> }
        <Divider />
        { models.map( (model) => (
          <MenuItem key={model.key} onClick={()=>handleSelectModel(model)}>{model.label}</MenuItem>
        ) ) }
      </Menu>
      <UrlDialog show={showDialog} setShow={setShowDialog} openDesign={openUrl} />
    </>
  )
}

