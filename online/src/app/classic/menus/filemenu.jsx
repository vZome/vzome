
import { createEffect, createSignal, mergeProps, onMount } from "solid-js";

import { controllerProperty, useEditor } from '../../framework/context/editor.jsx';
import { suspendMenuKeyEvents } from '../context/commands.jsx';
import { saveFileAs, openFile, saveTextFileAs, } from "../../../viewer/util/files.js";

import { CommandAction, Divider, Menu, MenuAction, MenuItem, SubMenu, LinkItem, } from "../../framework/menus.jsx";
import { UrlDialog } from '../dialogs/webloader.jsx'
import { SvgPreviewDialog } from "../dialogs/svgpreview.jsx";
import { useCamera } from "../../../viewer/context/camera.jsx";
import { useImageCapture } from "../../../viewer/context/export.jsx";
import { useViewer, EXPORT_FORMATS } from "../../../viewer/context/viewer.jsx";

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
  const { rootController, state, setState,
    createDesign, openDesignFile, fetchDesignUrl, importMeshFile, guard, edited } = useEditor();
  const { setProblem, exportAs } = useViewer();
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

  const handleOpen = asNew => () =>
  {
    const fileType = { description: 'vZome design file', accept: { '*/*' : [ '.vZome' ] } }
    openFile( [fileType] )
      .then( file => {
        if ( !!file ) {
          if ( asNew ) {
            setState( 'ignoreDesignName', true );  // transient, means we'll still have an untitled design after the fetch
            setState( 'designName', undefined ); // cooperatively managed by both worker and client
          }
          setState( 'fileHandle', asNew? undefined : file.handle );
          openDesignFile( file, false );
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

  const dropboxEnabled = window.Dropbox && window.localStorage.getItem( 'vzome.enable.dropbox' ) === 'true';

  const showDropboxChooser = () => {
    window.Dropbox.choose( {
      linkType: 'direct',
      extensions: ['.vzome'],
      success: (files) => {
        const url = files[ 0 ] .link .toLowerCase();
        setState( 'ignoreDesignName', true );  // transient, means we'll still have an untitled design after the fetch
        fetchDesignUrl( url, { preview: false, debug: false } );

        let name = files[ 0 ] .name;
        if ( name && name .toLowerCase() .endsWith( '.vzome' ) ) {
          name = name .substring( 0, name.length - 6 );
          setState( 'designName', name );
          setState( 'sharing', 'title', name .replaceAll( '-', ' ' ) );
        }

        /*
          My personal Dropbox holds 100s of vZome files back to 2003.  When I share them,
          I want to capture the original date, which is encoded in the file path (usually):
          .../vzome/attachments/2016/04-apr/10-Scott-deleteTest/testDeleteAndCut.vZome
        */
        const ATTACHMENTS = 'vzome/attachments/';
        if ( url .includes( ATTACHMENTS ) ) {
          const start = url .lastIndexOf( ATTACHMENTS ) + 18;
          const relPath = url .substring( start );
          try {
            const [ _, year, month, day ] = relPath .match( /([0-9]+)\/([0-9]+).*\/([0-9]+).*\// );
            const date = new Date( Number(year), Number(month)-1, Number(day) );
            setState( 'originalDate', date );
            console.log( 'Dropbox date:', date, 'for', year, month, day, relPath );
          } catch (error) {
            console.log( 'Could not parse Dropbox path as date:', relPath );
          }
        }
      },
    } );
  }

  // Open the design indicated in the query string, if any
  onMount( () => ( url && url.endsWith( ".vZome" ) ) ? openUrl( url ) : doCreate( 'golden' ) );

  const [ svgPreview, setSvgPreview ] = createSignal( false );

  const exportFile = ( extension, mimeType, format, params={} ) => evt =>
  {
    exportAs( format, params )
      .then( text => {
        const name = (state.designName || 'untitled') .concat( "." + extension );
        saveTextFileAs( name, text, mimeType );
      });
  }

  const { captureImage } = useImageCapture();
  const doCaptureImage = ( extension, mimeType ) => evt =>
  {
    captureImage( mimeType ) .then( blob => {
      if ( blob.size === 0 ) {
        setProblem( 'Captured image is empty; please report this as a defect' );
        return;
      }
      const name = (state.designName || 'untitled') .concat( "." + extension );
      saveFileAs( name, blob, mimeType );
    });
  }

  const ExportItem = props =>
  {
    const { ext=props.format, mime, label } = EXPORT_FORMATS[ props.format ];
    const params = { drawOutlines: cameraState.outlines }; // for SVG
    return <MenuItem onClick={ exportFile( ext, mime, props.format, params ) } disabled={props.disabled}>{label}</MenuItem>
  }

  const ImageCaptureItem = props =>
  {
    return <MenuItem onClick={ doCaptureImage( props.ext, props.mime ) } disabled={props.disabled}>{props.label}</MenuItem>
  }
  
    return (
    <Menu label="File" dialogs={<>
      <UrlDialog show={showDialog()} setShow={setShowDialog} openDesign={openUrl} />

      <SvgPreviewDialog open={svgPreview()} close={()=>setSvgPreview(false)} exportFile={exportFile} />
    </>}>
        <SubMenu label="New Design...">
          <For each={fields()}>{ field =>
            <NewDesignItem field={field} onClick={() => guard( () => doCreate(field) )}/>
          }</For>
        </SubMenu>

        <MenuAction label="Open..."     onClick={() => guard(handleOpen(false))} />
        <MenuAction label="Open URL..." onClick={() => guard(handleShowUrlDialog)} />
        <MenuAction label="Open As New Design..."     onClick={() => guard(handleOpen(true))} />
        { dropboxEnabled &&
           <MenuAction label="Choose from Dropbox..." onClick={() => guard(showDropboxChooser)} /> }

        <Divider/>

        <SubMenu label="Support Apps">
          <LinkItem label='GitHub Share Browser'       href='/app/browser' />
          <LinkItem label='Local File Browser'         href='/app/localfiles' />
          <LinkItem label='Build Plane (Experimental)' href='/app/buildplane' disabled={true} />
        </SubMenu>

        <Divider/>

        <MenuAction label="Close" disabled={true} />
        <CommandAction label="Save..."    action="Save" />
        <CommandAction label="Save As..." action="SaveAs" />
        <MenuAction label="Save Template..." disabled={true} />

        <Divider/>

        <SubMenu label="Import 3D Mesh">
          <MenuItem onClick={importFile( '.json', 'mesh' )}  >Simple Mesh JSON</MenuItem>
          <MenuItem onClick={importFile( '.json', 'cmesh' )} >Color Mesh JSON</MenuItem>
          <MenuItem onClick={importFile( '.vef', 'vef' )}    >vZome VEF</MenuItem>
        </SubMenu>

        <Divider/>

        <SubMenu label="Export 3D Rendering">
          <ExportItem format="dae" disabled={true} />
          <ExportItem format="pov" />
          <ExportItem format="shapes" />
          <ExportItem format="vrml" />
        </SubMenu>
        <SubMenu label="Export 3D Panels">
          <ExportItem format="stl" />
          <ExportItem format="off" />
          <ExportItem format="ply" />
          <ExportItem format="step" />
        </SubMenu>
        <SubMenu label="Export 3D Points & Lines">
          <ExportItem format="mesh" />
          <ExportItem format="cmesh" />
          <ExportItem format="dxf" />
        </SubMenu>
        <SubMenu label="Export 3D Balls & Sticks">
          <ExportItem format="scad" />
          <ExportItem format="build123d" />
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
          <ExportItem format="pdf" />
          <ExportItem format="svg" />
          <ExportItem format="ps" />
          <Divider/>
          <MenuItem onClick={ ()=>setSvgPreview(true) } >Customize...</MenuItem>
        </SubMenu>

        <Divider/>

        <MenuItem disabled={true} >Quit</MenuItem>

    </Menu>
  );
}
