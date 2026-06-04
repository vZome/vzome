
import { createEffect, createSignal, Show } from "solid-js";
import { Matrix4, Vector3 } from "three";
import { CSS2DObject } from "three-stdlib";

import { createSymmetryRenderer } from "./context/symmetry-renderer.js";
import { buildShapeGeometry, buildOutlineGeometry } from "./geometry.jsx";
import { useCamera } from "./context/camera.jsx";
import { useWebXRClient } from "./context/webxr.jsx";

const GROUP_ID = "default";
const STYLE_ID = "default";

// ShapedGeometry's InstancedShape (geometry.jsx) shows selection as a binary emissive
// swap -- "#c8c8c8" or "black" -- not a graded intensity. symmetry-renderer.js's
// highlightIntensity attribute feeds emissiveNode = white * highlightIntensity (see
// createMaterialForGroup), so 0xc8/0xff match the same visual result; 1.0 would be pure
// white, brighter than ShapedGeometry's highlight.
const SELECTED_HIGHLIGHT = 0xc8 / 0xff;

// The worker sends each orientation as a flat 16-element array (see
// vzome-worker-static.js), but registerSymmetryGroup wants THREE.Matrix4 instances.
// Use .set(...) -- not .fromArray() -- to match how geometry.jsx's Instance interprets
// the very same rotation arrays: m.set(...props.rotation), no transpose.
const toMatrix4 = flat => new Matrix4().set( ...flat );

// Instance positions arrive as [x, y, z] arrays (that's what T.Group position={...}
// accepts directly in geometry.jsx's Instance), but the renderer reads position.x/y/z.
const toVector3 = ( [ x, y, z ] ) => new Vector3( x, y, z );

// Rendering-only replacement for ShapedGeometry, built on createSymmetryRenderer's
// GPU-instanced meshes instead of one Three.js object per ball/strut. Selection highlight
// (Phase 6) and labels (Phase 7) are implemented; no picking/interaction (Phase 5) --
// deliberately deferred, this component is meant for viewer-only (non-editing) use cases
// for now, see symmetry-renderer-plan.md and symmetry-renderer-status.md.
export const SymmetryGeometry = ( props ) =>
{
  const { originGroupReady } = useWebXRClient();

  // SymmetryGeometry builds its Three.js objects imperatively (createSymmetryRenderer),
  // outside solid-three's reconciler, but still needs to parent under WebXRSupport's
  // originGroup (webxr.jsx) -- that's the group XRGripToMove/XRScaling reposition and
  // rescale during an XR session, so content attached under it moves/resizes correctly with
  // the viewer; content attached elsewhere (e.g. straight to the scene) does not, and sits
  // stranded at whatever transform it had before entering XR. originGroup only exists as a
  // signal, not a synchronously-readable value, because solid-three constructs a <T.*>
  // element's instance lazily; wait for it here before creating anything that needs it.
  return (
    <Show when={ originGroupReady() }>
      { parent => <SymmetryGeometryImpl { ...props } parent={ parent() } /> }
    </Show>
  );
};

