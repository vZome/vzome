
import { Divider, Menu, MenuAction, MenuItem } from "../components/menuaction.jsx";

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

          /* <DropdownMenu.Sub overlap gutter={4} shift={-8}>
            <DropdownMenu.SubTrigger class="dropdown-menu__sub-trigger">
              GitHub
              <div class="dropdown-menu__item-right-slot">
                <svg viewBox="0 0 15 15" width="20" height="20">
                  <path d="M6.1584 3.13508C6.35985 2.94621 6.67627 2.95642 6.86514 3.15788L10.6151 7.15788C10.7954 7.3502 10.7954 7.64949 10.6151 7.84182L6.86514 11.8418C6.67627 12.0433 6.35985 12.0535 6.1584 11.8646C5.95694 11.6757 5.94673 11.3593 6.1356 11.1579L9.565 7.49985L6.1356 3.84182C5.94673 3.64036 5.95694 3.32394 6.1584 3.13508Z" fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"></path>
                </svg>
              </div>
            </DropdownMenu.SubTrigger>
            <DropdownMenu.Portal>
              <DropdownMenu.SubContent class="dropdown-menu__sub-content">
                <DropdownMenu.Item class="dropdown-menu__item">
                  Create Pull Request…
                </DropdownMenu.Item>
                <DropdownMenu.Item class="dropdown-menu__item">
                  View Pull Requests
                </DropdownMenu.Item>
                <DropdownMenu.Item class="dropdown-menu__item">
                  Sync Fork
                </DropdownMenu.Item>
                <DropdownMenu.Separator class="dropdown-menu__separator" />
                <DropdownMenu.Item class="dropdown-menu__item">
                  Open on GitHub
                </DropdownMenu.Item>
              </DropdownMenu.SubContent>
            </DropdownMenu.Portal>
          </DropdownMenu.Sub>
          <DropdownMenu.Separator class="dropdown-menu__separator" />
          <DropdownMenu.CheckboxItem
            class="dropdown-menu__checkbox-item"
          >
            Show Git Log
          </DropdownMenu.CheckboxItem>
          <DropdownMenu.CheckboxItem
            class="dropdown-menu__checkbox-item"
          >
            Show History
          </DropdownMenu.CheckboxItem>
          <DropdownMenu.Separator class="dropdown-menu__separator" />
          <DropdownMenu.Group>
            <DropdownMenu.GroupLabel class="dropdown-menu__group-label">
              Branches
            </DropdownMenu.GroupLabel>
            <DropdownMenu.RadioGroup >
              <DropdownMenu.RadioItem class="dropdown-menu__radio-item" value="main">
                main
              </DropdownMenu.RadioItem>
              <DropdownMenu.RadioItem class="dropdown-menu__radio-item" value="develop">
                develop
              </DropdownMenu.RadioItem>
            </DropdownMenu.RadioGroup>
          </DropdownMenu.Group>
          <DropdownMenu.Arrow /> */
