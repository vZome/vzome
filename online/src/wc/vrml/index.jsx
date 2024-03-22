
import { REVISION } from '../../revision.js'

console.log( `vrml-viewer revision ${REVISION}` );

import { createSignal } from "solid-js";
import { render } from 'solid-js/web';

import { urlViewerCSS } from '../../viewer/urlviewer.css.js';
import { vZomeViewerCSS } from "../vzome-viewer.css";
import { CameraProvider, DesignViewer } from '../../viewer/index.jsx';
import { VrmlModel } from './vrml.jsx';


const renderVrmlViewer = ( container, src, config ) =>
{
  const bindComponent = () =>
  {
    return (
      <CameraProvider>
        <DesignViewer config={ { ...config, allowFullViewport: true } }
            componentRoot={container}
            children3d={ <VrmlModel url={src()} /> }
            height="100%" width="100%" >
        </DesignViewer>
      </CameraProvider>
    );
  }

  container .appendChild( document.createElement("style") ).textContent = urlViewerCSS;
  render( bindComponent, container );
}

export class VrmlViewerElement extends HTMLElement
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
    renderVrmlViewer( this.#container, this.#src, this.#config );
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

customElements.define( "vrml-viewer", VrmlViewerElement );
