
import { createEffect } from "solid-js";
import { DeclarativeMenu, MenuItem } from "../../framework/menus.jsx";

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
  const items = [
    { label: "Undo",     action: "undo" },
    { label: "Redo",     action: "redo" },
    { label: "Undo All", action: "undoAll" },
    { label: "Redo All", action: "redoAll" },

    { divider: true },

    { label: "Cut" },
    { label: "Copy" },
    { label: "Paste" },
    { label: "Delete" },

    { divider: true },

    { label: "Select All",          action: "SelectAll" },
    { label: "Select Neighbors",    action: "SelectNeighbors" },
    { label: "Invert Selection",    action: "InvertSelection" },
    { 
      label: "Select",
      submenu: [
        { label: "Balls",            action: "AdjustSelectionByClass/selectBalls" },
        { label: "Struts",           action: "AdjustSelectionByClass/selectStruts" },
        { label: "Panels",           action: "AdjustSelectionByClass/selectPanels" },
        { label: "Automatic Struts", action: "SelectAutomaticStruts" },
      ]
    },
    {
      label: "Deselect",
      submenu: [
        { label: "Balls",  action: "AdjustSelectionByClass/deselectBalls" },
        { label: "Struts", action: "AdjustSelectionByClass/deselectStruts" },
        { label: "Panels", action: "AdjustSelectionByClass/deselectPanels" },
        { label: "All",    action: "DeselectAll" },
      ]
    },

    { divider: true },

    { label: "Select Coplanar",     action: "SelectCoplanar" },
    { label: "Select Half Space",   action: "SelectByPlane" },
    { label: "Select by Diameter",  action: "SelectByDiameter" },
    { label: "Select by Radius",    action: "SelectByRadius" },

    { divider: true },

    { label: "Group",   action: "GroupSelection/group" },
    { label: "Ungroup", action: "GroupSelection/ungroup" },

    { divider: true },

    { label: "Hide",            action: "hideball" },
    { label: "Show All Hidden", action: "ShowHidden" },

    { divider: true },

    { menuSlot: "setColorItem" },
    { label: "Set Opacity",
      submenu: [
        { label: "Opaque", action: "TransparencyMapper@255", disabled: true },
        { label: "95%",    action: "TransparencyMapper@242", disabled: true },
        { label: "75%",    action: "TransparencyMapper@192", disabled: true },
        { label: "50%",    action: "TransparencyMapper@127", disabled: true },
        { label: "25%",    action: "TransparencyMapper@63",  disabled: true },
        { label: "5%",     action: "TransparencyMapper@13",  disabled: true },
      ]
    },

    { label: "Copy Last Selected Color",  action: "MapToColor/CopyLastSelectedColor" },
    { label: "Reset Colors",              action: "MapToColor/SystemColorMap" },

    { 
      label: "Color Effects",
      submenu: [
        { label: "Complement",           action: "MapToColor/ColorComplementor" },
        { label: "Invert",               action: "MapToColor/ColorInverter" },
        { label: "Maximize",             action: "MapToColor/ColorMaximizer" },
        { label: "Soften",               action: "MapToColor/ColorSoftener" },
        { label: "Darken with Distance", action: "MapToColor/DarkenWithDistance" },
        { label: "Darken near Origin",   action: "MapToColor/DarkenNearOrigin" },
      ]
    },

    {
      label: "Map Colors",
      submenu: [
        { label: "To Centroid",                          action: "MapToColor/RadialCentroidColorMap" },
        { label: "To Direction",                         action: "MapToColor/RadialStandardBasisColorMap" },
        { label: "To Canonical Orientation",             action: "MapToColor/CanonicalOrientationColorMap" },
        { label: "To Normal Polarity",                   action: "MapToColor/NormalPolarityColorMap" },
        { label: "To Octant",                            action: "MapToColor/CentroidByOctantAndDirectionColorMap" },
        { label: "To Coordinate Plane",                  action: "MapToColor/CoordinatePlaneColorMap" },
        { label: "System by Centroid",                   action: "MapToColor/SystemCentroidColorMap" },
        { label: "Nearest Predefined Orbit",             action: "MapToColor/NearestPredefinedOrbitColorMap" },
        { label: "Nearest Predefined Orbit by Centroid", action: "MapToColor/CentroidNearestPredefinedOrbitColorMap" },
        { label: "Nearest Special Orbit",                action: "MapToColor/NearestSpecialOrbitColorMap" },
        { label: "Nearest Special Orbit by Centroid",    action: "MapToColor/CentroidNearestSpecialOrbitColorMap" },
      ]
    }
  ];

  return (
    <DeclarativeMenu label="Edit" items={items}
      menuSlots={{ setColorItem: <SetColorItem ctrlr={rootController()} /> }}
    />
  );
}
