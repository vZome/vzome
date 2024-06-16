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
}

.step-buttons {
  margin: auto;
  min-height: 4rem;
  gap: 13px;
  display: flex;
  align-items: center;
}

.step-button {
  appearance: none;
  height: 60px;
  outline: none;
  border: 1px solid black;
  border-radius: 8px;
  padding: 0 30px;
  background-color: hsl(200 98% 39%);
  color: white;
  font-size: 3.5rem;
  line-height: 0;
}

.limit-step {
  height: 48px;
  padding: 0 18px;
  font-size: 2rem;
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
  background-color: hsl(200 98% 30%);
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
  grid-template-rows: min-content 1fr min-content;
  gap: 0.5em;
}

`;
