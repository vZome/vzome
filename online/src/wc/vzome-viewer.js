// babel workaround
import "regenerator-runtime/runtime";
import { loadDesign } from "../ui/viewer/design.js";

import { vZomeViewerCSS } from "./vzome-viewer.css";

export class VZomeViewer extends HTMLElement {
  #root;
  #stylesMount;
  #container;
  #design;
  constructor() {
    super();
    this.#root = this.attachShadow({ mode: "open" });

    this.#root.appendChild(document.createElement("style")).textContent = vZomeViewerCSS;
    this.#stylesMount = document.createElement("div");
    this.#container = this.#root.appendChild( this.#stylesMount );

    if ( this.hasAttribute( 'src' ) ) {
      const url = this.getAttribute( 'src' );
      if ( ! url.endsWith( ".vZome" ) ) {
        // This is the only case in which we don't resolve the promise with text,
        //  since there is no point in allowing download of non-vZome text.
        alert( `Unrecognized file name: ${url}` );
      }
      else
        this.#design = loadDesign( url );
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
        this.#reactElement = module.render( this.#design, this.#container, this.#stylesMount, this.src );
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
