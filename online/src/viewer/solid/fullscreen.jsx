
import { Show } from "solid-js";

import { Tooltip } from "@kobalte/core";

export const FullscreenButton = (props) =>
{
  return (
    <Tooltip.Root>
      <Tooltip.Trigger class="fullscreen__trigger corner__icon__button"
          onclick={props.toggle}>
        <Show when={props.fullScreen} fallback={
          <svg aria-hidden="true" viewBox="0 0 24 24" focusable="false" data-testid="FullscreenIcon">
            <path d="M0 0h24v24H0z" fill="none"></path>
            <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"></path>
          </svg>
        }>
          <svg aria-hidden="true" viewBox="0 0 24 24" focusable="false" data-testid="FullscreenExitIcon">
            <path d="M0 0h24v24H0z" fill="none"></path>
            <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"></path>
          </svg>
        </Show>
      </Tooltip.Trigger>
      <Tooltip.Portal mount={props.root}>
        <Tooltip.Content class="fullscreen__content">
          <Tooltip.Arrow />
          <p>{props.fullScreen? 'Collapse view' : 'Expand view'}</p>
        </Tooltip.Content>
      </Tooltip.Portal>
    </Tooltip.Root>
  );
}