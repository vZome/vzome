
import Toolbar from '@suid/material/Toolbar';

import { FileMenu } from '../menus/filemenu.jsx';
import { EditMenu } from '../menus/editmenu.jsx';
import { ConstructMenu } from '../menus/constructmenu.jsx';

export const MenuBar = ( props ) =>
{
  return (
    <Toolbar variant="dense" disableGutters="true">
      <FileMenu />
      <EditMenu controller={props.controller} />
      <ConstructMenu />
    </Toolbar>
  )
}