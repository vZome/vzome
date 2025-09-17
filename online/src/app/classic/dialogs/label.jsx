
import DialogContent from "@suid/material/DialogContent"
import Dialog from "@suid/material/Dialog"
import DialogTitle from "@suid/material/DialogTitle"
import DialogActions from "@suid/material/DialogActions"
import Button from "@suid/material/Button"
import TextField from "@suid/material/TextField"
import Typography from "@suid/material/Typography"
import FormControlLabel from "@suid/material/FormControlLabel"
import Checkbox from "@suid/material/Checkbox"

import { subController, useEditor } from '../../framework/context/editor.jsx';
import { createEffect, createSignal } from "solid-js"

export const LabelDialog = props =>
{
  const { rootController, controllerAction } = useEditor();
  const controller  = () => subController( rootController(), 'picking' );
  const [ value, setValue ] = createSignal( '' );
  const [ enableLatex, setEnableLatex ] = createSignal( false );
  let previewEl;
  
  createEffect( () => {
    if ( !! props.label )
      setValue( props.label );
  })

  const perform = event =>
  {
    let text = value();
    if (enableLatex()) {
      const trimmed = (text ?? '').trim();
      // If not already delimited with $...$ or $$...$$, wrap with single dollars
      const alreadyDelimited = /^\$(.|\n)*\$$/.test(trimmed) || /^\$\$(.|\n)*\$\$/.test(trimmed);
      text = alreadyDelimited ? trimmed : `$${trimmed}$`;
    }
    controllerAction( controller(), 'Label', { id: props.id, text } );
    props.close();
  }

  // Load KaTeX dynamically when LaTeX preview is enabled
  const ensureKatex = () => {
    if (window.katex || window.MathJax) return;
    const existing = document.getElementById('vzome-katex-css');
    if (!existing) {
      const katexCSS = document.createElement('link');
      katexCSS.id = 'vzome-katex-css';
      katexCSS.rel = 'stylesheet';
      katexCSS.href = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css';
      katexCSS.crossOrigin = 'anonymous';
      document.head.appendChild(katexCSS);
    }
    const scriptExisting = document.getElementById('vzome-katex-js');
    if (!scriptExisting) {
      const katexJS = document.createElement('script');
      katexJS.id = 'vzome-katex-js';
      katexJS.src = 'https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js';
      katexJS.crossOrigin = 'anonymous';
      katexJS.onload = () => {
        const autoRenderJS = document.createElement('script');
        autoRenderJS.id = 'vzome-katex-autorender-js';
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
  }

  const renderPreview = () => {
    if (!previewEl) return;
    const text = (value() ?? '').trim();
    if (!enableLatex()) {
      previewEl.textContent = '';
      return;
    }
    const looksLatex = text.includes('\\') || (text.startsWith('$') && text.endsWith('$'));
    if (!looksLatex) {
      previewEl.textContent = 'Enter LaTeX (use $...$ or commands like \\frac, \\sum, \\int)';
      return;
    }
    if (window.katex) {
      try {
        if (window.renderMathInElement) {
          previewEl.textContent = text;
          window.renderMathInElement(previewEl, {
            delimiters: [
              { left: '$$', right: '$$', display: true },
              { left: '$', right: '$', display: false }
            ],
            throwOnError: false
          });
        } else {
          const isDisplay = text.startsWith('$$') && text.endsWith('$$');
          const math = text.replace(/^\$+|\$+$/g, '');
          window.katex.render(math, previewEl, { displayMode: isDisplay, throwOnError: false });
        }
      } catch (err) {
        console.warn('KaTeX preview failed', err);
        previewEl.textContent = text;
      }
      return;
    }
    if (window.MathJax) {
      previewEl.innerHTML = text;
      window.MathJax.typesetPromise([previewEl]).catch(() => previewEl.textContent = text);
      return;
    }
    previewEl.textContent = 'Loading LaTeX engine…';
  }

  // React to toggles and input changes
  createEffect(() => {
    if (enableLatex()) ensureKatex();
    renderPreview();
  });
  // Re-render when KaTeX loads
  createEffect(() => {
    const onReady = () => renderPreview();
    const onAuto = () => renderPreview();
    window.addEventListener('vzome:katex-ready', onReady);
    window.addEventListener('vzome:katex-autorender-ready', onAuto);
    return () => {
      window.removeEventListener('vzome:katex-ready', onReady);
      window.removeEventListener('vzome:katex-autorender-ready', onAuto);
    };
  });

  return (
    <Dialog onClose={ () => props.close() } open={props.open}>
      <DialogTitle id="label-dialog">Label an Object</DialogTitle>
      <DialogContent sx={{ 'padding-bottom': '0px' }}>
        <TextField autoFocus id="label-value" label="value" variant="outlined" fullWidth value={value()} sx={{ margin: '5px' }}
            onChange={(event, value) => setValue( value )}
          />
        <FormControlLabel
          control={<Checkbox checked={enableLatex()} onChange={(event, checked) => setEnableLatex(checked)} />}
          label="Enable LaTeX rendering"
          sx={{ marginTop: '10px', marginLeft: '5px' }}
        />
        {enableLatex() && (
          <div style={{ margin: '8px 6px 0 6px' }}>
            <Typography variant="caption" sx={{ color: 'text.secondary' }}>Preview</Typography>
            <div ref={el => (previewEl = el)} style={{ padding: '6px 8px' }} />
          </div>
        )}
        {enableLatex() && (
          <Typography variant="body2" sx={{ margin: '10px 5px', color: 'text.secondary' }}>
            {`LaTeX examples: $x^2 + y^2 = r^2$, $\\int_0^\\infty e^{-x} dx$, $\\sum_{i=1}^n i = \\frac{n(n+1)}{2}$`}
          </Typography>
        )}
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" size="medium" onClick={ ()=>props.close() } color="secondary">
          Close
        </Button>
        <Button variant="contained" size="medium" onClick={ perform } color="primary">
          Apply
        </Button>
      </DialogActions>
    </Dialog>
  );
}
