
import { ContextMenu } from "@kobalte/core";

import { subController, useEditor } from '../../../viewer/context/editor.jsx';
import { useCamera } from "../../../viewer/context/camera.jsx";

import { ContextMenuItem, ContextMenuSeparator } from "../../framework/menus";

export const ContextualMenu = props =>
{
  const { state, setState, rootController, controllerAction } = useEditor();
  const { state: cameraState, setCamera } = useCamera();
  const pickingController  = () => subController( rootController(), 'picking' );
  const typeMatches = type =>
  {
    switch (type) {
      case 'ball':
        return state.picked?.type === 'ball';
      case 'strut':
        return state.picked?.type && state.picked.type !== 'ball';
      case 'panel':
        return state.picked?.type === null;
      default:
        return true;
    }
  }

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
  const PickingItem = props =>
  {
    const disabled = () => props.disabled || !typeMatches( props.type );
    return <ContextMenuItem onSelect={()=>doAction( props.action )} label={props.label} disabled={disabled()} />;
  }

  return (
      <Switch fallback={
          <ContextMenu.Content class="context-menu__content">
            <ContextMenuItem onSelect={copyCamera} label='Copy This View' />
            <ContextMenuItem onSelect={useCopiedCamera} label='Use Copied View' />
            <ContextMenuSeparator/>
            <ContextMenuItem onSelect={lookAtOrigin} label='Look At Origin' />
            <ContextMenuSeparator/>
            <ContextMenuItem onSelect={props.showDialog('color')} label='Set Background Color...' />
          </ContextMenu.Content>
        }>
        <Match when={ !!state.picked }>
          <ContextMenu.Content class="context-menu__content">
            <ContextMenuItem onSelect={lookAtThis} label='Look At This' />

            {/* if ( oldTools ) {
              <PickingItem action='lookAtSymmetryCenter' label='Look At Symmetry Center' disabled />
                <ContextMenuSeparator/>
                <PickingItem action='SymmetryCenterChange' label='Set Symmetry Center' disabled />
                <PickingItem action='SymmetryAxisChange' label='Set Symmetry Axis' disabled />
            } */}

            <ContextMenuSeparator/>

            <ContextMenuItem onSelect={props.showDialog( 'label', state.picked.id, state.picked.label )} label='Label This' />

            <ContextMenuSeparator/>

            <PickingItem action='setWorkingPlane' label='Set Working Plane' type='panel' disabled />
            <PickingItem action='setWorkingPlaneAxis' label='Set Working Plane Axis' type='strut' disabled />

            <ContextMenuSeparator/>

            <PickingItem action='SelectCoplanar' label='Select Coplanar' type='panel' />
            <PickingItem action='SelectCollinear' label='Select Collinear' type='strut' />
            <PickingItem action='SelectParallelStruts' label='Select Parallel Struts' type='strut' />
            <PickingItem action='AdjustSelectionByOrbitLength/selectSimilarStruts' label='Select Similar Struts' type='strut' />

            <PickingItem action='undoToManifestation' label='Undo Including This' disabled />

            <ContextMenuSeparator/>

            <PickingItem action='ReplaceWithShape' label='Replace With Panels' disabled />

            <ContextMenuSeparator/>

            {/* if(controller .propertyIsTrue( "create.strut.prototype" )) {
                // DJH - pretty much nobody will use these commands, 
                // so only add them to the menu if it's enabled in the prefs file.
                <PickingItem action='CreateStrutAxisPlus0' label='Create Strut AxisPlus0' disabled />
                <PickingItem action='CreateStrutPrototype' label='Create Strut Prototype' disabled />
            } */}

            <PickingItem action='setBuildOrbitAndLength' label='Build With This' type='strut' disabled />
            {/* // this .add( enabler .setMenuAction( "showProperties-"+key, this .controller, new JMenuItem( "Show Properties" ) ) ); */}
          </ContextMenu.Content>

        </Match>
      </Switch>
  );
}
