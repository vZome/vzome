
import { Divider, Menu, createMenuAction } from "../components/menuaction.jsx";

import { useWorkerClient } from "../../../workerClient/index.js";

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
        <EditAction label="Strut Midpoint" action="midpoint" />
        <EditAction label="Line-Line Intersection"  action="StrutIntersection" />
        <EditAction label="Line-Plane Intersection" action="LinePlaneIntersect" />
        <EditAction label="Panel-Panel Projection"  action="PanelPanelIntersection" />
        <EditAction label="Cross Product" action="CrossProduct" />
        <EditAction label="Normal to Skew Lines" action="JoinSkewLines" />

        <Divider />
        
        <EditAction label="Ball At Origin" action="ShowPoint/origin" />

        <Divider />
        
        <EditAction label="2D Convex Hull" action="ConvexHull2d" />
        <EditAction label="3D Convex Hull" action="ConvexHull3d" />

        <Divider />
        
        <EditAction label="Parallelepiped" action="Parallelepiped" mods="⇧⌘" key="P" />
    </Menu>
  );
}
