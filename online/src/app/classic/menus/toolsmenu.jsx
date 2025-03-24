
import { CommandAction, Divider, Menu } from "../../framework/menus.jsx";

import { controllerProperty, useEditor } from "../../framework/context/editor.jsx";

export const ToolsMenu = () =>
{
  const { rootController } = useEditor();
  const symmetries = () => controllerProperty( rootController(), 'symmetryPerspectives', 'symmetryPerspectives', true );
  const hasIcosa = () => symmetries().includes( 'icosahedral' );
  const hasOcta = () => symmetries().includes( 'octahedral' );
  const hasTetra = () => hasIcosa() || hasOcta();

  return (
      <Menu label="Tools">
        <Show when={ hasIcosa() }>
          <CommandAction label="Icosahedral Symmetry"         action="icosasymm" />
        </Show>
        <Show when={ hasOcta() }>
          <CommandAction label="Cubic / Octahedral Symmetry"  action="octasymm" />
        </Show>
        <Show when={ hasTetra() }>
          <CommandAction label="Tetrahedral Symmetry"         action="tetrasymm" />
        </Show>

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
