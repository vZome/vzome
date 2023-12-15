
import { useViewer } from "./context/viewer.jsx";

export const ErrorAlert = (props) =>
{
  const { problem, clearProblem } = useViewer();

  return (
    <Show when={ !!problem() }>
      {/* This is just Kobalte's AlertDialog, inlined... their state management was crap, assuming one per page */}
      <div>
        <div class="alert-dialog__overlay" data-expanded ></div>
        <div class="alert-dialog__positioner">
          <div class="alert-dialog__content" role="alertdialog" id="dialog-cl-3-content" tabindex="-1" data-expanded aria-labelledby="dialog-cl-3-title" aria-describedby="dialog-cl-3-description" >
            <div class="alert-dialog__header">
              <h2 class="alert-dialog__title" id="dialog-cl-3-title">There's a problem</h2>
              <button class="alert-dialog__close-button" aria-label="Dismiss" type="button" onClick={clearProblem} >
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="currentColor" d="M18.3 5.71a.996.996 0 0 0-1.41 0L12 10.59L7.11 5.7A.996.996 0 1 0 5.7 7.11L10.59 12L5.7 16.89a.996.996 0 1 0 1.41 1.41L12 13.41l4.89 4.89a.996.996 0 1 0 1.41-1.41L13.41 12l4.89-4.89c.38-.38.38-1.02 0-1.4z"></path></svg>
              </button>
            </div>
            <p class="alert-dialog__description" id="dialog-cl-3-description">
              {problem()}
            </p>
          </div>
        </div>
      </div>
    </Show>
  )
}
