
import Typography from '@suid/material/Typography'
import Link from '@suid/material/Link'
import { DiskIcon, WorldIcon } from '../framework/icons.jsx';
import { Menubar } from "@kobalte/core/menubar";
import { FileMenu } from './menus/filemenu.jsx';
import { EditMenu } from './menus/editmenu.jsx';
import { ConstructMenu } from './menus/constructmenu.jsx';
import { ToolsMenu } from './menus/toolsmenu.jsx';
import { SystemMenu } from './menus/systemmenu.jsx';
import { HelpMenu } from './menus/help.jsx';

import { controllerProperty, useEditor } from '../framework/context/editor.jsx';

import { VZomeAppBar } from './components/appbar.jsx';
import { ClassicEditor } from './classic.jsx';
import { SymmetryProvider } from './context/symmetry.jsx';
import { CommandsProvider } from './context/commands.jsx';

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

export const ClassicApp = () =>
{
  return (
    <SymmetryProvider>
      <CommandsProvider>
        <VZomeAppBar title=''
          spacer={ <>
            <Menubar>
              <FileMenu/>
              <EditMenu/>
              <ConstructMenu/>
              <ToolsMenu/>
              <SystemMenu/>
              <HelpMenu/>
            </Menubar>
            <Persistence/>
          </>}
          about={ <>
            <Typography gutterBottom>
              vZome is a design application
              for <Link target="_blank" href="https://zometool.com" rel="noopener" >Zometool</Link> and other geometric systems.
            </Typography>
            <Typography gutterBottom>
              This web application is a work in progress, still at the "beta" stage,
              since it is not quite a complete replacement for
              the <Link target="_blank" rel="noopener" href="https://vzome.com/home/index/vzome-7/">vZome desktop app</Link>
            </Typography>
            <Typography gutterBottom>
              If you want to stay informed about progress on this app, or better yet offer feedback,
              join the <Link target="_blank" rel="noopener" href="https://discord.gg/vhyFsNAFPS">Discord server</Link>.
            </Typography>
          </> }
        />
        <ClassicEditor/>
      </CommandsProvider>
    </SymmetryProvider>
  );
}
