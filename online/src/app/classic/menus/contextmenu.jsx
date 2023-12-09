
import { ContextMenu } from "@kobalte/core";
import { ContextMenuItem, ContextMenuSeparator } from "../../framework/menus";
import { useWorkerClient } from "../../../viewer/context/worker.jsx";
import { controllerAction, subController } from "../../../viewer/util/controllers-solid";
import { useCamera } from "../../../viewer/context/camera";

export const ContextualMenu = props =>
{
  const { state, setState, rootController } = useWorkerClient();
  const { state: cameraState, setCamera } = useCamera();
  const pickingController  = () => subController( rootController(), 'picking' );
  const notPicking = () => ! state.picked;

  const lookAt = position =>
  {
    const { lookAt, ...others } = cameraState.camera;
    // discard old lookAt
    setCamera( { lookAt: position, ...others } );
  }

  const lookAtThis   = () => lookAt( state.picked.position );
  const lookAtOrigin = () => lookAt( [0,0,0] );

  const copyOfCamera = camera =>
  {
    const { up, lookAt, lookDir, ...rest } = camera; // don't want copy-by-reference for the arrays
    return { ...rest, up: [...up], lookAt: [...lookAt], lookDir: [...lookDir] };
  }
  const copyCamera = () => setState( 'copiedCamera', copyOfCamera( cameraState.camera ) );
  
  const useCopiedCamera = () =>
  {
    setCamera( copyOfCamera( state.copiedCamera ) );
  }

  const doAction = action =>
  {
    controllerAction( pickingController(), action, { id: state.picked.id } );
  }
  const PickingItem = props => <ContextMenuItem onSelect={()=>doAction( props.action )} label={props.label} disabled={notPicking()} />;

  return (
    <ContextMenu.Content class="context-menu__content">

      <ContextMenuItem onSelect={copyCamera} label='Copy This View' />
      <ContextMenuItem onSelect={useCopiedCamera} label='Use Copied View' />

      <ContextMenuSeparator/>

      <ContextMenuItem onSelect={lookAtThis} label='Look At This' disabled={notPicking()} />
      <ContextMenuItem onSelect={lookAtOrigin} label='Look At Origin' />

      {/* if ( oldTools ) {
        <ContextMenuItem action='lookAtSymmetryCenter' label='Look At Symmetry Center' disabled />
          <ContextMenuSeparator/>
          <ContextMenuItem action='SymmetryCenterChange' label='Set Symmetry Center' disabled />
          <ContextMenuItem action='SymmetryAxisChange' label='Set Symmetry Axis' disabled />
      } */}

      <ContextMenuSeparator/>

      <ContextMenuItem action='setWorkingPlane' label='Set Working Plane' disabled />
      <ContextMenuItem action='setWorkingPlaneAxis' label='Set Working Plane Axis' disabled />

      <ContextMenuSeparator/>

      <PickingItem action='SelectCoplanar' label='Select Coplanar' />
      <PickingItem action='SelectCollinear' label='Select Collinear' />
      <PickingItem action='SelectParallelStruts' label='Select Parallel Struts' />
      <PickingItem action='AdjustSelectionByOrbitLength/selectSimilarStruts' label='Select Similar Struts' />

      <ContextMenuItem action='undoToManifestation' label='Undo Including This' disabled />

      <ContextMenuSeparator/>

      <ContextMenuItem action='setBackgroundColor' label='Set Background Color...' disabled />

      <ContextMenuSeparator/>

      <ContextMenuItem action='ReplaceWithShape' label='Replace With Panels' disabled />

      <ContextMenuSeparator/>

      {/* if(controller .propertyIsTrue( "create.strut.prototype" )) {
          // DJH - pretty much nobody will use these commands, 
          // so only add them to the menu if it's enabled in the prefs file.
          <ContextMenuItem action='CreateStrutAxisPlus0' label='Create Strut AxisPlus0' disabled />
          <ContextMenuItem action='CreateStrutPrototype' label='Create Strut Prototype' disabled />
      } */}

      <ContextMenuItem action='setBuildOrbitAndLength' label='Build With This' disabled />
      {/* // this .add( enabler .setMenuAction( "showProperties-"+key, this .controller, new JMenuItem( "Show Properties" ) ) ); */}

    </ContextMenu.Content>
  );
}
