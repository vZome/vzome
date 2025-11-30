
import { Show } from "solid-js";

import { Divider, CommandAction, DeclarativeMenu } from "../../framework/menus.jsx";

const customMenuItemsJSON = localStorage .getItem( 'vzome-custom-menu' );
let customMenuItems = null;
if ( customMenuItemsJSON ) {
  try {
    customMenuItems = JSON .parse( customMenuItemsJSON );
  }
  catch ( e ) {
    console.error( "Error parsing custom menu items from localStorage:", e );
  }
}

export const CustomMenu = () =>
{
  return (
    <Show when={customMenuItems?.length > 0}>
      <DeclarativeMenu label="Custom" items={customMenuItems} defaultMods="⌥⌃" />
    </Show>
  );
}
