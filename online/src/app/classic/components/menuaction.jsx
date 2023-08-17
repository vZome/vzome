
import { DropdownMenu } from "@kobalte/core";
import { mergeProps } from "solid-js";
import { controllerAction } from "../../../workerClient/controllers-solid";

const isMac = navigator.userAgentData?.platform === 'macOS' || navigator.userAgent .includes( 'Macintosh' );

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
    <DropdownMenu.Item class="dropdown-menu__item" disabled={props.disabled} onClick={props.onClick}>
      {props.label}
      <Show when={props.code || props.key} >
        <div class="dropdown-menu__item-right-slot">
          { props.code? "⌫" : modifiers + props.key }
        </div>
      </Show>
    </DropdownMenu.Item>
  );
}

export const MenuItem = ( props ) =>
{
  return (
    <DropdownMenu.Item class="dropdown-menu__item" disabled={props.disabled} onClick={props.onClick}>
      {props.children}
      <Show when={props.code || props.key} >
        <div class="dropdown-menu__item-right-slot">
          { props.code? "⌫" : modifiers + props.key }
        </div>
      </Show>
    </DropdownMenu.Item>
  );
}

const CheckboxItem = ( props ) =>
{
  return (
    <DropdownMenu.CheckboxItem class="dropdown-menu__checkbox-item" disabled={props.disabled}
      checked={props.checked}
      onChange={props.onClick}
    >
      <DropdownMenu.ItemIndicator class="dropdown-menu__item-indicator">
        <svg viewBox="0 0 15 15">
          <path d="M11.4669 3.72684C11.7558 3.91574 11.8369 4.30308 11.648 4.59198L7.39799 11.092C7.29783 11.2452 7.13556 11.3467 6.95402 11.3699C6.77247 11.3931 6.58989 11.3355 6.45446 11.2124L3.70446 8.71241C3.44905 8.48022 3.43023 8.08494 3.66242 7.82953C3.89461 7.57412 4.28989 7.55529 4.5453 7.78749L6.75292 9.79441L10.6018 3.90792C10.7907 3.61902 11.178 3.53795 11.4669 3.72684Z" fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"></path>
        </svg>
      </DropdownMenu.ItemIndicator>
      {props.label}
    </DropdownMenu.CheckboxItem>
  );
}

export const Divider = ( props ) =>
{
  return (
    <DropdownMenu.Separator class="dropdown-menu__separator" />
  );
}

export const Menu = ( props ) =>
{
  return (
    <DropdownMenu.Root>
      <DropdownMenu.Trigger class="dropdown-menu__trigger">
        {props.label}
      </DropdownMenu.Trigger>
      <DropdownMenu.Portal>
        <DropdownMenu.Content class="dropdown-menu__content">
          {props.children}
        </DropdownMenu.Content>
      </DropdownMenu.Portal>
      {props.dialogs}
    </DropdownMenu.Root>
  );
}

export const createMenuAction = ( controller ) => ( props ) =>
{
  const onClick = () => 
  {
    controllerAction( controller, props.action );
  }
  // I was destructuring props here, and lost reactivity!
  //  It usually doesn't matter for menu items, except when there is checkbox state.
  return MenuAction( mergeProps( { onClick }, props ) );
}

export const createCheckboxItem = ( controller ) => ( props ) =>
{
  const onClick = () => 
  {
    controllerAction( controller, props.action );
  }
  // I was destructuring props here, and lost reactivity!
  //  It usually doesn't matter for menu items, except when there is checkbox state.
  return CheckboxItem( mergeProps( { onClick }, props ) );
}
