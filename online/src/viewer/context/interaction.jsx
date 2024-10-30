
import { createContext, createSignal, useContext } from "solid-js";

const InteractionToolContext = createContext( [] );

export const grabTool = {

  allowTrackball: true, // THIS is the reason this component exists

  cursor: 'grab',

  onClick: () => {},
  bkgdClick: () => {},
  onDragStart: () => {},
  onDrag: () => {},
  onDragEnd: () => {},
  onTrackballEnd: () => {},
};

const MAX_CLICK_DURATION = 500; // msec
const MAX_CLICK_DISTANCE = 2; // pixels

const InteractionToolProvider = (props) =>
{
  const [ tool, setTool ] = createSignal( props.defaultTool || grabTool );

  let lastPointerDown = null;
  let dragStartEmitted = false;

  const wrappedTool = {

    allowTrackball: () => tool() ?.allowTrackball,

    cursor: () => tool() ?.cursor,

    onClick: ( id, position, type, selected ) => tool() ?.onClick( id, position, type, selected ),

    bkgdClick: () => tool() ?.bkgdClick(),
    
    onDragStart: ( e, id, position, type, selected ) => {
      // Defer the onDragStart until we see sufficient movement
      lastPointerDown = e;
      dragStartEmitted = false;
    },
    
    onDrag: ( e, id, position, type, selected ) => {
      // Ignore non-drag pointer movements; modal mouse tools would need different infrastructure.
      if ( lastPointerDown ) {
        // lastPointerDown is only non-null if the onDragStart handler exists, and we got a pointerDown event
        const deltaX = e.x - lastPointerDown .x;
        const deltaY = e.y - lastPointerDown .y;
        if ( deltaX > MAX_CLICK_DISTANCE || deltaY > MAX_CLICK_DISTANCE ) {
          // We are dragging... distance has changed sufficiently
          if ( !dragStartEmitted ) {
            // Emit the deferred onDragStart callback.  We already know the handler exists.
            tool() .onDragStart( lastPointerDown, id, position, type, selected );
            dragStartEmitted = true;
          }
          // Emit the onDrag callback.
          const handler = tool && tool() ?.onDrag;
          if ( handler ) {
            handler( e, id, position, type, selected );
          }
        }
      }
    },
    
    onDragEnd: ( e, id, position, type, selected, label ) => {
      if ( dragStartEmitted ) {
        // We are dragging... we must have seen a move event with sufficient change in distance
        const handler = tool && tool() ?.onDragEnd;
        if ( handler ) {
          handler( e, props.id, props.position, props.type, props.selected );
        }
        dragStartEmitted = false;
      } else {
        // We are not dragging, but we must be sure that not too much time has elapsed
        const handler = tool && tool() ?.onClick;
        const deltaT = e .timeStamp - lastPointerDown ?.timeStamp; // may subtract 0 here, if lastPointerDown===null
        if ( handler && deltaT < MAX_CLICK_DURATION ) {
          handler( id, position, type, selected, label );
        }  
      }
      lastPointerDown = null;  
    },
    
    onTrackballEnd: () => {},
  
    onContextMenu: ( id, position, type, selected, label ) => tool() ?.onContextMenu && tool() .onContextMenu( id, position, type, selected, label ),

  };

  return (
    <InteractionToolContext.Provider value={ [ wrappedTool, setTool ] }>
      {props.children}
    </InteractionToolContext.Provider>
  );
}

const useInteractionTool = () => { return useContext( InteractionToolContext ); };

export { InteractionToolProvider, useInteractionTool };