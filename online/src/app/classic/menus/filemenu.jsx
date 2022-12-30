
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import MenuItem from "@suid/material/MenuItem"
import Divider from "@suid/material/Divider";
import { createSignal } from "solid-js";
import { controllerAction, subController } from "../controllers-solid";

export const FileMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const handleClose = () => setAnchorEl( null ); 

  return (
    <div>
      <Button
        id="file-menu-button"
        aria-controls={open() ? "file-menu-menu" : undefined}
        aria-haspopup="true"
        aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        File
      </Button>
      <Menu
        id="file-menu-menu"
        anchorEl={anchorEl()}
        open={open()}
        onClose={handleClose}
        MenuListProps={{ "aria-labelledby": "file-menu-button" }}
      >
        <MenuItem disabled={true} onClick={handleClose}>New Design</MenuItem>
        <MenuItem disabled={true} onClick={handleClose}>Open</MenuItem>
        <MenuItem disabled={true} onClick={handleClose}>Save</MenuItem>
      </Menu>
    </div>
  );
}
