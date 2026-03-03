# Online vZome Automated Testing Strategy

## Current State

No automated tests exist for online vZome. There is a `regression.html` with manual test cases and ~40 HTML test pages under `online/serve/app/test/cases/`, but nothing runs in CI. The existing `online/src/app/index.test.js` is a dead React artifact. The `developer-docs/regression-testing.md` calls this a "huge exposure."

## Architecture & Testability

The online vZome architecture has clean separation that enables testing at multiple layers:

- **Web Worker** handles all heavy computation (parsing `.vZome` files, executing edits, computing geometry) via a message protocol
- **Main thread** (SolidJS) handles UI and rendering (Three.js via solid-three)
- **`<vzome-viewer>` web component** provides the embeddable viewer with Shadow DOM

### Worker Message Protocol

The worker communicates through a well-defined message interface:

**Inbound (main → worker):**
- `URL_PROVIDED` / `FILE_PROVIDED` / `TEXT_PROVIDED` — load a design
- `ACTION_TRIGGERED` — execute an edit command
- `PROPERTY_REQUESTED` / `PROPERTY_SET` — controller properties
- `SNAPSHOT_SELECTED` — switch between saved snapshots

**Outbound (worker → main):**
- `SCENE_RENDERED` — geometry, shapes, and parts list
- `SCENES_DISCOVERED` — snapshots and camera positions
- `DESIGN_XML_PARSED` — parsed XML tree
- `ALERT_RAISED` — errors and notifications

This message boundary is the primary testing seam.

## Three-Layer Testing Strategy

### Layer 1: Worker Logic Tests — No browser needed

**Priority: Start here.** This is the highest-ROI layer.

**Tool:** Vitest running in Node.js

**What to test:**
- `.vZome` file parsing and edit history replay
- Action execution (the 211 edit commands)
- Correct `SCENE_RENDERED` output for known input files
- Error handling (`ALERT_RAISED` for malformed files)
- Preview loading (`.shapes.json` fast path)

**How:**
1. Import the worker message handler functions directly
2. Feed them `.vZome` XML text from the existing ~40 test case files
3. Assert the output events contain expected geometry/shapes

**Why no browser:** The worker code is pure computation — no DOM, no canvas, no browser APIs. It runs in Node.js as-is.

**Test sketch:**
```js
import { describe, it, expect } from 'vitest';
import { textLoader } from '../src/worker/vzome-worker-static.js';

describe('vZome file loading', () => {
  it('parses icosahedron correctly', async () => {
    const xml = fs.readFileSync('serve/app/test/cases/icosahedron.vZome', 'utf-8');
    const events = [];
    const report = (type, payload) => events.push({ type, payload });

    await textLoader(xml, report);

    const rendered = events.find(e => e.type === 'SCENE_RENDERED');
    expect(rendered).toBeDefined();
    expect(rendered.payload.shapes).not.toBeEmpty();
  });
});
```

### Layer 2: Component Tests — JSDOM is enough

**Priority: Medium.**

**Tool:** Vitest + `solid-testing-library` + JSDOM

**What to test:**
- SolidJS UI components (menus, panels, scene selector)
- Event handling and state management
- Component rendering without Three.js

**Limitations:** Three.js rendering cannot be tested this way. Stick to DOM-based UI logic.

### Layer 3: End-to-End & Visual Regression — Requires a browser

**Priority: Medium (add after Layer 1 is solid).**

**Tool:** Playwright

**What to test:**
- `<vzome-viewer>` web component loads and renders in a real browser
- Shadow DOM behavior (catches issues like the Kobalte Select bug)
- Visual regression via screenshot comparison
- Cross-browser rendering (Chromium, Firefox, WebKit)

**How:** Automate the existing manual test cases:
```js
import { test, expect } from '@playwright/test';

test('icosahedron renders in viewer', async ({ page }) => {
  await page.goto('http://localhost:8532/app/test/cases/icosahedron.html');
  await page.waitForSelector('vzome-viewer[loaded]');
  await expect(page).toHaveScreenshot();
});
```

**CI integration:** Start the dev server, run Playwright against it, compare screenshots.

## Summary

| Layer | Tool | Browser? | What it covers | Priority |
|-------|------|----------|----------------|----------|
| Worker message protocol | Vitest (Node) | No | Core engine, file parsing, edit commands | **High** |
| `.vZome` replay correctness | Vitest (Node) | No | Backward compatibility of file format | **High** |
| SolidJS UI components | Vitest + JSDOM | No | Menus, panels, state management | Medium |
| `<vzome-viewer>` web component | Playwright | Yes | Rendering, Shadow DOM, integration | Medium |
| Visual regression | Playwright + snapshots | Yes | Pixel-level rendering correctness | Lower |

## Existing Assets to Leverage

- **~40 test case HTML files** in `online/serve/app/test/cases/` with `.vZome` and `.shapes.json` files
- **`regression.html`** — fetches files from the `vzome-sharing` GitHub repo for regression testing
- **211 action names** in `developer-docs/vZome-action-names.txt` — each is a testable edit command

## Next Steps

1. Add Vitest to `online/package.json` devDependencies
2. Create `online/tests/worker/` directory
3. Write a smoke test that loads a `.vZome` file through the worker and asserts output
4. Add a `test` script to `package.json`
5. Add the test to GitHub Actions CI
6. Expand to cover more test cases and action commands
7. Add Playwright for E2E testing once the worker layer is solid
