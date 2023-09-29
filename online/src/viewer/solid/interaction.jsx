
import { createContext, createEffect, createSignal, useContext } from "solid-js";

const InteractionToolContext = createContext( [] );

const grabTool = {

  allowTrackball: true, // THIS is the reason this component exists

  cursor: 'grab',

  onClick: () => {},
  bkgdClick: () => {},
  onDragStart: () => {},
  onDrag: evt => {},
  onDragEnd: evt => {}
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

const CameraTool = props =>
{
  const [ _, setTool ] = useInteractionTool();
  const cursor = 'url(rotate.svg) 9 9, url(rotate.png) 9 9, grab';
  createEffect( () => setTool( { ...grabTool, cursor } ) );

  return null;
}

export { InteractionToolProvider, useInteractionTool, CameraTool };