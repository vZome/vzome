import { render } from 'solid-js/web';
import { ErrorBoundary } from "solid-js";

import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'
import { DiskIcon, WorldIcon } from '../framework/icons.jsx';
import { FileMenu } from './menus/filemenu.jsx';
import { EditMenu } from './menus/editmenu.jsx';
import { ConstructMenu } from './menus/constructmenu.jsx';
import { ToolsMenu } from './menus/toolsmenu.jsx';
import { SystemMenu } from './menus/systemmenu.jsx';
import { HelpMenu } from './menus/help.jsx';

import { WorkerStateProvider } from '../../viewer/context/worker.jsx';
import { EditorProvider, controllerProperty, useEditor } from '../../viewer/context/editor.jsx';
import { ViewerProvider } from '../../viewer/context/viewer.jsx';
import { CameraProvider } from '../../viewer/context/camera.jsx';

import { VZomeAppBar } from './components/appbar.jsx';
import { ClassicEditor } from './classic.jsx';
import { SymmetryProvider } from './context/symmetry.jsx';
import { ImageCaptureProvider } from '../../viewer/context/export.jsx';

const Persistence = () =>
{
  const { state, rootController } = useEditor();
  const edited = () => controllerProperty( rootController(), 'edited' ) === 'true';
  return (
    <div class='persistence' >
      <Show when={state?.designName} >
        <div class='persistence-box' >
          <div class={ edited()? 'persistence-icon' : 'persistence-icon-saved' }>
            <Show when={state?.fileHandle} fallback={
              <WorldIcon/>
            }>
              <DiskIcon/>
            </Show>
          </div>
          <div class='persistence-title' >{state?.designName}</div>
        </div>
      </Show>
    </div>
  )
}

const Classic = () =>
{
  return (
    <ErrorBoundary fallback={ err => <div>{err.toString()}</div> } >
      <WorkerStateProvider>
      <CameraProvider name='common'>
      <ImageCaptureProvider>
      <ViewerProvider config={{ labels: true }}>
      <EditorProvider>
        <SymmetryProvider>
          <VZomeAppBar title='BETA'
            spacer={ <>
              <FileMenu/>
              <EditMenu/>
              <ConstructMenu/>
              <ToolsMenu/>
              <SystemMenu/>
              <HelpMenu/>
              <Persistence/>
            </>}
            about={ <>
              <Typography gutterBottom>
                vZome Online is a work in progress, still at the "beta" stage.
                The intention is for it to be a complete replacement for
                the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>,
                which is a modeling tool
                for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link> and other geometries.
              </Typography>
              <Typography gutterBottom>
                You can open most existing vZome design files, but not all of them.
                You can save files, too, but you should not trust the software too far;
                if you open an existing file, never save back to the same file, to avoid data loss.
              </Typography>
              <Typography gutterBottom>
                If you want to stay informed about progress on this app, or better yet offer feedback,
                join the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">Discord server</Link>.
              </Typography>
            </> }
          />
          <ClassicEditor/>
        </SymmetryProvider>
      </EditorProvider>
      </ViewerProvider>
      </ImageCaptureProvider>
      </CameraProvider>
      </WorkerStateProvider>
    </ErrorBoundary>
  );
}

render( Classic, document.getElementById( 'root' ) );
