
Reworks solid-three's WebXR integration so the consumer owns the session
and core stays out of the frame loop while a session presents. Continues
#58 on top of a cleaned-up next branch.

## Summary

Previously core drove the WebXR loop itself: on the renderer's
`sessionstart` it toggled `setAnimationLoop(...)` and set `xr.enabled`.
That breaks `WebGPURenderer` — three's WebGPU `XRManager` snapshots the
renderer's animation loop at `setSession` time, before `sessionstart`
fires, so toggling on `sessionstart` is too late and clobbers the
manager's own frame driver. On a Quest this surfaced as
`gl.getContextAttributes is not a function` on the WebGPU backend, and a
hung headset with endless "Not all layers submitted" warnings under
`forceWebGL`. The same internal driver also caused a latent
`frameloop="always"` + XR double-render.

Core now depends only on the stable, cross-renderer contract — the
consumer installs `setAnimationLoop(render)` before `setSession`, and
core reads the read-only `renderer.xr.isPresenting` to yield its window
loop while presenting, resuming via a single `sessionend` listener. No
WebGL-vs-WebGPU branching. The session wiring then lives behind one
small primitive so consumers can't trip the sharp edges.

## What changed

### `createXR()` — consumer-owned XR entry

A Solid primitive called in a component body (it owns one reactive
effect), built only on `context.gl`, `context.render`, and the
renderer's standard `xr` event target — no core internals, no
renderer-family branching. Members: `connect` (a cleanup-returning
`Ref<XRContext>` for `<Canvas ref={xr.connect}>`), `enter(mode, init?)`
/ `enter(session)`, `exit()`, `isSupported(mode)`, `isPresenting()`,
`session()`, and `Provider`. It absorbs the three edges of the contract
so the consumer never has to: snapshot order (`setAnimationLoop(render)`
before `setSession`), post-exit double-drive (`setAnimationLoop(null)`
on `sessionend`), and transient activation (`requestSession` first,
nothing awaited before it).

### `useXR()` + `createXR().Provider` — in-scene state

Wrap the subtree (Canvas + its button) in `<xr.Provider>`; scene
components read `{ isPresenting, session, exit }` with `useXR()` (which
throws outside a provider). This serves in-world UI — e.g. a mesh whose
`onClick` calls `exit()`, since the DOM exit button is not rendered
while an immersive session presents. `useXR` is deliberately not the
entry API: `use*` hooks read a provider from inside Canvas, whereas
`createXR()` is created outside it.

### Cleanup-returning refs

`useRef` now runs a callback ref's returned cleanup via `onCleanup` (the
React-19 cleanup-ref shape), and `<Canvas>`'s `ref` type is widened to
allow it (`RefWithCleanup<T>`). This is what makes `xr.connect` a
one-liner that returns its own disconnect.

### API surface

New exports: `createXR`, `useXR`, and types `XRContext` (`Pick<Context,
"gl" | "render">`) and `XRState`. `connect` is typed `XRContext`, not
the full `Context`, so its real dependency is explicit and any `{ gl,
render }` works — it is tied to neither `<Canvas>` nor its ref.

### Docs

New API pages `create-xr.mdx` and `use-xr.mdx`; `use-three.mdx`
documents the manual escape hatch and points at `createXR` as the
recommended path; `canvas.mdx` documents the cleanup-returning `ref`.
README gains `createXR`/`useXR` sections, a feature bullet, and TOC
entries. Design specs and TDD plans live under `docs/superpowers/`.

## Breaking

- Removed `Context.xr` (`{ connect, disconnect }`) — XR is now
consumer-wired (via `createXR`, or `gl.xr` directly).
- `Context.render` signature changed (`delta` → `timestamp`, added
`frame`) so the `XRFrame` flows through to `useFrame`.

## Manual escape hatch

`createXR` is the recommended path, but the raw contract is small enough
to wire by hand for both renderers: `gl.setAnimationLoop(render)` before
`gl.xr.setSession(session)` (with `gl.xr.enabled = true`), and
`gl.setAnimationLoop(null); gl.xr.enabled = false` to exit. For XR with
`WebGPURenderer` on three ≤ r184, construct it with `{ forceWebGL: true
}` (WebGPU-backend XR is unreleased upstream).

## Test plan

- `pnpm test` — 175 passed, 2 todo (12 files): core yields the window
loop while presenting and resumes on `sessionend`; core leaves
`gl.xr.enabled` alone; `XRFrame` forwarded to `useFrame`; `createXR`
state & wiring (snapshot order, post-exit loop-null, renderer-swap
re-attach, no-crash on an `xr` lacking `addEventListener`,
connect/disconnect); `enter` (request-first, provided-session overload,
error paths); `exit`/`isSupported`; `useXR` throws outside provider;
`Provider` bridges state across the Canvas boundary; cleanup-returning
`useRef`.
- `pnpm lint:types` — clean
- `pnpm lint:code` — clean
- Pending: on-device `WebGPURenderer({ forceWebGL: true })` + WebXR on
Quest 3 (the original report).
