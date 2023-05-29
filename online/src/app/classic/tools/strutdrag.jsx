
import { createEffect } from 'solid-js';
import { LineBasicMaterial, Vector3, BufferGeometry, SphereGeometry, MeshLambertMaterial } from 'three';
import { useInteractionTool } from './interaction.jsx';

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

const pseudoCamera = {
  position: new Vector3(),
  up: new Vector3(),
  lookAt: () => {},
  isPerspectiveCamera: () => {
    console.log( 'pseudoCamera isPerspectiveCamera' );
    return true
  },
}

const StrutDragTool = props =>
{
  const handlers = {

    allowTrackball: false,

    onClick: ( id, position, type, selected ) => {
      console.log( 'strutPreviewTool clicked?????!!!!!' );
    },
    bkgdClick: () => {},
    onDragStart: ( id, position, type, starting, evt ) => {
      console.log( 'strutPreviewTool drag started' );
    },
    onDrag: evt => {
      console.log( 'strutPreviewTool drag ongoing' );
    },
    onDragEnd: evt => {
      console.log( 'strutPreviewTool drag finished' );
    }
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => setTool( handlers ) );

  const color = 0x202020;
  const lineMaterial = new LineBasicMaterial( {
    color,
    linewidth: 6
  } );
  const tip = new Vector3( 0, 0, 20 );
  const points = [];
  points.push( new Vector3( 0, 0, 0 ) );
  points.push( tip );
  const lineGeom = new BufferGeometry().setFromPoints( points );
  const meshMaterial = new MeshLambertMaterial( { color } );
  const sphereGeom = new SphereGeometry( 0.2, 9, 9 );

  return (
    <group>
      {/* <TrackballControls camera={pseudoCamera} staticMoving='true' rotateSpeed={4.5} zoomSpeed={3} panSpeed={1} /> */}
      <lineSegments material={lineMaterial} geometry={lineGeom} />
      <mesh position={tip} material={meshMaterial} geometry={sphereGeom} />
    </group>
  );
}

export { StrutDragTool };