# Why the build output is named the way it is

Notes from working out why a newly split-off chunk was called
`exporters-MYLQC64J.js` while sixteen others are called `chunk-XXXXXXXX.js`.
Recorded because the naming is worth understanding before changing it, and
because the obvious lever (`chunkNames`) does not do what it looks like it does.

Everything below was measured against a real `yarn build`, not inferred.

## The rule

A build output gets a **real name** if esbuild has an *entry point* for it, and a
**generic `chunk-` name** otherwise.  In the current build that splits exactly:

| | count | naming |
|---|---:|---|
| outputs with an `entryPoint` in `meta.json` | 17 | named after the entry module |
| outputs without | 16 | `chunk-` + content hash |

There are two ways to become an entry point:

1. **Declared** in `entryPoints` in `scripts/esbuild-config.mjs` — `vzome.js`,
   `vzome-legacy.js`, `vzome-zomic.js`, `vzome-viewer.js`, and so on.
2. **Discovered**, by esbuild finding a literal `import()`.  This needs no config:
   `splitting: true` is already set, so a dynamic import is enough on its own.
   `exporters-MYLQC64J.js` is the only current example — its `meta.json` entry
   reads `entryPoint: src/worker/legacy/exporters.js`.

Declared entry points are written to `entryNames` (default `[name]`, so no hash).
Discovered ones fall under `chunkNames` (default `[name]-[hash]`), which is why
`exporters` is the only *named* output that still carries a hash.

The sixteen anonymous chunks are **shared code**: fragments esbuild extracted
because more than one entry point needs them.  They have no single defining
source, so there is no honest name to give them.

## The hash is load-bearing, not decoration

Tempting idea: set `chunkNames: '[name]'` and get readable names everywhere.
Tested — it does not work.  All sixteen shared chunks collapse into a single
`chunk.js`, because they all have `[name]` = `chunk` and nothing distinguishes
them.

Setting `chunkNames: '[name]-[hash]'` reproduces exactly the current output.
**The default is already the best a config flag can produce.**  Naming a chunk
requires giving it an identity, which means making it an entry point — one at a
time, deliberately.

## Cache-busting is *not* the reason

Worth stating plainly, because it is the reflexive explanation and it is wrong
here:

- The production deploy writes no cache headers at all.  The only `.htaccess`
  line, in `cicd/online.bash`, sets `Access-Control-Allow-Origin`.
- The two largest shipped artifacts, `vzome-legacy.js` and `vzome-zomic.js`,
  have always had plain unhashed names.

So content hashing is not part of a deliberate cache strategy in this project.
It is esbuild's default, and the reason to keep it is the collision problem
above, not cache invalidation.  If cache behaviour ever *does* become a concern,
it needs designing on its own terms rather than being assumed to already work.

## For debugging, prefer the metafile over renaming

The real question during debugging is usually "what is actually *in* this 2MB
chunk?", and `meta.json` answers it directly.  Every production build already
writes it (`scripts/prod-build.mjs` sets `metafile: true`).

Run this from `online/` (where `meta.json` is written), via `node -e` or a
scratch script:

```js
const m = JSON.parse( require('fs').readFileSync('meta.json') );
for ( const [ out, info ] of Object.entries( m.outputs ) ) {
  if ( ! out.endsWith('.js') ) continue;
  const ins = Object.entries( info.inputs );
  const top = ins.sort( (a,b) => b[1].bytesInOutput - a[1].bytesInOutput )[0];
  console.log( info.bytes, out, '<-', top && top[0] );
}
```

Which currently reports:

| output | bytes | dominant input |
|---|---:|---|
| `chunk-GFMAFH5Z.js` | 2,001,314 | `three/build/three.webgpu.js` (87 inputs) |
| `vzome-legacy.js` | 1,495,159 | the `from-java` tree (466 inputs) |
| `chunk-47C2ISC4.js` | 1,343,637 | `three/build/three.core.js` |
| `chunk-46WI4AYN.js` | 1,314,097 | the j4ts candy bundle |
| `chunk-SOJER2MF.js` | 447,372 | `app/classic/components/logo.jsx` (293 inputs) |
| `vrml-viewer.js` | 398,754 | `wc/vrml/VRMLLoader.js` |
| `vzome-zomic.js` | 323,094 | `antlr4` |
| `exporters-MYLQC64J.js` | 125,190 | the `core/exporters` tree (31 inputs) |

That table is more useful than a filename would be: it says the 2MB chunk is
three.js's WebGPU build, which no amount of renaming would have told you.

## When to declare an entry point anyway

A stable filename is worth declaring when something **loads the module by URL**,
because then the name is part of an external contract.  That is the actual reason
`vzome-legacy` and `vzome-zomic` are declared: test pages under `serve/app/test/`
import them directly, e.g.

```js
import { initialize } from "/modules/vzome-legacy.js";
```

`exporters` has no such consumer — it is only ever reached through the `import()`
in `vzome-worker-static.js` — so a hashed name is fine and it was left discovered.

## Open questions for a later session

- Should more chunks be declared entry points purely for legibility?  Doable, but
  each one is a decision about what that chunk *is*, and the sixteen shared ones
  mostly do not have a clean answer.
- Would a small `yarn analyze` script (the metafile table above, sorted) be worth
  having as a standing tool rather than an ad-hoc snippet?
- `chunk-GFMAFH5Z.js` is 2MB of `three.webgpu`, and `chunk-47C2ISC4.js` another
  1.3MB of `three.core`.  Worth checking whether every entry point that pulls
  those actually needs them.
- Three test scripts under `serve/app/test/cases/crf-polytope/` still import
  `vzomePkg`, an export removed in 425a35d0c.  Unrelated to naming, but it is the
  other thing `serve/app/test/` turned up.
