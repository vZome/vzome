
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import MenuItem from "@suid/material/MenuItem"
import { createSignal } from "solid-js";

export const FileMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

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
        onClose={doClose}
        MenuListProps={{ "aria-labelledby": "file-menu-button" }}
      >
        <MenuItem disabled={true} onClick={doClose}>New Design</MenuItem>
        <MenuItem disabled={true} onClick={doClose}>Open</MenuItem>
        <MenuItem disabled={true} onClick={doClose}>Save</MenuItem>
      </Menu>
    </div>
  );
}
