// babel workaround
import "regenerator-runtime/runtime";

import React from "react";
import ReactDOM from "react-dom";
// @ts-ignore // no types available
import * as vZome from "./urlviewer";
import { vZomeViewerCSS } from "./vzome-viewer.css";

export class VZomeViewer extends HTMLElement {
  #root: ShadowRoot;
  #container: HTMLElement;
  constructor() {
    super();
    this.#root = this.attachShadow({ mode: "open" });

    this.#root.appendChild(document.createElement("style")).textContent =
      vZomeViewerCSS;
    this.#container = this.#root.appendChild(document.createElement("div"));
  }

  connectedCallback(): void {
    this.#render();
  }

  #reactElement: React.ComponentElement<vZome.UrlViewer, any> | null = null;
  get reactElement(): React.ComponentElement<vZome.UrlViewer, any> | null {
    return this.#reactElement;
  }

  #render(): void {
    if (this.src === null || this.src === "") {
      ReactDOM.unmountComponentAtNode(this.#container);
      this.#reactElement = null;
      return;
    }

    // TODO: Scale canvas by `window.devicePixelRatio`.
    // TODO: Can we handle canvas resizing using `ResizeObserver` without modifying `vZome` or recreating the element constantly?
    this.#reactElement = React.createElement(vZome.UrlViewer, {
      url: this.src,
    });
    ReactDOM.render(this.#reactElement, this.#container);
  }

  static get observedAttributes(): string[] {
    return ["src"];
  }

  attributeChangedCallback(
    attributeName: string,
    _oldValue: string,
    _newValue: string
  ): void {
    switch (attributeName) {
      case "src":
        this.#render();
    }
  }

  // Reflect the attribute in a JS property.
  set src(newSrc: string | null) {
    if (newSrc === null) {
      this.removeAttribute("src");
    } else {
      this.setAttribute("src", newSrc);
    }
  }

  get src(): string | null {
    return this.getAttribute("src");
  }
}

customElements.define("vzome-viewer", VZomeViewer);
