// babel workaround
import "regenerator-runtime/runtime";

import { vZomeViewerCSS } from "./vzome-viewer.css";

import { muiCSS } from "./mui-styles.css";

import { createWorkerStore, fetchDesign } from '../ui/viewer/store.js';
import { createWorker } from "../workerClient/client";

export class VZomeViewer extends HTMLElement
{
  #root;
  #container;
  #store;
  #url;
  #config;

  constructor()
  {
    super();
    this.#root = this.attachShadow({ mode: "open" });

    this.#root.appendChild( document.createElement("style") ).textContent = vZomeViewerCSS;
    this.#root.appendChild( document.createElement("style") ).textContent = muiCSS;
    this.#container = document.createElement("div");
    this.#root.appendChild( this.#container );

    const worker = createWorker();
    worker .subscribe( {
      onWorkerError: () => {},
      onWorkerMessage: data => {
        switch ( data.type ) {

          case 'SCENE_RENDERED':
            this .dispatchEvent( new Event( 'vzome-design-rendered' ) );
            break;
        
          case 'ALERT_RAISED':
            this .dispatchEvent( new Event( 'vzome-design-failed' ) );
            break;
        
          default:
            break;
        }
      }
    } );
    this.#store = createWorkerStore( worker );

    this.#config = { preview: true, showScenes: false };

    if ( this.hasAttribute( 'show-scenes' ) ) {
      const showScenes = this.getAttribute( 'show-scenes' ) === 'true';
      this.#config = { ...this.#config, showScenes };
    }

    if ( this.hasAttribute( 'src' ) ) {
      const url = this.getAttribute( 'src' );
      if ( ! url.endsWith( ".vZome" ) ) {
        // This is the only case in which we don't resolve the promise with text,
        //  since there is no point in allowing download of non-vZome text.
        alert( `Unrecognized file name: ${url}` );
      }
      else
        this.#url = new URL( url, window.location ) .toString();
        // Get the fetch started by the worker before we load the dynamic module below,
        //  which is pretty big.
        this.#store.dispatch( fetchDesign( this.#url, this.#config ) );
    }
  }

  connectedCallback()
  {
    import( '../ui/viewer/index.jsx' )
      .then( module => {
        this.#reactElement = module.renderViewer( this.#store, this.#container, this.#url, this.#config );
      })
  }

  #reactElement = null;
  get reactElement()
  {
    return this.#reactElement;
  }

  static get observedAttributes()
  {
    return [ "src", "show-scenes" ];
  }

  attributeChangedCallback( attributeName, _oldValue, _newValue )
  {
    switch (attributeName) {

    case "src":
      const newUrl = new URL( _newValue, window.location ) .toString();
      if ( newUrl !== this.#url ) {
        this.#url = newUrl;
        this.#store.dispatch( fetchDesign( this.#url, this.#config ) );
      }
      break;

    case "show-scenes":
      const showScenes = _newValue === 'true';
      if ( showScenes !== this.#config.showScenes ) {
        this.#config = { ...this.#config, showScenes };
        this.#store.dispatch( fetchDesign( this.#url, this.#config ) );
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

  set showScenes( newValue )
  {
    if ( newValue === null ) {
      this.removeAttribute( "show-scenes" );
    } else {
      this.setAttribute( "show-scenes", newValue );
    }
  }

  get showScenes()
  {
    return this.getAttribute( "show-scenes" );
  }
}

customElements.define( "vzome-viewer", VZomeViewer );
