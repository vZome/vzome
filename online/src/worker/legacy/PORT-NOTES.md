# Port notes: four fixes in `core-java.js` that the TypeScript migration will drop

`core-java.js` carries four fixes that are **not** in the `adopt-typescript` branch. That
branch deletes this file (all ~53,000 lines) and replaces it with `from-java/**/*.ts`, so
these changes will **not conflict — they will simply vanish**, silently, and the bugs return.

Verified against `adopt-typescript`: all four replacement files exist and all four still
carry the unfixed code.

| # | Fix | Lives here in | Port to |
|---|---|---|---|
| 1 | `NumberFormat` honors fraction digits | `core-java.js` (class `NumberFormat`) | `from-java/java/text/NumberFormat.ts` |
| 2 | Deterministic POV emission order | `core-java.js` (`POVRayExporter`) | `from-java/com/vzome/core/exporters/POVRayExporter.ts` |
| 3 | FNV-1a shape hash in BigInt | `core-java.js` (`deriveGeometricKey`) | `from-java/com/vzome/core/math/Polyhedron.ts` |
| 4 | Transform/color dedup by content | `core-java.js` (`POVRayExporter`) | `from-java/com/vzome/core/exporters/POVRayExporter.ts` |

Each is described below with the behavior to preserve. The Java originals in `core/` are the
reference — port to match them, not merely to match this file.

---

## 1. `NumberFormat` must honor `setMaximumFractionDigits`

**Was:** the setters were empty stubs and `format()` was string concatenation:

```js
setMaximumFractionDigits(i) { }
format(x) { return '' + x; }
```

**Consequence:** every exporter asking for fixed digits silently got the full float. POV-Ray
asks for 8, so it wrote `4.236067771911621` where Java writes `4.23606777`, making *every*
exported file differ from its Java counterpart.

**Not POV-Ray-specific:** 12 call sites in `core-java.js` construct a `NumberFormat`
(`DaeExporter`, `OffExporter`, `VRMLExporter`, `RealVector`, others). All were affected.

**Behavior to preserve** (matching `java.text.NumberFormat`):

- the fraction-digit setters store their argument, keeping `min <= max`
- default `maximumFractionDigits` is 3
- `format(x)` rounds with `toFixed(maximumFractionDigits)`, then trims trailing zeros but
  never below `minimumFractionDigits`
- non-finite values fall back to plain string conversion
- grouping separators are never emitted — every caller writes machine-readable geometry

`adopt-typescript`'s `from-java/java/text/NumberFormat.ts` still has the empty stub.

---

## 2. POV-Ray export must emit in a deterministic order

**Was:** `POVRayExporter` iterated `mModel` directly. `RenderedModel` holds its
manifestations in a `HashSet` keyed on a random per-object UUID, so iteration order varied
run to run. That reordered the `object` lines *and* changed the lazily generated
`trans0`/`trans1` names, so exporting one design twice produced two different files —
**0 of 60 sampled designs exported reproducibly.**

**Behavior to preserve:** collect the manifestations into an array and sort by content —
never by guid — before emitting. The key, in order:

1. `getShapeKey()`
2. orientation (`toString()`)
3. location (`getLocationAV()`, via `compareTo`)
4. color (`toString()`)

with null-safe handling at each step. See `POVRayExporter.compareForExport` in `core/`.

The same ordering problem affects the triangles within one shape (`Polyhedron` holds faces
in a `HashSet`), so `exportShape` also collects the rendered `triangle {...}` strings and
sorts them before writing. Sorting the text changes no geometry.

After this, all 60 sampled designs exported reproducibly, and 148 of 148 at wider scale.

---

## 3. The FNV-1a shape hash must use BigInt

**Was:** the transpiled `deriveGeometricKey` did FNV-1a with plain JavaScript numbers:

```js
const FNV_OFFSET = -3750763034362895579;
let hash = FNV_OFFSET;
hash ^= s.charCodeAt(i);     // coerces to 32 bits
hash *= FNV_PRIME;           // exceeds 2^53, loses all precision
```

**Consequence:** the value collapsed, `toHexString` returned `""`, and **every
content-hashed shape got the key `"u"`.** Distinct panel shapes therefore shared one key.

**This is not only an export concern.** Those keys are the web geometry cache and
instancing keys, so unrelated shapes were sharing cache entries.

**Behavior to preserve:** compute the hash in `BigInt` with an explicit 64-bit mask, so it
reproduces Java's wraparound exactly, and print the unsigned hex:

```js
const FNV_MASK = 0xffffffffffffffffn;
const FNV_PRIME = 0x100000001b3n;
let hash = 0xcbf29ce484222325n;
// per char:  hash = ((hash ^ BigInt(code)) & FNV_MASK) * FNV_PRIME & FNV_MASK
return "u" + hash.toString(16);
```

Verify by exporting a design with panels on both platforms: the `#declare Su…` names must
match exactly.

---

## 4. Transforms and colors must dedup by content, not by reference

**Was:** `POVRayExporter` kept `transforms` and `colors` in a `java.util.HashMap` keyed on
the `AlgebraicMatrix` / `Color` object. In Java those have value-based `equals`/`hashCode`;
in the transpiled runtime the map compares references, so equal matrices each got their own
`transN` declaration — every identity orientation, for instance.

**Consequence:** bloated output that also differed from Java's.

**Behavior to preserve:** key both maps on the object's string form (`"" + transform`,
`"" + color`) rather than the object.

---

## Verifying a port

Export the same designs through both paths and compare the geometry:

```bash
./gradlew core:batchPov \
  -PpovInput=$PWD/core/src/regression/files -PpovOutput=/tmp/pov-java -PpovWorkers=1

cd online && node scripts/run-batch-pov.mjs \
  $PWD/../core/src/regression/files /tmp/pov-js \
  --compare /tmp/pov-java --geometry-only
```

`--geometry-only` is required: camera and lighting cannot match headlessly, because
`worldDirection` is computed from the live client camera. See `online/scripts/batch-pov.mjs`.

Expected once all four are ported: the `object`, `#declare S`, `#declare trans`,
`#declare color` and `triangle` lines match exactly for most designs. Known remaining
differences, tracked separately, are the large-integer precision defect
(`developer-docs/big-integer-precision-defect.md`) and a set of designs whose shape
resources are not shipped to the web.

Also run the JavaScript export **twice** and diff the two runs: they must be identical.
That is the check that fixes 2 and 3 are correctly in place.
