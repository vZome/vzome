// babel workaround
import "regenerator-runtime/runtime";

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
    if ( this.hasAttribute( 'src' ) ) {
      this.#worker.postMessage( { type: "fetchShapesAndText", url: this.getAttribute( 'src' ) } );
      console.log( 'Posted the text to the worker!' );
    }
  }

  connectedCallback() {
    this.#render();
  }

  #reactElement = null;
  get reactElement() {
    return this.#reactElement;
  }

  #render() {
    import( '../ui/viewer/index.jsx' )
      .then( module => {
        this.#reactElement = module.render( this.#worker, this.#container, this.#stylesMount, this.src );
      })
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
