
import { XRGripToMove } from "./grip2move.jsx";
import { XRInstructionText } from "./help.jsx";
import { XRSessionManager } from "./manager.jsx";

// ──────────────────────────────────────────────────────────────────────────────
// StartXRButton — pre-composed default: session manager + instruction text + grip-to-move.
// All bundled here so the dynamic module can be a single import in the main viewer.

const StartXRButton = ( props ) => (
  <XRSessionManager trackball={props.trackball} getRootGroup={props.getRootGroup}>
    <XRInstructionText text="Grip to move the model" />
    <XRGripToMove/>
  </XRSessionManager>
);

export default StartXRButton;