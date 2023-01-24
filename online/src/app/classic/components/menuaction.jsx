
import MenuItem from "@suid/material/MenuItem"
import Typography from "@suid/material/Typography";
import ListItemText from "@suid/material/ListItemText";
import { controllerAction } from "../controllers-solid";

const isMac = navigator.userAgentData?.platform === 'macOS';

export const MenuAction = ( props ) =>
{
  let modifiers = props.mods;
  if ( !isMac && modifiers )
    modifiers = modifiers .replace( '⌘', '⌃' );
  if ( !props.disabled ) {
    const targetCodes = props.code?.split( '|' ) || ( props.key && [ "Key" + props.key.toUpperCase() ] );
    if ( targetCodes ) {
      const hasMeta = !! modifiers ?.includes( '⌘' );
      const hasControl = !! modifiers ?.includes( '⌃' );
      const hasShift = !! modifiers ?.includes( '⇧' );
      const hasOption = !! modifiers ?.includes( '⌥' );
      document .addEventListener( "keydown", evt => {
        if ( targetCodes .indexOf( evt.code ) < 0 )
          return;
        if ( hasMeta !== evt.metaKey )
          return;
        if ( hasControl !== evt.ctrlKey )
          return;
        if ( hasShift !== evt.shiftKey )
          return;
        if ( hasOption !== evt.altKey )
          return;
        evt .preventDefault();
        props.onClick();
      } );
    }
  }

  return (
    <MenuItem disabled={props.disabled} onClick={props.onClick}>
      <ListItemText>{props.label}</ListItemText>
      <Show when={props.code || props.key} >
        <Typography variant="body2" color="text.secondary">
          { props.code? "⌫" : modifiers + props.key }
        </Typography>
      </Show>
    </MenuItem>
  );
}

export const createMenuAction = ( controller, doClose ) => ( props ) =>
{
  const onClick = () => 
  {
    doClose();
    controllerAction( controller, props.action );
  }
  return MenuAction( { ...props, onClick } );
}
