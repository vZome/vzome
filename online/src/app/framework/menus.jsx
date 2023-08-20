
import { DropdownMenu } from "@kobalte/core";
import { mergeProps } from "solid-js";
import { controllerAction } from "../../workerClient/controllers-solid";

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

export const SubMenu = props =>
{
  return (
    <DropdownMenu.Sub overlap gutter={4} shift={-8}>
      <DropdownMenu.SubTrigger class="dropdown-menu__sub-trigger">
        {props.label}
        <div class="dropdown-menu__item-right-slot">
          <svg viewBox="0 0 15 15" width="20" height="20">
            <path d="M6.1584 3.13508C6.35985 2.94621 6.67627 2.95642 6.86514 3.15788L10.6151 7.15788C10.7954 7.3502 10.7954 7.64949 10.6151 7.84182L6.86514 11.8418C6.67627 12.0433 6.35985 12.0535 6.1584 11.8646C5.95694 11.6757 5.94673 11.3593 6.1356 11.1579L9.565 7.49985L6.1356 3.84182C5.94673 3.64036 5.95694 3.32394 6.1584 3.13508Z" fill="currentColor" fill-rule="evenodd" clip-rule="evenodd"></path>
          </svg>
        </div>
      </DropdownMenu.SubTrigger>
      <DropdownMenu.Portal>
        <DropdownMenu.SubContent class="dropdown-menu__sub-content">
          {props.children}
        </DropdownMenu.SubContent>
      </DropdownMenu.Portal>
    </DropdownMenu.Sub>
  );
}

export const Choices = props =>
{
  return (
    <DropdownMenu.Group>
      <DropdownMenu.GroupLabel class="dropdown-menu__group-label">
        {props.label}
      </DropdownMenu.GroupLabel>
      <DropdownMenu.RadioGroup value={props.choice} onChange={props.setChoice}>
        <For each={props.choices}>{ value =>
          <DropdownMenu.RadioItem class="dropdown-menu__radio-item" value={value}>
            <DropdownMenu.ItemIndicator class="dropdown-menu__item-indicator">
            <svg viewBox="0 0 15 15">
              <path d="M9.875 7.5C9.875 8.81168 8.81168 9.875 7.5 9.875C6.18832 9.875 5.125 8.81168 5.125 7.5C5.125 6.18832 6.18832 5.125 7.5 5.125C8.81168 5.125 9.875 6.18832 9.875 7.5Z" fill="currentColor"></path>
            </svg>
            </DropdownMenu.ItemIndicator>
            {value}
          </DropdownMenu.RadioItem>
        }</For>
      </DropdownMenu.RadioGroup>
    </DropdownMenu.Group>
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
