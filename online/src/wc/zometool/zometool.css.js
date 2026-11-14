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
  --_switch-track: var(--zometool-switch-track, hsl(200deg 38.91% 75%));
  --_switch-thumb: var(--zometool-switch-thumb, white);
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

.step-switch {
  display: inherit;
  height: 30px;
  margin: 9px;
  .switch__control {
    display: block;
    margin: 23px;
    height: 25px;
    border-radius: 13px;
    width: 45px;
    border-color: var(--_accent);
    background-color: var(--_switch-track);
    .switch__thumb {
      height: 25px;
      width: 25px;
      border-radius: 13px;
      background-color: var(--_switch-thumb);
    }
  }
  .switch__control[data-checked] {
    border-color: var(--_accent);
    background-color: var(--_accent);
  }
  .switch__input:focus-visible + .switch__control {
    outline-color: var(--_accent);
  }
}
.step_switch__label {
  margin-right: 8px;
  color: var(--_text);
  user-select: none;
  font-size: 1em;
  min-width: max-content;
}

.step-controls {
  display: grid;
  grid-template-columns: 1fr 3fr;
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
.step-button:hover {
  background-color: var(--_accent-hover);
}
.step-button:active {
  background-color: var(--_accent-active);
}
.step-button:disabled {
  background-color: var(--_accent-disabled);
  color: var(--_button-text-disabled);
  border-color: var(--_button-border-disabled);
}

.step-button:focus-visible {
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

  .step-switch {
    display: inherit;
    .switch__control {
      display: inline-flex;
      margin: 5px;
    }
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
