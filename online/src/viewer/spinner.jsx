
import { Dialog } from "@kobalte/core/dialog";
import { Progress } from "@kobalte/core/progress";

export const Spinner = (props) =>
{
  return (
    <Dialog open>
      <Dialog.Portal mount={props.root}>
        <Dialog.Overlay class="progress__overlay" />
        <div class="progress__positioner">
          <Dialog.Content class="progress__content">
            <Progress indeterminate class="progress">
              <div class="progress__label-container">
                <Progress.Label class="progress__label">Loading...</Progress.Label>
              </div>
              <Progress.Track class="progress__track">
                <Progress.Fill class="progress__fill" />
              </Progress.Track>
            </Progress>
          </Dialog.Content>
        </div>
      </Dialog.Portal>
    </Dialog>
  );
}