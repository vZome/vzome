
import { REVISION } from '../../revision.js'

console.log( `vrml-viewer revision ${REVISION}` );

import { createSignal } from "solid-js";
import { render } from 'solid-js/web';

import { urlViewerCSS } from '../../viewer/urlviewer.css.js';
import { vZomeViewerCSS } from "../vzome-viewer.css";
import { CameraProvider, DesignViewer } from '../../viewer/index.jsx';
import { VrmlModel } from './vrml.jsx';
import { createDefaultCameraStore, fixedFrustum } from '../../viewer/context/camera.jsx';


const renderVrmlViewer = ( container, src, config, store ) =>
{
  const bindComponent = () =>
  {
    return (
      <CameraProvider cameraStore={store}>
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
  #cameraStore;

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

    this.#cameraStore = createDefaultCameraStore();

    const [ state, setState ] = this.#cameraStore;

    // These happen to be defaults that match all of George Hart's VRML
    setState( 'camera', fixedFrustum( 10 ) );
    setState( 'lighting', {
      backgroundColor: '#3380FF',
      ambientColor: '#b0b0b0',
      directionalLights: [
        { direction: [ 0.5, 1, 0 ], color: '#bbbbbb' },
        { direction: [ -0.5, -1, 0 ], color: '#bbbbbb' },
      ]
    } );
}

  connectedCallback()
  {
    renderVrmlViewer( this.#container, this.#src, this.#config, this.#cameraStore );
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
