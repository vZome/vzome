
import { Color, Scene, Group, Mesh, AmbientLight, DirectionalLight, WebGLRenderer } from "three";
import { WebGPURenderer } from 'three/webgpu';
import { createEffect, createRenderEffect, createResource, createSignal, mergeProps, onCleanup, For, Show, Suspense } from "solid-js";
import { createElementSize } from "@solid-primitives/resize-observer";

import { Dynamic } from "solid-js/web";
import { Canvas, createT, createXR, useFrame } from 'solid-three';
const T = createT({ Group, Mesh, AmbientLight, DirectionalLight });

import { TrackballControls } from "./trackballcontrols.jsx";
import { ControlledPerspectiveCamera } from "./perspectivecamera.jsx";
import { ControlledOrthographicCamera } from "./orthographiccamera.jsx";
import { useInteractionTool, useInteractionSurface } from "./context/interaction.jsx";
import { useCamera } from "./context/camera.jsx";
import { Labels } from "./labels.jsx";
import { useViewer } from "./context/viewer.jsx";
import { WebXRSupport, useWebXRClient } from "./context/webxr.jsx";
import { createCameraTrackball } from "./cameratrackball.js";

const Lighting = () =>
{
  const { state, perspectiveProps } = useCamera();
  // Adopting changes as required by https://discourse.threejs.org/t/updates-to-color-management-in-three-js-r152/50791
  //   and https://discourse.threejs.org/t/updates-to-lighting-in-three-js-r155/53733
  const mapColor = color => new Color() .setStyle( color ) .multiplyScalar( Math.PI );
  useFrame( ({scene}) => { scene.background = new Color() .setStyle( state.lighting.backgroundColor ) } ); // NOT converting to Linear!
  let centerObject;
  // The ambientLight has to be "invisible" so we don't get an empty node in glTF export.

  return (
    <T.Group position={perspectiveProps.position} up={perspectiveProps.up} target={perspectiveProps.target}>
      <T.Mesh ref={centerObject} visible={false} />
      <T.AmbientLight color={mapColor( state.lighting.ambientColor )} />
      <For each={state.lighting.directionalLights}>{ ( { direction, color } ) =>
        <T.DirectionalLight target={centerObject} intensity={1.4}
          color={mapColor( color )} position={direction.map( x => -x )} />
      }</For>
    </T.Group>
  )
}

// The hint panel: what the mouse does in the active tool.  A hint is { title, lines }, or just a
// title string.  The title row sits at the bottom with a collapse toggle, so the lines expand
// upward; the width is fixed, so it doesn't jump between tools or states.  Whether it is collapsed
// is remembered in localStorage, a per-browser convenience.
const HINTS_COLLAPSED_KEY = 'vzome.hintsCollapsed';
const readHintsCollapsed = () => { try { return localStorage.getItem( HINTS_COLLAPSED_KEY ) === 'true'; } catch { return false; } };
const writeHintsCollapsed = value => { try { localStorage.setItem( HINTS_COLLAPSED_KEY, String( value ) ); } catch {} };

const HintPanel = ( props ) =>
{
  const [ collapsed, setCollapsed ] = createSignal( readHintsCollapsed() );
  const toggle = () => {
    setCollapsed( value => ! value );
    writeHintsCollapsed( collapsed() );
  };
  const title = () => ( typeof props.hint === 'string' )? props.hint : props.hint ?.title;
  const lines = () => ( typeof props.hint === 'string' )? [] : ( props.hint ?.lines || [] );

  return (
    <div style={{
      position: 'absolute', left: '8px', bottom: '8px', width: '300px', 'max-width': 'calc(100% - 16px)',
      'box-sizing': 'border-box', padding: '6px 6px 6px 10px', 'border-radius': '4px',
      background: 'rgba(0,0,0,0.6)', color: 'white', 'font-size': '13px', 'line-height': '1.45',
      'font-family': 'sans-serif', 'pointer-events': 'none', 'user-select': 'none',
    }}>
      <Show when={ ! collapsed() && lines().length }>
        <div style={{ 'padding-bottom': '4px', 'margin-bottom': '4px', 'border-bottom': '1px solid rgba(255,255,255,0.25)' }}>
          <For each={ lines() }>{ line => <div>{ line }</div> }</For>
        </div>
      </Show>
      <div style={{ display: 'flex', 'align-items': 'center', 'justify-content': 'space-between', gap: '8px' }}>
        <span style={{ 'font-weight': 'bold' }}>{ title() }</span>
        <Show when={ lines().length }>
          <button onClick={toggle} aria-label={ collapsed()? 'Show mouse hints' : 'Hide mouse hints' }
              aria-expanded={ ! collapsed() } title={ collapsed()? 'Show mouse hints' : 'Hide mouse hints' }
              style={{
                'pointer-events': 'auto', cursor: 'pointer', background: 'transparent', border: 'none',
                color: 'inherit', padding: '0 2px', display: 'flex', 'align-items': 'center',
              }}>
            <svg width="16" height="16" viewBox="0 0 16 16" aria-hidden="true">
              <path d={ collapsed()? 'M4 10l4-4 4 4' : 'M4 6l4 4 4-4' } fill="none" stroke="currentColor"
                  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </button>
        </Show>
      </div>
    </div>
  );
}

const ControlledCamera = (props) =>
{
  const { state } = useCamera();

  return (
    <Show when={state.camera.perspective} fallback={
      <ControlledOrthographicCamera aspect={props.aspect}>
        {props.children}
      </ControlledOrthographicCamera>
    }>
      <ControlledPerspectiveCamera aspect={props.aspect}>
        {props.children}
      </ControlledPerspectiveCamera>
    </Show>
  );
}

