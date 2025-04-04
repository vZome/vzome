

import { Switch } from "@kobalte/core/switch";

import { useCamera } from "./context/camera.jsx";

export const CameraModes = props =>
{
  const { togglePerspective, toggleOutlines, state } = useCamera();

  return (
    <div class='perspective_toggle' >
      <Switch checked={state.camera.perspective} onChange={togglePerspective}>
        <Switch.Label class="switch__label">Perspective</Switch.Label>
        <Switch.Input class="switch__input" />
        <Switch.Control class="switch__control">
          <Switch.Thumb class="switch__thumb" />
        </Switch.Control>
      </Switch>
      <Show when={props.showOutlines}>
        <Switch checked={state.camera.outlines} onChange={toggleOutlines}>
          <Switch.Label class="switch__label">Outlines</Switch.Label>
          <Switch.Input class="switch__input" />
          <Switch.Control class="switch__control">
            <Switch.Thumb class="switch__thumb" />
          </Switch.Control>
        </Switch>
      </Show>
    </div>
  );
}
