
import { vZomeViewerCSS } from "./vzome-viewer.css";

import { fetchDesign, createWorker, createWorkerStore, selectScene } from '../workerClient/index.js';
import { decodeEntities } from "../workerClient/actions";

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
    this.#container = document.createElement("div");
    this.#root.appendChild( this.#container );

    const worker = createWorker();
    worker .subscribe( {
      onWorkerError: () => {},
      onWorkerMessage: data => {
        switch ( data.type ) {

          case 'SCENES_DISCOVERED':
            const titles = data.payload .map( (scene,i) =>  scene.title ? decodeEntities( scene.title ) : `#${i}` );
            this .dispatchEvent( new CustomEvent( 'vzome-scenes-discovered', { detail: titles } ) );
            break;

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

    if ( this.hasAttribute( 'scene' ) ) {
      const sceneTitle = this.getAttribute( 'scene' );
      this.#config = { ...this.#config, sceneTitle };
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
        this.#store.postMessage( fetchDesign( this.#url, this.#config ) );
    }
  }

  connectedCallback()
  {
    import( '../viewer/solid/index.jsx' )
      .then( module => {
        module.renderViewer( this.#store, this.#container, this.#url, this.#config );
      })
  }

  static get observedAttributes()
  {
    return [ "src", "show-scenes", "scene" ];
  }

  attributeChangedCallback( attributeName, _oldValue, _newValue )
  {
    switch (attributeName) {

    case "src":
      const newUrl = new URL( _newValue, window.location ) .toString();
      if ( newUrl !== this.#url ) {
        this.#url = newUrl;
        this.#store.postMessage( fetchDesign( this.#url, this.#config ) );
      }
      break;

    case "scene":
      if ( _newValue !== this.#config.sceneTitle ) {
        this.#config = { ...this.#config, sceneTitle: _newValue };
        // TODO: control the config prop on the viewer component, so the scenes menu behaves right
        this.#store.postMessage( selectScene( _newValue ) );
      }
      break;
  
    case "show-scenes":
      const showScenes = _newValue === 'true';
      if ( showScenes !== this.#config.showScenes ) {
        this.#config = { ...this.#config, showScenes };
        this.#store.postMessage( fetchDesign( this.#url, this.#config ) );
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

  set scene( newScene )
  {
    if (newScene === null) {
      this.removeAttribute("scene");
    } else {
      this.setAttribute("scene", newScene);
    }
  }

  get scene()
  {
    return this.getAttribute("scene");
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