// Split out from SymmetryGeometry purely so the group-dependent setup below (creating the
// renderer, registering effects) only ever runs once parent is a real Object3D.
const SymmetryGeometryImpl = ( props ) =>
{
  const { state: cameraState } = useCamera();

  const renderer = createSymmetryRenderer( props.parent );

  const [ groupReady, setGroupReady ] = createSignal( false );
  const registeredShapeIds = new Set();
  const colorIndexByCss = new Map();

  // Phase 6 (selection highlight): keyed by vZome instance id (props.id on each entry in
  // shape.instances), not by renderer instanceId directly, because the instance-registration
  // effect below fully tears down and re-adds every instance (removeAllInstances + re-add)
  // on any props.shapes change -- including a pure SELECTION_TOGGLED, since scene.jsx
  // replaces the whole shapes tree with new object/array identities rather than mutating a
  // fine-grained per-instance store (see symmetry-renderer-status.md's Phase 6 section for
  // why this wasn't also fixed here). So the renderer's own instanceId is NOT stable across
  // a selection change and can't be cached long-term; what IS stable is the vZome instance's
  // own id, which this map is keyed by, refreshed every time the registration effect runs.
  let instanceRefById = new Map(); // vZome instance id -> { shapeId, instanceId }
  let lastSelectedById = new Map(); // vZome instance id -> boolean, to diff against

  // Phase 7 (labels): retained from the group-registration effect below so the label-
  // positioning math (see the instance-registration effect) can replicate the GPU vertex
  // shader's rotatedPositionNode transform (orientation * localVertex + instanceTranslation,
  // see createMaterialForGroup in symmetry-renderer.js) once per label in JS, instead of
  // per-vertex on the GPU -- there is no per-instance mesh for a <Label> to be parented to
  // and inherit that transform from "for free" the way ShapedGeometry's Instance gets it.
  let orientationMatrices = [];
  // Shape centroids (local-space, one per shape, NOT per instance) are attached to the
  // BufferGeometry returned by buildShapeGeometry as .shapeCentroid (see geometry.jsx) but
  // only at registration time; retained here since registerShape doesn't hand it back.
  const shapeCentroidById = new Map(); // shapeId -> {x,y,z} (local space, from buildShapeGeometry)
  let labelById = new Map(); // vZome instance id -> CSS2DObject, currently attached to originGroup

  const colorIndexFor = ( cssColor ) =>
  {
    let index = colorIndexByCss.get( cssColor );
    if ( index === undefined ) {
      index = renderer.registerColor( cssColor );
      colorIndexByCss.set( cssColor, index );
    }
    return index;
  }

  // renderer.originGroup is now a child of WebXRSupport's own originGroup (props.parent
  // above), which already supplies globalScale -- and, during an XR session,
  // position/quaternion/scale for viewer-relative placement, grip-drag, and two-handed
  // resize -- via its own default (auto-updating) matrix. So this group's .matrix only
  // needs to encode the embedding transform, nothing more.
  //
  // originGroup.matrixAutoUpdate is false (see symmetry-renderer.js), so .matrix must be
  // built explicitly here -- setting .position/.quaternion/.scale alone does NOT work,
  // silently: with matrixAutoUpdate off, nothing ever recomputes .matrix from those
  // properties. (An earlier version of this effect had to premultiply a manual globalScale
  // in here too, back when originGroup was parented directly to the raw scene instead of to
  // WebXRSupport's group -- see git history/symmetry-renderer-status.md if that context is
  // needed again; it does not apply with the current parenting.)
  createEffect( () => {
    const m = new Matrix4();
    if ( props.embedding ) {
      m.set( ...props.embedding );
      m.transpose();
    }
    renderer.originGroup.matrix.copy( m );
  } );

  createEffect( () => {
    const shapes = props.shapes || {};
    const hasInstances = Object.values( shapes ) .some( shape => shape.instances.length > 0 );

    if ( groupReady() || ! props.orientations || props.orientations.length === 0 || ! hasInstances )
      return;

    // The renderer's material/shader is built when the group becomes active, and it
    // indexes into the color palette unconditionally -- so at least one color must be
    // registered before switchSymmetryGroup(), or that lookup dereferences a null palette.
    for ( const shape of Object.values( shapes ) ) {
      for ( const instance of shape.instances ) {
        colorIndexFor( instance.color );
      }
    }

    orientationMatrices = props.orientations.map( toMatrix4 );
    renderer.registerSymmetryGroup( GROUP_ID, orientationMatrices );
    renderer.registerStyle( GROUP_ID, STYLE_ID );
    renderer.switchSymmetryGroup( GROUP_ID );
    setGroupReady( true );
  } );

  createEffect( () => {
    if ( ! groupReady() )
      return;
    const shapes = props.shapes || {};

    for ( const [ shapeId, shape ] of Object.entries( shapes ) ) {
      if ( ! registeredShapeIds.has( shapeId ) ) {
        // Phase 4 (polygon mode) note: there is only ever ONE fill geometry per shape, always
        // built with polygons=true (the fan triangulation). buildShapeGeometry's polygons=false
        // path is NOT a valid alternate "style" -- for any face with more than 3 vertices
        // (pentagons, hexagons, ...) it pushes that face's raw corner list straight into a
        // TRIANGLES draw call with no triangulation at all, producing garbage: wrong vertex
        // groupings, wrong winding, geometry that has nothing to do with the actual face.
        // ShapedGeometry (geometry.jsx's InstancedShape) never hits this because it builds
        // exactly one geometry, always from the *live* scene.polygons value, so it only takes
        // the false branch when the whole scene is actually in non-polygon mode. A first
        // attempt here pre-built both branches as two switchable "styles" so toggling would
        // need no lazy rebuild -- that produced exactly this scattered-triangle corruption the
        // moment any shape had a non-triangular face. The fan triangulation (polygons=true) is
        // geometrically identical to the non-fan path for already-triangular faces and correct
        // for everything else, so using it unconditionally is always safe. scene.polygons still
        // matters, just not here -- see the outline-visibility effect below.
        const geometry = buildShapeGeometry( shape, true );
        renderer.registerShape( GROUP_ID, STYLE_ID, shapeId, geometry );
        renderer.registerOutline( GROUP_ID, shapeId, buildOutlineGeometry( shape ) );
        shapeCentroidById.set( shapeId, geometry.shapeCentroid );
        registeredShapeIds.add( shapeId );
      }
    }

    for ( const shapeId of registeredShapeIds ) {
      renderer.removeAllInstances( STYLE_ID, shapeId );
    }

    // Every removeAllInstances above invalidates all previously-returned renderer
    // instanceIds for this group, so instanceRefById (and lastSelectedById, so removed
    // instances' ids don't linger forever) are rebuilt from scratch here rather than
    // updated incrementally -- see the comment where they're declared.
    const nextInstanceRefById = new Map();
    const nextSelectedById = new Map();
    // Phase 7: labels are rebuilt in lockstep with instances for the same reason -- there is
    // no cheaper "just this one instance changed" path today (see the Phase 6 comment on
    // instanceRefById), so every registration cycle recomputes every label from scratch.
    // Existing CSS2DObjects are reused where the vZome instance id is unchanged (skip DOM
    // element churn on every keystroke-triggered rebuild); ones for ids no longer present are
    // removed from originGroup, which -- per CSS2DObject's own "removed" listener -- also
    // detaches its DOM element (see three-stdlib's CSS2DRenderer.js).
    const nextLabelById = new Map();

    for ( const [ shapeId, shape ] of Object.entries( shapes ) ) {
      const centroid = shapeCentroidById.get( shapeId );
      for ( const instance of shape.instances ) {
        const selected = !! instance.selected;
        const instanceId = renderer.addInstance( STYLE_ID, shapeId, {
          position: toVector3( instance.position ),
          // scene.jsx uses orientation === -1 to mean "no orientation, use identity"
          // (see its own `(orientation < 0) ? 0 : orientation` guard), but
          // normalizeOrientationIndex() in symmetry-renderer.js throws on negative
          // indices instead of tolerating them, so clamp here to match.
          orientationIndex: instance.orientation < 0 ? 0 : instance.orientation,
          colorIndex: colorIndexFor( instance.color ),
          // Set the initial highlight directly (rather than relying on the separate
          // Phase 6 diff effect below to catch up) so a selected instance never flashes
          // unhighlighted for a frame right after a rebuild.
          highlight: selected ? SELECTED_HIGHLIGHT : 0,
        } );
        nextInstanceRefById.set( instance.id, { shapeId, instanceId } );
        nextSelectedById.set( instance.id, selected );

        if ( instance.label ) {
          // Same transform the GPU vertex shader applies per-vertex (rotatedPositionNode in
          // symmetry-renderer.js's createMaterialForGroup): orientation * localVertex +
          // instanceTranslation. Here localVertex is the shape's centroid (not a real
          // vertex, but the same math applies) and the result is expressed in originGroup's
          // own local space, matching where instance positions are already expressed --
          // exactly the space CSS2DObjects parented directly to originGroup need.
          const orientationIndex = instance.orientation < 0 ? 0 : instance.orientation;
          const worldPos = new Vector3( centroid.x, centroid.y, centroid.z )
            .applyMatrix4( orientationMatrices[ orientationIndex ] )
            .add( toVector3( instance.position ) );

          let label = labelById.get( instance.id );
          if ( ! label ) {
            const elem = document.createElement( 'div' );
            elem.className = 'vzome-label';
            elem.id = `vzome-label-${instance.label}`;
            elem.textContent = instance.label;
            label = new CSS2DObject( elem );
            renderer.originGroup.add( label );
          }
          label.position.copy( worldPos );
          nextLabelById.set( instance.id, label );
          labelById.delete( instance.id );
        }
      }
    }

    // Whatever's left in labelById belonged to instances not seen in this pass (removed, or
    // lost their label) -- detach them; CSS2DObject's "removed" listener cleans up the DOM.
    for ( const staleLabel of labelById.values() ) {
      renderer.originGroup.remove( staleLabel );
    }

    instanceRefById = nextInstanceRefById;
    lastSelectedById = nextSelectedById;
    labelById = nextLabelById;
  } );

  // Phase 6: selection highlight. Diffs instance.selected against the last-seen value per
  // vZome instance id and only calls setInstanceHighlight for instances whose selection
  // actually changed, so toggling a selection stays cheap regardless of model size -- unlike
  // the instance-registration effect above, which necessarily rebuilds everything on any
  // props.shapes change (see the comment on instanceRefById). This effect still depends on
  // (and therefore re-runs whenever) props.shapes changes identity, same as that one, but
  // its own body only touches the GPU buffers for instances that actually flipped.
  createEffect( () => {
    if ( ! groupReady() )
      return;
    const shapes = props.shapes || {};

    for ( const shape of Object.values( shapes ) ) {
      for ( const instance of shape.instances ) {
        const selected = !! instance.selected;
        if ( lastSelectedById.get( instance.id ) === selected )
          continue;
        const ref = instanceRefById.get( instance.id );
        if ( ! ref )
          continue; // shouldn't happen: registration effect above always runs first
        renderer.setInstanceHighlight( ref.shapeId, ref.instanceId, selected ? SELECTED_HIGHLIGHT : 0 );
        lastSelectedById.set( instance.id, selected );
      }
    }
  } );

  // Phase 4: outline visibility depends on two independent things, both required:
  // - props.polygons: whether this scene's faces are real polygon data at all (a format
  //   capability -- older/triangles-only files have no true face edges to outline; see the
  //   long comment above on why the fill geometry itself doesn't depend on this).
  // - cameraState.outlines: the end user's own show/hide preference (Settings dialog's
  //   "Outlines" switch, toggleOutlines() in context/camera.jsx), independent of and
  //   defaulting false regardless of what the scene data supports.
  // ShapedGeometry's InstancedShape (geometry.jsx) does not currently wire the second one up
  // -- its showOutlines is scene.polygons alone -- so this is a real behavior improvement
  // over it, not just parity, per explicit direction.
  createEffect( () => {
    if ( ! groupReady() )
      return;
    renderer.setOutlinesVisible( !! props.polygons && !! cameraState.outlines );
  } );

  return null;
};
