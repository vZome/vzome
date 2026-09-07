# `online/src/worker/legacy/from-java/` — the transpiled core, now hand-maintained

These 433 modules were generated from Java by [JSweet](https://www.jsweet.org/), then
converted from global namespaces to ES modules and adopted as **source**.  JSweet can no
longer be run (its Artifactory server is permanently offline), so nothing regenerates
them: edit these files directly.

`yarn typecheck` in `online/` typechecks the whole tree and must stay green.  esbuild
consumes the `.ts` files directly, so there is no separate transpile step.

## Two different origins

**`com/vzome/**` and most of `org/w3c/dom/**` mirror Java that still exists.**
`core/src/main/java/com/vzome/...` and `desktop/src/main/java/com/vzome/...` remain the
desktop sources, and the paths correspond one-to-one:

    core/src/main/java/com/vzome/core/edits/Foo.java
    online/src/worker/legacy/from-java/com/vzome/core/edits/Foo.ts

Since nothing regenerates these, **a change to either side must be mirrored by hand in the
other** — see the *Coding Conventions* section of the repository's `AGENTS.md`.  Of 581
Java classes in `core` + `desktop`, 406 have a counterpart here; the rest are desktop-only
(mostly Swing UI) or were deliberately excluded from the original transpile.

**27 modules have no Java counterpart at all.**  They came from `online/src/worker/java/`,
a small tree of Java written solely as JSweet transpile input, deleted once these files
became the source of truth.  It held two kinds of thing:

| Here | Was |
|------|-----|
| `com/vzome/jsweet/{JsAlgebraicField,JsAlgebraicNumber,JsEditorModel}.ts` | The bridge classes letting transpiled Java call into hand-written JavaScript |
| `java/**`, `org/w3c/dom/**` | Minimal stubs for JDK classes the j4ts runtime does not provide |

The originals used `jsweet.util.Lang.any()` / `array()`, so they could only ever be
compiled by JSweet.  They are recoverable from git history if the intent of a stub is ever
unclear:

    git log --diff-filter=D --  'online/src/worker/java/**'

The `com.vzome.jsweet` package name is kept deliberately.  It is embedded in the
`__class` / `__interfaces` identity strings (see below), so renaming it would be a
breaking change for a cosmetic gain.

## Things to know before editing

**String literals holding fully-qualified class names are a runtime ABI.**  JSweet emits
`X["__class"] = "com.vzome.core.edits.Foo"` and `X["__interfaces"] = [...]`, and code tests
them with `.indexOf("com.vzome.core.model.Panel")`.  That is the `instanceof` emulation,
and hand-written JavaScript participates in it (`json.js`, `dom.js`, `core.js`,
`jsweet2js.js`, `controllers/buildplane.js`).  **Never "clean up" one of these strings**,
even when the surrounding code no longer mentions that package: `tsc` cannot see the
breakage, and it surfaces far from the edit.

**Name-keyed lookups must stay registered.**  Edit names, command names, and export
formats come out of `.vZome` XML, so a bundler cannot infer which classes are reachable.
Adding an edit, command, or exporter means adding it to `../registry.js` or
`../exporters.js`, or it is tree-shaken away and fails only for designs that use it.

**A few classes need their Java static initializer run eagerly.**  Seven modules end with
an explicit `X.__static_initialize();` call, because the lazy `_$LI$()` accessors are not
enough — `XmlSymmetryFormat`, for instance, registers every `.vZome` format namespace
there, and `getFormat()` reads that map without touching an accessor.  Keep those calls.

**The `java/**` stubs are minimal, not faithful.**  `Properties.getProperty()` returns
`""`, `File` is nearly empty, and `NumberFormat.format()` is `'' + x` with the
`setMaximumFractionDigits()` setter a no-op.  Transpiled code that depends on real JDK
behaviour will diverge silently from the desktop build; see
`online/developer-docs/povray-export-defects.md` for a case where that caused a real bug.

## History

`online/scripts/ts-to-esm.mjs` is the one-time codemod that performed the
namespace-to-ESM conversion.  It is kept as a record of how the conversion was done, is
not part of the build, and is not idempotent — it keys on `namespace X {`, which these
files no longer contain.
