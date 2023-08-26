
import { Divider, Menu, MenuAction, MenuItem, SubMenu } from "../../framework/menus.jsx";

import { createEffect, createSignal, mergeProps } from "solid-js";
import { controllerAction, controllerExportAction, controllerProperty } from "../../../workerClient/controllers-solid.js";
import { serializeVZomeXml, download } from '../../../workerClient/serializer.js';
import { UrlDialog } from '../components/webloader.jsx'
import { fetchDesign, openDesignFile, newDesign, importMeshFile } from "../../../workerClient/index.js";
import { useWorkerClient } from "../../../workerClient/index.js";
import { Guardrail } from "../components/guardrail.jsx";

const NewDesignItem = props =>
{
  const { rootController } = useWorkerClient();
  const fieldLabel = () => controllerProperty( rootController(), `field.label.${props.field}` );
  // TODO: enable ⌘N
  const modifiers = () => props.field === 'golden' && '⌘';
  const key = () => props.field === 'golden' && 'N';
  return !!fieldLabel() &&
    <MenuAction label={`${fieldLabel()} Field`} onClick={props.onClick} />
}

export const FileMenu = () =>
{
  const { postMessage, rootController, state } = useWorkerClient();
  const [ showDialog, setShowDialog ] = createSignal( false );
  const fields = () => controllerProperty( rootController(), 'fields', 'fields', true );
  const [ showGuardrail, setShowGuardrail ] = createSignal( false );
  const edited = () => controllerProperty( rootController(), 'edited' ) === 'true';

  // Since the initial render of the menu doesn't fetch these properties,
  //   we have to force this prefetch so the data is ready when we need it.
  createEffect( () =>
  {
    const getLabel = (field) => controllerProperty( rootController(), `field.label.${field}` );
    const isEdited = edited();
    for (const field of fields()) {
      const label = getLabel(field);
      if ( label === 'no logging' ) // trick the compiler
        console.log( `never logged: ${isEdited} ${label}`);
    }
  });

  const doCreate = field =>
  {
    postMessage( newDesign( field ) );
  }

  let inputRef;
  const onFileSelected = e => {
    const selected = e.target.files && e.target.files[0];
    const actionFn = inputRef[ 'data-action' ];
    if ( selected && actionFn ) {
      postMessage( actionFn( selected ) );
    }
    delete inputRef.accept;
    delete inputRef[ 'data-action' ];
    inputRef.value = null;
  }
  const openFile = evt =>
  {
    inputRef[ 'data-action' ] = selected => openDesignFile( selected, false );
    inputRef.accept = '.vZome';
    inputRef.click();
  }
  const importFile = ( extension, format ) => evt =>
  {
    inputRef[ 'data-action' ] = selected => importMeshFile( selected, format );
    inputRef.accept = extension;
    inputRef.click();
  }

  const handleShowUrlDialog = () => {
    setShowDialog( true );
  }
  const openUrl = url => {
    if ( url && url.endsWith( ".vZome" ) ) {
      postMessage( fetchDesign( url, { preview: false, debug: false } ) );
    }
  }

  let continuation;
  const guard = guardedAction =>
  {
    if ( edited() ) {
      continuation = guardedAction;
      setShowGuardrail( true );
    }
    else
      guardedAction();
  }
  const closeGuardrail = continued =>
  {
    setShowGuardrail( false );
    if ( continued )
      continuation();
    continuation = undefined;
  }

  const exportAs = ( format, mimeType, extension ) => evt =>
  {
    controllerExportAction( rootController(), format )
      .then( text => {
        const vName = rootController().source?.name || 'untitled.vZome';
        const name = vName.substring( 0, vName.length-6 ).concat( "." + extension );
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
        controllerAction( rootController(), 'clearChanges' );
      });
  }

  const ExportItem = props =>
  {
    props = mergeProps( { format: props.ext }, props );
    return <MenuItem onClick={ exportAs( props.format, props.mime, props.ext ) } disabled={props.disabled}>{props.label}</MenuItem>
  }

  return (
    <Menu label="File" dialogs={<>
      <input style={{ display: 'none' }} type="file" ref={inputRef} onChange={onFileSelected} />

      <UrlDialog show={showDialog()} setShow={setShowDialog} openDesign={openUrl} />

      <Guardrail show={showGuardrail()} close={closeGuardrail} />
    </>}>
        <SubMenu label="New Design...">
          <For each={fields()}>{ field =>
            <NewDesignItem field={field} onClick={() => guard( () => doCreate(field) )}/>
          }</For>
        </SubMenu>

        <MenuAction label="Open..." onClick={() => guard(openFile)} />
        <MenuAction label="Open URL..." onClick={() => guard(handleShowUrlDialog)} />
        <MenuItem disabled={true}>Open As New Model...</MenuItem>

        <Divider/>

        <MenuAction label="Close" disabled={true} />
        <MenuAction label="Save..." onClick={save} mods="⌘" key="S" />
        <MenuAction label="Save As..." disabled={true} />
        <MenuAction label="Save Template..." disabled={true} />

        <Divider/>

        <SubMenu label="Import 3D Mesh">
          <MenuItem onClick={importFile( '.json', 'mesh' )}  >Simple Mesh JSON</MenuItem>
          <MenuItem onClick={importFile( '.json', 'cmesh' )} >Color Mesh JSON</MenuItem>
          <MenuItem onClick={importFile( '.vef', 'vef' )}    >vZome VEF</MenuItem>
        </SubMenu>

        <Divider/>

        <SubMenu label="Export 3D Rendering">
          <ExportItem label="Collada DAE" ext="dae" mime="text/plain" disabled={true} />
          <ExportItem label="POV-Ray" ext="pov" mime="text/plain" disabled={true} />
          <ExportItem label="vZome Shapes JSON" ext="shapes" mime="text/plain" disabled={true} />
          <ExportItem label="VRML" ext="vrml" mime="text/plain" disabled={true} />
        </SubMenu>
        <SubMenu label="Export 3D Panels">
          <ExportItem label="StL (mm)" ext="stl" mime="application/sla" />
          <ExportItem label="OFF" ext="off" mime="text/plain" disabled={true} />
          <ExportItem label="PLY" ext="ply" mime="text/plain" disabled={true} />
        </SubMenu>
        <SubMenu label="Export 3D Mesh">
          <ExportItem label="Simple Mesh JSON" format="mesh" ext="mesh.json" mime="text/plain" />
          <ExportItem label="Color Mesh JSON" format="cmesh" ext="cmesh.json" mime="application/json" />
          <ExportItem label="AutoCAD DXF" ext="dxf" mime="text/plain" disabled={true} />
        </SubMenu>

        <Divider/>

        <MenuItem disabled={true}>Share using GitHub...</MenuItem>

        <Divider/>

        <SubMenu label="Capture Image">
          <MenuItem disabled={true} action="capture.jpg" >JPEG</MenuItem>
          <MenuItem disabled={true} action="capture.png" >PNG</MenuItem>
          <MenuItem disabled={true} action="capture.gif" >GIF</MenuItem>
          <MenuItem disabled={true} action="capture.bmp" >BMP</MenuItem>
        </SubMenu>

        <MenuItem disabled={true} action="capture-wiggle-gif" >Capture Animation</MenuItem>

        <SubMenu label="Capture Vector Drawing">
          <MenuItem disabled={true} action="export2d.pdf" >PDF</MenuItem>
          <MenuItem disabled={true} action="export2d.svg" >SVG</MenuItem>
          <MenuItem disabled={true} action="export2d.ps" >Postscript</MenuItem>
          <Divider/>
          <MenuItem disabled={true} action="snapshot.2d" >Customize...</MenuItem>
        </SubMenu>

        <Divider/>

        <MenuItem disabled={true} >Quit</MenuItem>

    </Menu>
  );
}
