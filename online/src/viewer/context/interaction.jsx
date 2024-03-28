
import { createContext, createSignal, useContext } from "solid-js";

const InteractionToolContext = createContext( [] );

export const grabTool = {

  allowTrackball: true, // THIS is the reason this component exists

  cursor: 'grab',

  onClick: () => {},
  bkgdClick: () => {},
  onDragStart: () => {},
  onDrag: evt => {},
  onDragEnd: evt => {},
  onTrackballEnd: () => {},
};

const InteractionToolProvider = (props) =>
{
  const [ tool, setTool ] = createSignal( props.defaultTool || grabTool );

  return (
    <InteractionToolContext.Provider value={ [ tool, setTool ] }>
      {props.children}
    </InteractionToolContext.Provider>
  );
}

const useInteractionTool = () => { return useContext( InteractionToolContext ); };

export { InteractionToolProvider, useInteractionTool };