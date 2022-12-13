
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import MenuItem from "@suid/material/MenuItem"
import { createSignal } from "solid-js";
import { controllerAction, subController } from "../controllers-solid";

export const EditMenu = ( props ) =>
{
  const undoRedoController = () => subController( props.controller, "undoRedo" );
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const handleClose = () => setAnchorEl( null );
  const doAction = action => () =>
  {
    setAnchorEl( null );
    controllerAction( undoRedoController(), action );
  }

  return (
    <div>
      <Button
        id="basic-button"
        aria-controls={open() ? "basic-menu" : undefined}
        aria-haspopup="true"
        aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        Edit
      </Button>
      <Menu
        id="basic-menu"
        anchorEl={anchorEl()}
        open={open()}
        onClose={handleClose}
        MenuListProps={{ "aria-labelledby": "basic-button" }}
      >
        <MenuItem onClick={ doAction( "undo" ) }>Undo</MenuItem>
        <MenuItem onClick={ doAction( "redo" ) }>Redo</MenuItem>
      </Menu>
    </div>
  );
}
