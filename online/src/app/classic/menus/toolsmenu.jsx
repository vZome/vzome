
import { CommandAction, Divider, Menu } from "../../framework/menus.jsx";

export const ToolsMenu = () =>
{
  return (
      <Menu label="Tools">
        <CommandAction label="Icosahedral Symmetry"         action="icosasymm" />
        <CommandAction label="Cubic / Octahedral Symmetry"  action="octasymm" />
        <CommandAction label="Tetrahedral Symmetry"         action="tetrasymm" />

        <CommandAction label="Point Reflection" action="pointsymm" />

        <Divider />

        <CommandAction label="Replace With Panels" action="ReplaceWithShape" />

        <Divider />
        
        <CommandAction label="Generate Polytope..." action="showPolytopeDialog" />

        <Divider />
        
        <CommandAction label="Validate Paneled Surface" action="Validate2Manifold" />

      </Menu>
  );
}
