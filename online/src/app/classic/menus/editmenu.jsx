
import { createEffect } from "solid-js";
import { CommandAction, Divider, Menu, MenuItem, SubMenu } from "../../framework/menus.jsx";

import { useEditor } from '../../framework/context/editor.jsx';

const SetColorItem = props =>
{
  const { controllerAction } = useEditor();
  let colorInputElement;
  const handleClick = () =>
  {
    colorInputElement.click();
  }
  const setColor = color =>
  {
    controllerAction( props.ctrlr, `ColorManifestations/${color}ff` );
  }
  createEffect( () => {
    // skip the leading "#"
    colorInputElement.addEventListener( "change", e => setColor( e.target.value.substring(1) ), false );
  });
  return ( <>
    <MenuItem onClick={handleClick} >Set Color...</MenuItem>
    <input ref={colorInputElement} type="color" name="color-picker" class='hidden-color-input' />
  </>);
}

export const EditMenu = () =>
{
  return (
      <Menu label="Edit">
        <CommandAction label="Undo"     action="undo" />
        <CommandAction label="Redo"     action="redo" />
        <CommandAction label="Undo All" action="undoAll" />
        <CommandAction label="Redo All" action="redoAll" />

        <Divider />

        <CommandAction label="Cut" />
        <CommandAction label="Copy" />
        <CommandAction label="Paste" />
        <CommandAction label="Delete" />

        <Divider />

        <CommandAction label="Select All" action="SelectAll" />
        <CommandAction label="Select Neighbors" action="SelectNeighbors" />
        <CommandAction label="Invert Selection" action="InvertSelection" />
        <SubMenu label="Select">
          <CommandAction label="Balls" action="AdjustSelectionByClass/selectBalls" />
          <CommandAction label="Struts" action="AdjustSelectionByClass/selectStruts" />
          <CommandAction label="Panels" action="AdjustSelectionByClass/selectPanels" />
          <CommandAction label="Automatic Struts" action="SelectAutomaticStruts" />
        </SubMenu>
        <SubMenu label="Deselect">
          <CommandAction label="Balls" action="AdjustSelectionByClass/deselectBalls" />
          <CommandAction label="Struts" action="AdjustSelectionByClass/deselectStruts" />
          <CommandAction label="Panels" action="AdjustSelectionByClass/deselectPanels" />
          <CommandAction label="All" action="DeselectAll" />
        </SubMenu>

        <Divider />

        <CommandAction label="Select Coplanar"    action="SelectCoplanar" />
        <CommandAction label="Select Half Space"  action="SelectByPlane" />
        <CommandAction label="Select by Diameter" action="SelectByDiameter" />
        <CommandAction label="Select by Radius"   action="SelectByRadius" />

        <Divider />

        <CommandAction label="Group"   action="GroupSelection/group" />
        <CommandAction label="Ungroup" action="GroupSelection/ungroup" />

        <Divider />

        <CommandAction label="Hide"            action="hideball" />
        <CommandAction label="Show All Hidden" action="ShowHidden" />

        <Divider />

        <EditAction label="Reverse Orientations" action="ReverseOrientations"/>

        <Divider />

        <SetColorItem ctrlr={rootController()} />

        <SubMenu label="Set Opacity">
          <CommandAction label="Opaque" action="TransparencyMapper@255" disabled={true} />
          <CommandAction label="95%"    action="TransparencyMapper@242" disabled={true} />
          <CommandAction label="75%"    action="TransparencyMapper@192" disabled={true} />
          <CommandAction label="50%"    action="TransparencyMapper@127" disabled={true} />
          <CommandAction label="25%"    action="TransparencyMapper@63"  disabled={true} />
          <CommandAction label="5%"     action="TransparencyMapper@13"  disabled={true} />
        </SubMenu>

        <CommandAction label="Copy Last Selected Color"  action="MapToColor/CopyLastSelectedColor" />
        <CommandAction label="Reset Colors"              action="MapToColor/SystemColorMap" />

        <SubMenu label="Color Effects">
          <CommandAction label="Complement"           action="MapToColor/ColorComplementor" />
          <CommandAction label="Invert"               action="MapToColor/ColorInverter" />
          <CommandAction label="Maximize"             action="MapToColor/ColorMaximizer" />
          <CommandAction label="Soften"               action="MapToColor/ColorSoftener" />
          <CommandAction label="Darken with Distance" action="MapToColor/DarkenWithDistance" />
          <CommandAction label="Darken near Origin"   action="MapToColor/DarkenNearOrigin" />
        </SubMenu>

        <SubMenu label="Map Colors">
          <CommandAction label="To Centroid"              action="MapToColor/RadialCentroidColorMap" />
          <CommandAction label="To Direction"             action="MapToColor/RadialStandardBasisColorMap" />
          <CommandAction label="To Canonical Orientation" action="MapToColor/CanonicalOrientationColorMap" />
          <CommandAction label="To Normal Polarity"       action="MapToColor/NormalPolarityColorMap" />
          <CommandAction label="To Octant"                action="MapToColor/CentroidByOctantAndDirectionColorMap" />
          <CommandAction label="To Coordinate Plane"      action="MapToColor/CoordinatePlaneColorMap" />
          <CommandAction label="System by Centroid"       action="MapToColor/SystemCentroidColorMap" />
          <CommandAction label="Nearest Predefined Orbit"             action="MapToColor/NearestPredefinedOrbitColorMap" />
          <CommandAction label="Nearest Predefined Orbit by Centroid" action="MapToColor/CentroidNearestPredefinedOrbitColorMap" />
          <CommandAction label="Nearest Special Orbit"                action="MapToColor/NearestSpecialOrbitColorMap" />
          <CommandAction label="Nearest Special Orbit by Centroid"    action="MapToColor/CentroidNearestSpecialOrbitColorMap" />
        </SubMenu>

      </Menu>
  );
}
