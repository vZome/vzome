
import { createEffect, createSignal, onCleanup, Show, untrack } from "solid-js";
import { unwrap } from "solid-js/store";
import { Matrix4, Vector3 } from "three";
import { CSS2DObject } from "three-stdlib";
import { useThree } from "solid-three";

import { createSymmetryRenderer } from "./context/symmetry-renderer.js";
import { buildShapeGeometry, buildOutlineGeometry, CanvasExportBindings } from "./geometry.jsx";
import { useCamera } from "./context/camera.jsx";
import { useWebXRClient } from "./context/webxr.jsx";
import { useInteractionSurface } from "./context/interaction.jsx";
import { useScene } from "./context/scene.jsx";
import { createRectTest } from "./rectpicking.js";

const STYLE_ID = "default";

// The renderer supports multiple coexisting symmetry groups, each baking its own orientation
// set into its own material's uniformArray at build time (see createMaterialForGroup /
// symmetry-renderer.js). That orientation uniform can neither be reassigned nor resized after
// the material exists, so a changed orientation set (icosahedral -> octahedral in the editor,
// or loading a different design into the SAME reused viewer component -- DesignViewer is reused
// across designs, not remounted, see index.jsx) requires switching to a DIFFERENT group keyed
// to that symmetry, not mutating the current one.
//
// Preferred key: props.symmetryId = "<field>:<symmetry>", propagated from the worker for vZome
// files (SYMMETRY_CHANGED -> scene.jsx). This is STABLE and, crucially, field-aware.
//
// Fallback key (previews/viewer, which carry no symmetry identity): hash the orientation float
// matrices. This works within one field but MUST NOT be used to distinguish fields: a symmetry's
// rotation matrices are the same real numbers in every algebraic field (octahedral rotations are
// octahedral rotations), so an orientation hash COLLIDES across fields -- which reused one field's
// group and its field-specific shapes for another, corrupting non-golden-field orientations. That
// is exactly why vZome files now send field:symmetry instead of relying on the hash.
const hashOrientations = ( orientations ) =>
  `g${orientations.length}:` + orientations.map( flat => flat.join( ',' ) ).join( ';' );
const groupKeyFor = ( symmetryId, orientations ) =>
  symmetryId ? `id:${symmetryId}` : hashOrientations( orientations );

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

// "No orientation, use identity" reaches us two different ways, and BOTH must map to 0:
//  - orientation === -1, how scene.jsx spells it (see its own `(orientation < 0) ? 0 :
//    orientation` guard), and what RenderedManifestation.strutZone defaults to.
//  - orientation absent entirely (undefined), how the .shapes.json exporter spells it for
//    manifestations with no strut zone -- balls always, and panels, whose vertices are in
//    absolute coordinates with position at the origin, so identity is mandatory for them.
// The old ShapedGeometry path folded both into identity for free, because the worker
// resolved the matrix itself as `orientations[ instance.orientation ] || IDENTITY_MATRIX`
// (vzome-worker-static.js) and `orientations[undefined]` is just undefined. This
// index-based path has no such fallback: a bare `orientation < 0` test is false for
// undefined, which then reaches normalizeOrientationIndex() and draws a RANDOM
// orientation -- rotating (and, about the origin, displacing) every panel in the design.
// Test with a `!( x >= 0 )` sense, not `x < 0`, so undefined and NaN land on 0 too --
// plus an explicit null check, since `null >= 0` is true in JS (null coerces to 0) and
// would otherwise slip through to be used as a literal index.
const orientationIndexOf = instance =>
  ( instance.orientation === null || ! ( instance.orientation >= 0 ) ) ? 0 : instance.orientation;

