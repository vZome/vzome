# Precision loss importing large integers on the web

Online vZome loses precision when a design imports mesh JSON containing integers larger
than 2^53. The exact rational arithmetic that is the whole point of vZome is therefore
*not* exact on the web for such designs, and the resulting geometry differs from the
desktop's.

Found while comparing POV-Ray exports between Java and JavaScript. It is the only
remaining geometry difference in the 2025 in-project subset, and — fittingly — the design
that exposes it is `core/src/regression/files/2025/12-Dec/09-Discord-bug/too-big-integers.vZome`.

## What was observed

The two platforms agree on the rendered mesh (identical triangles), but give the panel
different content-derived shape keys, because the *algebraic* coordinates differ:

```
java: 9017778255981932/224010153634365
js  : 45088891279909664/1120050768171825
```

Both fractions are individually reduced (gcd 1), and they are not equal:

```
difference = 4/1120050768171825   (about 3.6e-15)
```

Note the denominators are related by exactly 5 (`1120050768171825 = 5 × 224010153634365`),
but the numerators are not: `5 × 9017778255981932 = 45088891279909660`, whereas JavaScript
holds `...664`. Off by 4.

## The cause

`45088891279909660` and `45088891279909664` are **the same double** — both exceed
`Number.MAX_SAFE_INTEGER` (2^53 - 1 = 9007199254740991), where consecutive representable
doubles are 8 apart.

The design is built by `ImportColoredMeshJson`, so the coefficients arrive as JSON text.
In Node (and the browser):

```js
JSON.parse( "45088891279909660" )        // -> 45088891279909660, but as a double
BigInt( JSON.parse( "45088891279909660" ) )   // -> 45088891279909664n
BigInt( "45088891279909660" )                 // -> 45088891279909660n   (correct)
```

`JSON.parse` produces a double, and the exact value is gone before `BigInt` ever sees it.
`BigInt` then faithfully converts the *wrong* double, which is how a value that was never
in the file appears in the output.

`online/src/worker/legacy/jsweet2js.js` does `BigInt( num )` in `createBigRational` and
friends. That is correct when `num` is a string, and lossy when it is a number that has
already been through `JSON.parse`.

Note the field arithmetic itself is not at fault: `times` in `online/src/worker/fields/golden.js`
and `simplify` in `common.js` are pure BigInt throughout. The loss happens strictly at the
parse boundary.

## Why it matters beyond regression testing

vZome's central invariant is exact arithmetic — "never introduce floating-point math where
algebraic numbers are expected". This breaks that invariant on the web for any design whose
mesh JSON carries integers past 2^53. The geometry is wrong, not merely differently
formatted, and nothing reports an error.

It also breaks shape identity: `deriveGeometricKey` hashes the coordinate strings, so two
platforms compute different keys for what should be the same shape. That is what surfaced
it here.

## Fixing it

The parse path must keep large integers as text all the way to `BigInt`. Options, roughly
in order of preference:

1. **Parse mesh JSON with a reviver that preserves integer literals as strings** before they
   become numbers, then `BigInt( string )`. This is the only approach that is correct for
   arbitrary magnitudes.
2. If the JSON structure is known and shallow, extract the numeric fields textually (a
   regex or a streaming parse) rather than relying on `JSON.parse`.
3. At minimum, **detect and report** the condition: if a parsed number exceeds
   `Number.MAX_SAFE_INTEGER`, raise rather than silently producing wrong geometry.

Option 3 is worth doing regardless of whether 1 or 2 lands, since silent wrongness is the
worst property of the current behavior.

## Reproducing

```bash
# Java
./gradlew core:batchPov \
  -PpovInput=$PWD/core/src/regression/files/2025 -PpovOutput=/tmp/pov-java -PpovWorkers=1

# JavaScript, compared against it, ignoring camera and lighting
cd online && node scripts/run-batch-pov.mjs \
  $PWD/../core/src/regression/files/2025 /tmp/pov-js \
  --compare /tmp/pov-java --geometry-only
```

`too-big-integers.pov` is reported as the sole difference, as a mismatched `#declare Su…`
shape key. The mesh triangles under that declaration are identical on both sides — only the
key, derived from the algebraic coordinates, differs.
