
import { vZomeViewerCSS } from "./vzome-viewer.css";

import { createWorker } from '../viewer/context/worker.jsx';
import { fetchDesign, selectScene, decodeEntities } from "../viewer/util/actions.js";

export class VZomeViewer extends HTMLElement
{
  #root;
  #container;
  #workerclient;
  #config;
  #reactive;
  #urlChanged;
  #sceneChanged;

  constructor()
  {
    super();
    this.#root = this.attachShadow({ mode: "open" });

    this.#root.appendChild( document.createElement("style") ).textContent = vZomeViewerCSS;
    this.#container = document.createElement("div");
    this.#root.appendChild( this.#container );

    this.#workerclient = createWorker();
    this.#workerclient .subscribe( {
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

    this.#config = { preview: true, showScenes: false, camera: true, lighting: true, design: true, };

    this.#urlChanged = true;
    this.#sceneChanged = true;
    this.#reactive = true;
    if ( this.hasAttribute( 'reactive' ) ) {
      this.#reactive = this.getAttribute( 'reactive' ) !== 'false';
    }

    if ( this.hasAttribute( 'show-scenes' ) ) {
      const showScenes = this.getAttribute( 'show-scenes' ) === 'true';
      this.#config = { ...this.#config, showScenes };
    }

    if ( this.hasAttribute( 'scene' ) ) {
      const sceneTitle = this.getAttribute( 'scene' );
      this.#config = { ...this.#config, sceneTitle };
    }

    if ( this.hasAttribute( 'src' ) ) {
      let url = this.getAttribute( 'src' );
      if ( ! url.endsWith( ".vZome" ) ) {
        // This is the only case in which we don't resolve the promise with text,
        //  since there is no point in allowing download of non-vZome text.
        alert( `Unrecognized file name: ${url}` );
      }
      else
        url = new URL( url, window.location ) .toString();
        this.#config = { ...this.#config, url };
        // Get the fetch started by the worker before we load the dynamic module below,
        //  which is pretty big.
        this.update();
    }
  }

  update( loadFlags={} )
  {
    const { camera=true, lighting=true, design=true } = loadFlags;
    const load = { camera, lighting, design };
    const config = { ...this.#config, load };
    if ( this.#urlChanged ) {
      this.#workerclient.postMessage( fetchDesign( this.#config.url, config ) );
      this.#urlChanged = false;
    } else if ( this.#sceneChanged ) {
      this.#workerclient.postMessage( selectScene( this.#config.sceneTitle, load ) );
    }
  }

  connectedCallback()
  {
    import( '../viewer/index.jsx' )
      .then( module => {
        module.renderViewer( this.#workerclient, this.#container, this.#config );
      });
  }

  static get observedAttributes()
  {
    return [ "src", "show-scenes", "scene", "load-camera", "reactive" ];
  }

  attributeChangedCallback( attributeName, _oldValue, _newValue )
  {
    switch (attributeName) {

    case "src":
      const newUrl = new URL( _newValue, window.location ) .toString();
      if ( newUrl !== this.#config.url ) {
        this.#config.url = newUrl;
        this.#urlChanged = true;
        if ( this.#reactive )
          this.update();
      }
      break;
  
    case "scene":
      if ( _newValue !== this.#config.sceneTitle ) {
        this.#config = { ...this.#config, sceneTitle: _newValue };
        this.#sceneChanged = true;
        // TODO: control the config prop on the viewer component, so the scenes menu behaves right
        if ( this.#reactive )
          this.update();
      }
      break;
  
    case "show-scenes":
      const showScenes = _newValue === 'true';
      this.#config = { ...this.#config, showScenes };
      break;
  
    case "reactive":
      this.#reactive = _newValue === 'true';
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

  set reactive( value )
  {
    this .setAttribute( "reactive", value );
  }

  get reactive()
  {
    return this .getAttribute( "reactive" );
  }

  // These were briefly supported, so I don't want to break anyone's client code.
  //   We don't support the behavior any more.
  set loadCamera( newValue )
  {
    console.log( 'loadCamera is no longer supported.' );
  }
  get loadCamera()
  {
    console.log( 'loadCamera is no longer supported.' );
    return undefined;
  }
}

customElements.define( "vzome-viewer", VZomeViewer );
