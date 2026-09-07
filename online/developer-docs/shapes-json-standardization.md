# Standardizing `.shapes.json` across Java and JavaScript

`.shapes.json` is written by two independent implementations — the Java `ShapesJsonExporter`
and the web worker's `exportPreview` — and they do not agree. This document records exactly
how they differ, which differences are real versus artifacts of comparing at the wrong point
in the code, and what it would take to make one file comparable against the other.

The motivating question was whether `.shapes.json` could replace POV-Ray export as the basis
for regression testing. Its appeal is coverage: `.shapes.json` carries `scenes` and
`snapshots`, so it exercises more of the edit history than POV-Ray, which exports only the
final scene.

## Compare at the load point, not the write point

The single most important thing to know: **the two writers emit different shapes, but both
converge after loading.** A naive diff of raw output is misleading.

- Java `ShapesJsonExporter` writes the *legacy* format.
- The worker's `exportPreview` writes `format: 'online'`, and the client augments it first —
  `exportAs` in `online/src/viewer/context/viewer.jsx` supplies `camera`, `lighting` and
  `scenes` before the worker serializes.

At load, `online/src/worker/vzome-worker-static.js` reconciles them:

```js
const { lighting, scenes, ...rendered } =
    ( preview.format === 'online' ) ? preview : normalizePreview( preview );
```

`normalizePreview` (`online/src/worker/legacy/preview.js`) converts legacy → online: it
transposes the embedding, reshapes `shapes` from a list into a dict keyed by id, converts
instance positions from `{x,y,z}` objects to `[x,y,z]` arrays, synthesizes instance ids
(`main_0`, `main_1`, …), and maps a hardcoded table of old orbit/length names.

Run a Java-written file through `normalizePreview` and the two formats line up closely:

| field | after normalization |
|---|---|
| key set | identical (modulo `format`, and where `lighting` sits) |
| `shapes` | both dict, both `{faces, id, length, orbit, vertices}` — same schema |
| `instances` | same count, both `{id, position[], orientation, color, selected, shapeId, type}` |
| `embedding` | **equal** |
| `orientations` | element 0 equal (full array not yet equal — see ordering below) |

An earlier comparison of *raw* Java output against the online format suggested the formats
were fundamentally different (list vs dict, object vs array positions). That was an artifact
of reading at the write point. Those particular differences are entirely handled by
`normalizePreview`.

## What actually still differs

### 1. Shape ids — the unfinished half of `getShapeKey()`

Java emits content-derived keys; JavaScript emits random per-worker ids:

```
java: "icosahedral:blue:2 +4φ"
js  : "s20567399771598782"
```

Commit `e384c55b2` ("Content-derived shape ids for cross-worker/design sharing") introduced
`Polyhedron.shapeKey` precisely to fix this, and its own message states the motivation:

> on the web each design runs in its own worker with its own object graph, so that
> reference-sharing is gone and the random guid degrades to per-worker noise

That commit converted the **Java** consumers — `JsonMapper`, `unity.Renderer`,
`POVRayExporter` — but left the **JavaScript** ones untouched. Still using the per-object
guid today:

- `online/src/worker/legacy/scenes.js` — five sites (`getShapeId()` at lines 44, 62, 76, 83;
  `getGuid()` for shape ids at line 14)
- `online/src/worker/legacy/interpreter.js` — line 27

So the client's geometry cache and instancing remain keyed on random ids, which is the exact
problem `e384c55b2` set out to solve. Switching these to `getShapeKey()` finishes that work
and makes JS shape ids identical to Java's. **This is a real defect independent of regression
testing**, and it is the natural first step.

Note the j4ts `UUID.randomUUID()` shim is literally `Math.random()` (see `core-java.js`), so
these ids are not merely per-worker — they differ on every run of the same design.

### 2. Instance ids

Java's exporter writes no instance id at all; `normalizePreview` synthesizes `main_0`,
`main_1`, … positionally. The online format carries a random guid per instance.

These are not comparable as values. Either exclude them from comparison, or give instances a
content-derived id alongside the shape-key work.

### 3. Ordering

Both platforms emit `shapes` and `instances` in `HashSet` iteration order, so the same design
yields the same *set* in a different *sequence* — including run to run within one platform.
This is the same defect fixed for POV-Ray export (sort by a content key before emitting), and
the same fix applies here, on both sides.

### 4. `snapshots` representation

For one test design: Java emits 8 snapshots — one real (318 instances) plus **seven empty
index placeholders** — where JavaScript emits 0 and keeps the instances inline. The
information is equivalent; the shape is not. A normalizer rule can reconcile it.

Related: `parseArticle` in `online/src/worker/legacy/parser.js` deliberately drops pages
titled `"How to save notes"` (the old default explainer), so some designs legitimately yield
fewer scenes on the web than in Java. Worth remembering when a scene count looks wrong.

### 5. `polygons` is a string in Java

`ShapesJsonExporter` writes `generator.writeStringField( "polygons", "true" )` — the string
`"true"`, not the boolean. The online format writes a real boolean. Small, but a genuine type
bug in the Java exporter.

### 6. Float precision

Java rounds; JavaScript does not:

```
java: -6.854102
js  : -6.85410213470459
```

This needs either a shared rounding rule or a comparison tolerance. It is the only difference
here that is a judgment call rather than a defect.

## Assessment

Standardizing is tractable, and most of the work is worth doing on its own merits:

- **Item 1** finishes `e384c55b2` and fixes a live bug in web geometry caching/instancing.
- **Item 3** is the fix already applied to `POVRayExporter`, applied in one more place.
- **Items 2, 4, 5** are small, contained normalizer or exporter rules.
- **Item 6** requires deciding on a tolerance.

The main cost relative to POV-Ray comparison is that this is *structural* comparison rather
than a text diff, so the comparison code itself is larger and needs its own tests.

Recommended order: item 1 first (a real bug, and it removes the largest single difference),
then item 3, then the normalizer rules.

## Verifying

To reproduce the comparison, export the same design both ways and run the Java output through
the load path before diffing:

```js
import { normalizePreview } from 'online/src/worker/legacy/preview.js';
const loaded = ( raw.format === 'online' ) ? raw : normalizePreview( raw );
```

Comparing raw Java output against the online format will show differences that do not exist
after loading, which is how this investigation initially went wrong.
