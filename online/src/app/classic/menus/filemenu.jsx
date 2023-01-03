
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import MenuItem from "@suid/material/MenuItem"

import { createSignal } from "solid-js";
import { controllerExportAction } from "../controllers-solid.js";
import { serializeVZomeXml, download } from '../../../workerClient/serializer.js';
import { MenuAction } from "../components/menuaction.jsx";

export const FileMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

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
      <Button
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
        <MenuItem disabled={true} onClick={doClose}>New Design</MenuItem>
        <MenuItem disabled={true} onClick={doClose}>Open</MenuItem>
        <MenuAction label="Save" onClick={save} mods="⌘" key="S" />

        <Divider/>

        <MenuItem onClick={ exportAs( 'stl', 'application/sla' ) }>Export STL</MenuItem>

      </Menu>
    </div>
  );
}
