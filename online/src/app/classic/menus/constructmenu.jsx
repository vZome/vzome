
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import Divider from "@suid/material/Divider";
import { createSignal } from "solid-js";

import { createActionItem } from "../components/actionitem.jsx";

export const ConstructMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const doClose = () => setAnchorEl( null );

  const ActionItem = createActionItem( props.controller, 'editor', doClose );

  return (
    <div>
      <Button id="construct-menu-button"
        aria-controls={open() ? "construct-menu-menu" : undefined} aria-haspopup="true" aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        Construct
      </Button>
      <Menu id="construct-menu-menu" MenuListProps={{ "aria-labelledby": "construct-menu-button" }}
        anchorEl={anchorEl()} open={open()} onClose={doClose}
      >
        <ActionItem label="Loop Balls"         action="JoinPoints/CLOSED_LOOP" mods="⌘" key="J" />
        <ActionItem label="Chain Balls"        action="JoinPoints/CHAIN_BALLS" mods="⌥⌘" key="J" />
        <ActionItem label="Join Balls to Last" action="JoinPoints/ALL_TO_LAST" />
        <ActionItem label="Make All Possible Struts" action="JoinPoints/ALL_POSSIBLE" />

        <Divider />

        <ActionItem label="Panel" action="panel" mods="⌘" key="P" />
        <ActionItem label="Panel/Strut Vertices" action="ShowVertices" />
        <ActionItem label="Panel Normals" action="ShowNormals" />

        <Divider />
        
        <ActionItem label="Centroid" action="NewCentroid" />
        <ActionItem label="Strut Midpoint" action="midpoint" />
        <ActionItem label="Line-Line Intersection"  action="StrutIntersection" />
        <ActionItem label="Line-Plane Intersection" action="LinePlaneIntersect" />
        <ActionItem label="Panel-Panel Projection"  action="PanelPanelIntersection" />
        <ActionItem label="Cross Product" action="CrossProduct" />
        <ActionItem label="Normal to Skew Lines" action="JoinSkewLines" />

        <Divider />
        
        <ActionItem label="Ball At Origin" action="ShowPoint/origin" />

        <Divider />
        
        <ActionItem label="2D Convex Hull" action="ConvexHull2d" />
        <ActionItem label="3D Convex Hull" action="ConvexHull3d" />

        <Divider />
        
        <ActionItem label="Parallelepiped" action="Parallelepiped" mods="⇧⌘" key="P" disabled={true} />
      </Menu>
    </div>
  );
}
