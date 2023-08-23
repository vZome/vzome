
import { Link } from "@kobalte/core";
import { Divider, Menu, MenuItem, SubMenu } from "../../framework/menus.jsx";

const LinkItem  = props => (
  <MenuItem>
    <Link.Root class="link" href={props.href} target="_blank" rel="noopener">
      {props.label}
    </Link.Root>
  </MenuItem>
);

export const HelpMenu = () =>
{
  return (
      <Menu label="Help">
        <MenuItem disabled={true}>Quick Start</MenuItem>
        <SubMenu label="Symmetry Starters">
          <MenuItem disabled={true}>Icosahedral / Dodecahedral</MenuItem>
          <MenuItem disabled={true}>Cubic / Octahedral</MenuItem>
          <MenuItem disabled={true}>Cubic / Octahedral √2</MenuItem>
          <MenuItem disabled={true}>Tetrahedral</MenuItem>
        </SubMenu>
        <SubMenu label="3D Printing Starters">
          <MenuItem disabled={true}>Red-tip Struts</MenuItem>
          <MenuItem disabled={true}>Yellow-tip Struts</MenuItem>
          <MenuItem disabled={true}>Blue-tip Struts</MenuItem>
        </SubMenu>

        <Divider/>

        <LinkItem label='vZome Home'                 href='https://vzome.com' />
        <LinkItem label='Sharing vZome Files Online' href='https://vzome.github.io/vzome/sharing.html' />
        <LinkItem label='vZome Tips on YouTube'      href='https://www.youtube.com/c/Vzome' />

        <SubMenu label="Social Media">
          <LinkItem label='Geometry Blog'  href='https://vorth.github.io/vzome-sharing/' />
          <LinkItem label='Facebook Page'  href='https://www.facebook.com/vZome' />
          <LinkItem label='Twitter Page'   href='https://twitter.com/vZome' />
          <LinkItem label='Discord Server' href='https://discord.com/invite/vhyFsNAFPS' />
        </SubMenu>

        <SubMenu label="Misc. Online Documentation">
          <LinkItem label='The Direction (Orbit) Triangle' href='https://vorth.github.io/vzome-sharing/2019/07/19/vzome-icosahedral-orbits.html' />
          <LinkItem label='Capturing Vector Graphics'      href='https://vzome.github.io/vzome//capture-vector-graphics.html' />
        </SubMenu>

        <SubMenu label="Other Links">
          <LinkItem label='GitHub Source'                 href='https://github.com/vZome/vzome' />
          <LinkItem label='Logo T-Shirt'                  href='https://www.neatoshop.com/product/vZome-tetrahedron' />
          <LinkItem label='3D-Printed Parts at Shapeways' href='https://www.shapeways.com/shops/vzome' />
          <LinkItem label='Models on SketchFab'           href='https://sketchfab.com/scottvorthmann' />
          <LinkItem label='Observable Notebooks'          href='https://observablehq.com/collection/@vorth/vzome' />
        </SubMenu>

      </Menu>
  );
}
