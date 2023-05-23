
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import MenuItem from "@suid/material/MenuItem"

import { createSignal } from "solid-js";
import { controllerExportAction } from "../controllers-solid.js";
import { serializeVZomeXml, download } from '../../../workerClient/serializer.js';
import { MenuAction } from "../components/menuaction.jsx";
import { UrlDialog } from '../components/webloader.jsx'
import { fetchDesign, openDesignFile } from "../../../workerClient/index.js";

export const FileMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const [ showDialog, setShowDialog ] = createSignal( false );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  let inputRef;
  const openFile = evt =>
  {
    doClose();
    inputRef.click();
  }
  const onFileSelected = e => {
    const selected = e.target.files && e.target.files[0]
    if ( selected ) {
      props.controller.__store.postMessage( openDesignFile( selected, false ) );
    }
    inputRef.value = null;
  }

  const handleShowUrlDialog = () => {
    doClose();
    setShowDialog( true );
  }
  const openUrl = url => {
    if ( url && url.endsWith( ".vZome" ) ) {
      props.controller.__store.postMessage( fetchDesign( url, { preview: false, debug: false } ) );
    }
  }

  const exportAs = ( format, mimeType ) => evt =>
  {
    doClose();
    controllerExportAction( props.controller, format )
      .then( text => {
        const vName = props.controller.source?.name || 'untitled.vZome';
        const name = vName.substring( 0, vName.length-6 ).concat( "." + format );
        download( name, text, mimeType );
      });
  }

  const save = evt =>
  {
    doClose();
    controllerExportAction( props.controller, 'vZome' )
      .then( text => {
        const { camera, liveCamera, lighting } = props.scene;
        const fullText = serializeVZomeXml( text, lighting, liveCamera, camera );
        const name = props.controller.source?.name || 'untitled.vZome';
        download( name, fullText, 'application/xml' );
      });
  }

  return (
    <div>
      <Button sx={{ color: 'white', minWidth: 'auto' }}
        id="file-menu-button"
        aria-controls={open() ? "file-menu-menu" : undefined}
        aria-haspopup="true"
        aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        File
      </Button>
      <Menu
        id="file-menu-menu"
        anchorEl={anchorEl()}
        open={open()}
        onClose={doClose}
        MenuListProps={{ "aria-labelledby": "file-menu-button" }}
      >
        <MenuItem disabled={true} onClick={doClose}>New Design...</MenuItem>
        <MenuAction label="Open..." onClick={openFile} />
        <MenuAction label="Open URL..." onClick={handleShowUrlDialog} />
        <MenuItem disabled={true} onClick={doClose}>Open As New Model...</MenuItem>

        <Divider/>

        <MenuAction label="Save" onClick={save} mods="⌘" key="S" />

        <Divider/>

        <MenuItem onClick={ exportAs( 'stl', 'application/sla' ) }>Export STL</MenuItem>

      </Menu>

      <input style={{ display: 'none' }} type="file" ref={inputRef}
        onChange={onFileSelected} accept={".vZome"} />

      <UrlDialog show={showDialog()} setShow={setShowDialog} openDesign={openUrl} />
    </div>
  );
}
