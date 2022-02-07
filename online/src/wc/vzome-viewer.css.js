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
:host > div > div,
:host > div > div > div {
  width: 100% !important;
  height: 100% !important;
  overflow: hidden;
}


:host > div > div > div {
  display: grid;
  place-content: center;
}

.muiButton:hover {
  background-color: rgba(0, 0, 0, 0.08);
}

.muiButton {
  flex: 0 0 auto;
  color: inherit;
  padding: 12px;
  overflow: visible;
  font-size: 1.5rem;
  text-align: center;
  transition: background-color 150ms cubic-bezier(0.4, 0, 0.2, 1) 0ms;
  border-radius: 50%;
  border: 0;
  cursor: pointer;
  margin: 0;
  display: inline-flex;
  outline: 0;
  position: absolute;
  top: 5px;
  right: 5px;
  height: 50px;
  width: 50px;
  align-items: center;
  user-select: none;
  vertical-align: middle;
  -moz-appearance: none;
  justify-content: center;
  text-decoration: none;
  background-color: transparent;
  -webkit-appearance: none;
  -webkit-tap-highlight-color: transparent;
}
`;
