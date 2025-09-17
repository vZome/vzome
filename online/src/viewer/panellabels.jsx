import { createEffect, onMount, useFrame, useThree } from "solid-three";
import { Vector3 } from "three";
import { CSS2DObject, CSS2DRenderer, CSS3DObject, CSS3DRenderer } from "three-stdlib";

// CSS for panel labels
const panelLabelCSS = `
.vzome-panel-label {
  color: var(--vzome-panel-label-color, #333);
  background-color: transparent !important;
  font-size: var(--vzome-label-font-px, 16px);
  font-style: var(--vzome-panel-label-style, normal);
  font-weight: var(--vzome-panel-label-weight, 500);
  padding: 0; /* no padding so only math is visible */
  border: none;
  box-shadow: none;
  backdrop-filter: none;
  font-family: 'KaTeX_Main', 'Computer Modern', 'Times New Roman', serif;
  line-height: 1.3;
  text-align: center;
  min-width: 0;
  min-height: 0;
  transition: none;
}

.vzome-panel-label:hover {
  background-color: transparent !important;
  box-shadow: none;
  transform: none;
}

.vzome-panel-label .katex {
  font-size: inherit;
  color: inherit;
}

.vzome-panel-label .katex-display {
  margin: 0;
  text-align: center;
}

@media (prefers-color-scheme: dark) {
  .vzome-panel-label {
    color: var(--vzome-panel-label-color, #fff);
    background-color: transparent !important;
    border-color: transparent;
  }
  
  .vzome-panel-label:hover {
    background-color: transparent !important;
  }
}
`;

