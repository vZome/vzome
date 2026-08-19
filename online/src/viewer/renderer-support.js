
import { WebGPURenderer } from 'three/webgpu';

// When true, we skip the WebGPURenderer entirely and use the classic WebGLRenderer
// with ShapedGeometry (non-instanced) -- see makeCustomRenderer in ltcanvas.jsx and the
// symmetryRenderer gating in scenecanvas.jsx. This is the fallback path for machines where
// WebGPURenderer (even with forceWebGL:true) fails to initialize -- notably observed on
// Intel Macs running Safari on macOS Sequoia, where the viewer renders solid white.

const STORAGE_KEY = 'vzome.forceWebGLFallback';
const URL_PARAM = 'forceWebGL';

const readStorage = () => {
  // localStorage access can throw in sandboxed/partitioned embedded iframes.
  try { return globalThis.localStorage ?.getItem( STORAGE_KEY ) === 'true'; }
  catch { return false; }
};

const writeStorage = () => {
  try { globalThis.localStorage ?.setItem( STORAGE_KEY, 'true' ); }
  catch { /* non-fatal: URL param still forces fallback for this page load */ }
};

// The URL param (?forceWebGL=1) is a convenience so a link can be handed to an affected
// user instead of console instructions. Because the flag is per-origin (localStorage), and
// each domain embedding the web component is its own origin, honoring the param also
// persists it to localStorage so the user only needs the link once per domain.
const readUrlParam = () => {
  try {
    const value = new URLSearchParams( globalThis.location ?.search ) .get( URL_PARAM );
    // Present with no value (?forceWebGL), or an affirmative value, both count.
    return value === '' || value === '1' || value === 'true';
  } catch { return false; }
};

// Support/user escape hatch to force the WebGL + ShapedGeometry fallback regardless of the
// capability probe below. Set via console -- localStorage.setItem('vzome.forceWebGLFallback',
// 'true') -- or via a ?forceWebGL=1 link, which also persists it to localStorage.
export const isFallbackForced = () => {
  if ( readUrlParam() ) {
    writeStorage();
    return true;
  }
  return readStorage();
};

let probePromise = null;

// Resolves true if a WebGPURenderer configured the way ltcanvas.jsx configures it
// (forceWebGL:true) can actually initialize on this machine, false if it fails the way it
// does on the affected Intel Sequoia Safari. Probed at most once per page load and cached.
//
// This deliberately runs the same `await renderer.init()` that solid-three runs internally
// (see getPendingInit / rendererReady in solid-three's Canvas) -- but solid-three has no
// .catch on that init, so an async rejection there just leaves the canvas white with nothing
// surfaced. Running it ourselves first lets us both catch the rejection (console diagnostic)
// and choose the renderer up front.
export const canUseWebGPURenderer = () => {
  if ( probePromise )
    return probePromise;
  probePromise = (async () => {
    if ( isFallbackForced() ) {
      console.info( '[vZome] WebGL fallback forced (localStorage/URL); using classic WebGLRenderer.' );
      return false;
    }
    let renderer;
    try {
      const canvas = document.createElement( 'canvas' ); // never inserted into the DOM
      renderer = new WebGPURenderer( { canvas, antialias: true, alpha: true, forceWebGL: true } );
      await renderer.init();
      return true;
    } catch ( e ) {
      console.warn( '[vZome] WebGPURenderer init failed; using classic WebGLRenderer fallback.', e );
      return false;
    } finally {
      try { renderer ?.dispose ?.(); } catch { /* ignore dispose errors on a failed renderer */ }
    }
  })();
  return probePromise;
};
