
import { Divider, Menu, MenuAction, MenuItem } from "../../framework/menus.jsx";

import { createSignal } from "solid-js";
import { controllerExportAction } from "../../../workerClient/controllers-solid.js";
import { serializeVZomeXml, download } from '../../../workerClient/serializer.js';
import { UrlDialog } from '../components/webloader.jsx'
import { fetchDesign, openDesignFile } from "../../../workerClient/index.js";
import { useWorkerClient } from "../../../workerClient/index.js";

export const FileMenu = () =>
{
  const { postMessage, rootController, state } = useWorkerClient();
  const [ showDialog, setShowDialog ] = createSignal( false );

  let inputRef;
  const openFile = evt =>
  {
    inputRef.click();
  }
  const onFileSelected = e => {
    const selected = e.target.files && e.target.files[0]
    if ( selected ) {
      postMessage( openDesignFile( selected, false ) );
    }
    inputRef.value = null;
  }

  const handleShowUrlDialog = () => {
    setShowDialog( true );
  }
  const openUrl = url => {
    if ( url && url.endsWith( ".vZome" ) ) {
      postMessage( fetchDesign( url, { preview: false, debug: false } ) );
    }
  }

  const exportAs = ( format, mimeType ) => evt =>
  {
    controllerExportAction( rootController(), format )
      .then( text => {
        const vName = rootController().source?.name || 'untitled.vZome';
        const name = vName.substring( 0, vName.length-6 ).concat( "." + format );
        download( name, text, mimeType );
      });
  }

  const save = evt =>
  {
    controllerExportAction( rootController(), 'vZome' )
      .then( text => {
        const { camera, liveCamera, lighting } = state.scene;
        const fullText = serializeVZomeXml( text, lighting, liveCamera, camera );
        const name = rootController().source?.name || 'untitled.vZome';
        download( name, fullText, 'application/xml' );
      });
  }

  return (
    <Menu label="File" dialogs={<>
      <input style={{ display: 'none' }} type="file" ref={inputRef}
        onChange={onFileSelected} accept={".vZome"} />

      <UrlDialog show={showDialog()} setShow={setShowDialog} openDesign={openUrl} />
    </>}>
        <MenuItem disabled={true}>New Design...</MenuItem>
        <MenuAction label="Open..." onClick={openFile} />
        <MenuAction label="Open URL..." onClick={handleShowUrlDialog} />
        <MenuItem disabled={true}>Open As New Model...</MenuItem>

        <Divider/>

        <MenuAction label="Save" onClick={save} mods="⌘" key="S" />

        <Divider/>

        <MenuItem onClick={ exportAs( 'stl', 'application/sla' ) }>Export STL</MenuItem>
    </Menu>
  );
}
