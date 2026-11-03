# Design Note: The Mouse Tool Framework

Status (2026-11-03): steps 1 to 6 of the suggested order are implemented.

- Step 1: gesture recognition in `InteractionToolProvider`, hit sources, `picks`, and early
  picking-renderer warm-up.
- Step 2: `CameraTrackball` (`src/viewer/cameratrackball.js`), one per canvas, reached by
  tools through `useCameraControl()`; `CameraTool` replaces `SnapCameraTool` in the editor and
  its camera panel; WebXR suspends `{ suspend(), resume() }` camera controls instead of using
  the `TrackballControls` object.  `TrackballControls` remains for tools that set
  `allowTrackball` (the viewer, 59icosahedra, buildplane).  Not carried over to the editor's
  camera mode: two-finger touch pinch/pan, and `TrackballControls`' hold-A/S/D keys.
- Step 3: three modes in the editor (camera, select, build), with a third toolbar button,
  C / S / B shortcuts, and the static hint bar (`hints` on `SceneCanvas`).  Build mode starts
  active.  The tools kept their old names: `SelectionTool` is the select mode and
  `StrutDragTool` the build mode's drag-out metaphor (the sketches below call them
  `SelectTool` and `BuildTool`); `UnifiedTool` is gone.  The editor already had a
  hold-Alt key for temporary camera mode, which now returns to the last editing mode.
- Step 4: marquee selection in select mode (`useMarquee`, `app/classic/tools/marquee.jsx`),
  via the provider's `hitsInRect` and `setOverlay`.  An object is inside when every vertex of
  its shape projects inside the box (stricter than "a ball's center").  The worker gets one
  `SelectManifestations` picking-controller action and records one `SelectManifestation` per
  object; no explicit `BeginBlock`/`EndBlock` is needed, because `performAndRecord` already calls
  `mergeSelectionChanges()`, which brackets consecutive selection edits.  As with runs of clicks,
  the newest selection edit stays just outside the block, so undoing a marquee takes two steps.
