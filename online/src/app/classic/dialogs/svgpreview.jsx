
import { createEffect, createSignal, untrack } from "solid-js"
import { unwrap } from "solid-js/store"

import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"
import Switch from "@suid/material/Switch";
import FormControlLabel from "@suid/material/FormControlLabel";

import { useCamera } from "../../../viewer/context/camera"
import { useViewer } from "../../../viewer/context/viewer"

const SvgPreviewDialog = props =>
{
  const { state, mapViewToWorld } = useCamera();
  const { exportAs } = useViewer();
  const [ useLighting, setUseLighting ] = createSignal( true );
  const [ useShapes, setUseShapes ] = createSignal( true );
  const [ drawOutlines, setDrawOutlines ] = createSignal( false );
  const [ monochrome, setMonochrome ] = createSignal( false );
  const [ showBackground, setShowBackground ] = createSignal( true );

  createEffect( () => setDrawOutlines( state.outlines ) );

  let svgRef;
  createEffect( () => {
    if ( props.open ) { // always regenerate when opening the dialog
      const camera = unwrap( untrack( () => state.camera ) );
      const lighting = unwrap( untrack( () => state.lighting ) );
      lighting .directionalLights .forEach( light => light .worldDirection = mapViewToWorld( light.direction ) );
      const params = { camera, lighting,
        useShapes     : useShapes(),
        drawOutlines  : drawOutlines(),
        monochrome    : monochrome(),
        showBackground: showBackground(),
        useLighting   : useLighting(),
      }
      exportAs( 'svg', params )
        .then( text => {
          svgRef .innerHTML = text; // automatically parses the text
        });
      console.log( 'Regenerating SVG' );
    }
  });

  const exportConfigured = ( ext, mime ) => () =>
  {
    props.close();
    const params = {
      useShapes     : useShapes(),
      drawOutlines  : drawOutlines(),
      monochrome    : monochrome(),
      showBackground: showBackground(),
      useLighting   : useLighting(),
    }
    props.exportFile( ext, mime, ext, params )();
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
        <Button size="small" onClick={ exportConfigured( 'svg', 'image/svg+xml' ) } color="primary">Save SVG</Button>
        <Button size="small" onClick={ exportConfigured( 'pdf', 'application/pdf' ) } color="primary" disabled={true}>Save PDF</Button> {/* missing boilerplate resource */}
        <Button size="small" onClick={ exportConfigured( 'ps', 'application/postscript' ) } color="primary">Save Postscript</Button>
      </DialogActions>
    </Dialog>
  );
}

export { SvgPreviewDialog };
