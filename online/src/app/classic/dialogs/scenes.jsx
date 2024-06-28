
import { createSignal } from "solid-js"

import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"

import { useViewer } from "../../../viewer/context/viewer.jsx";
import { useEditor } from '../../framework/context/editor.jsx';
import { SceneCanvas } from "../../../viewer/scenecanvas.jsx";
import { CameraProvider } from "../../../viewer/index.jsx"
import { SceneProvider } from "../../../viewer/context/scene.jsx"

const ScenesDialog = props =>
{
  const [ sceneIndex, setSceneIndex ] = createSignal( 1 );
  const { scenes } = useViewer();
  const { state: { source } } = useEditor();

  return (
    <Dialog onClose={ () => props.close() } open={props.open} fullWidth='true' maxWidth='xl'>
      <DialogTitle id="scenes-dialog">Scenes</DialogTitle>
      <DialogContent>
        <div class='scenes-dialog-content'>
          <div class='scenes-list'>
            <For each={ scenes } >{ (scene,i) =>
              (i() > 0) &&
              <div class={ i()===sceneIndex()? 'scenes-entry scenes-selected' : 'scenes-entry'}
                   onClick={ () => setSceneIndex( i() ) }>
                <span>{i()}</span>
                <span>{scene.title}</span>
              </div>
            }</For>
          </div>
          <div class='scenes-canvas-outer'>
            <div class='scenes-canvas-inner'>
              <CameraProvider>
                <SceneProvider name={ `#${sceneIndex()}` } config={{ preview: true, debug: false, labels: props.config?.labels, source: false }}>
                  <SceneCanvas height="100%" width="100%" />
                </SceneProvider>
              </CameraProvider>
            </div>
          </div>
        </div>
      </DialogContent>
      <DialogActions>
        <Button size="small" onClick={ ()=>props.close() } color="primary">Close</Button>
        {/* <Button size="small" onClick={ exportAs( 'svg', 'image/svg+xml' ) } color="primary">Save SVG</Button>
        <Button size="small" onClick={ exportAs( 'pdf', 'application/pdf' ) } color="primary">Save PDF</Button>
        <Button size="small" onClick={ exportAs( 'ps', 'application/postscript' ) } color="primary">Save Postscript</Button> */}
      </DialogActions>
    </Dialog>
  );
}

export { ScenesDialog };
