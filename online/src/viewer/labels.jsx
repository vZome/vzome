
import { createEffect, onMount, useFrame, useThree } from "solid-three";
import { Vector3 } from "three";
import { CSS2DObject, CSS2DRenderer } from "three-stdlib";

export const Labels = (props) =>
{
  const scene = useThree(({ scene }) => scene);
  const camera = useThree(({ camera }) => camera);
  const webGL = useThree(({gl}) => gl);

  let labelRenderer;
  onMount( () => {
    labelRenderer = new CSS2DRenderer();
    const labelsElem = labelRenderer.domElement;
    labelsElem.style.isolation = 'isolate';
    labelsElem.style.position = 'absolute';
    labelsElem.style.pointerEvents = 'none';
    labelsElem.style.inset = '0px';
    labelsElem.style.width = '100%';
    labelsElem.style.height = '100%';
    labelsElem.classList .add( 'labels' );

    webGL() .domElement .insertAdjacentElement( "beforebegin", labelsElem );
  });

  createEffect( () => {
    props.size && labelRenderer .setSize( props.size .width, props.size .height );
  } );

  useFrame( () => {
    labelRenderer .render( scene(), camera() );
  })

  return null;
}

export const Label = (props) =>
{
  let label;  
  let elem;
  const camera = useThree(({ camera }) => camera);
  const gl = useThree(({ gl }) => gl);
  const worldPos = new Vector3();
  onMount( () => {
    elem = document .createElement( 'div' );
    elem.className = 'vzome-label';
    elem.id = `vzome-label-${props.text}`;
    label = new CSS2DObject( elem );
    props.parent .add( label );

  // Initial content render (attempt LaTeX if available)
    renderContent();

    // Re-render when KaTeX becomes available after async load
    const onKatexReady = () => renderContent();
    window.addEventListener('vzome:katex-ready', onKatexReady);
    label.userData.__onDispose = () => window.removeEventListener('vzome:katex-ready', onKatexReady);
  });

  const renderContent = () => {
    const text = props.text ?? '';
    const looksLatex = text.includes('\\') || text.includes('$');
    if (!looksLatex) {
      elem.textContent = text;
      return;
    }
    if (window.MathJax) {
      elem.innerHTML = text;
      window.MathJax.typesetPromise([elem]).catch((err) => {
        console.warn('MathJax typesetting failed:', err);
        elem.textContent = text;
      });
      return;
    }
    if (window.katex) {
      try {
        if (window.renderMathInElement) {
          elem.textContent = text; // set raw, the auto-render scans text content
          window.renderMathInElement(elem, {
            delimiters: [
              { left: '$$', right: '$$', display: true },
              { left: '$', right: '$', display: false }
            ],
            throwOnError: false
          });
        } else {
          const isDisplay = text.startsWith('$$') && text.endsWith('$$');
          const math = text.replace(/^\$+|\$+$/g, '');
          window.katex.render(math, elem, { displayMode: isDisplay, throwOnError: false });
        }
      } catch (err) {
        console.warn('KaTeX rendering failed:', err);
        elem.textContent = text;
      }
      return;
    }
    // Fallback until KaTeX loads
    elem.textContent = text;
  }

  createEffect( () => {
    // Re-render on text change
    renderContent();
    if (props.position && typeof props.position === 'object') {
      const { x, y, z } = props.position;
      label.position.set( x || 0, y || 0, z || 0 );
    }
  })

  // Update label scaling per frame to reflect camera distance and ball radius
  useFrame(() => {
    if (!elem || !props || props.type !== 'ball') return;
    const parent = label?.parent;
    if (!parent) return;
    try {
      // Determine radius from geometry boundingSphere if available
      const geom = parent.geometry;
      const radius = geom?.boundingSphere?.radius || 1;
      parent.getWorldPosition(worldPos);
      const cam = camera();
      const heightPx = gl()?.domElement?.clientHeight || 800;
      let pixelsPerUnit = 12; // fallback
      if (cam && cam.isPerspectiveCamera) {
        const vFOV = cam.fov * Math.PI / 180;
        const dist = cam.position.distanceTo(worldPos);
        pixelsPerUnit = heightPx / (2 * Math.tan(vFOV / 2) * Math.max(dist, 0.0001));
      } else if (cam && cam.isOrthographicCamera) {
        const worldHeight = cam.top - cam.bottom;
        pixelsPerUnit = heightPx / Math.max(worldHeight, 0.0001);
      }
      // Base sizing: 0.9em per unit radius on screen, clamped
      const px = Math.max(10, Math.min(48, radius * pixelsPerUnit * 0.22));
      elem.style.fontSize = `${px.toFixed(0)}px`;
      elem.style.transformOrigin = 'center center';
    } catch {
      // ignore
    }
  });

  return label;
}