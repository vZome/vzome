# vZome — Copilot Instructions

## What is vZome?

vZome is an application for creating virtual [Zometool](https://zometool.com/) models and exploring other constrained geometric realms.  It was started around 2001 by Scott Vorthmann, with significant contributions from David Hall.  The project has 20+ years of history, with this Git repository dating to December 2014 (reorganized from an even older repo).

There are two variants:

- **Desktop vZome** — a Java Swing application (`desktop/` + `core/`).  Development is largely frozen; the online version is the future.
- **Online vZome** — a web application at <https://vzome.com/app>, plus related web components (`online/`).  This is the active focus of development.

The live website at <https://vzome.com> is partly built from the `website/` folder using Eleventy.  Some of the content is still manually maintained outside of this repo, but the long-term goal is to migrate all documentation and user-facing content into the repo for better maintainability.

## Repository Structure

| Folder | Language | Status | Purpose |
|--------|----------|--------|---------|
| `core/` | Java | Active | Core domain model: algebraic fields, symmetry systems, edit commands, construction geometry, exporters.  No UI code. |
| `desktop/` | Java (Swing) | Frozen | Desktop vZome controllers and views.  Depends on `core`. |
| `online/` | JavaScript (SolidJS) | **Active** | Web application + `<vzome-viewer>` web component.  Uses `esbuild` for bundling. |
| `website/` | Markdown / Eleventy | Active | Source for much of <https://vzome.com>. |
| `pwa/` | React | Superseded | Earlier progressive web app experiment, replaced by `online`. |
| `server/` | Java | Abandoned | WebSocket server + exporter servlet (was on Heroku).  Client-server approach abandoned for pure client-side. |
| `oculus/` | C# / Unity | Dormant | VR app for Meta Quest.  Frameworks out of date; future VR will use WebXR in `online`. |
| `buildSrc/` | Gradle | Active | Shared Gradle build logic. |
| `cicd/` | Bash | Active | Build and deploy scripts. |
| `developer-docs/` | Markdown | Partially stale | Architecture decision records and design notes. |

## Key Domain Concepts

### Algebraic Number Fields

vZome performs **exact arithmetic** — no floating point.  Every coordinate is a linear combination of irrational basis values with integer (or rational) coefficients.  The algebraic fields define what numbers are representable:

| Field class | Basis / Irrationals | Typical use |
|-------------|---------------------|-------------|
| `PentagonField` | φ (golden ratio) | Standard Zometool / icosahedral geometry |
| `RootTwoField` | √2 | Octahedral geometry |
| `RootThreeField` | √3 | Tetrahedral / hexagonal geometry |
| `HeptagonField` | Heptagonal cosines | 7-fold symmetry |
| `SnubCubeField` | Snub cube tribonacci | Snub cube constructions |
| `SnubDodecField` / `SnubDodecahedronField` | Snub dodecahedron constant | Snub dodecahedron constructions |
| `EdPeggField` | Ed Pegg's constant | Specific geometric explorations |
| `PlasticNumberField` | Plastic number | Padovan-sequence geometry |
| `PlasticPhiField` | Plastic number × φ | Combined field |
| `SuperGoldenField` | Supergolden ratio | Specific geometric explorations |
| `PolygonField` | N-gon cosines (parameterized) | Arbitrary regular polygon symmetry |

All field classes live in `com.vzome.core.algebra`.  `AlgebraicField` is the interface; `AbstractAlgebraicField` and `ParameterizedField` are the main base classes.

### Symmetry Systems

Symmetry is central to vZome.  A **symmetry group** is defined by its generators (rotation/reflection matrices and corresponding permutations).  Key implementations are in `com.vzome.core.math.symmetry`:

- `IcosahedralSymmetry` — the primary Zometool symmetry (120 elements)
- `OctahedralSymmetry` — cube/octahedron symmetry (48 elements)
- `DodecagonalSymmetry` — 12-fold planar symmetry
- `AntiprismSymmetry` — parameterized antiprism symmetry

A **SymmetryPerspective** (`com.vzome.core.editor.SymmetryPerspective`) bundles a symmetry group with the tools and shapes available in that symmetry context.  A **FieldApplication** (`com.vzome.core.kinds.*FieldApplication`) bundles an algebraic field with its available symmetry perspectives.

### Orbits and Directions

An **orbit** (also called a **Direction**) is the equivalence class of a vector under the symmetry group.  Orbits have:
- A **prototype vector** — canonical representative
- A **color** — for rendering (blue, red, yellow, green, etc. in icosahedral symmetry map to specific orbit families)
- A **unit length** — for the length panel UI and shape rendering

Struts (edges) in a vZome model belong to orbits.  The orbit determines the strut's color, available lengths, and shape.

### The Edit Model

A vZome document is a **linear history of edit commands** applied to a `RealizedModel` (the geometric model of balls, struts, and panels).  Each edit is an `UndoableEdit` subclass in `com.vzome.core.edits`.  The XML `.vZome` file format stores this history; opening a file replays every edit.

Key concepts:
- **Selection** — the set of currently-selected model elements, which most edits operate on
- **Construction** — the geometric objects (points, segments, polygons) that edits create, stored in `com.vzome.core.construction`
- **Manifestation** — the visible realization of a construction in the model (`com.vzome.core.model`)
- **Tools** — reusable transformations defined by the user (symmetry, rotation, scaling, etc.) in `com.vzome.core.tools`

### Zomic Language

Zomic is a scripting language for programmatic Zome model construction, with a virtual machine model (location, orientation, scale, build mode).  Grammar and reference are in `core/docs/ZomicReference.md`.  Parser is in `com.vzome.core.zomic`.

## Desktop vZome Architecture

There are three main layers:
1. **Model** (`core/`) — pure Java domain model (fields, symmetry, edit commands, construction geometry, exporters).  No UI code.
2. **Controllers** (`desktop/src/main/java/com/vzome/desktop/controller/`) — handle user input and coordinate between the model and the view.
3. **Views** (`desktop/src/main/java/org/vorthmann/`) — UI components and rendering logic.

The separation of `core` from `desktop` allows the core logic to be reused in other contexts (online, VR) without any Swing or AWT dependencies. 

The generic `Controller` interface in `desktop` is the main entry point for handling user actions.  The various controllers maintain the current `RealizedModel`, the edit history, the selection state, etc.  The view layer should not have any direct dependencies on specific controller classes.
This isolation is what allowed the online version to take over a number of controller classes directly; they now live, as TypeScript, in `online/src/worker/legacy/from-java/com/vzome/desktop/controller/` and must be kept in sync with their Java counterparts.  It also would allow
the Java Swing UI to be replaced with a different framework (even a native one, or a command-line) if desired, without affecting the core logic.

## Online vZome Architecture

### Build System

- **esbuild** for bundling ES modules (config in `online/scripts/esbuild-config.mjs`).  It consumes `.ts` directly — there is no separate transpile step.
- **TypeScript** for typechecking only (`online/tsconfig.json`, `yarn typecheck`)
- **Yarn** as the package manager
- Dev server started via VS Code build task or `cicd/online.bash dev`
- Test page at `online/serve/app/test/index.html`

### Source Layout (`online/src/`)

| Path | Purpose |
|------|---------|
| `app/` | Web applications (main editor, 59icosahedra, fivecell, bhall, browser, classic, buildplane, localfiles) |
| `viewer/` | SolidJS components for 3D viewing (Three.js via solid-three): scene canvas, camera controls, geometry rendering |
| `wc/` | Web components, primarily `<vzome-viewer>` (`vzome-viewer.js`) — the embeddable viewer for any website |
| `worker/` | Web Worker code — loads and interprets vZome designs off the main thread |
| `worker/legacy/` | Hand-written JS that drives the transpiled model, plus the j4ts runtime |
| `worker/legacy/from-java/` | **433 TypeScript modules transpiled from Java `core` — now edited directly** |
| `worker/fields/` | Field-specific worker modules |
| `both-contexts.js` | Code shared between main thread and worker |

### Web Worker Architecture

Heavy computation (parsing `.vZome` files, executing edit commands, convex hulls, 4D projections) runs in a **Web Worker** to keep the UI responsive.  The worker is **stateful** — it maintains the mesh state for an open design.  Mostly rendering state ("render events") flows back to the main thread.
At the moment, each worker manages a single open design, but the architecture could be extended to support multiple designs in memory if needed.

When the `vzome-viewer` web component is used, it gets a dedicated worker instance.  This allows multiple viewers on the same page, each with its own design and state.

There are two loading paths:
1. **`.shapes.json` preview** — fast path for viewing; contains pre-computed final geometry
2. **`.vZome` file** — full path for editing; replays the entire edit history (loads the large legacy code module)
The preview mode will fall back to the full path if the JSON is missing or incompatible with the current code version.

### SolidJS + solid-three

The UI framework is **SolidJS** (not React).  3D rendering uses **Three.js** via **solid-three**.  State flows between the main SolidJS context and the web worker, mapped to the legacy Controller architecture in the transpiled Java code.

### Legacy Bridge: TypeScript is now the source of truth

The `core` Java code was originally transpiled to JavaScript using **JSweet** (a now-dormant project).  Scott maintains custom forks of 4 JSweet repos:
- `vorth/jsweet`, `vorth/jsweet-maven-plugin`, `vorth/j4ts`, `vorth/jsweet-gradle-plugin`

**The JSweet Artifactory server is permanently offline**, so that pipeline can no longer be run by new contributors.  Rather than stay frozen, the generated TypeScript has been adopted as real source:

- `online/src/worker/legacy/from-java/` holds **433 per-class ES modules** (see its [README](online/src/worker/legacy/from-java/README.md)) — `com/vzome/...`, plus the `java/*` and `org/w3c/dom/*` shims vZome transpiled for itself.  This is now **the source of truth for the online version** — edit it directly.
- `yarn typecheck` (`online/tsconfig.json`) typechecks the whole tree and must stay green.  It is typecheck-only; **esbuild** consumes the `.ts` files directly and does the transpiling.
- `online/src/worker/legacy/candies/j4ts-2.1.0-SNAPSHOT/` holds the j4ts runtime (`bundle.js`) and its types (`j4ts.d.ts`, `bundle.d.ts`).  `j4ts.d.ts` carries a local edit making `Iterable.forEach` / `Iterator.forEachRemaining` / `remove` optional, because JSweet never emits them — re-apply it if that file is ever refreshed.
- `online/src/worker/legacy/from-java/shims.d.ts` declares a few types j4ts lacks (`java.math`, `java.io.FileWriter`, `java.lang.Thread`) that exist only on unreachable paths.

`online/scripts/ts-to-esm.mjs` is the one-time codemod that performed the namespace-to-ESM conversion.  It is kept as a record of how the conversion was done; it is **not** part of the build and is not idempotent (it keys on `namespace X {`, which the converted files no longer contain).

#### Runtime type identity is a cross-language ABI

JSweet emits `X["__class"] = "com.vzome.core.edits.Foo"` and `X["__interfaces"] = [...]`, and code tests them with `.indexOf("com.vzome.core.model.Panel")`.  This is the `instanceof` emulation, and hand-written JavaScript participates in it (`json.js`, `dom.js`, `core.js`, `jsweet2js.js`, `controllers/buildplane.js` all register or test these strings).

**Never "clean up" one of these fully-qualified strings**, even when the surrounding code no longer mentions that package.  They are matched by value at runtime, `tsc` cannot see the breakage, and the failure surfaces far from the edit.

#### Name-keyed lookups must stay in the registries

Edit names, command names, and export formats come out of `.vZome` XML or client config, so a bundler cannot infer which classes are reachable.  They are listed explicitly:

- `online/src/worker/legacy/registry.js` — edit, command, and editor classes
- `online/src/worker/legacy/exporters.js` — the 2D and 3D exporter tables

**Adding an edit, command, or exporter means adding it to the corresponding table**, or it will be tree-shaken out and fail only at runtime, only for designs that use it.

## File Formats

- **`.vZome`** — XML format storing the complete edit history, symmetry system metadata, and orbit definitions.  A `.vZome` file is the source of truth.
- **`.shapes.json`** — JSON preview format containing pre-computed geometry (instances, camera, scenes, lighting) for fast web viewing.
- **`.vef`** — Vertex-Edge-Face format, a simple text format for importing/exporting vertex-edge-face mesh geometry.

## Export Formats

vZome can export to: DAE (Collada), POV-Ray, VRML, STL, OFF, PLY, DXF, OpenSCAD, STEP, PDF, SVG, PostScript, glTF, and more.  Exporters live in `com.vzome.core.exporters`.

## Build & Run

### Online (primary development)

```bash
# Start dev server (JS only, no Java transpilation)
cicd/online.bash dev
# Or use VS Code: Terminal → Run Build Task → "Build online vZome for Development"

# Production build
cicd/online.bash prod
```

### Desktop

```bash
# Run desktop vZome
./gradlew desktop:run
# Or use the VS Code Gradle task
```

### Core tests

```bash
./gradlew core:test
```

### Typechecking the online legacy code

```bash
cd online && yarn typecheck     # tsc --noEmit over worker/legacy/from-java
```

Run this after any change under `online/src/worker/legacy/from-java/`.  It is the only
automated check that tree has.

## Coding Conventions

### Java and TypeScript must be kept in sync — BY HAND

This is the single most important rule for `core` changes.

JSweet can no longer be run, so **nothing regenerates the TypeScript from the Java**.  The two trees are now independent sources that happen to describe the same model:

| Source | Drives |
|--------|--------|
| `core/src/main/java/com/vzome/...` | Desktop vZome |
| `online/src/worker/legacy/from-java/com/vzome/...` | Online vZome |

**A change to one must be mirrored in the other, in the same commit.**  Both directions matter:

- Changing Java for the desktop?  Port the same change to the corresponding `.ts` file, or online silently keeps the old behavior.
- Changing TypeScript for online?  Back-port it to the Java, or desktop silently keeps the old behavior — and the next person to diff the trees cannot tell which side is correct.

Where a class exists on both sides, the paths correspond exactly: `com/vzome/core/edits/Foo.java` ↔ `from-java/com/vzome/core/edits/Foo.ts`, and the TypeScript reads like the Java, so most edits translate almost literally.

**Not every Java class has a counterpart.**  The original transpile had an explicit include/exclude list (recoverable from git history: `git show 51264def5:online/build.gradle`).  Of 581 Java classes in `core` + `desktop`, **406 have a TypeScript equivalent and 175 do not**.  Most of those 175 are desktop UI (`org.vorthmann.zome.ui`, `com.vzome.desktop.awt`) and are correctly desktop-only, but some sit in `core` packages — a number of `core.editor`, `core.exporters`, `core.algebra`, and `core.zomic` classes were deliberately excluded or reimplemented in hand-written JavaScript.  Conversely 27 TypeScript modules have no Java source: the `java/*` and `org/w3c/dom/*` shims vZome transpiles for itself, and the `com.vzome.jsweet.*` bridge classes.

So before mirroring: **check whether the counterpart file exists.**  If it does, mirror the change.  If it does not, decide deliberately whether the online version needs that class at all, rather than assuming the absence is an oversight.

If you genuinely cannot mirror a change (a Java-only dependency, or an online-only feature), **say so explicitly in the commit message** and note why, so the divergence is deliberate and discoverable rather than a silent bug.

Anything touching the `.vZome` file format is especially sensitive: a design saved by one version must still open in the other.

After editing TypeScript, run `yarn typecheck` in `online/`.  After editing Java, run `./gradlew core:test`.

- **Java**: Standard Java conventions.  The `core` package avoids any UI or platform dependencies — it must remain portable (runs on Android for VR, and its transpiled TypeScript counterpart drives the web version).
- **JavaScript/JSX**: SolidJS JSX (`.jsx` files), not React.  Components use SolidJS reactivity (signals, effects, stores) — do NOT apply React patterns like `useState`/`useEffect`.
  - **Prefer context over props.**  Components should pull what they need from context providers (`WorkerProvider`, `ViewerProvider`, `SceneProvider`, `CameraProvider`, `SymmetryProvider`, etc.) rather than threading data down through prop chains.  Keep generic providers generic — don't add flags to a shared provider to serve one caller; compose the specialized behavior around it instead.
  - **Add behaviors via composition — the "behavioral component."**  A component may render `null` and exist purely to install a behavior into the surrounding context, mounted as a sibling in the JSX tree (e.g. `TrackballLoader`, `SceneChangeListener`).  Add behaviors by placing such components in the tree, not by growing the API of an existing component.  This keeps each provider single-purpose and makes concerns individually removable.
- **Web Components**: The `<vzome-viewer>` custom element uses Shadow DOM.  It's designed to be embedded on any website with a simple `<script>` tag.

## Important Caveats

1. **Java and TypeScript no longer regenerate — they drift**: JSweet cannot be run any more, so `core/src/main/java` and `online/src/worker/legacy/from-java` are two independent sources for the same model.  Every change to one must be mirrored by hand in the other (see *Coding Conventions*).  A missed mirror is invisible to both `./gradlew core:test` and `yarn typecheck`: each tree still compiles, and the two versions of vZome simply behave differently.
   - Corollary: the old advice to "avoid Java features JSweet doesn't support" now only matters if you intend to re-run a transpile.  The live constraint is that a human keeps the two trees equivalent.
2. **No automated testing for online**: Regression testing of the web version is entirely manual.  This is a known critical gap.
3. **Algebraic fields use exact arithmetic**: Never introduce floating-point math where algebraic numbers are expected.  The entire point is exact computation.
4. **The edit history is append-only**: A `.vZome` file must always open successfully regardless of code changes.  Backward compatibility of the XML format is paramount.
5. **Orbit/direction data can be stored per-document**: This was introduced to insulate files from changes to orbit definitions in code.  See `developer-docs/Symmetry-System-Enhancements.md` for the design rationale.
6. **Use yarn, never npm, in `online/`**: This project uses **yarn** as its package manager (`online/yarn.lock`).  Running bare `npm install`/`npm uninstall` — even with `--no-save` — rewrites `yarn.lock` with a large unwanted diff and leaves a stray `package-lock.json`.  Use `yarn add`/`yarn remove` instead, or isolate throwaway tooling in a scratchpad with its own `package.json` so it never touches the project lockfile.

## Key People

- **Scott Vorthmann** (`vorth`) — creator and primary maintainer since ~2001
- **David Hall** — significant contributor to core Java code

## Where to Find More

Two folders of design notes and architecture decision records are the primary place to look:

- **`developer-docs/`** — repo-wide architecture and design notes (symmetry system, rendering state, regression testing, TypeScript-migration and LLM-knowledge plans, the full action-name catalog).  Partially stale, but the best record of *why* things are the way they are.
- **`online/developer-docs/`** — online-vZome-specific docs (architecture, the symmetry-renderer plan/status, worker↔client scene protocol, testing strategy).

Frequently useful individual files:

| Topic | Location |
|-------|----------|
| Online architecture | `online/developer-docs/architecture.md` |
| Worker↔client scene protocol | `online/developer-docs/worker-client-scene-protocol.md` |
| Symmetry renderer plan / status | `online/developer-docs/symmetry-renderer-plan.md`, `online/developer-docs/symmetry-renderer-status.md` |
| Symmetry system design | `developer-docs/Symmetry-System-Enhancements.md` |
| Rendering state model | `developer-docs/rendering-state.md` |
| Regression testing ideas | `developer-docs/regression-testing.md`, `online/developer-docs/testing-strategy.md` |
| Zomic language reference | `core/docs/ZomicReference.md` |
| All action/command names | `developer-docs/vZome-action-names.txt` |
| Online TODO items | `online/TODO.md` |

## Things Only Scott Knows (Gaps to Fill)

> These topics exist primarily in the maintainer's memory and should be documented over time:

- Pre-2014 history: origins, early Java3D version, design philosophy evolution
- Why each algebraic field was added and what geometric explorations motivated it  
- The full story of the Zometool company relationship
- Community/user base: who uses vZome and for what
- Architectural decisions that were considered and rejected
- The complete mental model of how symmetry perspectives, field applications, and orbit systems interact
- Plans and vision for the future of online vZome


## What's Missing (highest ROI next steps)
 1. **Brain-dump transcripts** — Open a Claude or ChatGPT conversation and have it interview you about the topics in the "Things Only Scott Knows" section of this file. Save each transcript as docs/knowledge/transcript-TOPIC.md. Even one 30-minute session on the pre-2014 history would be irreplaceable.

 2. **Discord/email export** — Use DiscordChatExporter for any vZome-related Discord channels. For email, search for "vZome" in your mail client and export as .mbox. Drop these into a docs/archives/ folder (or a private repo if they contain personal info).

 3. **Git history mining** — Your 4,238 commits are a goldmine. A script like git log --all --format='%ai %s' --grep='field\|symmetry\|orbit\|jsweet\|export\|worker' can extract the narrative of major feature introductions. This can be fed to an LLM to generate a timeline.

 4. **NotebookLM as the "persistent expert"** — Upload your copilot-instructions.md, the developer-docs, the brain-dump transcripts, and the Discord exports to a Google NotebookLM notebook. It will create a grounded, citation-backed conversational expert that anyone can query — LLM-agnostic in the sense that the source material remains portable Markdown in your repo.

 5. **Action catalog** — Your 211 action names in vZome-action-names.txt are a treasure map but opaque without context. Adding even one-line descriptions would be enormously valuable for any future contributor or LLM.

