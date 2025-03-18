
import { Divider, Menu, SubMenu, CommandAction } from "../../framework/menus.jsx";

export const ConstructMenu = () =>
{
  return (
    <Menu label="Construct">
        <CommandAction label="Loop Balls"         action="JoinPoints/CLOSED_LOOP" />
        <CommandAction label="Chain Balls"        action="JoinPoints/CHAIN_BALLS" />
        <CommandAction label="Join Balls to Last" action="JoinPoints/ALL_TO_LAST" />
        <CommandAction label="Make All Possible Struts" action="JoinPoints/ALL_POSSIBLE" />

        <Divider />

        <CommandAction label="Panel" action="panel" />
        <CommandAction label="Panel/Strut Vertices" action="ShowVertices" />
        <CommandAction label="Panel Normals" action="ShowNormals" />

        <Divider />
        
        <CommandAction label="Centroid" action="NewCentroid" />
        <CommandAction label="Strut Midpoints" action="midpoint" />
        <CommandAction label="Panel Centroids" action="PanelCentroids" />
        <CommandAction label="Panel Perimeters" action="PanelPerimeters" />

        <Divider />

        <CommandAction label="Line-Line Intersection"  action="StrutIntersection" />
        <CommandAction label="Line-Plane Intersection" action="LinePlaneIntersect" />
        <CommandAction label="Panel-Panel Projection"  action="PanelPanelIntersection" />
        <CommandAction label="Cross Product" action="CrossProduct" />
        <CommandAction label="Normal to Skew Lines" action="JoinSkewLines" />

        <Divider />
        
        <CommandAction label="Ball At Origin" action="ShowPoint/origin" />

        <Divider />
        
        <CommandAction label="2D Convex Hull" action="ConvexHull2d" />
        <SubMenu label="3D Convex Hull">
          <CommandAction label="Complete" action="ConvexHull3d" />
          <CommandAction label="Panels Only" action="ConvexHull3d/onlyPanels" />
          <CommandAction label="Struts Only" action="ConvexHull3d/noPanels" />
        </SubMenu>

        <Divider />
        
        <CommandAction label="Parallelepiped" action="Parallelepiped" />
    </Menu>
  );
}
