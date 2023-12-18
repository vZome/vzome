
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

    // I'd like to remove #workerclient, but I have to set up these subscriptions somehow...
    this.#workerclient = createWorker();
    this.#workerclient .subscribeFor( 'ALERT_RAISED', () => this .dispatchEvent( new Event( 'vzome-design-failed' ) ) );
    this.#workerclient .subscribeFor( 'SCENE_RENDERED', () => this .dispatchEvent( new Event( 'vzome-design-rendered' ) ) );
    this.#workerclient .subscribeFor( 'SCENES_DISCOVERED', payload => {
      const titles = payload .map( (scene,i) =>  scene.title ? decodeEntities( scene.title ) : `#${i}` );
      this .dispatchEvent( new CustomEvent( 'vzome-scenes-discovered', { detail: titles } ) );
    } );

    this.#config = { preview: true, showScenes: 'none', camera: true, lighting: true, design: true, };

    this.#urlChanged = true;
    this.#sceneChanged = true;
    this.#reactive = true;
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
        
        // We used to do this in the constructor, after worker creation, for better responsiveness.
        // However, that causes a race condition on slow networks -- the model loads before the viewer
        //   is ready for the results, leaving a blank canvas.
        this.update();
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
      const showScenes = _newValue;
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
