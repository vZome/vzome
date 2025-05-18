
import { Dialog } from "@kobalte/core/dialog";
import { Switch } from "@kobalte/core/switch";

import { useCamera } from "./context/camera.jsx";

export const Settings = (props) =>
{
  const { togglePerspective, toggleOutlines, state } = useCamera();

  return (
    <Dialog open>
      <Dialog.Portal mount={props.root}>
        <Dialog.Overlay class="settingsdialog__overlay" />
        <div class="settingsdialog__positioner">
          <Dialog.Content class="settingsdialog__content">
            <div class="settingsdialog__header">
              <Dialog.Title class="settingsdialog__title">View Settings</Dialog.Title>
              <Dialog.CloseButton class="dialog__close-button" onClick={props.close}>x
                {/* <CrossIcon /> */}
              </Dialog.CloseButton>
            </div>
            <div class="settingsdialog__body">
              <Switch class="switch" checked={state.camera.perspective} onChange={togglePerspective}>
                <Switch.Label class="switch__label">Perspective</Switch.Label>
                <Switch.Input class="switch__input" />
                <Switch.Control class="switch__control">
                  <Switch.Thumb class="switch__thumb" />
                </Switch.Control>
              </Switch>
              <Show when={props.showOutlines}>
                <Switch class="switch" checked={state.camera.outlines} onChange={toggleOutlines}>
                  <Switch.Label class="switch__label">Outlines</Switch.Label>
                  <Switch.Input class="switch__input" />
                  <Switch.Control class="switch__control">
                    <Switch.Thumb class="switch__thumb" />
                  </Switch.Control>
                </Switch>
              </Show>
            </div>
          </Dialog.Content>
        </div>
      </Dialog.Portal>
    </Dialog>
  );
}