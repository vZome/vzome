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
| `website/` | Markdown / Eleventy | Active | Source for <https://vzome.com>. |
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

## Online vZome Architecture

### Build System

- **esbuild** for bundling ES modules (config in `online/scripts/esbuild-config.mjs`)
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
| `worker/legacy/` | JSweet-transpiled Java code (the bridge from Java `core` to JavaScript) |
| `worker/fields/` | Field-specific worker modules |
| `both-contexts.js` | Code shared between main thread and worker |

### Web Worker Architecture

Heavy computation (parsing `.vZome` files, executing edit commands, convex hulls, 4D projections) runs in a **Web Worker** to keep the UI responsive.  The worker is **stateful** — it maintains the mesh state for every open design.  Only rendering state ("render events") flows back to the main thread.

Two loading paths:
1. **`.shapes.json` preview** — fast path for viewing; contains pre-computed final geometry
2. **`.vZome` file** — full path; replays the entire edit history (loads the large legacy code module)

### SolidJS + solid-three

The UI framework is **SolidJS** (not React).  3D rendering uses **Three.js** via **solid-three**.  State flows between the main SolidJS context and the web worker, mapped to the legacy Controller architecture in the transpiled Java code.

### JSweet Legacy Bridge

The core Java code is transpiled to JavaScript using **JSweet** (a now-dormant project).  Scott maintains custom forks of 4 JSweet repos:
- `vorth/jsweet`, `vorth/jsweet-maven-plugin`, `vorth/j4ts`, `vorth/jsweet-gradle-plugin`

**The JSweet Artifactory server is permanently offline**, so the transpilation pipeline is no longer viable for new contributors.  The transpiled output is checked into the repo under `online/src/worker/legacy/`.  Any changes to Java code that need to reach the web version require someone with a working local JSweet build.

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

## Coding Conventions

- **Java**: Standard Java conventions.  The `core` package avoids any UI or platform dependencies — it must remain portable (runs on Android for VR, transpiles to JS via JSweet).
- **JavaScript/JSX**: SolidJS JSX (`.jsx` files), not React.  Components use SolidJS reactivity (signals, effects, stores) — do NOT apply React patterns like `useState`/`useEffect`.
- **Web Components**: The `<vzome-viewer>` custom element uses Shadow DOM.  It's designed to be embedded on any website with a simple `<script>` tag.

## Important Caveats

1. **JSweet is fragile**: The transpilation from Java to JS is a one-way bridge maintained by custom forks of a dormant project.  Changes to core Java code must be careful not to break JSweet compatibility (e.g., avoid Java features JSweet doesn't support).
2. **No automated testing for online**: Regression testing of the web version is entirely manual.  This is a known critical gap.
3. **Algebraic fields use exact arithmetic**: Never introduce floating-point math where algebraic numbers are expected.  The entire point is exact computation.
4. **The edit history is append-only**: A `.vZome` file must always open successfully regardless of code changes.  Backward compatibility of the XML format is paramount.
5. **Orbit/direction data can be stored per-document**: This was introduced to insulate files from changes to orbit definitions in code.  See `developer-docs/Symmetry-System-Enhancements.md` for the design rationale.

## Key People

- **Scott Vorthmann** (`vorth`) — creator and primary maintainer since ~2001
- **David Hall** — significant contributor to core Java code

## Where to Find More

| Topic | Location |
|-------|----------|
| Online architecture | `online/developer-docs/architecture.md` |
| Symmetry system design | `developer-docs/Symmetry-System-Enhancements.md` |
| Rendering state model | `developer-docs/rendering-state.md` |
| Regression testing ideas | `developer-docs/regression-testing.md` |
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

