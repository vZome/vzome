
import { createEffect, onCleanup, onMount } from "solid-js";
import { useFrame, useThree } from 'solid-three';
import { CSS2DObject, CSS2DRenderer } from "three-stdlib";

export const Labels = (props) =>
{
  // useThree() is called once here, but .scene/.camera are live getters on the returned
  // object (cameraStack.peek() ?? camera() -- see solid-three's source) -- destructuring
  // captures whatever they return at THIS instant, not a live reference. ControlledCamera
  // (perspectivecamera.jsx/orthographiccamera.jsx) registers the real camera via
  // setCamera(cam) sometime after mount; if that happens after this destructure runs,
  // a destructured `camera` binding here would be permanently stuck on solid-three's
  // fallback default camera (which sits at the origin), even though the actual WebGL
  // render -- which re-reads the getter fresh every frame internally -- uses the correct,
  // repositioned one. Symptom (found the hard way): the 3D scene renders correctly, but
  // CSS2DRenderer.render(scene, camera) with a stale camera projects labels against a
  // camera sitting at/near the geometry itself -- degenerate projection math, labels
  // silently get display:none (fail the [-1,1] clip-space z check) with no error anywhere.
  // Fixed by reading three.camera fresh inside useFrame instead of destructuring it above.
  const three = useThree();
  const { scene, canvas } = three;

  let labelRenderer;
  onMount( () => {
    labelRenderer = new CSS2DRenderer();
    const labelsElem = labelRenderer.domElement;
    labelsElem.style.isolation = 'isolate';
    labelsElem.style.position = 'absolute';
    labelsElem.style.pointerEvents = 'none';
    labelsElem.style.inset = '0px';
    labelsElem.style.width = '100%';
    labelsElem.style.height = '100%';
    labelsElem.classList .add( 'labels' );

    canvas .insertAdjacentElement( "beforebegin", labelsElem );
  });

  createEffect( () => {
    props.size && labelRenderer .setSize( props.size .width, props.size .height );
  } );

  useFrame( () => {
    labelRenderer .render( scene, three.camera );
  })

  return null;
}

export const Label = (props) =>
{
  let label;
  onMount( () => {
    const elem = document .createElement( 'div' );
    elem.className = 'vzome-label';
    elem.id = `vzome-label-${props.text}`;
    elem.textContent = props.text;
    label = new CSS2DObject( elem );
    props.parent .add( label );
  });

  // props.parent is a <T.Mesh> managed by solid-three's own scene-graph reconciler
  // (see Instance in geometry.jsx), which automatically detaches meshes it created from
  // their own parent on unmount -- but `label` was added to that mesh *imperatively*
  // (props.parent.add(label) above), outside solid-three's JSX-child tracking, so that
  // automatic cleanup has no knowledge of it. Without this, switching away from a scene
  // that had a labeled instance unmounts <Instance> (and detaches its mesh from the scene
  // graph), but `label` -- still a child of that now-orphaned, detached mesh -- keeps
  // existing with a matrixWorld that's simply never recomputed or traversed again.
  // Symptom (found the hard way): the label doesn't disappear, it freezes in place on
  // screen at wherever it last was, because CSS2DRenderer.render()'s traversal starts
  // from the live scene and can no longer reach it, so it never touches (or hides) that
  // DOM element again. Removing it here (rather than leaving it to hang off an orphaned
  // mesh) also fires CSS2DObject's own "removed" event listener, which detaches the DOM
  // element automatically (see three-stdlib's CSS2DRenderer.js) -- no manual DOM cleanup
  // needed, matching the pattern already used for this in SymmetryGeometry's own labels.
  onCleanup( () => label?.parent?.remove( label ) );

  createEffect( () => {
    const { x, y, z } = props.position;
    label.position.set( x, y, z );
  })

  return label;
}
