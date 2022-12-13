
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import MenuItem from "@suid/material/MenuItem"
import { createSignal } from "solid-js";

export const FileMenu = () =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const handleClose = () => setAnchorEl( null ); 

  return (
    <div>
      <Button
        id="basic-button"
        aria-controls={open() ? "basic-menu" : undefined}
        aria-haspopup="true"
        aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        File
      </Button>
      <Menu
        id="basic-menu"
        anchorEl={anchorEl()}
        open={open()}
        onClose={handleClose}
        MenuListProps={{ "aria-labelledby": "basic-button" }}
      >
        <MenuItem disabled={true} onClick={handleClose}>New Design</MenuItem>
        <MenuItem disabled={true} onClick={handleClose}>Open</MenuItem>
        <MenuItem disabled={true} onClick={handleClose}>Save</MenuItem>
      </Menu>
    </div>
  );
}
