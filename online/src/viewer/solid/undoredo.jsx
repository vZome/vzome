
import { useWorkerClient } from "../../workerClient/context.jsx";
import { doControllerAction } from "../../workerClient/actions.js";
import { IconButton } from "./iconbutton";

export const UndoRedoButtons = (props) =>
{
  const { postMessage } = useWorkerClient();
  const historyAction = action => () =>
  {
    postMessage( doControllerAction( 'undoRedo', action ) );
  }

return ( <>
    <IconButton class='undo' tooltip='Undo' onClick={ historyAction( 'undo' ) } root={props.root}>
      <svg focusable="false" aria-hidden="true" viewBox="0 0 24 24" >
        <path d="M12.5 8c-2.65 0-5.05.99-6.9 2.6L2 7v9h9l-3.62-3.62c1.39-1.16 3.16-1.88 5.12-1.88 3.54 0 6.55 2.31 7.6 5.5l2.37-.78C21.08 11.03 17.15 8 12.5 8z"></path>
      </svg>
    </IconButton>
    <IconButton class='redo' tooltip='Redo' onClick={ historyAction( 'redo' ) } root={props.root}>
      <svg focusable="false" aria-hidden="true" viewBox="0 0 24 24" >
        <path d="M18.4 10.6C16.55 8.99 14.15 8 11.5 8c-4.65 0-8.58 3.03-9.96 7.22L3.9 16c1.05-3.19 4.05-5.5 7.6-5.5 1.95 0 3.73.72 5.12 1.88L13 16h9V7l-3.6 3.6z"></path>
      </svg>
    </IconButton>
  </> );
}