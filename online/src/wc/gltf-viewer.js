
import { createSignal } from "solid-js";
import { vZomeViewerCSS } from "./vzome-viewer.css";

export class GltfViewerElement extends HTMLElement
{
  #root;
  #container;
  #config;
  #src
  #setSrc;

  constructor()
  {
    super();
    this.#root = this.attachShadow({ mode: "open" });
    
    const [ src, setSrc ] = createSignal( null );
    this.#src = src;
    this.#setSrc = setSrc;

    this.#root.appendChild( document.createElement("style") ).textContent = vZomeViewerCSS;
    this.#container = document.createElement("div");
    this.#root.appendChild( this.#container );

    this.#config = { preview: true, showScenes: 'none', camera: true, lighting: true, design: true, };
  }

  connectedCallback()
  {
    import( '../viewer/index.jsx' )
      .then( module => {
        if ( this.#config.url .toLowerCase() .endsWith( 'gltf' ) )
          module.renderGlTFViewer( this.#container, this.#config );
        else if ( this.#config.url .toLowerCase() .endsWith( 'vrml' ) )
          module.renderVrmlViewer( this.#container, this.#src, this.#config );
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
        this.#setSrc( newUrl );
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
