

/*

After digging through three.js TrackballControls.js

   https://github.com/mrdoob/three.js/blob/master/examples/jsm/controls/TrackballControls.js

it seems I can use it to manipulate a "camera" as long as that object has the same
"up", "position", "lookAt", and "isPerspectiveCamera" fields as an actual camera.
(Yay, duck typing!)

However, I would have to somehow "reverse" the effect of the transformations
to the object.

I think it may be better to copy the TrackballControls.js code, discard the pan() and zoom()
features, and diddle the rotate() function to do what I need... keeping the necessary
event listening code.

I'll also want to copy part of the lifecycle code from Drei's React wrapper.

*/


export const createSwitcherTool = ( selectionTool, strutPreviewTool, minDrag ) =>
{
  let currentTool = selectionTool;
  let timer = 0;

  const dragging = () =>
  {
    return currentTool === strutPreviewTool;
  }

  const bkgdClick = () =>
  {
    selectionTool.bkgdClick();
  }

  const onHover = ( id, position, type, starting ) =>
  {

  }

  const onClick = ( id, position, type, selected ) =>
  {
    if ( currentTool === selectionTool ) { // drag hasn't started
      console.log( 'SwitcherTool onClick', new Date().getTime() );
      // abort the pending drag, forward the click to the selectionTool
      clearTimeout( timer );
      selectionTool .onClick( id, position, type, selected );
    }
  }

  const onDragStart = ( id, position, type, selected, evt ) =>
  {
    if ( type === 'ball' ) {
      timer = setTimeout(() => {
        console.log( 'SwitcherTool timer done', new Date().getTime() );
        currentTool = strutPreviewTool;
        strutPreviewTool .onDragStart( id, position, type, selected, evt );
      }, minDrag );
    }
  }

  const onDrag = evt =>
  {
    if ( currentTool === strutPreviewTool ) {
      strutPreviewTool .onDrag( evt );
    }
  }

  const onDragEnd = evt =>
  {
    if ( currentTool === strutPreviewTool ) {
      strutPreviewTool .onDragEnd( evt );
      currentTool = selectionTool;
    }
  }

  return { bkgdClick, onHover, onClick, onDragStart, onDrag, onDragEnd, dragging };
}

// {
//   const { sendToWorker, subscribe } = worker;
//   useEffect( () => {
//     // Connect the worker store to the local store, to listen to worker events
//     subscribe( {
//       onWorkerError: error => {},
//       onWorkerMessage: msg => {},
//     } );
//   }, [] );
// }