// LaTeX panel label component that doesn't use CSS2DObject for panel labels
export const PanelLabel = (props) =>
{
  let labelElement;
  let elem;
  
  const renderContent = () => {
    const text = props.text ?? '';
    const looksLatex = text.includes('\\') || text.includes('$');
    if (!looksLatex) {
      elem.textContent = text;
      return;
    }
    if (window.MathJax) {
      elem.innerHTML = text;
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
          elem.textContent = text;
          window.renderMathInElement(elem, {
            delimiters: [
              { left: '$$', right: '$$', display: true },
              { left: '$', right: '$', display: false }
            ],
            throwOnError: false
          });
        } else {
          const isDisplayMode = text.startsWith('$$') && text.endsWith('$$');
          const mathText = text.replace(/^\$+|\$+$/g, '');
          window.katex.render(mathText, elem, { displayMode: isDisplayMode, throwOnError: false });
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

  onMount( () => {
    elem = document.createElement( 'div' );
    elem.className = 'vzome-panel-label';
    elem.id = `vzome-panel-label-${props.text}`;

    // Initial render
    renderContent();

    // Re-render when KaTeX becomes available
  const onKatexReady = () => renderContent();
  const onAutoReady = () => renderContent();
    window.addEventListener('vzome:katex-ready', onKatexReady);
  window.addEventListener('vzome:katex-autorender-ready', onAutoReady);

  // Use CSS3D so the label inherits orientation from its parent
  labelElement = new CSS3DObject( elem );
    props.parent.add( labelElement );

    // Best-effort removal on disposal
    labelElement.userData.__onDispose = () => {
      window.removeEventListener('vzome:katex-ready', onKatexReady);
      window.removeEventListener('vzome:katex-autorender-ready', onAutoReady);
    };
  });

  createEffect( () => {
    if (labelElement && props.position && typeof props.position === 'object') {
      const { x, y, z } = props.position;
      labelElement.position.set( x || 0, y || 0, z || 0 );
    }
    // Optional: adjust font size based on type (panels generally keep a consistent size)
    try {
      const type = props.type;
      if (type === 'ball') {
        elem.style.fontSize = '14px';
        elem.style.transformOrigin = 'center center';
      } else {
        elem.style.fontSize = '';
        elem.style.transformOrigin = '';
      }
    } catch {}
  });

  return labelElement;
};

// Enhanced Labels component that handles both regular and panel labels
export const EnhancedLabels = (props) =>
{
  const scene = useThree(({ scene }) => scene);
  const camera = useThree(({ camera }) => camera);
  const webGL = useThree(({gl}) => gl);

  let labelRenderer;
  onMount( () => {
    // Inject CSS for panel labels if not already present
    if (!document.querySelector('#vzome-panel-labels-css')) {
      const style = document.createElement('style');
      style.id = 'vzome-panel-labels-css';
      style.textContent = panelLabelCSS;
      document.head.appendChild(style);
    }
    
    labelRenderer = new CSS2DRenderer();
    const labelsElem = labelRenderer.domElement;
    labelsElem.style.isolation = 'isolate';
    labelsElem.style.position = 'absolute';
    labelsElem.style.pointerEvents = 'none';
    labelsElem.style.inset = '0px';
    labelsElem.style.width = '100%';
    labelsElem.style.height = '100%';
    labelsElem.classList.add( 'labels' );

    const gl = webGL();
    if (gl && gl.domElement) {
      // Ensure the overlay sits ABOVE the WebGL canvas
      labelsElem.style.zIndex = '3';
      gl.domElement.insertAdjacentElement( "afterend", labelsElem );
    }
    
    // Load LaTeX rendering library (KaTeX is lighter than MathJax)
    if (!window.katex && !window.MathJax) {
      // Dynamically load KaTeX if not already available
      const katexCSS = document.createElement('link');
      katexCSS.rel = 'stylesheet';
      katexCSS.href = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css';
      katexCSS.integrity = 'sha384-GvrOXuhMATgEsSwCs4smul74iXGOixntILdUW9XmUC6+HX0sLNAK3q71HotJqlAn';
      katexCSS.crossOrigin = 'anonymous';
      document.head.appendChild(katexCSS);
      
      const katexJS = document.createElement('script');
      katexJS.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js';
      katexJS.integrity = 'sha384-cpW21h6RZv/phavutF+AuVYrr+dA8xD9zs6FwLpaCct6O9ctzYFfFr4dgmgccOTx';
      katexJS.crossOrigin = 'anonymous';
      katexJS.onload = () => {
        // Also load auto-render for $...$ scanning
        const autoRenderJS = document.createElement('script');
        autoRenderJS.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js';
        autoRenderJS.crossOrigin = 'anonymous';
        autoRenderJS.onload = () => {
          window.dispatchEvent(new CustomEvent('vzome:katex-ready'));
          window.dispatchEvent(new CustomEvent('vzome:katex-autorender-ready'));
        };
        document.head.appendChild(autoRenderJS);
      };
      document.head.appendChild(katexJS);
    }
    // If KaTeX already present (cached/previous load), still signal readiness
    if (window.katex) {
      window.dispatchEvent(new CustomEvent('vzome:katex-ready'));
      if (window.renderMathInElement) {
        window.dispatchEvent(new CustomEvent('vzome:katex-autorender-ready'));
      }
    }
  });

  createEffect( () => {
    if (labelRenderer && props.size) {
      labelRenderer.setSize( props.size.width, props.size.height );
    }
  } );

  useFrame( () => {
    if (labelRenderer) {
      // Ensure matrixWorld is current so label transforms track their parents
      const s = scene();
      s && s.updateMatrixWorld();
      labelRenderer.render( s, camera() );
    }
  });

  return null;
};

// 3D-oriented labels overlay (CSS3DRenderer)
export const OrientedLabels = (props) =>
{
  const scene = useThree(({ scene }) => scene);
  const camera = useThree(({ camera }) => camera);
  const webGL = useThree(({gl}) => gl);

  let labelRenderer3D;
  onMount( () => {
    labelRenderer3D = new CSS3DRenderer();
    const labelsElem = labelRenderer3D.domElement;
    labelsElem.style.isolation = 'isolate';
    labelsElem.style.position = 'absolute';
    labelsElem.style.pointerEvents = 'none';
    labelsElem.style.inset = '0px';
    labelsElem.style.width = '100%';
    labelsElem.style.height = '100%';
    labelsElem.classList.add( 'labels-3d' );

    const gl = webGL();
    if (gl && gl.domElement) {
      // Ensure the 3D labels are above the canvas but under 2D labels
      labelsElem.style.zIndex = '2';
      gl.domElement.insertAdjacentElement( "afterend", labelsElem );
    }

    // Ensure KaTeX is available if needed
    if (!window.katex && !window.MathJax) {
      const katexCSS = document.createElement('link');
      katexCSS.rel = 'stylesheet';
      katexCSS.href = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css';
      katexCSS.crossOrigin = 'anonymous';
      document.head.appendChild(katexCSS);

      const katexJS = document.createElement('script');
      katexJS.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js';
      katexJS.crossOrigin = 'anonymous';
      katexJS.onload = () => {
        const autoRenderJS = document.createElement('script');
        autoRenderJS.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js';
        autoRenderJS.crossOrigin = 'anonymous';
        autoRenderJS.onload = () => {
          window.dispatchEvent(new CustomEvent('vzome:katex-ready'));
          window.dispatchEvent(new CustomEvent('vzome:katex-autorender-ready'));
        };
        document.head.appendChild(autoRenderJS);
      };
      document.head.appendChild(katexJS);
    } else {
      // Signal existing availability
      window.dispatchEvent(new CustomEvent('vzome:katex-ready'));
      if (window.renderMathInElement)
        window.dispatchEvent(new CustomEvent('vzome:katex-autorender-ready'));
    }
  });

  createEffect( () => {
    if (labelRenderer3D && props.size) {
      labelRenderer3D.setSize( props.size.width, props.size.height );
    }
  } );

  useFrame( () => {
    if (labelRenderer3D) {
      const s = scene();
      s && s.updateMatrixWorld();
      labelRenderer3D.render( s, camera() );
    }
  });

  return null;
}

// Keep the original Label component for backward compatibility
export const Label = (props) =>
{
  let label;  
  onMount( () => {
    const elem = document.createElement( 'div' );
    elem.className = 'vzome-label';
    elem.id = `vzome-label-${props.text}`;
    elem.textContent = props.text;
    label = new CSS2DObject( elem );
    props.parent.add( label );
  });

  createEffect( () => {
    if (props.position && typeof props.position === 'object') {
      const { x, y, z } = props.position;
      label.position.set( x || 0, y || 0, z || 0 );
    }
  });

  return label;
};