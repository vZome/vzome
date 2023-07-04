
import { createContext, createSignal, useContext } from "solid-js";

const InteractionToolContext = createContext( [] );

const InteractionToolProvider = (props) =>
{
  const [ tool, setTool ] = createSignal( props.defaultTool || {} );

  return (
    <InteractionToolContext.Provider value={ [ tool, setTool ] }>
      {props.children}
    </InteractionToolContext.Provider>
  );
}

const useInteractionTool = () => { return useContext( InteractionToolContext ); };

export { InteractionToolProvider, useInteractionTool };