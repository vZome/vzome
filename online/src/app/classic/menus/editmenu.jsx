
import { createEffect } from "solid-js";
import { Divider, Menu, MenuAction, MenuItem, SubMenu, createMenuAction } from "../../framework/menus.jsx";

import { controllerExportAction, subController, useEditor } from "../../../viewer/context/editor.jsx";

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
    <input ref={colorInputElement} type="color" id="color-picker" name="color-picker" class='hidden-color-input' />
  </>);
}

export const EditMenu = () =>
{
  const { rootController, setState, controllerAction } = useEditor();
  const EditAction = createMenuAction( rootController() );
  const undoRedoController = () => subController( rootController(), 'undoRedo' );
  const UndoRedoAction = createMenuAction( undoRedoController() );

  const doCut = () =>
  {
    controllerExportAction( rootController(), 'cmesh', { selection: true } )
      .then( text => {
        navigator.clipboard .writeText( text );
        controllerAction( rootController(), 'Delete' );
        });
  }
  const doCopy = () =>
  {
    controllerExportAction( rootController(), 'cmesh', { selection: true } )
      .then( text => {
        navigator.clipboard .writeText( text );
        });
  }
  const doPaste = () =>
  {
    if ( ! navigator.clipboard .readText ) {
      console.warn( 'Clipboard paste is not supported in this browser.' );
      setState( 'problem', 'Clipboard paste is not supported in this browser.' );
      return;
    }
    navigator.clipboard .readText()
      .then( text => {
        controllerAction( rootController(), "ImportColoredMeshJson/clipboard", { vef: text } )
      });
  }

  return (
      <Menu label="Edit">
        <UndoRedoAction label="Undo"     action="undo"    mods="⌘" key="Z" />
        <UndoRedoAction label="Redo"     action="redo"    mods="⌘" key="Y" />
        <UndoRedoAction label="Undo All" action="undoAll" mods="⌥⌘" key="Z" />
        <UndoRedoAction label="Redo All" action="redoAll" mods="⌥⌘" key="Y" />

        <Divider />

        <EditAction label="Cut"    onClick={doCut}   mods="⌘" key="X" />
        <EditAction label="Copy"   onClick={doCopy}  mods="⌘" key="C" />
        <MenuAction label="Paste"  onClick={doPaste} mods="⌘" key="V" />
        <EditAction label="Delete" action="Delete" code="Delete|Backspace" />

        <Divider />

        <EditAction label="Select All"       action="SelectAll"       mods="⌘" key="A" />
        <EditAction label="Select Neighbors" action="SelectNeighbors" mods="⌥⌘" key="A" />
        <EditAction label="Invert Selection" action="InvertSelection" />
        <SubMenu label="Select">
          <EditAction label="Balls" action="AdjustSelectionByClass/selectBalls" />
          <EditAction label="Struts" action="AdjustSelectionByClass/selectStruts" />
          <EditAction label="Panels" action="AdjustSelectionByClass/selectPanels" />
          <EditAction label="Automatic Struts" action="SelectAutomaticStruts" />
        </SubMenu>
        <SubMenu label="Deselect">
          <EditAction label="Balls" action="AdjustSelectionByClass/deselectBalls" />
          <EditAction label="Struts" action="AdjustSelectionByClass/deselectStruts" />
          <EditAction label="Panels" action="AdjustSelectionByClass/deselectPanels" />
          <EditAction label="All" action="DeselectAll" />
        </SubMenu>

        <Divider />

        <EditAction label="Select Coplanar"    action="SelectCoplanar" />
        <EditAction label="Select Half Space"  action="SelectByPlane" />
        <EditAction label="Select by Diameter" action="SelectByDiameter" />
        <EditAction label="Select by Radius"   action="SelectByRadius" />

        <Divider />

        <EditAction label="Group"   action="GroupSelection/group" mods="⌘" key="G" />
        <EditAction label="Ungroup" action="GroupSelection/ungroup" mods="⌥⌘" key="G" />

        <Divider />

        <EditAction label="Hide"            action="hideball"   mods="⌃" key="H" />
        <EditAction label="Show All Hidden" action="ShowHidden" mods="⌥⌃" key="H" />

        <Divider />

        <SetColorItem ctrlr={rootController()} />

        <SubMenu label="Set Opacity">
          <EditAction label="Opaque" action="TransparencyMapper@255" disabled={true} />
          <EditAction label="95%"    action="TransparencyMapper@242" disabled={true} />
          <EditAction label="75%"    action="TransparencyMapper@192" disabled={true} />
          <EditAction label="50%"    action="TransparencyMapper@127" disabled={true} />
          <EditAction label="25%"    action="TransparencyMapper@63"  disabled={true} />
          <EditAction label="5%"     action="TransparencyMapper@13"  disabled={true} />
        </SubMenu>

        <EditAction label="Copy Last Selected Color"  action="MapToColor/CopyLastSelectedColor" />
        <EditAction label="Reset Colors"              action="MapToColor/SystemColorMap" />

        <SubMenu label="Color Effects">
          <EditAction label="Complement"           action="MapToColor/ColorComplementor" />
          <EditAction label="Invert"               action="MapToColor/ColorInverter" />
          <EditAction label="Maximize"             action="MapToColor/ColorMaximizer" />
          <EditAction label="Soften"               action="MapToColor/ColorSoftener" />
          <EditAction label="Darken with Distance" action="MapToColor/DarkenWithDistance" />
          <EditAction label="Darken near Origin"   action="MapToColor/DarkenNearOrigin" />
        </SubMenu>

        <SubMenu label="Map Colors">
          <EditAction label="To Centroid"              action="MapToColor/RadialCentroidColorMap" />
          <EditAction label="To Direction"             action="MapToColor/RadialStandardBasisColorMap" />
          <EditAction label="To Canonical Orientation" action="MapToColor/CanonicalOrientationColorMap" />
          <EditAction label="To Normal Polarity"       action="MapToColor/NormalPolarityColorMap" />
          <EditAction label="To Octant"                action="MapToColor/CentroidByOctantAndDirectionColorMap" />
          <EditAction label="To Coordinate Plane"      action="MapToColor/CoordinatePlaneColorMap" />
          <EditAction label="System by Centroid"       action="MapToColor/SystemCentroidColorMap" />
          <EditAction label="Nearest Predefined Orbit"             action="MapToColor/NearestPredefinedOrbitColorMap" />
          <EditAction label="Nearest Predefined Orbit by Centroid" action="MapToColor/CentroidNearestPredefinedOrbitColorMap" />
          <EditAction label="Nearest Special Orbit"                action="MapToColor/NearestSpecialOrbitColorMap" />
          <EditAction label="Nearest Special Orbit by Centroid"    action="MapToColor/CentroidNearestSpecialOrbitColorMap" />
        </SubMenu>

      </Menu>
  );
}
