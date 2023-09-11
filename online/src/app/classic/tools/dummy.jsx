
import { createEffect } from "solid-js";

import { useInteractionTool } from "../../../viewer/solid/interaction.jsx";

const DummyTool = props =>
{
  const handlers = {

    allowTrackball: true, // THIS is the reason this component exists

    cursor: 'url(rotate.svg) 9 9, url(rotate.png) 9 9, grab',

    onClick: () => {},
    bkgdClick: () => {},
    onDragStart: () => {},
    onDrag: evt => {},
    onDragEnd: evt => {}
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  return null;
}

export { DummyTool };