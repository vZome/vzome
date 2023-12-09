
import { Divider, Menu, SubMenu, createMenuAction } from "../../framework/menus.jsx";

import { useWorkerClient } from "../../../viewer/context/worker.jsx";

export const ConstructMenu = () =>
{
  const { rootController } = useWorkerClient();
  const EditAction = createMenuAction( rootController() );

  return (
    <Menu label="Construct">
        <EditAction label="Loop Balls"         action="JoinPoints/CLOSED_LOOP" mods="⌘" key="J" />
        <EditAction label="Chain Balls"        action="JoinPoints/CHAIN_BALLS" mods="⌥⌘" key="J" />
        <EditAction label="Join Balls to Last" action="JoinPoints/ALL_TO_LAST" />
        <EditAction label="Make All Possible Struts" action="JoinPoints/ALL_POSSIBLE" />

        <Divider />

        <EditAction label="Panel" action="panel" mods="⌘" key="P" />
        <EditAction label="Panel/Strut Vertices" action="ShowVertices" />
        <EditAction label="Panel Normals" action="ShowNormals" />

        <Divider />
        
        <EditAction label="Centroid" action="NewCentroid" />
        <EditAction label="Strut Midpoints" action="midpoint" />
        <EditAction label="Panel Centroids" action="PanelCentroids" />
        <EditAction label="Panel Perimeters" action="PanelPerimeters" />

        <Divider />

        <EditAction label="Line-Line Intersection"  action="StrutIntersection" />
        <EditAction label="Line-Plane Intersection" action="LinePlaneIntersect" />
        <EditAction label="Panel-Panel Projection"  action="PanelPanelIntersection" />
        <EditAction label="Cross Product" action="CrossProduct" />
        <EditAction label="Normal to Skew Lines" action="JoinSkewLines" />

        <Divider />
        
        <EditAction label="Ball At Origin" action="ShowPoint/origin" />

        <Divider />
        
        <EditAction label="2D Convex Hull" action="ConvexHull2d" />
        <SubMenu label="3D Convex Hull">
          <EditAction label="Complete" action="ConvexHull3d" />
          <EditAction label="Panels Only" action="ConvexHull3d/onlyPanels" />
          <EditAction label="Struts Only" action="ConvexHull3d/noPanels" />
        </SubMenu>

        <Divider />
        
        <EditAction label="Parallelepiped" action="Parallelepiped" mods="⇧⌘" key="P" />
    </Menu>
  );
}
