
import { Link } from "@kobalte/core/link";
import { Divider, Menu, MenuItem, SubMenu } from "../../framework/menus.jsx";
import { useEditor } from '../../framework/context/editor.jsx';

const LinkItem  = props => (
  <MenuItem>
    <Link class="link" href={props.href} target="_blank" rel="noopener">
      {props.label}
    </Link>
  </MenuItem>
);

export const HelpMenu = () =>
{
  const { setState, fetchDesignUrl, guard } = useEditor();

  const fetchStarter = resource => () => guard(() =>
  {
    setState( 'ignoreDesignName', true );  // transient, means we'll still have an untitled design after the fetch
    setState( 'fileHandle', undefined );
    const url = new URL( '/app/classic/resources/' + resource, window.location.toString() ) .toString();
    fetchDesignUrl( url, { preview: false, debug: false } );
  })

    return (
      <Menu label="Help">
        <MenuItem disabled={true}>Quick Start</MenuItem>
        <SubMenu label="Symmetry Starters">
          <MenuItem onClick={fetchStarter('com/vzome/starters/symmetry/icosahedral/starter.vZome')}>Icosahedral / Dodecahedral</MenuItem>
          <MenuItem onClick={fetchStarter('com/vzome/starters/symmetry/octahedral/starter.vZome')}>Cubic / Octahedral</MenuItem>
          <MenuItem onClick={fetchStarter('com/vzome/starters/symmetry/octahedral/sqrt2/starter.vZome')}>Cubic / Octahedral √2</MenuItem>
          <MenuItem onClick={fetchStarter('com/vzome/starters/symmetry/tetrahedral/starter.vZome')}>Tetrahedral</MenuItem>
        </SubMenu>
        <SubMenu label="3D Printing Starters">
          <MenuItem onClick={fetchStarter('org/vorthmann/zome/print3d/redStruts/struts-template-enlarged.vZome')}>Red-tip Struts</MenuItem>
          <MenuItem onClick={fetchStarter('org/vorthmann/zome/print3d/yellowStruts/struts-template-enlarged.vZome')}>Yellow-tip Struts</MenuItem>
          <MenuItem onClick={fetchStarter('org/vorthmann/zome/print3d/blueStruts/struts-template-enlarged.vZome')}>Blue-tip Struts</MenuItem>
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
