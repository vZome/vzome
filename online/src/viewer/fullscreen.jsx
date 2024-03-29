
import { Show } from "solid-js";
import { IconButton } from "./iconbutton.jsx";

export const FullscreenButton = (props) =>
{
  return (
    <Show when={props.fullScreen} fallback={
      <IconButton class='fullscreen' tooltip='Expand view' onClick={props.toggle} root={props.root}>
        <svg aria-hidden="true" viewBox="0 0 24 24" focusable="false" data-testid="FullscreenIcon">
          <path d="M0 0h24v24H0z" fill="none"></path>
          <path d="M7 14H5v5h5v-2H7v-3zm-2-4h2V7h3V5H5v5zm12 7h-3v2h5v-5h-2v3zM14 5v2h3v3h2V5h-5z"></path>
        </svg>
      </IconButton>
    }>
      <IconButton class='fullscreen' tooltip='Collapse view' onClick={props.toggle} root={props.root}>
        <svg aria-hidden="true" viewBox="0 0 24 24" focusable="false" data-testid="FullscreenExitIcon">
          <path d="M0 0h24v24H0z" fill="none"></path>
          <path d="M5 16h3v3h2v-5H5v2zm3-8H5v2h5V5H8v3zm6 11h2v-3h3v-2h-5v5zm2-11V5h-2v5h5V8h-3z"></path>
        </svg>
      </IconButton>
    </Show>
  );
}