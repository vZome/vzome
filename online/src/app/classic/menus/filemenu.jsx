
import { createEffect, createSignal, mergeProps, onMount } from "solid-js";
import { unwrap } from "solid-js/store";

import { controllerExportAction, controllerProperty, useEditor, suspendMenuKeyEvents } from "../../../viewer/context/editor.jsx";
import { saveFileAs, openFile, saveTextFileAs, saveTextFile } from "../../../viewer/util/files.js";

import { Divider, Menu, MenuAction, MenuItem, SubMenu } from "../../framework/menus.jsx";
import { UrlDialog } from '../dialogs/webloader.jsx'
import { SvgPreviewDialog } from "../dialogs/svgpreview.jsx";
import { useCamera } from "../../../viewer/context/camera.jsx";
import { useImageCapture } from "../../../viewer/context/export.jsx";

const queryParams = new URLSearchParams( window.location.search );
const relativeUrl = queryParams.get( 'design' );

// Must make this absolute before the worker tries to, with the wrong base URL
const url = ( relativeUrl && new URL( relativeUrl, window.location ) .toString() );

const NewDesignItem = props =>
{
  const { rootController } = useEditor();
  const fieldLabel = () => controllerProperty( rootController(), `field.label.${props.field}` );
  // TODO: enable ⌘N
  const modifiers = () => props.field === 'golden' && '⌘';
  const key = () => props.field === 'golden' && 'N';
  return !!fieldLabel() &&
    <MenuAction label={`${fieldLabel()} Field`} onClick={props.onClick} />
}

