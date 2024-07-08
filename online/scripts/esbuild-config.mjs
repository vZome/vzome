
import { solidPlugin } from './esbuild-solid-plugin.mjs';
import { DOMElements, SVGElements } from "solid-js/web/dist/dev.cjs";

/*
  With help and advice from Lucas Garron, I've adopted esbuild as my toolchain, for the
  following reasons:

    1. Bundling happens identically for the dev server and for build, so there are no
        surprises.  (This shared config helps ensure that.)
    2. Dev server and build are *both* very fast, unlike Vite.  The tradeoff is that
        code splitting is not as good as Vite, which uses Rollup.
    3. The control over bundle entry points is very straightforward.  With a little work,
        I could probably get away from anonymous chunks entirely, or mostly.

  Since esbuild does not touch HTML, each of the apps' index.html uses the final module name
  in the script tag.  This works because esbuild bundles identically for the dev server.

  I'm using a pure ESM approach, with no bundling for legacy browsers.  This even extends
  to the worker, which currently implies that nothing runs in Firefox, unfortunately.
  See online/src/workerClient/client.js.  You'll also find a "trampoline" mechanism there
  to work around an esbuild issue with web workers:

     https://github.com/evanw/esbuild/issues/312#issuecomment-1025066671

  Each app gets its own entry point, as does the web component.  The web component also
  loads the bulk of the rendering code as a dynamic import, for good time-to-first-render.
  (The apps should do this too, since three.js is so fat, particularly after all the React
  is replaced with SolidJS!)  All the "back end" work is done in a web worker,
  and there is another dynamic import in the worker when the legacy (transpiled from Java)
  code is required to parse or interpret a vZome file.
*/

/*
  This dictionary form for entryPoints does not match the esbuild documentation,
  but works anyway.  Furthermore, I tried switching to the `{ out:..., in:... }`
  they document, and the build failed.
*/
export const esbuildConfig = {
  entryPoints: {
  // apps
    'vzome-online'    : 'src/app/index.jsx',
    'vzome-buildplane': 'src/app/buildplane/index.jsx',
    'vzome-browser'   : 'src/app/browser/index.jsx',
    'vzome-classic'   : 'src/app/classic/index.jsx',
    'fivecell'        : 'src/app/fivecell/index.jsx',
    'bhall-basic'     : 'src/app/bhall/basic/index.jsx',
    '59icosahedra'    : 'src/app/59icosahedra/index.jsx',
  // web components, not used by apps
    'vzome-viewer'        : 'src/wc/vzome-viewer.js',
    'gltf-viewer'         : 'src/wc/gltf/index.jsx',
    'vrml-viewer'         : 'src/wc/vrml/index.jsx',
    'zometool'            : 'src/wc/zometool/index.jsx',
  // client rendering code, dynamically imported for fast time-to-first-render
    'vzome-viewer-dynamic': 'src/viewer/index.jsx',
  // Worker entry point, only used as a module worker
    'vzome-worker-static' : 'src/worker/vzome-worker-static.js',
  // Legacy code, dynamically loaded as needed by the worker to parse vZome files or edit designs
    'vzome-legacy'        : 'src/worker/legacy/dynamic.js',
  // Legacy Zomic code, dynamically loaded as needed by the worker to parse Zomic scripts
    'vzome-zomic'         : 'src/worker/legacy/zomic/index.js',
},
  bundle: true,
  splitting: true,
  loader: { '.vef': 'dataurl' },
  format: 'esm',
  target: 'es2022',
  platform: 'browser',
  plugins: [ solidPlugin(
    {
      solid: {
        moduleName: "solid-js/web",
        // @ts-ignore
        generate: "dynamic",
        renderers: [
          {
            name: "dom",
            moduleName: "solid-js/web",
            elements: [...DOMElements.values(), ...SVGElements.values()],
          },
          {
            name: "universal",
            moduleName: "solid-three",
            elements: [],
          },
        ],
      },
    }
  )],
};
