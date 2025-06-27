
import { createSignal } from "solid-js";
import { render } from 'solid-js/web';

import { vZomeViewerCSS } from "../vzome-viewer.css";
import { urlViewerCSS } from "../../viewer/urlviewer.css.js";
import { CameraProvider, SceneViewer } from "../../viewer/index.jsx";
import { GltfModel } from './gltf.jsx';


const renderGlTFViewer = ( container, config ) =>
{
  const bindComponent = () =>
  {
    return (
      <CameraProvider>
        <SceneViewer config={ { ...config, allowFullViewport: true, showOutlines: false } }
            componentRoot={container}
            children3d={ <GltfModel url={config.url} /> }
            height="100%" width="100%" >
        </SceneViewer>
      </CameraProvider>
    );
  }

  container .appendChild( document.createElement("style") ).textContent = urlViewerCSS;
  render( bindComponent, container );
}

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
    renderGlTFViewer( this.#container, this.#config );
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
