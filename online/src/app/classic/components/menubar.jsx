
import Toolbar from '@suid/material/Toolbar';

import { FileMenu } from '../menus/filemenu.jsx';
import { EditMenu } from '../menus/editmenu.jsx';
import { ConstructMenu } from '../menus/constructmenu.jsx';
import { ToolsMenu } from '../menus/toolsmenu.jsx';
import { SystemMenu } from '../menus/systemmenu.jsx';
import { HelpMenu } from '../menus/help.jsx';

export const MenuBar = ( props ) =>
{
  return (
    <Toolbar variant="dense" disableGutters="true">
      <FileMenu      controller={props.controller} />
      <EditMenu      controller={props.controller} />
      <ConstructMenu controller={props.controller} />
      <ToolsMenu     controller={props.controller} />
      <SystemMenu    controller={props.controller} />
      <HelpMenu      controller={props.controller} />
    </Toolbar>
  )
}