
import { mergeProps } from "solid-js";
import { Divider, Menu, MenuAction, createMenuAction } from "../../framework/menus.jsx";

import { useWorkerClient } from "../../../workerClient/context.jsx";
import { useSymmetry } from "../classic.jsx";
import { controllerAction } from "../../../workerClient/controllers-solid.js";

export const SymmetryAction = ( props ) =>
{
  const { symmetryController } = useSymmetry();
  const onClick = () => 
  {
    controllerAction( symmetryController(), props.action );
  }
  // I was destructuring props here, and lost reactivity!
  //  It usually doesn't matter for menu items, except when there is checkbox state.
  return MenuAction( mergeProps( { onClick }, props ) );
}

export const ToolsMenu = () =>
{
  const { rootController } = useWorkerClient();
  const EditAction = createMenuAction( rootController() );
  const { showPolytopesDialog } = useSymmetry();

  const openPolytopes = () =>
  {
    showPolytopesDialog();
  }

  return (
      <Menu label="Tools">
        <SymmetryAction label="Icosahedral Symmetry"         action="icosasymm" mods="⌘" key="I" />
        <SymmetryAction label="Cubic / Octahedral Symmetry"  action="octasymm"  mods="⌥⌘" key="C" />
        <SymmetryAction label="Tetrahedral Symmetry"         action="tetrasymm" mods="⌥⌘" key="T" />

        <EditAction label="Point Reflection" action="pointsymm" />

        <Divider />

        <SymmetryAction label="Replace With Panels" action="ReplaceWithShape" disabled={true} />

        <Divider />
        
        <MenuAction label="Generate Polytope..." onClick={ openPolytopes } mods="⌥⌘" key="P" />

        <Divider />
        
        <EditAction label="Validate Paneled Surface" action="Validate2Manifold" />

      </Menu>
  );
}