export const FileMenu = () =>
{
  const { rootController, controllerAction,
    state, setState,
    createDesign, openDesignFile, fetchDesignUrl, importMeshFile, guard, edited } = useEditor();
  const { state: cameraState } = useCamera();
  const [ showDialog, setShowDialog ] = createSignal( false );
  const fields = () => controllerProperty( rootController(), 'fields', 'fields', true );

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
    setState( 'designName', undefined ); // cooperatively managed by both worker and client
    setState( 'fileHandle', undefined );
    // TODO: reset the camera
    createDesign( field );
  }

  const handleOpen = evt =>
  {
    const fileType = { description: 'vZome design file', accept: { '*/*' : [ '.vZome' ] } }
    openFile( [fileType] )
      .then( file => {
        if ( !!file ) {
          openDesignFile( file, false );
          // TODO: re-enable this once we have more confidence in serialization
          // setState( 'fileHandle', file.handle );
        }
      });
  }
  const importFile = ( extension, format ) => evt =>
  {
    const fileType = { description: `${format} file`, accept: { '*/*' : [ extension ] } }
    openFile( [fileType] )
      .then( file => {
        !!file && importMeshFile( file, format );
      });
  }

  const handleShowUrlDialog = () => {
    suspendMenuKeyEvents();
    setShowDialog( true );
  }
  const openUrl = url => {
    if ( url && url.endsWith( ".vZome" ) ) {
      setState( 'fileHandle', undefined );
      fetchDesignUrl( url, { preview: false, debug: false } );
    }
  }

  // Open the design indicated in the query string, if any
  onMount( () => url && openUrl( url ) );

  const [ svgPreview, setSvgPreview ] = createSignal( false );

  const exportAs = ( extension, mimeType, format=extension, params={} ) => evt =>
  {
    const camera = unwrap( cameraState.camera );
    const lighting = unwrap( cameraState.lighting );
    controllerExportAction( rootController(), format, { camera, lighting, ...params } )
      .then( text => {
        const name = (state.designName || 'untitled') .concat( "." + extension );
        saveTextFileAs( name, text, mimeType );
      });
  }

  const { capturer } = useImageCapture();
  const captureImage = ( extension, mimeType ) => evt =>
  {
    const { capture } = capturer();
    capture( mimeType, blob => {
      const name = (state.designName || 'untitled') .concat( "." + extension );
      saveFileAs( name, blob, mimeType );
    });
  }

  const doSave = ( chooseFile = false ) =>
  {
    let name;
    const { camera, lighting } = unwrap( cameraState );
    controllerExportAction( rootController(), 'vZome', { camera, lighting } )
      .then( text => {
        name = state?.designName || 'untitled';
        const mimeType = 'application/xml';
        if ( state.fileHandle && !chooseFile )
          return saveTextFile( state.fileHandle, text, mimeType )
        else
          return saveTextFileAs( name + '-ONLINE.vZome', text, mimeType );
      })
      .then( result => {
        const { handle, success } = result;
        if ( success ) {
          if ( !!handle ) { // file system API supported
            setState( 'fileHandle', handle );
            name = handle.name;
            if ( name .toLowerCase() .endsWith( '.vZome' .toLowerCase() ) )
              name = name .substring( 0, name.length - 6 );
          }
          setState( 'designName', name ); // cooperatively managed by both worker and client
          controllerAction( rootController(), 'clearChanges' );
        }
      })
  }

  const ExportItem = props =>
  {
    props = mergeProps( { format: props.ext }, props );
    const params = { drawOutlines: cameraState.outlines }; // for SVG
    console.log( `ExportItem ${props.label}`);
    return <MenuItem onClick={ exportAs( props.ext, props.mime, props.format, params ) } disabled={props.disabled}>{props.label}</MenuItem>
  }

  const ImageCaptureItem = props =>
  {
    return <MenuItem onClick={ captureImage( props.ext, props.mime ) } disabled={props.disabled}>{props.label}</MenuItem>
  }
  
    return (
    <Menu label="File" dialogs={<>
      <UrlDialog show={showDialog()} setShow={setShowDialog} openDesign={openUrl} />

      <SvgPreviewDialog open={svgPreview()} close={()=>setSvgPreview(false)} exportAs={exportAs} />
    </>}>
        <SubMenu label="New Design...">
          <For each={fields()}>{ field =>
            <NewDesignItem field={field} onClick={() => guard( () => doCreate(field) )}/>
          }</For>
        </SubMenu>

        <MenuAction label="Open..." onClick={() => guard(handleOpen)} />
        <MenuAction label="Open URL..." onClick={() => guard(handleShowUrlDialog)} />
        <MenuItem disabled={true}>Open As New Model...</MenuItem>

        <Divider/>

        <MenuAction label="Close" disabled={true} />
        <MenuAction label="Save..." onClick={ () => doSave() } mods="⌘" key="S" />
        <MenuAction label="Save As..." onClick={ () => doSave( true ) }/>
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
          <ExportItem label="vZome Shapes JSON" format="shapes" ext="shapes.json" mime="application/json" />
          <ExportItem label="VRML" ext="vrml" mime="text/plain" />
        </SubMenu>
        <SubMenu label="Export 3D Panels">
          <ExportItem label="StL (mm)" ext="stl" mime="application/sla" />
          <ExportItem label="OFF" ext="off" mime="text/plain" />
          <ExportItem label="PLY" ext="ply" mime="text/plain" />
        </SubMenu>
        <SubMenu label="Export 3D Points & Lines">
          <ExportItem label="Simple Mesh JSON" format="mesh" ext="mesh.json" mime="application/json" />
          <ExportItem label="Color Mesh JSON" format="cmesh" ext="cmesh.json" mime="application/json" />
          <ExportItem label="AutoCAD DXF" ext="dxf" mime="text/plain" />
        </SubMenu>
        <SubMenu label="Export 3D Balls & Sticks">
          <ExportItem label="OpenSCAD" ext="scad" mime="text/plain" />
          <ExportItem label="Python build123d" ext="py" format="build123d" mime="text/plain" />
        </SubMenu>

        <Divider/>

        <MenuItem disabled={true}>Share using GitHub...</MenuItem>

        <Divider/>

        <SubMenu label="Capture Image">
          <ImageCaptureItem ext="png" label="PNG" mime="image/png" />
          <ImageCaptureItem ext="jpg" label="JPEG" mime="image/jpeg" />
          <ImageCaptureItem ext="webp" label="WebP" mime="image/webp" />
          <ImageCaptureItem ext="bmp" label="BMP" mime="image/bmp" />
        </SubMenu>

        <MenuItem disabled={true} action="capture-wiggle-gif" >Capture Animation</MenuItem>

        <SubMenu label="Capture Vector Drawing">
          <ExportItem label="PDF" ext="pdf" mime="application/pdf" />
          <ExportItem label="SVG" ext="svg" mime="image/svg+xml" />
          <ExportItem label="Postscript" ext="ps" mime="application/postscript" />
          <Divider/>
          <MenuItem onClick={ ()=>setSvgPreview(true) } >Customize...</MenuItem>
        </SubMenu>

        <Divider/>

        <MenuItem disabled={true} >Quit</MenuItem>

    </Menu>
  );
}
