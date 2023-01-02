
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import MenuItem from "@suid/material/MenuItem"
import Divider from "@suid/material/Divider";
import Link from '@suid/material/Link';
import { createSignal } from "solid-js";

const createLinkItem = doClose => ( { href, label } ) => (
  <MenuItem component={Link} href={href} target="_blank" rel="noopener" onClick={doClose}>{label}</MenuItem>
);

export const HelpMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );
  const LinkItem = createLinkItem( doClose );

  return (
    <div>
      <Button id="help-menu-button"
        aria-controls={open() ? "help-menu-menu" : undefined} aria-haspopup="true" aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        Help
      </Button>
      <Menu id="help-menu-menu" MenuListProps={{ "aria-labelledby": "help-menu-button" }}
        anchorEl={anchorEl()} open={open()} onClose={doClose}
      >
        <MenuItem disabled={true} onClick={doClose}>Quick Start...</MenuItem>
        <MenuItem disabled={true} onClick={doClose}>Symmetry Starters...</MenuItem>
        <MenuItem disabled={true} onClick={doClose}>3D Printing Starters...</MenuItem>

        <Divider/>

        <LinkItem label='vZome Home...'                 href='https://vzome.com' />
        <LinkItem label='Sharing vZome Files Online...' href='https://vzome.github.io/vzome/sharing.html' />
        <LinkItem label='vZome Tips on YouTube...'      href='https://www.youtube.com/c/Vzome' />

        <Divider/>

        <LinkItem label='Geometry Blog...'  href='https://vorth.github.io/vzome-sharing/' />
        <LinkItem label='Facebook Page...'  href='https://www.facebook.com/vZome' />
        <LinkItem label='Twitter Page...'   href='https://twitter.com/vZome' />
        <LinkItem label='Discord Server...' href='https://discord.com/invite/vhyFsNAFPS' />

        <Divider/>

        <LinkItem label='The Direction (Orbit) Triangle...' href='https://vorth.github.io/vzome-sharing/2019/07/19/vzome-icosahedral-orbits.html' />
        <LinkItem label='Capturing Vector Graphics...'      href='https://vzome.github.io/vzome//capture-vector-graphics.html' />

        <Divider/>

        <LinkItem label='GitHub Source...'                 href='https://github.com/vZome/vzome' />
        <LinkItem label='Logo T-Shirt...'                  href='https://www.neatoshop.com/product/vZome-tetrahedron' />
        <LinkItem label='3D-Printed Parts at Shapeways...' href='https://www.shapeways.com/shops/vzome' />
        <LinkItem label='Models on SketchFab...'           href='https://sketchfab.com/scottvorthmann' />
        <LinkItem label='Observable Notebooks...'          href='https://observablehq.com/collection/@vorth/vzome' />

      </Menu>
    </div>
  );
}
