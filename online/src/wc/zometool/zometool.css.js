import { PLACEHOLDER_BACKGROUND_COLOR } from '../../viewer/util/colors.js';

export const instructionsCSS = `
:host {
  display: inline-grid;
  overflow: hidden;
  position: relative;
  font-family: var(--zometool-font-family, sans-serif);
  font-size: var(--zometool-font-size, 17px); /* text sizes below are in em, so this scales them all */
  --vzome-label-background: white;
  container-type: inline-size;

  /* Theming API: a host page may set any --zometool-* property, on this element or any ancestor.
     The private --_* copies supply the defaults, so that ancestor settings are not blocked here. */
  --_accent: var(--zometool-accent, hsl(200 98% 39%));
  --_accent-hover: var(--zometool-accent-hover, hsl(201 98% 44%));
  --_accent-active: var(--zometool-accent-active, hsl(201 98% 48%));
  --_accent-disabled: var(--zometool-accent-disabled, hsl(200deg 44.28% 69.6%));
  --_button-text: var(--zometool-button-text, white);
  --_button-text-disabled: var(--zometool-button-text-disabled, rgba(255, 255, 255, 0.2));
  --_button-border: var(--zometool-button-border, black);
  --_button-border-disabled: var(--zometool-button-border-disabled, light-dark(rgba(118, 118, 118, 0.3), rgba(195, 195, 195, 0.3)));
  --_button-radius: var(--zometool-button-radius, 18px);
  --_text: var(--zometool-text, hsl(240 6% 10%));
}

:host > div:empty {
  background-color: ${PLACEHOLDER_BACKGROUND_COLOR};
}

:host > div,
:host > div > div {
  position: absolute;
  inset: 0;
}

.mode-button {
  appearance: none;
  align-self: center;
  justify-self: start;
  margin: 9px;
  padding: 0.5em 1em;
  outline: none;
  border: 1px solid var(--_button-border);
  border-radius: var(--_button-radius);
  background-color: var(--_accent);
  color: var(--_button-text);
  font: inherit; /* buttons do not inherit fonts by default */
  cursor: pointer;
}

.step-controls {
  display: grid;
  grid-template-columns: 1.3fr 3fr;
}

.step-number {
  color: var(--_text);
  font-size: 2em;
  min-width: 2em;
  display: flex;
  justify-content: center;
}

.step-buttons {
  margin: auto;
  min-height: 6rem;
  gap: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-button {
  appearance: none;
  height: 60px;
  outline: none;
  border: 1px solid var(--_button-border);
  border-radius: var(--_button-radius);
  padding: 0 16px;
  background-color: var(--_accent);
  color: var(--_button-text);
  font-size: 56px; /* not rem, which would tie the icon to the host page's root font size */
  line-height: 0;
}

.limit-step {
  height: 48px;
  padding: 0 12px;
  font-size: 32px;
}

.step-button-svg {
  user-select: none;
  width: 1em;
  height: 1em;
  display: inline-block;
  fill: currentColor;
  flex-shrink: 0;
}
.step-button:hover,
.mode-button:hover {
  background-color: var(--_accent-hover);
}
.step-button:active,
.mode-button:active {
  background-color: var(--_accent-active);
}
.step-button:disabled {
  background-color: var(--_accent-disabled);
  color: var(--_button-text-disabled);
  border-color: var(--_button-border-disabled);
}

.step-button:focus-visible,
.mode-button:focus-visible {
  outline: 2px solid var(--_accent);
  outline-offset: 2px;
}

.zometool-instructions {
  height: 100%;
  display: grid;
  grid-template-rows: min-content 1fr;
}

:where(.zometool-products-heading) {
  font-weight: bold;
}

/* Theming for zometool-covering-products, which renders into light DOM, not a shadow root.
   A var() with no fallback, when its property is not set, makes the declaration act as if absent,
   so by default everything still inherits from the page.  :where() gives zero specificity,
   so any ordinary page rule still wins. */
:where(zometool-covering-products) {
  color: var(--zometool-text);
  font-family: var(--zometool-font-family);
  font-size: var(--zometool-font-size);
}
:where(.zometool-products-container .link) {
  color: var(--zometool-accent, LinkText);
}
:where(.zometool-products-container .link:visited) {
  color: var(--zometool-accent, VisitedText);
}

.zometool-products-container {
  max-height: 8.7rem;
  overflow: auto;
  margin-block-start: 8px;
}

.matched-product {
  min-width: 250px;
  display: flex;
  justify-content: space-between;
}

@container ( max-width: 650px ) {

  :host > div {
    inset-inline: 10px;
  }

  .step-controls {
    display: block;
  }

  .mode-button {
    display: block;
    margin: 5px auto;
  }

  .step-buttons {
    transform: scale(0.8);
    min-height: 4rem;
  }

  .step-button {
    padding: 0 0;
  }
  .limit-step {
    padding: 0 6px;
  }
  .step-number {
    min-width: 1.4em;
  }
}

`;
