// Sample code: step-by-step controls for <zometool-instructions controls="none">.
//
// This relies only on the element's step API:
//  - the "step" property: set a number from 1 to stepCount to show that step (out-of-range numbers
//      are clamped), or null to show the finished design.  It reads back the step being shown.
//  - the "zometool-step-shown" event, with detail { step, stepCount }.  It fires once the design's
//      scenes are discovered, and again on every change, including those made by this code.
//      A stepCount of 0 means the design has no steps.
// Everything else here (markup, labels, styling) is yours to change.

const instructions = document .querySelector( 'zometool-instructions' );
const controls = document .querySelector( '.step-controls' );
const modeButton = controls .querySelector( '.mode-button' );
const stepButtons = controls .querySelector( '.step-buttons' );
const firstButton = controls .querySelector( '.first-step' );
const previousButton = controls .querySelector( '.previous-step' );
const nextButton = controls .querySelector( '.next-step' );
const lastButton = controls .querySelector( '.last-step' );
const stepNumber = controls .querySelector( '.step-number' );

let stepCount = 0;
let lastStepShown = 1; // so returning to the steps resumes where the user left off

// The mode button is a call to action, so its label names what a click will show.
modeButton .addEventListener( 'click', () => {
  instructions.step = ( instructions.step === null )? lastStepShown : null;
} );
firstButton    .addEventListener( 'click', () => instructions.step = 1 );
previousButton .addEventListener( 'click', () => instructions.step = instructions.step - 1 );
nextButton     .addEventListener( 'click', () => instructions.step = instructions.step + 1 );
lastButton     .addEventListener( 'click', () => instructions.step = stepCount );

instructions .addEventListener( 'zometool-step-shown', ( { detail } ) => {
  const { step } = detail;
  stepCount = detail.stepCount;
  controls.hidden = ( stepCount === 0 );

  const showingSteps = ( step !== null );
  modeButton.textContent = showingSteps? modeButton.dataset.designLabel : modeButton.dataset.stepsLabel;
  stepButtons.hidden = ! showingSteps;
  if ( showingSteps ) {
    lastStepShown = step;
    stepNumber.textContent = step;
    firstButton.disabled = previousButton.disabled = ( step === 1 );
    nextButton.disabled = lastButton.disabled = ( step === stepCount );
  }
} );