- Step 5: buildplane on the framework.  Hit sources gained a `priority`.  Buildplane's widgets
  register through a shared raycasting hit source (`src/viewer/raycasting.js`, which
  ShapedGeometry now uses too), at priority -1: asked only where the scene has nothing, so balls
  seen through the large translucent disk stay clickable.  (The scene's GPU pick reports no
  depth, so nearest-wins across sources isn't possible; among widgets, the nearest wins.)  A
  widget's hit record carries its own `click()` and `hover( entering )`.  Hover works on the
  symmetry renderer through the provider's generic hover picking.  Buildplane's drags now use
  `CameraTrackball` instead of `TrackballControls` (same touch and A/S/D-key losses as step 2).
  `TrackballControls` remains only in the viewer and 59icosahedra.
- Step 6 (redesigned and implemented 2026-11-03): the "classic preference" became two
  independent build-mode options, `vzome.buildClick` and `vzome.buildDrag`, and the editor
  stays at three tools.  `StrutDragTool` became the `useStrutDrag()` hook, and `BuildTool`
  (`app/classic/tools/build.jsx`) is composed from the option tables.  The modes are a
  `MODES` array in `editor.jsx`.  Users set the options in a generic Settings dialog, to
  which each app contributes its own categories.  See "Build-mode options" and
  recommendation step 4.

Everything else is still a recommendation.

Online vZome handles mouse input through a framework of interchangeable "mouse tools."
This note evaluates that framework against four goals and recommends how to evolve it.

1. **A desktop-style option.**  Desktop vZome uses one mouse tool for both camera rotation
   and strut creation.  Online splits these into separate modes, because the combined tool
   confuses users, makes accidental struts easy, and makes it hard to decide which gesture
   a drag is.  We still want to offer the combined behavior as an option for users who
   know desktop (the "build-mode options" below).
2. **Marquee selection** in the editor: drag a box on screen and select every object whose
   projection falls inside it.
3. **No built-in construction metaphor.**  The framework must make no assumptions about
   strut dragging.  Other ways of building and manipulating parts are planned, such as the
   `buildplane` app's.  The framework must be usable there as it is in the editor.
4. **Users can see what the mouse will do.**  Modes are only acceptable if it is always
   clear what a click and a drag will do in the current one.

**Recommendation: keep the framework, but bring gesture recognition and camera control
inside it, make hits opaque records from pluggable sources, and keep everything about
balls, struts and selection out of the framework and in each app's behaviors.  Replace
`TrackballControls` in the editor and buildplane with our own camera trackball.  For
users, offer three orthogonal modes (camera, select, build), explain them with a hint bar
over the canvas.  Two opt-in preferences add clicks and other drags to build mode;
desktop-style building is one setting of the two.**

---

## Current state

### The pieces

| File | Role |
|---|---|
| `src/viewer/context/interaction.jsx` | `InteractionToolProvider`: holds the active tool; tells clicks from drags by deferring `onDragStart` until the pointer moves `MAX_CLICK_DISTANCE`; exports `grabTool` |
| `src/viewer/trackballcontrols.jsx` | Wraps three-stdlib `TrackballControls`; enabled only when `tool.allowTrackball()` is true; calls `tool.onTrackballEnd()` |
| `src/viewer/geometry.jsx` | `Instance` (ShapedGeometry path): solid-three pointer events → tool handlers, including `onHover` |
| `src/viewer/symmetry-geometry.jsx` | SymmetryGeometry path: raw canvas listeners + async GPU `pickAt` → tool handlers; no hover |
| `src/viewer/ltcanvas.jsx` | Canvas-level `pointerup`, `wheel`, `onClickMissed` → tool handlers (partly disabled when `symmetryRenderer` is on) |
| `src/app/classic/tools/snapcamera.jsx` | `SnapCameraTool`: `grabTool` + `onTrackballEnd: snapCamera` (the editor's "camera" mode) |
| `src/app/classic/tools/unified.jsx` | `UnifiedTool`: click-select + drag-a-strut-from-a-ball + wheel adjusts strut length (the editor's "select" mode) |
| `src/app/classic/tools/selection.jsx`, `strutdrag.jsx` | `SelectionTool`, `StrutDragTool`: the two halves of `UnifiedTool`; **not imported anywhere** |
| `src/app/classic/tools/trackball.jsx`, `drag.ts` | `ObjectTrackball` + `DragHandler`: a stripped copy of `TrackballControls`' event handling, used to roll the preview strut's direction |
| `src/app/buildplane/buildplane.jsx` | `BuildPlaneTool`: `allowTrackball: true`; `onClick` on balls/struts, `bkgdClick`, `onHover`; plus its own 3D widgets (disks, hoops, zone tubes) with direct solid-three `onClick` handlers |

Mode switching is in `src/app/classic/components/editor.jsx`: a `Switch` picks
`SnapCameraTool` when `viewing()`, otherwise `UnifiedTool`.  The context menu is disabled
in camera mode (`ContextualMenuArea disabled={viewing()}`).

Note the naming: `UnifiedTool` combines *selection and strut-drag*, not rotation and
strut-drag.  The unused `SelectionTool` and `StrutDragTool` are exactly the select and
build modes proposed below, so the three-mode design revives that split.

### How desktop does it

`DocumentController.java` (around lines 370-440) states the principles:

1. Every tool receives every event; conditions that can't both be true make sure only one
   tool acts (`MouseToolFilter`).
2. `LeftMouseDragAdapter` provides click/drag hysteresis: the "press" is withheld from the
   delegate until 200 ms have passed, then sent along with the first drag.
3. Mode switches attach and detach sets of tools.
4. `Trackball` is subclassed according to what it rotates (camera, preview strut).

The main camera trackball (`modelModeMainTrackball`) is wrapped in a filter that does a
synchronous pick in its (deferred) `mousePressed` and stays inactive if the press was over
a ball.  `StrutBuilderGraphicsController` does the opposite: its deferred press starts a
preview strut only over a ball.  The wheel adjusts strut length during a strut drag and
zooms otherwise.

**The rotate-or-strut decision is made when the drag is confirmed, not at pointerdown.**
`InteractionToolProvider` already has that moment, since it defers `onDragStart`.  What
is missing is the plumbing around it.

---

## Evaluation

### What works, and is worth keeping

- Tools are components that render nothing and register a handler object through
  context.  The scene renderers report hits without knowing which tool is active.  This
  keeps mouse behavior separate from the scene, and follows the same principles as desktop.
- Click/drag disambiguation is in one place (the provider), not in each tool.
- Modes are easy to compose and switch.

### Structural weaknesses

1. **Camera rotation is outside the framework.**  `TrackballControls` listens to the raw
   DOM by itself.  The only control over it is a fixed per-tool flag, `allowTrackball`
   (`trackballcontrols.jsx`, the `enabled` effect).  A flag can't say "rotate on this drag
   but not that one."  `TrackballControls` also commits to rotating at pointerdown, while
   the GPU pick is async, so the decision can't be made in time.  Starting a rotation and
   cancelling it once the pick resolves would visibly jerk the camera.
2. **Only drags that start on an object exist as drags.**  `symmetry-geometry.jsx`
   forwards pointermove only when the pick hit something.  On pointerup after a miss it
   always calls `bkgdClick`, however far the pointer moved.  In a desktop-style build mode,
   every camera rotation would end with Deselect All, and a marquee drag has nowhere to
   start.
3. **Click/drag recognition is spread across three files.**  The provider,
   `symmetry-geometry.jsx`, and `ltcanvas.jsx` each hold part of it, and coordinate
   through gates like "skip this if `symmetryRenderer`" (see the long comments in those
   files).  This is fragile, and another gesture type would make it worse.
4. **Strut-drag motion bypasses the framework.**  `ObjectTrackball`/`DragHandler` attach
   their own `document` listeners (and `connect()` runs three times: harmless, since the
   listener functions are the same, but a sign of the problem).  This is why
   `symmetry-geometry.jsx` must never call `stopPropagation`.
5. **`allowTrackball` means two different things**: "the camera may rotate" and "skip
   GPU picking" (`pickingSuppressed` in `symmetry-geometry.jsx`).  A tool that wants both
   camera rotation on drag and picking on click, which is exactly what buildplane needs,
   can't be expressed.
6. **The two renderers disagree on the contract.**  The handlers aren't one interface:
   - SymmetryGeometry has no hover, so `onHover` works only on the ShapedGeometry path.
   - `geometry.jsx`'s `handleClick` calls `tool.onClick( e.nativeEvent, id, ... )`, but
     the provider's `onClick` takes `( id, ... )`, so on the ShapedGeometry path the event
     appears to arrive as the `id`.  (By reading; not tested.  This may relate to the known
     ShapedGeometry editor defects.)
7. **Tool-owned scene objects are outside the framework.**  Buildplane's disks, hoops and
   zone tubes use solid-three `onClick` directly, with their own click test
   (`e.delta < 5`).  They don't take part in click/drag arbitration, so the framework
   can't tell a widget click from a background click, or let a drag that starts on a
   widget rotate the camera.

### Buildplane today

By reading, not tested: buildplane mounts `DesignViewer`, which turns `symmetryRenderer`
on by default (`src/viewer/index.jsx`).  `BuildPlaneTool` sets `allowTrackball: true`, so
SymmetryGeometry suppresses picking (weakness 5): `onClick` and `bkgdClick` never fire,
and `onHover` isn't implemented there (weakness 6).  Clicking balls or the background
probably does nothing; only its own widget clicks still work.  This should be checked in
the browser.  Either way, buildplane is a good test that the framework is free of the
editor's assumptions.

---

## User experience

### Who we design for

There are three groups of users:

1. people comfortable with current online vZome;
2. people comfortable with current desktop vZome;
3. new users who know neither.

**We optimize for group 3** and accommodate the other two with settings.

### Three orthogonal modes

Fewer modes is not automatically simpler.  A mode that does several things depending on
where the drag starts, or which key is held, is hard to predict and hard to discover.  CAD
tools generally accept more modes, each with simple behavior: SketchUp has separate
Select, Orbit, Pan and Line tools; Blender keeps Select, Move and Rotate apart.  A
toolbar button is visible; a modifier key is not.  So online vZome gets three modes, each
affecting exactly one thing:

| Mode | Affects | Click | Drag | Right-drag | Wheel | Right-click |
|---|---|---|---|---|---|---|
| **Camera** | the view | nothing | rotate | pan | zoom | (none; used for pan) |
| **Select** | the selection | toggle selection / background: deselect all | marquee (adds) | — | zoom | context menu |
| **Build** | the model | depends on the build metaphor | depends on the build metaphor | — | zoom; may be claimed while building | context menu |

Each mode can be described in one sentence, which is the test for whether a new user can
hold it in their head:

- *Camera mode moves your view.*
- *Select mode changes what is selected: click objects, or drag a box around them.*
- *Build mode adds to the model, in the way the chosen build metaphor describes.*

**Only gestures that never conflict work in every mode:** the wheel zooms everywhere
(unless a build drag claims it, as strut length does), and the context menu works wherever
right-click isn't needed for something else.  Today the context menu is already disabled
in camera mode, so right-drag pan there takes nothing away.  Shift-drag also pans in
camera mode, for trackpads, where right-drag is awkward.

Choices behind this table:

- **In select mode, every drag is a marquee**, even one that starts on a ball.  Nothing
  in select mode is a drag handle, so there are no exceptions to remember, and in a dense
  model a box can start anywhere.
- **A marquee adds to the selection; it never replaces it.**  This matches clicks, which
  toggle (`SelectManifestation`) rather than replace.  "Replace" is a background click
  followed by a marquee.  No add/replace modifier is needed.  A "subtract" modifier can
  come later if people ask for it.
- **Camera mode doesn't select, and by default build mode doesn't rotate.**  By default, a
  drag in build mode that the build metaphor doesn't use does nothing.  Making it rotate
  would bring back the desktop problem this design started from: accidental struts when the
  user meant to rotate.  The hint bar (below) says what to do instead.  Users who want
  rotation or selection in build mode can opt in (see "Build-mode options").

### Build metaphors

Build mode is where construction metaphors live, as sub-modes:

| Metaphor | Click | Drag |
|---|---|---|
| **Drag out struts** (the editor today) | nothing | from a ball: build a strut (wheel adjusts length); elsewhere: nothing |
| **Click to join** (the `buildplane` app) | ball: set the center, or join to it; strut: set the hinge; build-plane widgets: their own actions | nothing |

The build button in the toolbar can carry a small menu for choosing the metaphor once there
is more than one in the editor.  The framework knows nothing about any of them: each
metaphor is one behavior (see recommendation step 4).

The standalone `buildplane` app has no mode toolbar, so its tool adds camera rotation on
drag to the click-to-join metaphor.  That is an app-level choice, made by composing
behaviors, not something the framework has to know about.

By default, a click in the drag-out metaphor does nothing (decided 2026-11-02): strict
orthogonality, so build mode never changes the selection.  The build-mode options below
can change that.

### Showing users what the mouse will do

**A hint bar** along the bottom of the canvas describes the current mode in one line,
much like Blender's status bar or SketchUp's status text:

- static: `Select:  click — select/deselect   drag — box select   right-click — menu`
- context-sensitive, using what is under the pointer: in build mode over a ball,
  `drag — build a strut`; over empty space, `drag — nothing here · camera mode (C) rotates`.

The context-sensitive version needs hover picking, which the click-to-join metaphor needs
anyway.  The static version can come first.

The hint bar is also where shortcuts become discoverable ("hold Space to rotate").  So
discoverability doesn't force a choice between more modes and more modifiers: modifiers
can be taught there.  Three modes are still the better design, because they keep each
mode simple.

Two cheap reinforcements:

- **cursors** per mode, also changing with what is under the pointer (the `cell` cursor
  over a ball in build mode);
- **two-line tooltips** on the mode buttons ("Click: select · Drag: box select"),
  including the mode's shortcut key.

**Letting users compose arbitrary behaviors themselves was considered and rejected.**
Every combination would have to resolve conflicts (two behaviors claiming the same drag),
and each would have to be documented and supported.  Users choose only among a few
build-mode options, which never conflict, because each one fills a gesture that build mode
otherwise leaves unused (see "Build-mode options").

### Easing mode switches

Switching modes is the main cost of this design.  Plan for two remedies from the start:

- **single-key shortcuts** for the three modes, shown in tooltips and the hint bar:
  **C**, **S** and **B** (decided 2026-11-02; no menu command uses a bare letter);
- **hold a key for temporary camera mode** from any mode, as with the hand tool in many
  drawing apps, with the cursor and hint bar changing while the key is held.  The editor
  already does this with **Alt**.

### Current online users

Today's select mode splits into select and build.  Nothing they could do is gone; it is
one button away.  They gain the marquee, and wheel zoom outside camera mode, which today
works only in camera mode (by reading: `TrackballControls`, which handles the wheel, is
disabled whenever `allowTrackball` is false).  They do lose click-to-select while in build
mode.  No setting is needed for this group.

### Build-mode options

The editor always shows the same three buttons: camera, select and build.  Build mode
always builds a strut when a drag starts on a ball.  Two independent preferences fill the
gestures it otherwise leaves unused:

| Preference | Values | Default | What it adds to build mode |
|---|---|---|---|
| **Build click** | `none`, `select` | `none` | `select`: a click toggles selection; a click on empty space deselects all |
| **Build drag** | `none`, `rotate`, `marquee` | `none` | a left-drag that doesn't start on a ball: `rotate` the camera (snapping at the end), or draw a `marquee` that adds to the selection |

Users set them in the **Settings dialog** (the gear at the right end of the app bar),
under **Build Tool**, and each change takes effect at once.  Each is a reactive setting
(`createSetting` in `src/app/framework/settings.js`) stored in a `localStorage` key
(`vzome.buildClick`, `vzome.buildDrag`), with every access inside try/catch.  An unknown
value, or storage that is unavailable, means `none`.

**The Settings dialog is generic.**  `SettingsDialog` (`src/app/framework/settingsdialog.jsx`)
lists categories on the left and shows the selected one's panel on the right.  Each app
contributes its own categories, `[ { id, label, Panel } ]`, through `VZomeAppBar`'s
`settings` prop, and the gear appears only when there are some.  So far only the classic
app does (`src/app/classic/settings.jsx`); buildplane and the GitHub browser can add theirs
the same way.

"Desktop mode" is not a separate feature.  It is documentation telling users to set
**`select` + `rotate`**.  Some combinations worth naming in that documentation:

| Click | Drag | Gives |
|---|---|---|
| `none` | `none` | the default: build mode only builds |
| `select` | `rotate` | desktop vZome's single mode |
| `select` | `marquee` | today's `main`-branch select mode, plus marquee |
| `select` | `none` | exactly today's `main`-branch select mode |

In the dialog, desktop style is "Select or deselect" plus "Rotate the view".

Notes:

- **A drag from a strut or panel counts as "not on a ball."**  It rotates or draws a
  marquee, as on desktop, where the trackball filter checks only for a ball (`Connector`).
  With `marquee`, the only box that can't be drawn in build mode is one starting on a ball;
  select mode remains the place for that.
- **Camera and select modes never change.**  So the clicks in `select` mode are redundant
  with select mode on purpose: a click means the same thing in both, and switching modes
  never changes it.
- **The UI reflects the options.**  The build button's tooltip and the hint panel list
  the lines each chosen value adds, so a user can see what their preferences do.
- **The cost of the options is ambiguity by target.**  With `rotate` or `marquee`, what a
  left-drag does depends on whether it starts on a ball, and a near miss changes the
  outcome: a drag just beside a ball rotates or selects instead of building, and a click
  that drifts more than 2px builds a strut instead of selecting.  That is desktop's
  behavior, and it is why both options are off by default.  Context-sensitive cursors and
  hints (step 7) matter more for users who turn them on.
- **Panning in build mode** still means camera mode (C, or hold Alt).  Right-click stays the
  context menu, because on macOS `contextmenu` fires on the press and can't share the
  button with a right-drag.

If many people turn on `select` + `rotate` and like it, that is evidence for revisiting the
default.  The original objection still stands, though: on desktop, accidental struts happen
precisely when the user is trying to rotate.

### Not covered

**Touch input.**  On touch, one-finger drag is the only easy gesture, so modes matter even
more there.  Pinch-zoom and two-finger rotate are the natural additions, and marquee on
touch needs its own thinking.

---

## Recommendation

### 1. Do all gesture recognition in `InteractionToolProvider`

Renderers report three raw events and nothing more: pointerdown, pointermove, and
pointerup.  The provider:

- checks the drag distance;
- asks the hit sources (step 2) what is under the pointer, and waits for the answer when
  the drag is confirmed: the decision point, as in desktop;
- sends either `onClick( e, hit )` / `bkgdClick( e )`, or `onDragStart( e, hit )` where
  `hit` may be `null` for a background drag;
- sends the rest of the gesture to whoever accepted the drag (see below);
- uses `setPointerCapture` instead of `document` listeners.

This removes the cross-file gates and the `stopPropagation` constraint, and `bkgdClick`
fires only for a real click.  Apart from that fix, it should cause no behavior change in
the existing modes.

**`onDragStart` returns an object for the rest of the drag.**  The behavior that accepts
the drag returns `{ onDrag, onDragEnd, onWheel? }` (or `null` to ignore the drag), and the
provider sends the remaining events of that gesture straight to it.  That makes "who owns
this drag" part of the structure: the `operating()` checks in `UnifiedTool` go away, and so
does desktop's `live`-flag pattern.  The wheel rule follows naturally: if the active drag
has an `onWheel`, it gets the wheel (strut length, for example); otherwise the tool's own
`onWheel` does (zoom).

If the pick hasn't resolved when the drag is confirmed, buffer the moves until it does.
Desktop-style time hysteresis is an alternative.

Every handler gets the native event, so behaviors can read the button and modifier keys
(right-drag and Shift-drag pan in camera mode) without the framework knowing what they
mean.  Right-button drags must reach the tool, not only left-button ones.

### 2. Keep the framework neutral about construction metaphors

The framework (`src/viewer/`) knows about pointer gestures, hits, drag objects, the
wheel, hover, the context menu, a screen-space overlay, hints, and an optional camera
trackball.  It knows nothing about balls, struts, selection, preview struts or edits.
Those live in each app's behaviors (`src/app/classic/...`, `src/app/buildplane/...`).
Concretely:

**Hits are opaque records from pluggable hit sources.**  The framework passes a hit
through without looking inside it, except for a `source` tag:

- the scene renderer (SymmetryGeometry or ShapedGeometry) answers
  `{ source: 'scene', id, position, type, selected, label }`, and both renderers return
  the same shape (fixing weakness 6);
- tool-owned widgets, such as buildplane's disks, hoops and build dots, register as a
  second source and answer with whatever the tool attaches.  Buildplane's widgets answer
  `{ source: 'widget', id, click, hover }`, carrying their own actions.  Widgets are few,
  so they are raycast on the CPU (`src/viewer/raycasting.js`).  Widget gestures then go
  through the same click/drag arbitration (fixing weakness 7).

A hit source offers `pickAt( x, y )`, and optionally `hitsInRect( rect )` for marquee
selection (step 5), `warmUp()` to prepare for picking early (step 6), and a `priority`.
The provider asks sources in priority order (default 0, higher first) and takes the first
hit.  It can't take the nearest hit instead, because the scene's GPU pick reports no
depth.  Buildplane registers its widgets at priority -1, so they answer only where the
scene has nothing.  Otherwise its large translucent disk would hide every ball seen
through it.

**Tools declare what they need, instead of `allowTrackball`.**  For example
`picks: { click: true, drag: false }`: select mode picks on click, but every drag is a
marquee, so it skips the pick cost on drag; the camera tool needs none of the three
(`NO_PICKS`).  A tool that doesn't declare `picks` gets all three: click, drag and hover.
This replaces the second meaning of `allowTrackball` (weakness 5).  Camera behavior
becomes an ordinary behavior (steps 3 and 4).  `allowTrackball`, `onTrackballEnd` and
`grabTool` remain only for tools that still rely on `TrackballControls`: the viewer and
59icosahedra.

**Hover is part of the contract.**  The provider sends `onHover( e, hit )` only to tools
that define it and declare `picks.hover`, and only when the hit under the pointer changes
(`hit` is null when the pointer leaves everything).  The provider picks at most once per
frame while the pointer moves without a button down, through the same hit sources as
clicks, so no renderer needs hover code of its own.  The click-to-join metaphor depends
on it.

**Hints are part of the contract.**  A tool supplies `hint`: `{ title, lines }`, or just a
title string.  The provider publishes the current tool's hint, and `ltcanvas.jsx` renders
it in the hint panel, on canvases that ask for one (`hints` on `SceneCanvas`; the editor
does, buildplane doesn't).  The framework displays the text without knowing what it says.
A hint that depends on the hit under the pointer is step 7.

**The overlay is generic.**  Tools render inside the solid-three tree, so they can't
easily put HTML over the canvas.  The provider offers `setOverlay( jsx | null )`, and
`ltcanvas.jsx` renders it in a positioned layer over the canvas.  The marquee rectangle
is the first user; nothing about it is marquee-specific.

**Acceptance test:** `BuildPlaneTool` written against the new contract.  The only
framework change it needed was hit-source `priority`, described above.  Abridged from
`src/app/buildplane/buildplane.jsx`:

```jsx
// The widgets' hit source, asked after the scene.  Widget components register their meshes
// with widgetRef( widgets, { click, hover } ), so each hit record carries its own actions.
const widgets = createRaycastHitSource( useThree() );
onCleanup( surface .addHitSource( { pickAt: widgets.pickAt, priority: -1 } ) );

setTool( {
  picks: { click: true, drag: false, hover: true },
  cursor: 'cell',
  suppressContextMenu: true, // the right button pans

  // Every drag moves the camera: this app has no separate camera mode.
  onDragStart: e => {
    switch ( e.button ) {
      case 0:  return e.shiftKey? camera.pan( e ) : camera.rotate( e );
      case 1:  return camera.zoom( e );
      case 2:  return camera.pan( e );
      default: return null;
    }
  },
  onWheel: camera.onWheel,

  bkgdClick: () => dispatch( doToggleDisk() ),
  onClick: ( e, hit ) => ( hit.source === 'widget' )? hit.click?.() : sceneClick( hit ),
                                               // ball: set center or join; strut: set hinge
  onHover: ( e, hit ) => {
    // widgets: hover( true / false ) on enter and leave; balls: preview a strut to them;
    // anything else (e.g. the disk) counts as nothing, clearing the preview
  },
} );
```

### 3. Replace `TrackballControls` with our own `CameraTrackball`

A plain object with `start` / `move` / `end` and no DOM listeners of its own; tools feed
it events.  Most of the math already exists in `drag.ts` and `trackball.jsx` (screen
movement → world axis → quaternion).  Factor that into one shared trackball helper used
both for rolling the preview strut and for rotating the camera (desktop's principle 4).
`CameraTrackball` and the shared helper belong in `src/viewer/`; the strut roll belongs
in `src/app/classic/`.

The camera side must reproduce what `trackballcontrols.jsx` does today:

- `cancelTweens()` at start;
- `trackballProps.sync( target, name )` on change;
- an optional end hook supplied by the tool (`snapCamera` for the editor's camera mode;
  snapping is a classic-app concept, so the framework only offers the hook).

`CameraTrackball` should read the camera fresh at the start of each drag rather than cache
camera state between gestures, as `TrackballControls` does.  Then nothing needs resyncing
after something else (a tween, a snap, a WebXR session) moves the camera.

It also needs **pan** (camera mode's right-drag and Shift-drag) and **zoom** (the wheel),
which `TrackballControls` provides today because the editor uses `rotationOnly={false}`.
A drag object can claim the wheel instead of zoom (step 1), which is how strut length is
adjusted during a strut drag, as desktop's `previewStrutLength` does.

### 4. Build each tool by combining behaviors, not by copying code

Once the decision happens at drag start, reuse is easy (contrary to the comment at the top
of `unified.jsx`).

**Behaviors become hooks, not tools.**  Each behavior is a hook that a tool calls, and each
returns handlers or a `startDrag` that the tool routes to:

- `useCameraControl()` (framework) returns `{ rotate( e, options ), pan( e ), zoom( e ), onWheel }`;
- `useSelectClicks()` (classic, `tools/selection.jsx`) returns `{ onClick, bkgdClick }`;
- `useMarquee()` (classic, `tools/marquee.jsx`) returns `{ startDrag( e ) }`;
- `useStrutDrag()` (classic, `tools/strutdrag.jsx`) returns `{ startDrag( e, hit ), Preview }`:
  the drag-out metaphor.  `startDrag` returns `null` unless the drag is a left-drag from a
  ball.  `Preview` is a component the tool renders: it puts `<ObjectTrackball>`, which rolls
  the preview strut's direction, in the scene during a drag.  (It still attaches its own
  `document` listeners; moving the roll onto the drag object's `onDrag` would leave nothing
  to render, and is a possible later cleanup.)
- buildplane's click-to-join metaphor is its own behavior in `src/app/buildplane/`.

`rotate`, `pan`, `zoom` and every `startDrag` return the drag object described in step 1.

**Three tools; build mode's options are data.**  `CameraTool` and `SelectionTool` route
their gestures by hand, as they do now.  There is only one build tool, not one per option
combination.  Each option value is a row in a table: its hint lines at module level, so the
toolbar tooltip can share them, and its behavior inside the tool, where the hooks are
available.  The tool picks one row from each table:

```jsx
// tools/build.jsx

// Each option value's label (for the Settings dialog) and hint lines.  BuildTool supplies
// the matching behavior.
export const BUILD_CLICKS = {
  none:   { label: 'Nothing', lines: [] },
  select: { label: 'Select or deselect, as in select mode',
            lines: [ 'Click: select or deselect', 'Click empty space: deselect all' ] },
};
export const BUILD_DRAGS = {
  none:    { label: 'Nothing', lines: [] },
  rotate:  { label: 'Rotate the view, as in camera mode', lines: [ 'Drag elsewhere: rotate' ] },
  marquee: { label: 'Select everything inside a box, as in select mode',
             lines: [ 'Drag elsewhere: add everything inside a box' ] },
};

// Reactive settings, shared with the Settings dialog (framework/settings.js).
export const buildClick = createSetting( 'vzome.buildClick', BUILD_CLICKS, 'none' );
export const buildDrag = createSetting( 'vzome.buildDrag', BUILD_DRAGS, 'none' );

// What build mode does with the current settings, for the hint panel and the toolbar tooltip.
export const buildSummary = () =>
  [ 'Drag from a ball: build a strut',
    ...BUILD_CLICKS[ buildClick.value() ].lines, ...BUILD_DRAGS[ buildDrag.value() ].lines ];

export const BuildTool = props =>
{
  const { setState } = useEditor();
  const { snapCamera } = useSymmetry();
  const camera = useCameraControl();
  const strut = useStrutDrag();
  const marquee = useMarquee();

  // The behavior for each option value: hooks are called unconditionally, and only the
  // chosen entries are used.
  const clicks = { none: {}, select: useSelectClicks() };
  const drags = {
    none:    () => null,
    rotate:  e => camera.rotate( e, { onEnd: snapCamera } ),
    marquee: e => marquee.startDrag( e ),
  };

  const [ _, setTool ] = useInteractionTool();
  createEffect( () => {
    // Re-runs whenever a setting changes, so the Settings dialog takes effect at once.
    const click = buildClick.value();
    const drag = drags[ buildDrag.value() ];
    const [ ballDrag, ...others ] = buildSummary();
    setTool( {
      cursor: 'cell',
      // A pick on click only when a click does something: without one, the provider never
      // calls onClick or bkgdClick.
      picks: { click: click !== 'none', drag: true, hover: false },
      hint: {
        title: 'Build mode (B)',
        lines: [ ballDrag, 'Wheel while dragging: strut length', ...others,
                 'Wheel: zoom', 'Right-click: menu', 'Hold Alt: camera mode' ],
      },
      ...clicks[ click ],
      // The strut drag always comes first: only a left-drag that it declines (not from a
      // ball) reaches the option.  Other buttons do nothing; right-click is the menu.
      onDragStart: ( e, hit ) => strut.startDrag( e, hit ) || ( ( e.button === 0 )? drag( e ) : null ),
      onWheel: camera.onWheel,   // the strut drag object claims the wheel while it lasts
      onContextMenu: ( e, hit ) => setState( 'picked', hit ),
    } );
  } );

  return <strut.Preview debug={props.debug} />;
}
```

The routing is still explicit in one line of `onDragStart`, not a generic "first one eats"
chain.  The rejected alternative was `combine( select, strut, camera )`, which offers each
drag to the behaviors in order and lets the first to accept win.  That is the chain
desktop's principle 1 rejects: order would matter without being visible, and a tool's
behavior would no longer be readable in one place.  Here the order is fixed (strut first),
and the options only fill what the strut drag leaves unused, so they can't conflict.

**The modes are data too.**  `editor.jsx` keeps one array, and the toolbar buttons, the C
/ S / B keys and the active tool all come from it.  So adding or reordering a mode is a
one-place change:

```jsx
const MODES = [
  { value: 'camera', key: 'KeyC', label: 'Camera (C)', Tool: CameraTool, Icon: ...,
    summary: () => [ 'Drag: rotate', 'Right-drag: pan', 'Wheel: zoom' ] },
  { value: 'select', key: 'KeyS', label: 'Select (S)', Tool: SelectionTool, Icon: ...,
    summary: () => [ 'Click: select', 'Click empty space: deselect all' ] },
  { value: 'build',  key: 'KeyB', label: 'Build (B)',  Tool: BuildTool,     Icon: ...,
    summary: buildSummary },
];

// In SceneEditor:
<Dynamic component={ currentMode() .Tool } />
...
<For each={MODES}>{ m =>
  <ToggleButton value={ m.value } aria-label={ m.value }
      title={ [ m.label, m.summary() .join( ' · ' ) ] .join( '\n' ) }>
    <m.Icon/>
  </ToggleButton>
}</For>
```

Hold-Alt keeps working as it does now: camera mode, then back to the last editing mode.

### 5. Marquee selection

The gestures are settled under "User experience": in select mode, every drag is a
marquee, and a marquee always adds to the selection.

**Feedback:** the marquee drag object draws a rectangle through the overlay (step 2) on
each `onDrag`, and clears it at `onDragEnd`.

**Which objects are inside.**  At `onDragEnd`, ask the scene hit source for
`hitsInRect( rect, 'contained' )`.  The renderer projects each instance's defining points
through the current camera and keeps those whose points all fall inside the rectangle:
the center for a ball, both ends for a strut, every vertex for a panel.

- This includes objects hidden behind others, which is what people usually expect from
  selecting by region.  It runs once per marquee, so a linear scan is fine.
- SymmetryGeometry's per-instance metadata has each instance's `position`; strut ends and
  panel vertices still need to be added there or computed from shape and orientation.
- Alternative: read the GPU pick buffer over the rectangle.  That selects only what is
  visible, including partly visible objects, and skips hidden ones.  It could be offered
  later as a second mode (`'visible'`), but it shouldn't be the default.

**Recording the edit.**  Today each click is one `SelectManifestation` edit: one worker
message, one undo step, one element in the file's history.  A marquee over N objects
should be one message and one undo step.  Two ways to record it:

- **Recommended:** a `BeginBlock`, then N `SelectManifestation` edits, then an `EndBlock`.
  These elements all exist already, so desktop can read the file.  To check: that both
  desktop and online undo a block as one step.  `SelectManifestation` toggles unless its
  `mode` is `replace`, so to add, send only objects not already selected (the hit record
  has `selected`).  The client sends the list of ids in one message, and a new
  picking-controller action builds the block.  The new controller action doesn't affect
  the file format.
- **Rejected:** a new `SelectManifestations` edit holding a list.  It is more compact in
  the file, but a history element desktop doesn't know would make those files unreadable
  there until desktop catches up.

A large marquee adds N elements to the history.  That seems acceptable, but is worth
watching.

### 6. Start the pick renderer early

Every tool except the camera tool picks: select mode on clicks, build mode on drags (and
on hover, for context-sensitive hints), buildplane on clicks.  Today the first pick lazily
creates a second renderer, which stalls for several hundred milliseconds.  Do that setup
when a tool that picks is mounted.

### 7. Roll out in stages

Switch the editor and buildplane to the new path first.  The editor includes its camera
panel (`src/app/classic/components/camera.jsx`, which also mounts `SnapCameraTool`); it is
rotation-only, so it needs nothing the new path lacks.  Buildplane is small, probably
broken today (see "Buildplane today"), and the best test of step 2.  Leave the other
contexts (`<vzome-viewer>`, 59icosahedra) on `TrackballControls` until the replacement
matches it on touch and pinch, right-drag pan, and middle-drag zoom.  `TrackballControls`
provides all of those today.

#### WebXR

Moving WebXR off `TrackballControls` is not a goal, because WebXR doesn't use it for
interaction: in an AR session the headset or phone pose drives the camera.  This is all
`src/xr/manager.jsx` does with the object it receives through `setTrackball`:

- at session start: saves `trackball.target` and sets `trackball.enabled = false`, so mouse
  input doesn't fight the headset;
- at session end: restores `target`, sets `enabled = true`, and calls `trackball.update()`
  once after restoring the camera, because `TrackballControls` caches its own copy of the
  camera state.

So WebXR only needs something it can switch off and reset.  It comes up because
`<TrackballControls>` is mounted in `ltcanvas.jsx`, which every canvas uses, and it
registers itself through `setTrackball`.  If the editor stops mounting it, WebXR has
nothing to disable and reset there.

**Recommendation:** change what `setTrackball` receives, from the `TrackballControls`
object to a small interface such as `{ suspend(), resume() }`:

- the `TrackballControls` wrapper adapts to it (`suspend` saves the target and disables;
  `resume` restores, enables and calls `update()`);
- `CameraTrackball` implements it too: `suspend` ignores gestures, and `resume` has little
  or nothing to do, since `CameraTrackball` reads the camera fresh on each drag (step 3).

During the staged rollout both implementations exist side by side (some canvases on
`TrackballControls`, others on `CameraTrackball`), and with the interface `manager.jsx`
works with either.  The alternative, having `CameraTrackball` imitate `target` /
`enabled` / `update()` exactly, needs no change to `manager.jsx` but ties our class to a
three-stdlib detail.

Not yet checked: whether the editor canvas ever offers the XR button in practice.  The
WebXR code is in `ltcanvas.jsx`, so it can, but the viewer is clearly the main use.

### Suggested order

1. Centralize gesture recognition in the provider, with the opaque hit record and hit
   sources (no behavior change in the editor, apart from the `bkgdClick` fix).  Start the
   pick renderer early.
2. `CameraTrackball` with rotate, pan and zoom; the camera tool built on it; the
   `{ suspend(), resume() }` interface for WebXR.
3. Three modes in the editor: split today's select mode into `SelectTool` and
   `BuildTool`, with a third toolbar button, mode shortcuts, and the static hint bar.
4. Marquee selection in `SelectTool`.
5. Buildplane on the new framework: widgets as a hit source, hover in SymmetryGeometry,
   declared picking.  This is the test of recommendation step 2.
6. Build-mode options: the behavior hooks (`useStrutDrag`, `useSelectClicks`), one
   `BuildTool` composed from the two option tables, the modes as data in `editor.jsx`, the
   two settings, and the generic Settings dialog.  With both options at `none`, nothing
   changes for users, so the refactor can be tested against step 3's behavior first.
7. Context-sensitive hints and cursors, which matter most once a build option is on.

### Open decisions

- Where the "desktop mode" instructions (`select` + `rotate`) live for users: the news
  post, the online help, or both.  The Build Tool settings panel mentions them briefly.
- Whether settings changed in another tab should apply here too (a `storage` event).
  For now each tab reads them when it loads.
