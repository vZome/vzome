
import { vZomeViewerCSS } from "./vzome-viewer.css";

export class GltfViewerElement extends HTMLElement
{
  #root;
  #container;
  #config;

  constructor()
  {
    super();
    this.#root = this.attachShadow({ mode: "open" });

    this.#root.appendChild( document.createElement("style") ).textContent = vZomeViewerCSS;
    this.#container = document.createElement("div");
    this.#root.appendChild( this.#container );

    this.#config = { preview: true, showScenes: 'none', camera: true, lighting: true, design: true, };
  }

  connectedCallback()
  {
    import( '../viewer/index.jsx' )
      .then( module => {
        module.renderGlTFViewer( this.#container, this.#config );
      });
  }

  static get observedAttributes()
  {
    return [ "src" ];
  }

  attributeChangedCallback( attributeName, _oldValue, _newValue )
  {
    switch (attributeName) {

    case "src":
      const newUrl = new URL( _newValue, window.location ) .toString();
      if ( newUrl !== this.#config.url ) {
        this.#config.url = newUrl;
      }
      break;
    }
  }

  set src(newSrc)
  {
    if (newSrc === null) {
      this.removeAttribute("src");
    } else {
      this.setAttribute("src", newSrc);
    }
  }

  get src()
  {
    return this.getAttribute("src");
  }
}

customElements.define( "gltf-viewer", GltfViewerElement );
