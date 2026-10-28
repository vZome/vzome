import { PLACEHOLDER_BACKGROUND_COLOR } from "../viewer/util/colors.js";

export const vZomeViewerCSS = `
:host {
  display: inline-grid;
  width: 384px;
  height: 256px;
  overflow: hidden;
  position: relative;

  --vzome-label-background: white;
}

:host > div {
  display: grid;
  overflow: hidden;
}

:host > div:empty {
  background-color: ${PLACEHOLDER_BACKGROUND_COLOR};
}

:host > div,
:host > div > div {
  width: 100% !important;
  height: 100% !important;
  overflow: hidden;
}
`;
