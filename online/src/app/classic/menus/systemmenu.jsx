
import { Divider, Menu, MenuAction, createCheckboxItem } from "../components/menuaction.jsx";

import { controllerProperty } from "../../../workerClient/controllers-solid.js";
import { useWorkerClient } from "../../../workerClient/index.js";
import { useSymmetry } from "../classic.jsx";

export const SystemMenu = () =>
{
  const { rootController } = useWorkerClient();

  const { showOrbitsDialog, showShapesDialog } = useSymmetry();

  const symmetries = () => controllerProperty( rootController(), 'symmetryPerspectives', 'symmetryPerspectives', true );
  const hasIcosa = () => symmetries() .includes( 'icosahedral' );
  const currentSymm = () => controllerProperty( rootController(), 'symmetry' ); // TODO move to useSymmetry?

  const EditAction = createCheckboxItem( rootController() );

  return (
      <Menu label="System">
        <Show when={ hasIcosa() }>
          <EditAction label="Icosahedral System" action="setSymmetry.icosahedral" checked={ currentSymm() === 'icosahedral' } />
        </Show>
        <EditAction label="Octahedral System" action="setSymmetry.octahedral" checked={ currentSymm() === 'octahedral' } />

        <Divider />
        
        <MenuAction label="Shapes..." onClick={ showShapesDialog } />

        <MenuAction label="Directions..." onClick={ showOrbitsDialog } />

        <EditAction label="Show Directions Graphically" action="toggleOrbitViews" disabled="true" checked="true" />
        <EditAction label="Show Strut Scales" action="toggleStrutScales" disabled="true" checked="true" />
        <EditAction label="Show Frame Labels" action="toggleFrameLabels" disabled="true" />
        <EditAction label="Show Panel Normals" action="toggleNormals" disabled="true" />

      </Menu>
  );
}
