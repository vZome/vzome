
import { Divider, Menu, SubMenu, CommandAction, DeclarativeMenu } from "../../framework/menus.jsx";
import { controllerProperty, useEditor } from '../../framework/context/editor.jsx';

export const ConstructMenu = () =>
{
  const { rootController } = useEditor();
  const affineModes = () => controllerProperty( rootController(), 'affinePolygon.modes', 'affinePolygon.modes', true );
  const affineLabels = () => controllerProperty( rootController(), 'affinePolygon.labels', 'affinePolygon.labels', true );

  const items = [
    { label: "Loop Balls",               action: "JoinPoints/CLOSED_LOOP" },
    { label: "Chain Balls",              action: "JoinPoints/CHAIN_BALLS" },
    { label: "Join Balls to Last",       action: "JoinPoints/ALL_TO_LAST" },
    { label: "Make All Possible Struts", action: "JoinPoints/ALL_POSSIBLE" },
    { divider: true },
    { label: "Panel",                    action: "panel" },
    { label: "Panel/Strut Vertices",     action: "ShowVertices" },
    { label: "Panel Normals",            action: "ShowNormals" },
    { divider: true },
    { label: "Centroid",                 action: "NewCentroid" },
    { label: "Strut Midpoints",          action: "midpoint" },
    { label: "Panel Centroids",          action: "PanelCentroids" },
    { label: "Panel Perimeters",         action: "PanelPerimeters" },
    { divider: true },
    { label: "Line-Line Intersection",   action: "StrutIntersection" },
    { label: "Line-Plane Intersection",  action: "LinePlaneIntersect" },
    { label: "Panel-Panel Projection",   action: "PanelPanelIntersection" },
    { label: "Cross Product",            action: "CrossProduct" },
    { label: "Normal to Skew Lines",     action: "JoinSkewLines" },
    { divider: true },
    { label: "Ball At Origin",           action: "ShowPoint/origin" },
    { divider: true },
    { label: "2D Convex Hull",           action: "ConvexHull2d" },
    { 
      label: "3D Convex Hull", 
      submenu: [
        { label: "Complete",               action: "ConvexHull3d" },
        { label: "Panels Only",            action: "ConvexHull3d/onlyPanels" },
        { label: "Struts Only",            action: "ConvexHull3d/noPanels" },
      ]
    }
  ];

  return (
    <DeclarativeMenu label="Construct" items={items}>

        <Divider />
        
        <CommandAction label="Parallelogram" action="AffinePolygon/4" />
        <For each={affineModes()}>{ (value,i) =>
          <CommandAction label={`Affine ${affineLabels()[i()]}`} action={`AffinePolygon/${value}`} />
        }</For>
        <CommandAction label="Parallelepiped" action="Parallelepiped" />
    </DeclarativeMenu>
  );
}
