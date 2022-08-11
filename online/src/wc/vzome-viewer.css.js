export const vZomeViewerCSS = `
:host {
  display: inline-grid;
  width: 384px;
  height: 256px;
  overflow: hidden;
  position: relative;
}

:host > div {
  display: grid;
  overflow: hidden;
}

:host > div:empty {
  background: radial-gradient(#EEE, #AAA);
}

:host > div,
:host > div > div {
  width: 100% !important;
  height: 100% !important;
  overflow: hidden;
}
`;