// Replacement for ShapedGeometry, built on createSymmetryRenderer's GPU-instanced meshes instead
// of one Three.js object per ball/strut. Fully drives the classic editor (not viewer-only):
// GPU picking + interaction (Phase 5, see pickAt and the pointer handlers below), selection
// highlight (Phase 6), and labels (Phase 7) are all implemented. See symmetry-renderer-plan.md
// and symmetry-renderer-status.md.
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
  // The viewer can swap this component out for ShapedGeometry when a design loads (see SceneViewer),
  //  so its content must leave the scene with it.  (GPU resources are not yet disposed.)
  onCleanup( () => props.parent.remove( renderer.originGroup ) );

  // The renderer group key currently switched-to (null until the first group is registered).
  // Its identity is what every downstream effect keys off; see groupKeyFor.
  const [ activeGroupKey, setActiveGroupKey ] = createSignal( null );
  // Which renderer groups have already been registered (registerSymmetryGroup called once per
  // key). switchSymmetryGroup itself is idempotent, but registerSymmetryGroup throws on a
  // duplicate key, so we must track this ourselves.
  const registeredGroupKeys = new Set();
  // Shapes are registered per group (each group has its own slots), and different symmetries
  // generally have different shape sets, so this can't be a single flat Set shared across
  // groups the way it was when there was only ever one hardcoded group -- key -> Set(shapeId).
  const registeredShapeIdsByGroup = new Map();
  const colorIndexByCss = new Map();

  // Selection highlight: maps stable vZome instance id -> renderer refs, so the O(1)
  // highlightSelection hook (registered on the scene context below) can highlight exactly the
  // instance a SELECTION_TOGGLED names without scanning. Keyed by vZome id, not renderer
  // instanceId, because the instance-registration effect tears down and re-adds every instance
  // (replaceShapeInstances) on any structural props.shapes change, so renderer instanceIds are
  // not stable across a rebuild; the vZome id is. Refreshed every registration cycle.
  let instanceRefById = new Map(); // vZome instance id -> { shapeId, instanceId }

  // Phase 5 (picking): the reverse direction of instanceRefById, keyed by the renderer's
  // own numeric instanceId (what renderer.pickAt() returns) instead of the vZome instance
  // id -- this is what a pick hit needs to resolve into the hit record
  // { source: 'scene', id, position, type, selected, label } that the interaction tools
  // receive (the same shape ShapedGeometry's hit source returns, in geometry.jsx). Rebuilt
  // every registration cycle in lockstep with instanceRefById, for the same reason (see its
  // comment) -- instanceIds are not stable across a full rebuild.
  let metadataByInstanceId = new Map(); // renderer instanceId -> { source, id, position, type, selected, label }

  // Phase 7 (labels): the ACTIVE group's orientation matrices, refreshed by the group-
  // registration effect on every symmetry switch (not just once) so the label-positioning
  // math (see the instance-registration effect) can replicate the GPU vertex shader's
  // rotatedPositionNode transform (orientation * localVertex + instanceTranslation, see
  // createMaterialForGroup in symmetry-renderer.js) once per label in JS, instead of
  // per-vertex on the GPU -- there is no per-instance mesh for a <Label> to be parented to
  // and inherit that transform from "for free" the way ShapedGeometry's Instance gets it.
  // Must track the active group: after a symmetry switch the instance orientation indices
  // index into the NEW group's orientation set, so labels would be mis-placed if this still
  // held the previous group's matrices.
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
    // originGroup.matrixAutoUpdate is false, so three.js never recomputes .matrixWorld from
    // .matrix on its own; without flagging it, a changed embedding updates .matrix but the group
    // keeps rendering its stale .matrixWorld. This is invisible for most switches (the group-
    // switch churn happens to flush a world update), but switching TO an identity embedding (e.g.
    // heptagon octahedral, coming from the skewed antiprism) left the old skew on screen.
    renderer.originGroup.matrixWorldNeedsUpdate = true;
  } );

  // Group-registration / symmetry-switch effect. Re-runs whenever props.symmetryId or
  // props.orientations changes (the values that change on -- and only on -- a real symmetry
  // change; see groupKeyFor). On the FIRST symmetry, and on every change to a not-yet-seen one
  // (editor symmetry switch, or a different design loaded into this reused component), it
  // registers a new renderer group and switches to it; switching back to a previously-seen one
  // reuses that group with no rebuild. scene.jsx writes symmetryId then orientations in the same
  // (batched) event handler, so both are consistent by the time this effect flushes.
  createEffect( () => {
    if ( ! props.orientations || props.orientations.length === 0 )
      return;

    const key = groupKeyFor( props.symmetryId, props.orientations );
    // untrack the read: this effect also writes activeGroupKey below, and a tracked self-read
    // would make it re-run on its own write. Its real dependencies are symmetryId/orientations.
    if ( key === untrack( activeGroupKey ) )
      return; // same symmetry as currently active -- nothing to switch

    // Refresh the retained active-group matrices for the label transform (see its comment) --
    // on every switch, not just first registration, since a switch back to a previously-seen
    // group still changes which orientation set the current instance indices refer to.
    orientationMatrices = props.orientations.map( toMatrix4 );

    if ( ! registeredGroupKeys.has( key ) ) {
      renderer.registerSymmetryGroup( key, orientationMatrices );
      renderer.registerStyle( key, STYLE_ID );
      registeredGroupKeys.add( key );
    }

    // switchSymmetryGroup builds this group's GPU material (with the correct orientation
    // uniform) the first time it becomes active, and hides the previously-active group's
    // meshes. The shape/instance effects below re-register this group's shapes and instances
    // (keyed off activeGroupKey, which we set here), so the switch and the content refill
    // happen in lockstep.
    renderer.switchSymmetryGroup( key );
    setActiveGroupKey( key );
  } );

  createEffect( () => {
    const groupKey = activeGroupKey();
    if ( ! groupKey )
      return;
    const shapes = props.shapes || {};

    // Shapes are registered per group; different symmetries have different shape sets, and a
    // switch back to a prior group must not re-register shapes it already has. Track them by
    // the active group key.
    let registeredShapeIds = registeredShapeIdsByGroup.get( groupKey );
    if ( ! registeredShapeIds ) {
      registeredShapeIds = new Set();
      registeredShapeIdsByGroup.set( groupKey, registeredShapeIds );
    }

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
        renderer.registerShape( groupKey, STYLE_ID, shapeId, geometry );
        renderer.registerOutline( groupKey, shapeId, buildOutlineGeometry( shape ) );
        shapeCentroidById.set( shapeId, geometry.shapeCentroid );
        registeredShapeIds.add( shapeId );
      }
    }

    // Every registration cycle rebuilds every shape's instances from scratch -- worker scene
    // updates (e.g. a single strut-preview move during a drag) replace props.shapes wholesale
    // (see scene.jsx), so there's no per-instance diff available here yet to do less work.
    // What IS fixed here: batching each shape's instances into ONE renderer.replaceShapeInstances
    // call instead of removeAllInstances + one renderer.addInstance call per instance --
    // addInstance/removeInstance each trigger their own full GPU-buffer rewrite for the whole
    // shape (see syncShapeInstances in symmetry-renderer.js), so building up a shape via N
    // addInstance calls was O(N^2) in that shape's instance count, on top of running on every
    // single worker update. replaceShapeInstances does exactly one rewrite regardless of count.
    // This invalidates all previously-returned renderer instanceIds for this group, so
    // instanceRefById is rebuilt from scratch here rather than updated incrementally -- see the
    // comment where it's declared.
    const nextInstanceRefById = new Map();
    const nextMetadataByInstanceId = new Map();
    // Phase 7: labels are rebuilt in lockstep with instances for the same reason -- there is
    // no cheaper "just this one instance changed" path for labels today, so every registration
    // cycle recomputes every label from scratch.
    // Existing CSS2DObjects are reused where the vZome instance id is unchanged (skip DOM
    // element churn on every keystroke-triggered rebuild); ones for ids no longer present are
    // removed from originGroup, which -- per CSS2DObject's own "removed" listener -- also
    // detaches its DOM element (see three-stdlib's CSS2DRenderer.js).
    const nextLabelById = new Map();

    for ( const [ shapeId, shape ] of Object.entries( shapes ) ) {
      const centroid = shapeCentroidById.get( shapeId );
      const instanceOptionsList = shape.instances.map( instance => {
        // untrack the `selected` read: this registration effect rewrites every shape's whole
        // GPU buffer, so it must NOT re-run on a mere selection toggle -- live toggles are
        // owned by the O(1) highlightSelection hook (registered below), which updates only the
        // flipped instance. Reading selected here (for anti-flash initial-highlight seeding, see
        // `highlight` below) without untrack would subscribe this expensive effect to every
        // instance's selected flag, making every pick rebuild the entire model. untrack still
        // returns the current value; it only suppresses the dependency. (Structural reads --
        // instances array identity, position, orientation, color -- stay tracked, so genuine
        // edits still rebuild.)
        const selected = untrack( () => !! instance.selected );
        return {
          position: toVector3( instance.position ),
          // Both spellings of "no orientation" must become identity here; see
          // orientationIndexOf above for why a bare `< 0` test is not enough.
          orientationIndex: orientationIndexOf( instance ),
          colorIndex: colorIndexFor( instance.color ),
          // Set the initial highlight directly (rather than relying on a later toggle to catch
          // up) so an already-selected instance never flashes unhighlighted for a frame right
          // after a structural rebuild. The store's `selected` is the source of truth here.
          highlight: selected ? SELECTED_HIGHLIGHT : 0,
        };
      } );
      const instanceIds = renderer.replaceShapeInstances( STYLE_ID, shapeId, instanceOptionsList );

      shape.instances.forEach( ( instance, i ) => {
        // untrack for the same reason as the initial-highlight read above: this only seeds the
        // picking metadata (selected field), and must not make this effect a subscriber to
        // selection toggles.
        const selected = untrack( () => !! instance.selected );
        const instanceId = instanceIds[ i ];
        nextInstanceRefById.set( instance.id, { shapeId, instanceId } );
        nextMetadataByInstanceId.set( instanceId, {
          source: 'scene', id: instance.id, position: instance.position, type: instance.type,
          selected, label: instance.label,
        } );

        if ( instance.label ) {
          // Same transform the GPU vertex shader applies per-vertex (rotatedPositionNode in
          // symmetry-renderer.js's createMaterialForGroup): orientation * localVertex +
          // instanceTranslation. Here localVertex is the shape's centroid (not a real
          // vertex, but the same math applies) and the result is expressed in originGroup's
          // own local space, matching where instance positions are already expressed --
          // exactly the space CSS2DObjects parented directly to originGroup need.
          // Must use the very same index the addInstance call above used -- otherwise the
          // label is placed by a different transform than its own instance is drawn with.
          const orientationIndex = orientationIndexOf( instance );
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
      } );
    }

    // Shapes that existed before but have no instances in this pass still need their buffers
    // cleared (replaceShapeInstances above only runs for shapes present in `shapes`).
    for ( const shapeId of registeredShapeIds ) {
      if ( ! ( shapeId in shapes ) ) {
        renderer.removeAllInstances( STYLE_ID, shapeId );
      }
    }

    // Whatever's left in labelById belonged to instances not seen in this pass (removed, or
    // lost their label) -- detach them; CSS2DObject's "removed" listener cleans up the DOM.
    for ( const staleLabel of labelById.values() ) {
      renderer.originGroup.remove( staleLabel );
    }

    instanceRefById = nextInstanceRefById;
    labelById = nextLabelById;
    metadataByInstanceId = nextMetadataByInstanceId;
  } );

  // Selection highlight (O(1) per toggle). Registered with the scene context so scene.jsx's
  // SELECTION_TOGGLED handler can call this directly with the exact { shapeId, id, selected }
  // the worker named -- an id-keyed lookup, no scanning. This replaces an earlier reactive
  // effect that re-scanned every instance of every shape (through slow SolidJS store proxies)
  // on every toggle just to rediscover the one changed id -- O(N) per single click, and
  // O(N^2) for select-all/deselect-all (one SELECTION_TOGGLED per manifestation).
  //
  // shapeId from the message is a vZome shape id; instanceRefById is keyed by vZome instance
  // id and already carries the renderer's own shapeId/instanceId, so the message's shapeId is
  // not needed for the lookup (kept in the signature to match the event payload). instanceRef/
  // metadata maps are reassigned each registration cycle; this closure reads the current `let`
  // bindings at call time, so it always sees the latest.
  const { setSelectionHighlighter } = useScene();
  setSelectionHighlighter( ( shapeId, id, selected ) => {
    const ref = instanceRefById.get( id );
    if ( ! ref )
      return; // instance not (yet) registered in the active group
    renderer.setInstanceHighlight( ref.shapeId, ref.instanceId, selected ? SELECTED_HIGHLIGHT : 0 );
    // Keep pick metadata's `selected` fresh (it is the hit record tools receive).
    const meta = metadataByInstanceId.get( ref.instanceId );
    if ( meta )
      meta.selected = !! selected;
  } );
  onCleanup( () => setSelectionHighlighter( null ) );

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
    if ( ! activeGroupKey() )
      return;
    renderer.setOutlinesVisible( !! props.polygons && !! cameraState.outlines );
  } );

  // Phase 5: picking. ShapedGeometry (geometry.jsx) can raycast its per-instance meshes, but
  // SymmetryGeometry has no per-instance mesh at all -- everything is GPU-instanced into a
  // handful of shared InstancedMeshes -- so there is nothing for a raycaster to hit-test
  // against. renderer.pickAt() (an offscreen ID-encoded render pass + pixel readback) is the
  // GPU-instancing-compatible equivalent. This component registers it as a hit source with
  // the InteractionToolProvider, which owns all pointer handling and decides when to pick
  // (see context/interaction.jsx); tools that don't pick (e.g. CameraTool) never trigger
  // a pick, so they never build the picking renderer either.
  //
  // useThree().gl and .camera are live getters (see the long comment on this same footgun
  // in labels.jsx, found and fixed earlier in this project) -- must be re-read fresh at
  // each use, never destructured into a stale binding captured once at setup time.
  const three = useThree();
  const surface = useInteractionSurface();
  const pickAt = async ( clientX, clientY ) =>
  {
    const hit = await renderer.pickAt( clientX, clientY, three.gl, three.camera );
    // null on a miss, or for a stale pick racing a rebuild (props.shapes changed mid-flight)
    return ( hit && metadataByInstanceId.get( hit.instanceId ) ) || null;
  };

  // Marquee selection: every instance whose shape projects entirely inside the rectangle.  This
  // replicates the vertex shader's transform (originGroup world * translation * orientation) on
  // the CPU, once per marquee; hidden-behind-others instances count, since this isn't a GPU pick.
  const hitsInRect = ( rect ) =>
  {
    const test = createRectTest( three.camera, three.canvas, rect );
    renderer.originGroup .updateMatrixWorld( true );
    const originWorld = renderer.originGroup.matrixWorld;
    const instanceMatrix = new Matrix4(), modelMatrix = new Matrix4(), identity = new Matrix4();
    const hits = [];
    const shapes = unwrap( untrack( () => props.shapes ) ) || {};
    for ( const shape of Object.values( shapes ) ) {
      for ( const instance of shape.instances ) {
        const ref = instanceRefById.get( instance.id );
        const meta = ref && metadataByInstanceId.get( ref.instanceId );
        if ( ! meta )
          continue;
        const [ x, y, z ] = instance.position;
        instanceMatrix .copy( orientationMatrices[ orientationIndexOf( instance ) ] || identity ) .setPosition( x, y, z );
        test .setModelMatrix( modelMatrix .multiplyMatrices( originWorld, instanceMatrix ) );
        if ( shape.vertices .every( v => test .contains( v.x, v.y, v.z ) ) )
          hits .push( meta );
      }
    }
    return hits;
  };

  if ( surface )
    onCleanup( surface.addHitSource( { pickAt, hitsInRect, warmUp: renderer.warmPicking } ) );

  // This component contributes no JSX scene content of its own -- the renderer's GPU-instanced
  // meshes are parented imperatively under props.parent -- but it still must mount
  // CanvasExportBindings, which is what supplies image capture (File > Export image, the
  // GitHub sharing dialog) and glTF export for this canvas. ShapedGeometry mounts the same
  // component; whichever geometry path is active wires up the export contexts.
  return <CanvasExportBindings />;
};
