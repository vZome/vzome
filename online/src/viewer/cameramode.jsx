

import { Switch } from "@kobalte/core";

import { useCamera } from "./context/camera";

export const CameraMode = () =>
{
  const { togglePerspective, state } = useCamera();

  return (
    <div style={ { position: 'absolute', top: '1em', right: '1em' } }>
      <Switch.Root checked={state.camera.perspective} onChange={togglePerspective}>
        <Switch.Label class="switch__label">perspective</Switch.Label>
        <Switch.Input class="switch__input" />
        <Switch.Control class="switch__control">
          <Switch.Thumb class="switch__thumb" />
        </Switch.Control>
      </Switch.Root>
    </div>
  );
}
