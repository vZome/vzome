
// MeshBasicNodeMaterial/MeshLambertNodeMaterial (and the rest of the Node-material family)
// only exist in the three/webgpu build, not the plain "three" entry point -- the POC this
// was ported from imported everything via `import * as THREE from "three/webgpu"`. The
// other classes here (Matrix4, Vector3, etc.) are the same class identities either way,
// so importing them from three/webgpu too keeps this file on a single consistent source.
import {
  Group,
  InstancedMesh,
  Matrix4,
  MeshBasicNodeMaterial,
  MeshLambertNodeMaterial,
  RenderTarget,
  Scene,
  Vector3,
  Color,
  InstancedBufferAttribute,
  Euler,
  BufferGeometry,
  UnsignedByteType,
  RGBAFormat,
  WebGPURenderer,
} from "three/webgpu";
import {
  Fn,
  attribute,
  float,
  floor,
  mod,
  positionLocal,
  uniformArray,
  vec3,
  vec4
} from "three/tsl";

export const INITIAL_SHAPE_CAPACITY = 64;


// parent is whatever Object3D the managed originGroup should attach under -- the raw
// Three.js scene for the non-XR desktop path, or WebXRSupport's own originGroup (see
// symmetry-geometry.jsx) so that XRGripToMove/XRScaling, which reposition/rescale that
// group on XR session start, carry this renderer's content along automatically instead of
// leaving it stranded at whatever desktop-camera-relative transform it had before entering XR.
export function createSymmetryRenderer(parent)
{
  // Create a group to hold all managed meshes. matrixAutoUpdate must be off: callers
  // (e.g. symmetry-geometry.jsx, for the embedding matrix) set originGroup.matrix directly
  // via .matrix.copy()/applyMatrix4(), and Three's default per-frame auto-update would
  // silently overwrite that with an identity/position-quaternion-scale recompute.
  // ShapedGeometry's equivalent wrapper group in geometry.jsx sets the same T.Group
  // matrixAutoUpdate={false} for the same reason.
  const originGroup = new Group();
  originGroup.matrixAutoUpdate = false;
  parent.add(originGroup);

  const colorMatrices = [];
  let colorPalette = null;

  const symmetryGroups = new Map();
  let activeGroupId = null;
  let discardInactiveShaders = false;

  // Picking uses its own dedicated WebGPURenderer, rendering to an offscreen canvas that's
  // never inserted into the DOM. This rules out any state confusion from sharing the main
  // renderer (which owns the continuous animation-loop render of the visible scene) across
  // two different Scene graphs. GPU resource caches (Attributes/Geometries, both WeakMaps
  // keyed by the source BufferGeometry/attribute objects) are per-renderer-instance in
  // three.js, so sharing geometries between the main renderer and this one is safe -- each
  // uploads and caches its own GPU-side copy independently.
  let pickingRendererPromise = null;
  async function getPickingRenderer() {
    if (!pickingRendererPromise) {
      pickingRendererPromise = (async () => {
        const canvas = document.createElement("canvas");
        const renderer = new WebGPURenderer({
          canvas,
          antialias: false,
          alpha: true,
          forceWebGL: true,
        });
        await renderer.init();
        return renderer;
      })();
    }
    return pickingRendererPromise;
  }

  function registerSymmetryGroup(groupId, orientations) {
    if (symmetryGroups.has(groupId)) {
      throw new Error(`Symmetry group '${groupId}' already exists.`);
    }
    if (!Array.isArray(orientations) || orientations.length === 0) {
      throw new Error("A symmetry group needs at least one orientation.");
    }

    const orientationMatrices = orientations.map((item) => {
      if (item instanceof Matrix4) {
        return item.clone();
      }
      if (item instanceof Euler) {
        return new Matrix4().makeRotationFromEuler(item);
      }
      throw new Error("Orientation must be a Euler or Matrix4.");
    });

    const group = {
      id: groupId,
      orientations: orientationMatrices,
      styles: new Map(),
      slots: new Set(),
      activeStyleId: null,
      instancesByShape: new Map(),
      nextInstanceId: 1,
      gpu: null
    };

    symmetryGroups.set(groupId, group);
    return group;
  }

  function registerStyle(groupId, styleId) {
    const group = getSymmetryGroup(groupId);
    if (group.styles.has(styleId)) {
      throw new Error(`Style '${styleId}' is already registered in group '${groupId}'.`);
    }
    group.styles.set(styleId, { id: styleId, geometries: new Map() });
    if (group.activeStyleId === null) {
      group.activeStyleId = styleId;
    }
  }

  function registerShape(groupId, styleId, slotId, geometry) {
    if (!(geometry instanceof BufferGeometry)) {
      throw new Error("Shape geometry must be a BufferGeometry.");
    }
    const group = getSymmetryGroup(groupId);
    const style = getStyle(group, styleId);
    if (style.geometries.has(slotId)) {
      throw new Error(`Geometry for slot '${slotId}' is already registered in style '${styleId}'.`);
    }

    style.geometries.set(slotId, geometry);
    group.slots.add(slotId);

    if (group.gpu) {
      // Add to the geometry cache for this slot/style
      let slotCache = group.gpu.cachedGeometries.get(slotId);
      if (!slotCache) {
        slotCache = new Map();
        group.gpu.cachedGeometries.set(slotId, slotCache);
      }
      slotCache.set(styleId, geometry.clone());

      // Create shape entry only if this is a brand-new slot
      if (!group.gpu.shapeEntries.has(slotId)) {
        const activeGeom = (group.activeStyleId && slotCache.get(group.activeStyleId))
          ?? slotCache.values().next().value;
        const outlineGeom = group.gpu.outlineGeometries.get(slotId) ?? null;
        const entry = createShapeEntry(slotId, activeGeom, outlineGeom, group.gpu.material, group.gpu.pickingMaterial, group.gpu.outlineMaterial);
        group.gpu.shapeEntries.set(slotId, entry);
        if (group.id === activeGroupId) {
          originGroup.add(entry.mesh);
          group.gpu.pickingOriginGroup.add(entry.pickingMesh);
          if (entry.outlineMesh) {
            originGroup.add(entry.outlineMesh);
          }
        }
      }
    }
  }

  // Outline (wireframe) geometry is registered per-shape. Every registered outline mesh
  // starts hidden (see makeOutlineMesh); call setOutlinesVisible() to show them -- this is
  // independent of fill style (see the comment on setOutlinesVisible).
  function registerOutline(groupId, slotId, outlineGeometry) {
    if (!(outlineGeometry instanceof BufferGeometry)) {
      throw new Error("Outline geometry must be a BufferGeometry.");
    }
    const group = getSymmetryGroup(groupId);
    if (group.gpu && group.gpu.outlineGeometries.has(slotId)) {
      throw new Error(`Outline geometry for slot '${slotId}' is already registered in group '${groupId}'.`);
    }

    if (group.gpu) {
      group.gpu.outlineGeometries.set(slotId, outlineGeometry.clone());
      const entry = group.gpu.shapeEntries.get(slotId);
      if (entry && !entry.outlineMesh) {
        attachOutlineMeshToEntry(group, entry, slotId);
      }
    } else {
      // No GPU state yet: stash for ensureGroupGpu to pick up when it builds shape entries.
      group.pendingOutlineGeometries = group.pendingOutlineGeometries ?? new Map();
      group.pendingOutlineGeometries.set(slotId, outlineGeometry.clone());
    }
  }

  function switchSymmetryGroup(groupId, options = {}) {
    const {
      discardUnusedShaders = discardInactiveShaders
    } = options;
    const nextGroup = getSymmetryGroup(groupId);

    if (activeGroupId === groupId) {
      return;
    }

    if (activeGroupId !== null) {
      const previous = symmetryGroups.get(activeGroupId);
      setGroupMeshesCount(previous, 0);
    }

    ensureGroupGpu(nextGroup);
    ensureGroupHasActiveStyle(nextGroup);
    addGroupMeshesToScene(nextGroup);
    activeGroupId = groupId;
    syncAllInstancesForGroup(nextGroup);

    if (discardUnusedShaders) {
      for (const group of symmetryGroups.values()) {
        if (group.id !== activeGroupId) {
          disposeGroupGpu(group);
        }
      }
    }
  }

  function setDiscardInactiveShaders(enabled) {
    discardInactiveShaders = Boolean(enabled);
  }

  function switchStyle(styleId) {
    const group = getActiveGroup();
    getStyle(group, styleId);
    if (group.activeStyleId === styleId) {
      return;
    }
    group.activeStyleId = styleId;
    if (group.gpu) {
      swapGeometriesForStyle(group, styleId);
    }
  }

  function swapGeometriesForStyle(group, styleId) {
    for (const [slotId, entry] of group.gpu.shapeEntries) {
      swapBaseGeometry(entry, slotId, styleId, group.gpu.cachedGeometries);
    }
  }

  // Independent of styles/switchStyle: shows or hides every registered outline mesh on the
  // active group. Outline visibility is not tied to which fill style is active -- a single
  // fill geometry (the fan-triangulated one; see the long comment where symmetry-geometry.jsx
  // builds it) is correct for every shape regardless of outline visibility, so there is no
  // reason to route this through a style switch.
  function setOutlinesVisible(visible) {
    const group = getActiveGroup();
    if (!group.gpu) return;
    for (const entry of group.gpu.shapeEntries.values()) {
      if (entry.outlineMesh) {
        entry.outlineMesh.visible = visible;
      }
    }
  }

  // Move instanced attributes from the current mesh geometry to the cached geometry
  // for newStyleId, then point the mesh at that geometry.
  // No cloning and no disposal: the old cached geometry stays alive for future switches.
  function swapBaseGeometry(entry, slotId, newStyleId, cachedGeometries) {
    const slotCache = cachedGeometries.get(slotId);
    if (!slotCache) return;
    const newGeom = slotCache.get(newStyleId);
    if (!newGeom) return;
    const oldGeom = entry.mesh.geometry;
    if (oldGeom === newGeom) return;

    for (const name of ["orientationIndex", "instanceTranslation", "colorIndex", "highlightIntensity", "pickingId"]) {
      const attr = oldGeom.getAttribute(name);
      if (attr) {
        oldGeom.deleteAttribute(name);
        newGeom.setAttribute(name, attr);
      }
    }
    entry.mesh.geometry = newGeom;
    if (entry.pickingMesh) {
      entry.pickingMesh.geometry = newGeom;
    }
    // oldGeom is still in the cache with only base vertex data, ready for the next switch back
  }

  function registerColor(colorInput) {
    const color = normalizeColorInput(colorInput);
    colorMatrices.push(color);
    colorPalette = uniformArray(colorMatrices);
    return colorMatrices.length - 1;
  }

  function addInstance(styleId, shapeId, instanceOptions = {}) {
    const group = getActiveGroup();
    getStyle(group, styleId);
    ensureStyleIsActiveForInstances(group, styleId);
    const shapeMap = getOrCreateShapeInstanceMap(group, shapeId);

    const position = instanceOptions.position ?? new Vector3();
    const orientationIndex = normalizeOrientationIndex(group, instanceOptions.orientationIndex);
    const colorIndex = normalizeColorIndex(instanceOptions.colorIndex);
    const highlight = instanceOptions.highlight ?? 0;

    const id = group.nextInstanceId;
    group.nextInstanceId += 1;
    shapeMap.set(id, {
      position,
      orientationIndex,
      colorIndex,
      highlight
    });

    syncShapeInstances(group, shapeId);
    return id;
  }

  function removeInstance(styleId, shapeId, instanceId) {
    const group = getActiveGroup();
    const shapeMap = group.instancesByShape.get(shapeId);
    if (!shapeMap) {
      return false;
    }
    const removed = shapeMap.delete(instanceId);
    if (removed) {
      syncShapeInstances(group, shapeId);
    }
    return removed;
  }

  function removeAllInstances(styleId, shapeId) {
    const group = getActiveGroup();
    group.instancesByShape.delete(shapeId);
    syncShapeInstances(group, shapeId);
  }

  // Batch equivalent of removeAllInstances + N x addInstance, but with exactly ONE
  // syncShapeInstances call (one GPU buffer rewrite) regardless of instance count, instead of
  // one rewrite per removeAllInstances/addInstance call -- addInstance/removeInstance are each
  // O(instances in that shape) because syncShapeInstances rewrites the whole shape's buffers
  // from scratch, so building up a shape via N addInstance calls is O(N^2). Callers that
  // already know the full desired instance list for a shape (e.g. symmetry-geometry.jsx's
  // registration effect) should use this instead. Returns the assigned instanceIds, in the
  // same order as `instances`, for the caller to associate back with its own ids.
  function replaceShapeInstances(styleId, shapeId, instances) {
    const group = getActiveGroup();
    getStyle(group, styleId);
    ensureStyleIsActiveForInstances(group, styleId);
    const shapeMap = new Map();
    const assignedIds = new Array(instances.length);
    for (let i = 0; i < instances.length; i += 1) {
      const instanceOptions = instances[i];
      const id = group.nextInstanceId;
      group.nextInstanceId += 1;
      shapeMap.set(id, {
        position: instanceOptions.position ?? new Vector3(),
        orientationIndex: normalizeOrientationIndex(group, instanceOptions.orientationIndex),
        colorIndex: normalizeColorIndex(instanceOptions.colorIndex),
        highlight: instanceOptions.highlight ?? 0,
      });
      assignedIds[i] = id;
    }
    group.instancesByShape.set(shapeId, shapeMap);
    syncShapeInstances(group, shapeId);
    return assignedIds;
  }

  function clearActiveInstances() {
    const group = getActiveGroup();
    clearGroupInstances(group);
    setGroupMeshesCount(group, 0);
  }


  function createMaterialForGroup(group) {
    const orientationUniform = uniformArray(group.orientations);
    const orientationIndexNode = attribute("orientationIndex", "float");
    const instanceTranslationNode = attribute("instanceTranslation", "vec3");
    const colorIndexNode = attribute("colorIndex", "float");

    const highlightIntensityNode = attribute("highlightIntensity", "float");

    const rotatedPositionNode = Fn(() => {
      const orientationMat = orientationUniform.element(orientationIndexNode.toInt());
      return orientationMat.mul(vec4(positionLocal, 1.0)).xyz.add(instanceTranslationNode);
    })();
    const indexedColorNode = colorPalette.element(colorIndexNode.toInt());

    // MeshLambertNodeMaterial (diffuse only, no specular term) to match ShapedGeometry's
    // MeshLambertMaterial exactly -- see geometry.jsx's InstancedShape. The original
    // MeshPhongNodeMaterial (shininess:55, a fairly bright bluish specular) came from the
    // webxr-poc port this file started from and was never meant to match the app's actual
    // look; it made flat-faceted low-poly shapes show a distracting specular highlight that
    // ShapedGeometry never had.
    const material = new MeshLambertNodeMaterial({
      flatShading: true,
    });
    material.positionNode = rotatedPositionNode;
    material.colorNode = indexedColorNode;
    // Use white for emissive light, modulated by highlight intensity
    material.emissiveNode = vec4(1.0, 1.0, 1.0, 1.0).xyz.mul(highlightIntensityNode);

    // Picking material: encodes per-instance ID into RGB, shares the same vertex transform
    const pickingIdNode = attribute("pickingId", "float");
    // R = id % 256, G = floor(id/256) % 256, B = floor(id/65536)
    const pickingColorNode = vec3(
      mod(pickingIdNode, float(256.0)).div(255.0),
      mod(floor(pickingIdNode.div(float(256.0))), float(256.0)).div(255.0),
      floor(pickingIdNode.div(float(65536.0))).div(255.0)
    );
    const pickingMaterial = new MeshBasicNodeMaterial({ toneMapped: false });
    pickingMaterial.positionNode = rotatedPositionNode;
    pickingMaterial.colorNode = pickingColorNode;

    // Outline material: plain black lines, shares the same per-instance vertex transform.
    // ShapedGeometry's equivalent (geometry.jsx) uses LineBasicMaterial with linewidth=4.4,
    // but WebGL/WebGPU both ignore Material.linewidth for regular (non-Line2) line
    // rendering, so it's a fixed 1px line here regardless -- same visual limitation
    // ShapedGeometry already silently has.
    const outlineMaterial = new MeshBasicNodeMaterial({ toneMapped: false });
    outlineMaterial.positionNode = rotatedPositionNode;
    outlineMaterial.colorNode = vec3(0, 0, 0);

    return { material, pickingMaterial, outlineMaterial };
  }

  // meshGeometry is a pre-cloned cached geometry; it is used directly (not cloned again).
  // outlineGeometry (also pre-cloned, or null if none registered yet) gets its own
  // InstancedMesh forced into line-list draw mode -- see makeOutlineMesh below.
  function createShapeEntry(slotId, meshGeometry, outlineGeometry, material, pickingMaterial, outlineMaterial) {
    const mesh = new InstancedMesh(meshGeometry, material, INITIAL_SHAPE_CAPACITY);
    const orientBuffer = new Float32Array(INITIAL_SHAPE_CAPACITY);
    const translationBuffer = new Float32Array(INITIAL_SHAPE_CAPACITY * 3);
    const colorIndexBuffer = new Float32Array(INITIAL_SHAPE_CAPACITY);
    const highlightBuffer = new Float32Array(INITIAL_SHAPE_CAPACITY);
    const pickingIdBuffer = new Float32Array(INITIAL_SHAPE_CAPACITY);
    attachAttributes(mesh.geometry, orientBuffer, translationBuffer, colorIndexBuffer, highlightBuffer, pickingIdBuffer);
    initializeIdentityMatrices(mesh, INITIAL_SHAPE_CAPACITY);
    mesh.count = 0;
    // Three's default frustum culling uses meshGeometry's bounding sphere, which only
    // covers the single un-instanced base shape (e.g. one ball near the local origin) --
    // it knows nothing about the per-instance translation applied in the vertex shader via
    // positionNode. As the camera frustum tightens (e.g. zooming in), that tiny bounding
    // sphere can fall outside it well before the actual (scattered) instances do, causing
    // Three to cull the whole mesh at once -- every instance of this shape disappears
    // together, while other shapes with different local bounding spheres stay visible.
    // Disabling frustum culling here trades a small amount of GPU work (never culled, even
    // when genuinely off-screen) for correctness.
    mesh.frustumCulled = false;

    // Picking mesh shares the same geometry (and therefore all instanced attributes)
    const pickingMesh = new InstancedMesh(meshGeometry, pickingMaterial, INITIAL_SHAPE_CAPACITY);
    initializeIdentityMatrices(pickingMesh, INITIAL_SHAPE_CAPACITY);
    pickingMesh.count = 0;
    pickingMesh.frustumCulled = false;

    const outlineMesh = outlineGeometry
      ? makeOutlineMesh(outlineGeometry, outlineMaterial, INITIAL_SHAPE_CAPACITY, orientBuffer, translationBuffer, highlightBuffer, pickingIdBuffer)
      : null;

    return {
      slotId,
      capacity: INITIAL_SHAPE_CAPACITY,
      mesh,
      pickingMesh,
      outlineMesh,
      orientBuffer,
      translationBuffer,
      colorIndexBuffer,
      highlightBuffer,
      pickingIdBuffer,
    };
  }

  // Builds an InstancedMesh that draws as GL_LINES instead of triangles. WebGPUUtils'
  // getPrimitiveTopology (and the WebGL fallback backend's equivalent dispatch) both check
  // object.isLineSegments *before* object.isMesh, and InstancedMesh extends Mesh without
  // setting isLineSegments itself -- so forcing that flag true here is enough to make an
  // *instanced* line-list mesh, which Three's public API has no constructor for. Instancing
  // itself (drawIndexed/instanceCount) is orthogonal to topology, so this is safe.
  // outlineGeometry is a plain (non-instanced-attribute) BufferGeometry from
  // buildOutlineGeometry; the instanced attributes are attached here from buffers the
  // fill mesh already owns, so per-instance data (position/orientation/highlight/picking
  // id) never needs writing twice -- both geometries' attributes wrap the same arrays.
  function makeOutlineMesh(outlineGeometry, outlineMaterial, capacity, orientBuffer, translationBuffer, highlightBuffer, pickingIdBuffer) {
    outlineGeometry.setAttribute("orientationIndex", new InstancedBufferAttribute(orientBuffer, 1));
    outlineGeometry.setAttribute("instanceTranslation", new InstancedBufferAttribute(translationBuffer, 3));
    outlineGeometry.setAttribute("highlightIntensity", new InstancedBufferAttribute(highlightBuffer, 1));
    outlineGeometry.setAttribute("pickingId", new InstancedBufferAttribute(pickingIdBuffer, 1));

    const outlineMesh = new InstancedMesh(outlineGeometry, outlineMaterial, capacity);
    outlineMesh.isLineSegments = true;
    initializeIdentityMatrices(outlineMesh, capacity);
    outlineMesh.count = 0;
    outlineMesh.frustumCulled = false;
    // Default hidden; callers that need it visible (e.g. ensureShapeCapacity's
    // regrowth path, carrying over the previous mesh's visibility) set this after return.
    outlineMesh.visible = false;
    return outlineMesh;
  }

  function ensureGroupGpu(group) {
    if (group.gpu) {
      return;
    }

    const { material, pickingMaterial, outlineMaterial } = createMaterialForGroup(group);

    // Pre-clone one geometry per slot per style so style switches need no allocation
    const cachedGeometries = new Map();
    for (const slotId of group.slots) {
      const slotCache = new Map();
      for (const [styleId, style] of group.styles) {
        if (style.geometries.has(slotId)) {
          slotCache.set(styleId, style.geometries.get(slotId).clone());
        }
      }
      cachedGeometries.set(slotId, slotCache);
    }

    // outlineGeometries carries over anything registered via registerOutline() before this
    // group had GPU state yet (see the pendingOutlineGeometries stash in registerOutline).
    const outlineGeometries = group.pendingOutlineGeometries ?? new Map();
    group.pendingOutlineGeometries = undefined;

    // Build shape entries using the active style's cached geometry
    const shapeEntries = new Map();
    for (const slotId of group.slots) {
      const slotCache = cachedGeometries.get(slotId);
      if (!slotCache) continue;
      const geom = (group.activeStyleId && slotCache.get(group.activeStyleId))
        ?? slotCache.values().next().value;
      if (geom) {
        const outlineGeom = outlineGeometries.get(slotId) ?? null;
        shapeEntries.set(slotId, createShapeEntry(slotId, geom, outlineGeom, material, pickingMaterial, outlineMaterial));
      }
    }

    // Picking scene: mirrors the visible geometry with ID-encoded colors for hit detection
    const pickingScene = new Scene();
    const pickingOriginGroup = new Group();
    // originGroup's transform is built as an explicit .matrix (see the comment on it, and
    // symmetry-geometry.jsx's matrix-building effect) -- .position/.quaternion/.scale are
    // never touched, so pickingOriginGroup must mirror .matrix directly too, with
    // matrixAutoUpdate off for the same reason (nothing would ever recompute .matrix from
    // position/quaternion/scale if those were used instead, and they're never set).
    pickingOriginGroup.matrixAutoUpdate = false;
    pickingScene.add(pickingOriginGroup);
    // Use RenderTarget (not WebGLRenderTarget) so the WebGPU renderer registers the texture
    const pickingTarget = new RenderTarget(1, 1, {
      type: UnsignedByteType,
      format: RGBAFormat,
    });

    group.gpu = { material, pickingMaterial, outlineMaterial, shapeEntries, cachedGeometries, outlineGeometries, pickingScene, pickingOriginGroup, pickingTarget };
  }

  // Attaches an outline mesh to a shape entry that was created before its outline geometry
  // was registered (registerOutline() arriving after registerShape() for the same slot).
  function attachOutlineMeshToEntry(group, entry, slotId) {
    const outlineGeom = group.gpu.outlineGeometries.get(slotId);
    if (!outlineGeom) return;
    const outlineMesh = makeOutlineMesh(
      outlineGeom, group.gpu.outlineMaterial, entry.capacity,
      entry.orientBuffer, entry.translationBuffer, entry.highlightBuffer, entry.pickingIdBuffer
    );
    outlineMesh.count = entry.mesh.count;
    entry.outlineMesh = outlineMesh;
    if (entry.mesh.parent === originGroup) {
      originGroup.add(outlineMesh);
    }
  }

  function addGroupMeshesToScene(group) {
    if (!group.gpu) {
      return;
    }
    for (const entry of group.gpu.shapeEntries.values()) {
      if (entry.mesh.parent !== originGroup) {
        originGroup.add(entry.mesh);
      }
      if (entry.outlineMesh && entry.outlineMesh.parent !== originGroup) {
        originGroup.add(entry.outlineMesh);
      }
      if (entry.pickingMesh && entry.pickingMesh.parent !== group.gpu.pickingOriginGroup) {
        group.gpu.pickingOriginGroup.add(entry.pickingMesh);
      }
    }
  }

  function setGroupMeshesCount(group, count) {
    if (!group || !group.gpu) {
      return;
    }
    for (const entry of group.gpu.shapeEntries.values()) {
      entry.mesh.count = count;
      if (entry.pickingMesh) {
        entry.pickingMesh.count = count;
      }
      if (entry.outlineMesh) {
        entry.outlineMesh.count = count;
      }
    }
  }

  function clearGroupInstances(group) {
    if (!group) {
      return;
    }
    group.instancesByShape.clear();
  }

  function syncShapeInstances(group, slotId) {
    if (!group.gpu) {
      return;
    }
    const entry = group.gpu.shapeEntries.get(slotId);
    if (!entry) {
      throw new Error(`Shape slot '${slotId}' is not registered for active group '${group.id}'.`);
    }

    const shapeMap = group.instancesByShape.get(slotId);
    const instances = shapeMap ? Array.from(shapeMap.entries()) : [];
    ensureShapeCapacity(group, slotId, instances.length);

    const currentEntry = group.gpu.shapeEntries.get(slotId);
    currentEntry.mesh.count = instances.length;
    if (currentEntry.pickingMesh) {
      currentEntry.pickingMesh.count = instances.length;
    }
    if (currentEntry.outlineMesh) {
      currentEntry.outlineMesh.count = instances.length;
    }
    for (let i = 0; i < instances.length; i += 1) {
      const [instanceId, { position, orientationIndex, colorIndex, highlight = 0 }] = instances[i];
      currentEntry.orientBuffer[i] = orientationIndex;
      const base = i * 3;
      currentEntry.translationBuffer[base] = position.x;
      currentEntry.translationBuffer[base + 1] = position.y;
      currentEntry.translationBuffer[base + 2] = position.z;
      currentEntry.colorIndexBuffer[i] = colorIndex;
      currentEntry.highlightBuffer[i] = highlight;
      currentEntry.pickingIdBuffer[i] = instanceId;
    }

    const geom = currentEntry.mesh.geometry;
    geom.getAttribute("orientationIndex").needsUpdate = true;
    geom.getAttribute("instanceTranslation").needsUpdate = true;
    geom.getAttribute("colorIndex").needsUpdate = true;
    geom.getAttribute("highlightIntensity").needsUpdate = true;
    geom.getAttribute("pickingId").needsUpdate = true;

    // outlineMesh's geometry is a *different* BufferGeometry (see makeOutlineMesh) that
    // wraps the same underlying typed arrays via its own InstancedBufferAttribute
    // instances, so the data above is already current -- but needsUpdate must still be
    // flagged separately, once per GPU buffer.
    if (currentEntry.outlineMesh) {
      const outlineGeom = currentEntry.outlineMesh.geometry;
      outlineGeom.getAttribute("orientationIndex").needsUpdate = true;
      outlineGeom.getAttribute("instanceTranslation").needsUpdate = true;
      outlineGeom.getAttribute("highlightIntensity").needsUpdate = true;
      outlineGeom.getAttribute("pickingId").needsUpdate = true;
    }
  }

  function ensureShapeCapacity(group, key, needed) {
    const entry = group.gpu.shapeEntries.get(key);
    if (needed <= entry.capacity) {
      return;
    }

    const expandedCapacity = Math.max(needed, entry.capacity * 2);
    const nextOrient = new Float32Array(expandedCapacity);
    const nextTranslation = new Float32Array(expandedCapacity * 3);
    const nextColor = new Float32Array(expandedCapacity);
    const nextHighlight = new Float32Array(expandedCapacity);
    const nextPickingId = new Float32Array(expandedCapacity);
    nextOrient.set(entry.orientBuffer);
    nextTranslation.set(entry.translationBuffer);
    nextColor.set(entry.colorIndexBuffer);
    nextHighlight.set(entry.highlightBuffer);
    nextPickingId.set(entry.pickingIdBuffer);

    const previousMesh = entry.mesh;
    const previousGeometry = previousMesh.geometry;

    // Clone from the source geometry so the new cached geom has clean base vertex data
    const activeStyle = group.styles.get(group.activeStyleId);
    const sourceGeom = activeStyle ? activeStyle.geometries.get(entry.slotId) : null;
    const nextGeometry = sourceGeom ? sourceGeom.clone() : previousGeometry.clone();
    attachAttributes(nextGeometry, nextOrient, nextTranslation, nextColor, nextHighlight, nextPickingId);

    // Update the geometry cache: replace the active style's entry with the new geometry
    const slotCache = group.gpu.cachedGeometries.get(entry.slotId);
    if (slotCache && group.activeStyleId) {
      slotCache.set(group.activeStyleId, nextGeometry);
    }

    const nextMesh = new InstancedMesh(nextGeometry, group.gpu.material, expandedCapacity);
    initializeIdentityMatrices(nextMesh, expandedCapacity);
    nextMesh.count = previousMesh.count;
    // See the matching comment in createShapeEntry: bounding-sphere-based frustum culling
    // doesn't account for per-instance translation, so it's disabled on every mesh here too
    // (this replacement mesh doesn't inherit frustumCulled from previousMesh automatically).
    nextMesh.frustumCulled = false;

    if (previousMesh.parent === originGroup) {
      originGroup.remove(previousMesh);
      originGroup.add(nextMesh);
    }

    const nextPickingMesh = new InstancedMesh(nextGeometry, group.gpu.pickingMaterial, expandedCapacity);
    initializeIdentityMatrices(nextPickingMesh, expandedCapacity);
    nextPickingMesh.count = previousMesh.count;
    nextPickingMesh.frustumCulled = false;

    const previousPickingMesh = entry.pickingMesh;
    if (previousPickingMesh && previousPickingMesh.parent === group.gpu.pickingOriginGroup) {
      group.gpu.pickingOriginGroup.remove(previousPickingMesh);
      group.gpu.pickingOriginGroup.add(nextPickingMesh);
    }

    // Outline mesh, if this shape has one, needs its own geometry (different vertex/index
    // topology than nextGeometry) but reuses the same expanded buffers -- see makeOutlineMesh.
    let nextOutlineMesh = null;
    const previousOutlineMesh = entry.outlineMesh;
    if (previousOutlineMesh) {
      const sourceOutlineGeom = group.gpu.outlineGeometries.get(entry.slotId);
      const nextOutlineGeometry = sourceOutlineGeom ? sourceOutlineGeom.clone() : previousOutlineMesh.geometry.clone();
      nextOutlineMesh = makeOutlineMesh(
        nextOutlineGeometry, group.gpu.outlineMaterial, expandedCapacity,
        nextOrient, nextTranslation, nextHighlight, nextPickingId
      );
      nextOutlineMesh.count = previousOutlineMesh.count;
      nextOutlineMesh.visible = previousOutlineMesh.visible;
      if (previousOutlineMesh.parent === originGroup) {
        originGroup.remove(previousOutlineMesh);
        originGroup.add(nextOutlineMesh);
      }
      const previousOutlineGeometry = previousOutlineMesh.geometry;
      requestAnimationFrame(() => previousOutlineGeometry.dispose());
    }

    // The old geometry is no longer in the cache; defer disposal to avoid destroying
    // buffers that the current frame's command buffer may still reference
    requestAnimationFrame(() => previousGeometry.dispose());

    group.gpu.shapeEntries.set(key, {
      ...entry,
      capacity: expandedCapacity,
      mesh: nextMesh,
      pickingMesh: nextPickingMesh,
      outlineMesh: nextOutlineMesh,
      orientBuffer: nextOrient,
      translationBuffer: nextTranslation,
      colorIndexBuffer: nextColor,
      highlightBuffer: nextHighlight,
      pickingIdBuffer: nextPickingId,
    });
  }

  function attachAttributes(geometry, orientBuffer, translationBuffer, colorIndexBuffer, highlightBuffer, pickingIdBuffer) {
    geometry.setAttribute("orientationIndex", new InstancedBufferAttribute(orientBuffer, 1));
    geometry.setAttribute("instanceTranslation", new InstancedBufferAttribute(translationBuffer, 3));
    geometry.setAttribute("colorIndex", new InstancedBufferAttribute(colorIndexBuffer, 1));
    geometry.setAttribute("highlightIntensity", new InstancedBufferAttribute(highlightBuffer, 1));
    geometry.setAttribute("pickingId", new InstancedBufferAttribute(pickingIdBuffer, 1));
  }

  function initializeIdentityMatrices(mesh, capacity) {
    const identity = new Matrix4();
    for (let i = 0; i < capacity; i += 1) {
      mesh.setMatrixAt(i, identity);
    }
    mesh.instanceMatrix.needsUpdate = true;
  }

  function disposeGroupGpu(group) {
    if (!group.gpu) {
      return;
    }

    for (const entry of group.gpu.shapeEntries.values()) {
      if (entry.mesh.parent === originGroup) {
        originGroup.remove(entry.mesh);
      }
      if (entry.pickingMesh && entry.pickingMesh.parent === group.gpu.pickingOriginGroup) {
        group.gpu.pickingOriginGroup.remove(entry.pickingMesh);
      }
      if (entry.outlineMesh) {
        if (entry.outlineMesh.parent === originGroup) {
          originGroup.remove(entry.outlineMesh);
        }
        // Unlike the fill/picking geometry, the outline mesh's geometry is not shared with
        // anything else and isn't kept in cachedGeometries, so it must be disposed here.
        entry.outlineMesh.geometry.dispose();
      }
      // Fill/picking geometry is owned by cachedGeometries; disposed below
    }
    for (const slotCache of group.gpu.cachedGeometries.values()) {
      for (const geom of slotCache.values()) {
        geom.dispose();
      }
    }
    group.gpu.material.dispose();
    group.gpu.pickingMaterial.dispose();
    group.gpu.outlineMaterial.dispose();
    group.gpu.pickingTarget.dispose();
    group.gpu = null;
  }

  function syncAllInstancesForGroup(group) {
    for (const slotId of group.instancesByShape.keys()) {
      syncShapeInstances(group, slotId);
    }
  }

  function getSymmetryGroup(groupId) {
    const group = symmetryGroups.get(groupId);
    if (!group) {
      throw new Error(`Unknown symmetry group '${groupId}'.`);
    }
    return group;
  }

  function getStyle(group, styleId) {
    const style = group.styles.get(styleId);
    if (!style) {
      throw new Error(`Unknown style '${styleId}' in group '${group.id}'.`);
    }
    return style;
  }

  function getGroupIds() {
    return [...symmetryGroups.keys()];
  }

  function getActiveGroupId() {
    return activeGroupId;
  }

  function getActiveGroup() {
    if (activeGroupId === null) {
      throw new Error("No active symmetry group. Call switchSymmetryGroup() first.");
    }
    return getSymmetryGroup(activeGroupId);
  }

  function getOrCreateShapeInstanceMap(group, key) {
    let shapeMap = group.instancesByShape.get(key);
    if (!shapeMap) {
      shapeMap = new Map();
      group.instancesByShape.set(key, shapeMap);
    }
    return shapeMap;
  }

  function normalizeOrientationIndex(group, orientationIndex) {
    if (orientationIndex === undefined || orientationIndex === null) {
      return Math.floor(Math.random() * group.orientations.length);
    }
    if (!Number.isInteger(orientationIndex)) {
      throw new Error("orientationIndex must be an integer.");
    }
    if (orientationIndex < 0 || orientationIndex >= group.orientations.length) {
      throw new Error(`orientationIndex out of range for group '${group.id}'.`);
    }
    return orientationIndex;
  }

  function normalizeColorIndex(colorIndex) {
    if (colorIndex === undefined || colorIndex === null) {
      return Math.floor(Math.random() * colorMatrices.length);
    }
    if (!Number.isInteger(colorIndex)) {
      throw new Error("colorIndex must be an integer.");
    }
    if (colorIndex < 0 || colorIndex >= colorMatrices.length) {
      throw new Error("colorIndex out of range.");
    }
    return colorIndex;
  }

  function normalizeColorInput(colorInput) {
    if (colorInput instanceof Vector3) {
      return colorInput.clone();
    }
    if (colorInput instanceof Color) {
      return new Vector3(colorInput.r, colorInput.g, colorInput.b);
    }
    if (Array.isArray(colorInput) && colorInput.length === 3) {
      return new Vector3(colorInput[0], colorInput[1], colorInput[2]);
    }
    if (typeof colorInput === "object" && colorInput !== null && "r" in colorInput && "g" in colorInput && "b" in colorInput) {
      return new Vector3(colorInput.r, colorInput.g, colorInput.b);
    }
    if (typeof colorInput === "number" || typeof colorInput === "string") {
      const color = new Color(colorInput);
      return new Vector3(color.r, color.g, color.b);
    }
    throw new Error("registerColor expects a Color, Vector3, [r,g,b], {r,g,b}, number, or CSS color string.");
  }

  function ensureGroupHasActiveStyle(group) {
    if (group.activeStyleId !== null) {
      return;
    }
    const firstStyle = group.styles.keys().next();
    group.activeStyleId = firstStyle.done ? null : firstStyle.value;
  }

  function ensureStyleIsActiveForInstances(group, styleId) {
    if (group.activeStyleId === null) {
      group.activeStyleId = styleId;
      return;
    }
    if (group.activeStyleId === styleId) {
      return;
    }
    group.activeStyleId = styleId;
    if (group.gpu) {
      swapGeometriesForStyle(group, styleId);
    }
  }

  function setInstanceHighlight(shapeId, instanceId, intensity = 1) {
    const group = getActiveGroup();
    const shapeMap = group.instancesByShape.get(shapeId);
    if (!shapeMap || !shapeMap.has(instanceId)) {
      return false;
    }
    shapeMap.get(instanceId).highlight = intensity;
    syncShapeInstances(group, shapeId);
    return true;
  }

  function clearHighlights(shapeId) {
    const group = getActiveGroup();
    if (shapeId !== undefined) {
      const shapeMap = group.instancesByShape.get(shapeId);
      if (shapeMap) {
        for (const inst of shapeMap.values()) {
          inst.highlight = 0;
        }
        syncShapeInstances(group, shapeId);
      }
    } else {
      for (const [sid, shapeMap] of group.instancesByShape) {
        for (const inst of shapeMap.values()) {
          inst.highlight = 0;
        }
        syncShapeInstances(group, sid);
      }
    }
  }

  function listShapeKeys(group, styleId) {
    const keys = [];
    for (const slotId of group.slots) {
      keys.push({ styleId, shapeId: slotId });
    }
    return keys;
  }

  // Picking renders into a tiny NxN target instead of a full-canvas-sized one -- rasterizing/
  // shading a multi-megapixel target just to read one pixel back was the dominant per-click
  // cost (noticeable even for a handful of instances, since fill cost scales with screen area,
  // not instance count). PICK_SIZE=3 gives a 1px cursor tolerance ring on each side, useful
  // for thin strut geometry, while keeping the target (and the readback) tiny and fixed-cost.
  const PICK_SIZE = 3;
  const pickProjectionMatrix = new Matrix4();

  // GPU picking: render an offscreen pass with per-instance ID colors, read back the pixel under
  // the cursor, and return the hit { shapeId, instanceId } or null for background.
  // Instance IDs are encoded as 24-bit values across RGB (up to ~16.7M unique instances).
  // Uses its own dedicated WebGPURenderer (see getPickingRenderer above), not the main
  // on-screen renderer, so the caller's renderer/domElement are only used for sizing/coordinate
  // mapping to match the visible canvas.
  async function pickAt(clientX, clientY, mainRenderer, camera) {
    if (activeGroupId === null) return null;
    const group = symmetryGroups.get(activeGroupId);
    if (!group || !group.gpu) return null;

    const { pickingScene, pickingOriginGroup: pickGroup, pickingTarget } = group.gpu;

    // Sync picking group transform with the visible origin group. originGroup.matrix alone
    // (copied here previously) is only its LOCAL transform, always identity -- the real
    // embedding/globalScale/XR transform lives on `parent` (WebXRSupport's own originGroup,
    // see createSymmetryRenderer's parameter comment), which originGroup is parented under.
    // pickingScene, by contrast, is a standalone root Scene with no parent at all, so
    // pickGroup must be given originGroup's full WORLD matrix (which does include that
    // parent-chain scale) directly as its own local matrix. Missing this made every picking
    // render use an unscaled (globalScale ~= 0.0088x too large) geometry -- e.g. a ball with
    // local radius ~1 was rendered as if it had radius ~1 world units instead of ~0.0088,
    // so the camera (positioned in real scaled-world coordinates, near plane ~0.0009) ended
    // up INSIDE the oversized sphere: every fragment was clipped or back-facing, producing a
    // draw call that genuinely ran (confirmed via renderer.info.render.calls) but wrote
    // nothing visible to the target -- the root cause of the "picking renders nothing" bug.
    originGroup.updateMatrixWorld(true);
    pickGroup.matrix.copy(originGroup.matrixWorld);
    pickGroup.updateMatrixWorld(true);

    const pickingRenderer = await getPickingRenderer();

    const canvas = mainRenderer.domElement;
    const w = canvas.width;
    const h = canvas.height;
    if (pickingRenderer.domElement.width !== PICK_SIZE || pickingRenderer.domElement.height !== PICK_SIZE) {
      pickingRenderer.setSize(PICK_SIZE, PICK_SIZE, false);
    }
    if (pickingTarget.width !== PICK_SIZE || pickingTarget.height !== PICK_SIZE) {
      pickingTarget.setSize(PICK_SIZE, PICK_SIZE);
    }

    // Convert CSS client coordinates to full-canvas pixel coordinates (top-left origin)
    const rect = canvas.getBoundingClientRect();
    const scaleX = w / rect.width;
    const scaleY = h / rect.height;
    const pixelX = Math.floor((clientX - rect.left) * scaleX);
    const pixelY = Math.floor((clientY - rect.top) * scaleY);

    // Reproject so the PICK_SIZE x PICK_SIZE neighborhood around the cursor, in the full
    // canvas's NDC space, fills the ENTIRE tiny render target -- i.e. offset+scale NDC space
    // so only that small pixel neighborhood ever gets rasterized/shaded, regardless of the
    // real canvas resolution. Standard GPU-picking technique (see e.g. three.js's own
    // GPUPickHelper examples). NDC X is left-to-right like pixelX; NDC Y is bottom-to-top,
    // the opposite of pixelY's top-to-bottom, hence the flip on the Y term.
    const ndcCenterX = ((pixelX + 0.5) / w) * 2 - 1;
    const ndcCenterY = 1 - ((pixelY + 0.5) / h) * 2;
    const scaleNdcX = w / PICK_SIZE;
    const scaleNdcY = h / PICK_SIZE;
    pickProjectionMatrix.set(
      scaleNdcX, 0, 0, -ndcCenterX * scaleNdcX,
      0, scaleNdcY, 0, -ndcCenterY * scaleNdcY,
      0, 0, 1, 0,
      0, 0, 0, 1
    );
    pickProjectionMatrix.multiply(camera.projectionMatrix);

    const savedProjectionMatrix = camera.projectionMatrix;
    camera.projectionMatrix = pickProjectionMatrix;

    // Render picking pass with transparent clear so background pixels have alpha=0
    pickingRenderer.setRenderTarget(pickingTarget);
    pickingRenderer.setClearColor(0x000000, 0);
    pickingRenderer.clear(true, true, true);
    pickingRenderer.render(pickingScene, camera);
    pickingRenderer.setRenderTarget(null);

    camera.projectionMatrix = savedProjectionMatrix;

    // Read back the whole tiny target and use its center pixel (the exact cursor position);
    // PICK_SIZE is intentionally small enough that reading it all back costs about the same
    // as reading one pixel, while giving a little tolerance for future nearest-hit logic.
    const buffer = await pickingRenderer.readRenderTargetPixelsAsync(pickingTarget, 0, 0, PICK_SIZE, PICK_SIZE);
    const centerIdx = (Math.floor(PICK_SIZE / 2) * PICK_SIZE + Math.floor(PICK_SIZE / 2)) * 4;
    const pixelBuffer = buffer.subarray(centerIdx, centerIdx + 4);

    if (pixelBuffer[3] === 0) return null; // transparent = background

    // Decode 24-bit instance ID from RGB channels
    const instanceId = pixelBuffer[0] + pixelBuffer[1] * 256 + pixelBuffer[2] * 65536;
    if (instanceId === 0) return null;

    // Find which shape owns this instanceId
    for (const [shapeId, shapeMap] of group.instancesByShape) {
      if (shapeMap.has(instanceId)) {
        return { shapeId, instanceId };
      }
    }
    return null;
  }

  // Expose a setOrigin method to set the group's position
  function setOrigin(vec3) {
    originGroup.position.copy(vec3);
  }

  return {
    registerSymmetryGroup,
    registerStyle,
    registerShape,
    registerOutline,
    registerColor,
    switchSymmetryGroup,
    switchStyle,
    setOutlinesVisible,
    setDiscardInactiveShaders,
    addInstance,
    removeInstance,
    removeAllInstances,
    replaceShapeInstances,
    clearActiveInstances,
    setInstanceHighlight,
    clearHighlights,
    getGroupIds,
    getActiveGroupId,
    getActiveGroup,
    listShapeKeys,
    pickAt,
    setOrigin,
    originGroup,
  };
}
