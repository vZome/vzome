// babel workaround
import "regenerator-runtime/runtime";

import React from "react";
import ReactDOM from "react-dom";
import { StylesProvider, jssPreset } from '@material-ui/styles';
import { create } from 'jss';

import * as vZome from "./urlviewer";
import { vZomeViewerCSS } from "./vzome-viewer.css";

export class VZomeViewer extends HTMLElement {
  #root;
  #stylesMount;
  #container;
  #worker;
  constructor() {
    super();
    this.#root = this.attachShadow({ mode: "open" });

    this.#root.appendChild(document.createElement("style")).textContent = vZomeViewerCSS;
    this.#stylesMount = document.createElement("div");
    this.#container = this.#root.appendChild( this.#stylesMount );

    this.#worker = new Worker( '/modules/vzome-worker.js' );
  }

  connectedCallback() {
    this.#render();
  }

  #reactElement = null;
  get reactElement() {
    return this.#reactElement;
  }

  #render() {
    if (this.src === null || this.src === "") {
      ReactDOM.unmountComponentAtNode(this.#container);
      this.#reactElement = null;
      return;
    }

    // TODO: Can we handle canvas resizing using `ResizeObserver` without modifying `vZome` or recreating the element constantly?
    const viewerElement = React.createElement(vZome.UrlViewer, {
      url: this.src, worker: this.#worker
    });

    // We need JSS to inject styles on our shadow root, not on the document head.
    // I found this solution here:
    //   https://stackoverflow.com/questions/51832583/react-components-material-ui-theme-not-scoped-locally-to-shadow-dom
    const jss = create({
        ...jssPreset(),
        insertionPoint: this.#container
    });
    this.#reactElement = React.createElement( StylesProvider, { jss: jss }, [ viewerElement ] );

    ReactDOM.render(this.#reactElement, this.#stylesMount);
  }

  static get observedAttributes() {
    return ["src"];
  }

  attributeChangedCallback(
    attributeName,
    _oldValue,
    _newValue
  ) {
    switch (attributeName) {
      case "src":
        this.#render();
    }
  }

  // Reflect the attribute in a JS property.
  set src(newSrc) {
    if (newSrc === null) {
      this.removeAttribute("src");
    } else {
      this.setAttribute("src", newSrc);
    }
  }

  get src() {
    return this.getAttribute("src");
  }
}

customElements.define("vzome-viewer", VZomeViewer);