// Lets WebXR suspend the camera control during a session.  Must be inside WebXRSupport.
const XRCameraControl = ( props ) =>
{
  const { addCameraControl } = useWebXRClient();
  if ( addCameraControl )
    onCleanup( addCameraControl( props.control ) );
  return null;
}

export const LightedTrackballCanvas = ( props ) =>
{
  let size;
  const aspect = () => ( size && size.height )? size.width / size.height : 1;
  const canvasSize = () => size;
  const { labels } = useViewer();

  const xr = createXR();
  const [xrSupported] = createResource( () => xr.isSupported( 'immersive-ar' ) );

  // connectRef captures the canvas DOM element (via ctx.gl.domElement) and wires createXR.
  // Canvas must be rendered inside <xr.Provider> (not pre-assigned) so that useXR() in
  // Canvas children can find the provider context.
  const [canvasEl, setCanvasEl] = createSignal( null );
  size = createElementSize( canvasEl );
  const connectRef = ( ctx ) => {
    setCanvasEl( ctx.gl.domElement );
    return xr.connect( ctx );
  };

  const [ tool ] = useInteractionTool();
  const surface = useInteractionSurface();

  // All pointer, wheel and context-menu handling belongs to the InteractionToolProvider
  // (context/interaction.jsx); the canvas just hands it the element.
  createEffect( () => {
    const el = canvasEl();
    if ( el && surface )
      onCleanup( surface.attachCanvas( el ) );
  });

  // The camera control that tools use (see cameratrackball.js), configured for this canvas.
  // TrackballControls, below, is still used by tools that set allowTrackball.
  const { trackballProps, perspectiveProps, cancelTweens } = useCamera();
  const cameraSettings = mergeProps( { rotateSpeed: 4.5, zoomSpeed: 3, panSpeed: 1 }, props );
  const cameraTrackball = createCameraTrackball( {
    camera: trackballProps.camera,
    getTarget: () => perspectiveProps.target,
    sync: target => trackballProps.sync( target ),
    onStart: cancelTweens,
    getCanvas: canvasEl,
    settings: () => cameraSettings,
  } );
  if ( surface )
    onCleanup( surface.setCameraControl( cameraTrackball ) );

  const makeCustomRenderer = ( canvas ) =>
  {
    // props.useWebGL is resolved by the capability probe upstream (see SceneCanvas and
    // renderer-support.js). On machines where WebGPURenderer fails to initialize -- notably
    // Intel Macs on Safari/Sequoia, where the viewer otherwise renders solid white -- we fall
    // back to the classic WebGLRenderer here. That path also forces symmetryRenderer off (see
    // SceneCanvas), since the classic renderer can't consume the symmetry renderer's TSL node
    // materials; the combined fallback is WebGLRenderer + ShapedGeometry.
    if ( props.useWebGL ) {
      const renderer = new WebGLRenderer({
        powerPreference: "high-performance",
        canvas,
        antialias: true,
        alpha: true,
        preserveDrawingBuffer: true,
      });
      return renderer;
    }
    const renderer = new WebGPURenderer({
      powerPreference: "high-performance",
      canvas,
      antialias: true,
      alpha: true,
      preserveDrawingBuffer: true,
      forceWebGL: true,
    });
    // renderer.xr.enabled = true;
    return renderer;
  }

  createRenderEffect( () => {
    const el = canvasEl();
    if ( el ) el.style.cursor = (tool ?.cursor()) || 'auto';
  });

  return (
    <xr.Provider>
      <div style={{ position: 'relative', height: props.height ?? "100%", width: props.width ?? "100%" }}>
        <Canvas ref={connectRef} class='canvas3d' dpr={ window.devicePixelRatio } gl={makeCustomRenderer}
            style={{
              height: "100%",
              width: "100%",
              display: 'flex',
            }}
            frameloop="always" >
          <WebXRSupport xrSupported={xrSupported}>

            { /* This should add the camera to the scene so that the lights move with the camera,
                  but it apparently does not. */ }
            <ControlledCamera aspect={aspect()} >
              <Lighting />
            </ControlledCamera>

            <TrackballControls rotationOnly={props.rotationOnly}
              rotateSpeed={props.rotateSpeed} zoomSpeed={props.zoomSpeed} panSpeed={props.panSpeed} />
            <XRCameraControl control={cameraTrackball} />

            {props.children}

            {labels && labels() && <Labels size={canvasSize()} />}

          </WebXRSupport>
        </Canvas>
        {/* Whatever the active tool draws over the canvas, e.g. a marquee rectangle. */}
        <Show when={ surface ?.overlay() }>{ overlay =>
          <div style={{ position: 'absolute', inset: 0, overflow: 'hidden', 'pointer-events': 'none' }}>
            <Dynamic component={ overlay() } />
          </div>
        }</Show>
        {/* Only canvases that ask for it show the hint panel. */}
        <Show when={ props.hints && surface ?.hint() }>
          <HintPanel hint={ surface.hint() } />
        </Show>
        <Suspense>
          <Show when={xrSupported()}>
            <button
              style={{
                position: 'absolute', bottom: '20px', left: '50%', transform: 'translateX(-50%)',
                padding: '12px 24px', 'border-radius': '4px', border: 'none',
                background: 'rgba(0,0,0,0.7)', color: 'white', cursor: 'pointer',
                'font-size': '13px', 'font-family': 'sans-serif',
              }}
              onClick={() => xr.enter( 'immersive-ar', { optionalFeatures: ['local-floor', 'hand-tracking'] } )}
            >
              START AR
            </button>
          </Show>
        </Suspense>
      </div>
    </xr.Provider>
  );
}
