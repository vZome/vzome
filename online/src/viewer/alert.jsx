
import { AlertDialog } from "@kobalte/core";

import { useViewer } from "./context/viewer.jsx";

export const ErrorAlert = (props) =>
{
  const { problem, clearProblem } = useViewer();

  return (
    <AlertDialog.Root modal={false} open={!!problem()} onOpenChange={ () => clearProblem() }>
      <AlertDialog.Portal mount={props.root}>
        <AlertDialog.Overlay class="alert-dialog__overlay" />
        <div class="alert-dialog__positioner">
          <AlertDialog.Content class="alert-dialog__content">
            <div class="alert-dialog__header">
              <AlertDialog.Title class="alert-dialog__title">There's a problem</AlertDialog.Title>
              <AlertDialog.CloseButton class="alert-dialog__close-button">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="currentColor" d="M18.3 5.71a.996.996 0 0 0-1.41 0L12 10.59L7.11 5.7A.996.996 0 1 0 5.7 7.11L10.59 12L5.7 16.89a.996.996 0 1 0 1.41 1.41L12 13.41l4.89 4.89a.996.996 0 1 0 1.41-1.41L13.41 12l4.89-4.89c.38-.38.38-1.02 0-1.4z"/></svg>
              </AlertDialog.CloseButton>
            </div>
            <AlertDialog.Description class="alert-dialog__description">
              {problem()}
            </AlertDialog.Description>
          </AlertDialog.Content>
        </div>
      </AlertDialog.Portal>
    </AlertDialog.Root>
  )
}
