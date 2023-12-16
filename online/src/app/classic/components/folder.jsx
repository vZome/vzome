
import IconButton from '@suid/material/IconButton'
import FolderOpenRoundedIcon from '@suid/icons-material/FolderOpenRounded'
import Menu from '@suid/material/Menu'
import MenuItem from '@suid/material/MenuItem'
import Divider from '@suid/material/Divider';

import { UrlDialog } from '../dialogs/webloader.jsx';
import { Tooltip } from '../../framework/tooltip.jsx';
import { For, createSignal } from 'solid-js';
import { useEditor } from '../../../viewer/context/editor.jsx';

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

export const getModelURL = ( key, pathToRoot='./models' ) => new URL( `${pathToRoot}/${key}.vZome`, window.location ) .toString();

export const OpenMenu = props =>
{
  const { fetchDesignUrl, openDesignFile } = useEditor();
  const [anchor, setAnchor] = createSignal(null);
  const [showDialog, setShowDialog] = createSignal(false);
  const acceptFiles = ".vZome";
  let inputRef;

  const dropboxEnabled = window.Dropbox && window.localStorage.getItem( 'vzome.enable.dropbox' );

  const chooseFile = () => {
    setAnchor(null)
    inputRef.click();
  }
  const showDropboxChooser = () => {
    setAnchor(null)
    window.Dropbox.choose( {
      linkType: 'direct',
      extensions: ['.vzome'],
      success: (files) => {
        fetchDesignUrl( files[0].link, { preview: false, debug: props.forDebugger } );
      },
    } );
  }
  const onFileSelected = e => {
    const selected = e.target.files && e.target.files[0]
    if ( selected )
    openDesignFile( selected, props.forDebugger );
    inputRef.value = null;
  }

  const openUrl = url => {
    if ( url && url.endsWith( ".vZome" ) ) {
      fetchDesignUrl( url, { preview: false, debug: props.forDebugger } );
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
    setAnchor( event.currentTarget )
  }

  const handleSelectModel = model => {
    setAnchor(null)
    const { url, key } = model
    openUrl( url || getModelURL( key, props.pathToRoot ), key );
  }

  const handleClose = () => {
    setAnchor(null)
  }

  const handleShowUrlDialog = () => {
    setAnchor(null)
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
        anchorEl={anchor()}
        open={Boolean(anchor())}
        onClose={handleClose}
      >
        <MenuItem onClick={chooseFile} >Local vZome file
          <input type="file" ref={inputRef} style={{ display: 'none' }}
            onChange={onFileSelected} accept={acceptFiles} /> 
        </MenuItem>
        <MenuItem onClick={handleShowUrlDialog} >Remote vZome URL</MenuItem>
        { dropboxEnabled && <MenuItem onClick={showDropboxChooser} >Choose from Dropbox</MenuItem> }
        <Divider />
        <For each={models}>{ model =>
          <MenuItem onClick={()=>handleSelectModel(model)}>{model.label}</MenuItem>
        }</For>
      </Menu>
      <UrlDialog show={showDialog()} setShow={setShowDialog} openDesign={openUrl} />
    </>
  )
}

