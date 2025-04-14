export const instructionsCSS = `
:host {
  display: inline-grid;
  overflow: hidden;
  position: relative;
  font-family: sans-serif;
  --vzome-label-background: white;
}

:host > div:empty {
  background: radial-gradient(#EEE, #AAA);
}

:host > div,
:host > div > div {
  position: absolute;
  inset: 0;
}

.switch {
  height: 30px;
  margin: 6px;
}
.step_switch__label {
  margin-right: 8px;
  color: hsl(240 6% 10%);
  user-select: none;
  font-size: 17px;
}

.step-controls {
  display: grid;
  grid-template-columns: 1fr 3fr;
}

.step-number {
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
}

.step-button {
  appearance: none;
  height: 60px;
  outline: none;
  border: 1px solid black;
  border-radius: 18px;
  padding: 0 16px;
  background-color: hsl(200 98% 39%);
  color: white;
  font-size: 3.5rem;
  line-height: 0;
}

.limit-step {
  height: 48px;
  padding: 0 12px;
  font-size: 2rem;
}

@media ( max-width: 650px ) {

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

.step-button-svg {
  user-select: none;
  width: 1em;
  height: 1em;
  display: inline-block;
  fill: currentColor;
  flex-shrink: 0;
}
.step-button:hover {
  background-color: hsl(201 98% 44%);
}
.step-button:active {
  background-color: hsl(201 98% 48%);
}
.step-button:disabled {
  background-color: hsl(200deg 44.28% 69.6%);
  color: rgba(255, 255, 255, 0.2);
  border-color: light-dark(rgba(118, 118, 118, 0.3), rgba(195, 195, 195, 0.3));
}

.step-button:focus-visible {
  outline: 2px solid hsl(200 98% 39%);
  outline-offset: 2px;
}

.zometool-instructions {
  height: 100%;
  display: grid;
  grid-template-rows: min-content 1fr;
}

zometool-covering-products::before {
   content: "Supporting products:";
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
`;
