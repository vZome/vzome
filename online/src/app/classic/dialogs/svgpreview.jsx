
import { createEffect, createSignal } from "solid-js"
import { unwrap } from "solid-js/store"

import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"
import Switch from "@suid/material/Switch";
import FormControlLabel from "@suid/material/FormControlLabel";

import { useWorkerClient } from "../../../workerClient/context.jsx"
import { controllerExportAction } from "../../../workerClient/controllers-solid"
import { useCamera } from "../../../workerClient/camera"

const SvgPreviewDialog = props =>
{
  const { rootController } = useWorkerClient();
  const { state } = useCamera();
  const [ useLighting, setUseLighting ] = createSignal( true );
  const [ useShapes, setUseShapes ] = createSignal( true );
  const [ drawOutlines, setDrawOutlines ] = createSignal( true );
  const [ monochrome, setMonochrome ] = createSignal( false );
  const [ showBackground, setShowBackground ] = createSignal( true );

  let svgRef;
  createEffect( () => {
    if ( ! props.open ) return;
    const camera = unwrap( state.camera );
    const lighting = unwrap( state.lighting );
    const params = { camera, lighting,
      useShapes     : useShapes(),
      drawOutlines  : drawOutlines(),
      monochrome    : monochrome(),
      showBackground: showBackground(),
      useLighting   : useLighting(),
    }
    controllerExportAction( rootController(), 'svg', params )
      .then( text => {
        svgRef .innerHTML = text; // automatically parses the text
      });
  });

  const exportAs = ( ext, mime ) => () =>
  {
    props.close();
    props.exportAs( ext, mime )();
  }

  return (
    <Dialog onClose={ () => props.close() } open={props.open} fullWidth='true' maxWidth='lg'>
      <DialogTitle id="orbits-dialog">Capture Vector Drawing</DialogTitle>
      <DialogContent>
        <FormControlLabel label="background"
            control={
            <Switch checked={showBackground()} onChange={()=>setShowBackground( v => !v )}
              inputProps={{ "aria-label": "background" }} />
          }/>
        <FormControlLabel label="shapes"
            control={
            <Switch checked={useShapes()} onChange={()=>setUseShapes( v => !v )}
              inputProps={{ "aria-label": "shapes" }} />
          }/>
        <FormControlLabel label="lighting"
            control={
            <Switch checked={useShapes() && useLighting()} onChange={()=>setUseLighting( v => !v )} disabled={!useShapes()}
              inputProps={{ "aria-label": "lighting" }} />
          }/>
        <FormControlLabel label="outlines"
            control={
            <Switch checked={useShapes() && drawOutlines()} onChange={()=>setDrawOutlines( v => !v )} disabled={!useShapes()}
              inputProps={{ "aria-label": "outlines" }} />
          }/>
        <FormControlLabel label="monochrome"
            control={
            <Switch checked={!useShapes() && monochrome()} onChange={()=>setMonochrome( v => !v )} disabled={!!useShapes()}
              inputProps={{ "aria-label": "monochrome" }} />
          }/>
        <div ref={ svgRef }></div>
      </DialogContent>
      <DialogActions>
        <Button size="small" onClick={ ()=>props.close() } color="primary">Cancel</Button>
        <Button size="small" onClick={ exportAs( 'svg', 'image/svg+xml' ) } color="primary">Save SVG</Button>
        <Button size="small" onClick={ exportAs( 'pdf', 'application/pdf' ) } color="primary">Save PDF</Button>
        <Button size="small" onClick={ exportAs( 'ps', 'application/postscript' ) } color="primary">Save Postscript</Button>
      </DialogActions>
    </Dialog>
  );
}

export { SvgPreviewDialog };
