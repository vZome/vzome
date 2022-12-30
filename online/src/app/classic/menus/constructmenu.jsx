
import Button from "@suid/material/Button"
import Menu from "@suid/material/Menu"
import MenuItem from "@suid/material/MenuItem"
import Divider from "@suid/material/Divider";
import { createSignal } from "solid-js";
import { controllerAction, subController } from "../controllers-solid";

export const ConstructMenu = ( props ) =>
{
  const [ anchorEl, setAnchorEl ] = createSignal( null );
  const open = () => Boolean( anchorEl() );
  const handleClose = () => setAnchorEl( null ); 

  const editorController = () => subController( props.controller, "editor" );
  const doAction = action => () =>
  {
    setAnchorEl( null );
    controllerAction( editorController(), action );
  }

  return (
    <div>
      <Button
        id="construct-menu-button"
        aria-controls={open() ? "construct-menu-menu" : undefined}
        aria-haspopup="true"
        aria-expanded={open() ? "true" : undefined}
        onClick={ (event) => setAnchorEl(event.currentTarget) }
      >
        Construct
      </Button>
      <Menu
        id="construct-menu-menu"
        anchorEl={anchorEl()}
        open={open()}
        onClose={handleClose}
        MenuListProps={{ "aria-labelledby": "construct-menu-button" }}
      >
        <MenuItem onClick={ doAction( "JoinPoints/CLOSED_LOOP" ) }>Loop Balls</MenuItem>
        <MenuItem onClick={ doAction( "JoinPoints/CHAIN_BALLS" ) }>Chain Balls</MenuItem>
        <MenuItem onClick={ doAction( "JoinPoints/ALL_TO_LAST" ) }>Join Balls to Last</MenuItem>
        <MenuItem onClick={ doAction( "JoinPoints/ALL_POSSIBLE" ) }>Make All Possible Struts</MenuItem>

        <Divider />

        <MenuItem onClick={ doAction( "panel" ) }>Panel</MenuItem>
        <MenuItem onClick={ doAction( "ShowVertices" ) }>Panel/Strut Vertices</MenuItem>
        <MenuItem onClick={ doAction( "ShowNormals" ) }>Panel Normals</MenuItem>

        <Divider />
        
        <MenuItem onClick={ doAction( "NewCentroid" ) }>Centroid</MenuItem>
        <MenuItem onClick={ doAction( "midpoint" ) }>Strut Midpoint</MenuItem>
        <MenuItem onClick={ doAction( "StrutIntersection" ) }>Line-Line Intersection</MenuItem>
        <MenuItem onClick={ doAction( "LinePlaneIntersect" ) }>Line-Plane Intersection</MenuItem>
        <MenuItem onClick={ doAction( "PanelPanelIntersection" ) }>Panel-Panel Projection</MenuItem>
        <MenuItem onClick={ doAction( "CrossProduct" ) }>Cross Product</MenuItem>
        <MenuItem onClick={ doAction( "JoinSkewLines" ) }>Normal to Skew Lines</MenuItem>

        <Divider />
        
        <MenuItem onClick={ doAction( "ShowPoint/origin" ) }>Ball At Origin</MenuItem>

        <Divider />
        
        <MenuItem onClick={ doAction( "ConvexHull2d" ) }>2D Convex Hull</MenuItem>
        <MenuItem onClick={ doAction( "ConvexHull3d" ) }>3D Convex Hull</MenuItem>

        <Divider />
        
        <MenuItem onClick={ doAction( "Parallelepiped" ) }>Parallelepiped</MenuItem>
      </Menu>
    </div>
  );
}
