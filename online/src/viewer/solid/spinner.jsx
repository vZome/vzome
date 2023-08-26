
import { Dialog } from "@kobalte/core";
import { Progress } from "@kobalte/core";

export const Spinner = (props) =>
{
  return (
    <Dialog.Root open>
      <Dialog.Portal mount={props.root}>
        <Dialog.Overlay class="progress__overlay" />
        <div class="progress__positioner">
          <Dialog.Content class="progress__content">
            <Progress.Root indeterminate class="progress">
              <div class="progress__label-container">
                <Progress.Label class="progress__label">Loading...</Progress.Label>
              </div>
              <Progress.Track class="progress__track">
                <Progress.Fill class="progress__fill" />
              </Progress.Track>
            </Progress.Root>
          </Dialog.Content>
        </div>
      </Dialog.Portal>
    </Dialog.Root>
  );
}