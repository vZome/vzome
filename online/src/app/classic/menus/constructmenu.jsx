
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import MenuItem from "@suid/material/MenuItem"
import { createSignal } from "solid-js";

export const ConstructMenu = () =>
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
        Construct
      </Button>
      <Menu
        id="basic-menu"
        anchorEl={anchorEl()}
        open={open()}
        onClose={handleClose}
        MenuListProps={{ "aria-labelledby": "basic-button" }}
      >
        <MenuItem disabled={true} onClick={handleClose}>Panel</MenuItem>
      </Menu>
    </div>
  );
}
