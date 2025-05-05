
import { vZomeViewerCSS } from "./vzome-viewer.css";

import { decodeEntities } from "../viewer/util/actions.js";
import { VZomeViewerFirstButton, VZomeViewerLastButton, VZomeViewerNextButton, VZomeViewerPrevButton } from "./index-buttons.js";
import { createDefaultCameraStore } from "../viewer/context/camera.jsx";

const debug = false;
class VZomeViewer extends HTMLElement
{
  #root;
  #container;
  #config;
  #reactive;
  #urlChanged;
  #sceneChanged;
  #moduleLoaded;
  #updateCalled;
  #loadFlags;

  #indexed;
  #sceneIndices;
  #sceneTitles;
  #sceneIndex;
  #cameraStore;

  #viewerClient;

  constructor()
  {
    super();
    this.#root = this.attachShadow({ mode: "open" });

    this.#root.appendChild( document.createElement("style") ).textContent = vZomeViewerCSS;
    this.#container = document.createElement("div");
    this.#root.appendChild( this.#container );

    this.#cameraStore = createDefaultCameraStore();

    this.#config = {
      preview:         true,
      showScenes:      'none',
      camera:          true,
      lighting:        true,
      design:          true,
      labels:          false,
      showPerspective: true,
      download:        true,
      useSpinner:      false,
    };

    this.#indexed = false;
    this.#urlChanged = true;
    this.#sceneChanged = true;
    this.#reactive = true;
    this.#moduleLoaded = false;
    this.#updateCalled = false;
    this.#loadFlags = {};
    debug && console.log( 'custom element constructed' );
  }

  selectScene( index, loadFlags={} )
  {
    debug && console.log( 'User called selectScene()' );
    this.#loadFlags = loadFlags;
    if ( ! this.#indexed ) {
      console.log( 'This selectScene call ignored; the viewer is not indexed.  Set indexed to true if you want to use selectScene.' );
      return;
    }
    if ( ! this.#sceneIndices ) {
      console.log( 'This selectScene call ignored; no scenes were discovered.' );
      return;
    }
    this.#sceneIndex = ( index < 0 )? this.#sceneIndices.length-1 : 0;
    this.#config = { ...this.#config, sceneTitle: this.#sceneIndices[ this.#sceneIndex ] };
    this.#sceneChanged = true;
    this.#triggerWorker();
  }

  previousScene( loadFlags={} )
  {
    debug && console.log( 'User called previousScene()' );
    this.#loadFlags = loadFlags;
    if ( ! this.#indexed ) {
      console.log( 'This previousScene call ignored; the viewer is not indexed.  Set indexed to true if you want to use previousScene.' );
      return;
    }
    if ( ! this.#sceneIndices ) {
      console.log( 'This previousScene call ignored; no scenes were discovered.' );
      return;
    }
    this.#sceneIndex = Math.max( this.#sceneIndex - 1, 0 );
    this.#config = { ...this.#config, sceneTitle: this.#sceneIndices[ this.#sceneIndex ] };
    this.#sceneChanged = true;
    this.#triggerWorker();
  }

  nextScene( loadFlags={} )
  {
    debug && console.log( 'User called nextScene()' );
    this.#loadFlags = loadFlags;
    if ( ! this.#indexed ) {
      console.log( 'This nextScene call ignored; the viewer is not indexed.  Set indexed to true if you want to use nextScene.' );
      return;
    }
    if ( ! this.#sceneIndices ) {
      console.log( 'This nextScene call ignored; no scenes were discovered.' );
      return;
    }
    this.#sceneIndex = Math.min( this.#sceneIndex + 1, this.#sceneIndices.length-1 );
    this.#config = { ...this.#config, sceneTitle: this.#sceneIndices[ this.#sceneIndex ] };
    this.#sceneChanged = true;
    this.#triggerWorker();
  }

  update( loadFlags={} )
  {
    debug && console.log( 'User called update()' );
    this.#loadFlags = loadFlags;
    if ( this.#reactive ) {
      console.log( 'This update call ignored; the viewer is reactive to attribute changes.  Set reactive to false if you want programmatic control of the viewer.' );
      return;
    }
    this.#updateCalled = true;
    this.#triggerWorker();
  }

  #triggerWorker()
  {
    if ( ! this.#moduleLoaded ) {
      // User code called update() in initialization or an event handler, before the dynamic import has completed
      debug && console.log( 'update ignored; module not loaded' );
      return;
    }
    const { camera=true, lighting=true, design=true } = this.#loadFlags;
    const load = { camera, lighting, design };
    const config = { ...this.#config, load };
    if ( this.#config.url && this.#urlChanged ) {
      debug && console.log( 'sending fetchDesign to worker' );
      this.#viewerClient .requestDesign( this.#config.url, config );
      this.#urlChanged = false;
    } else if ( this.#sceneChanged ) {
      debug && console.log( 'sending selectScene to worker' );
      this.#viewerClient .requestScene( this.#config.sceneTitle, load );
    }
  }

  loadFromText( name, contents )
  {
    if ( ! this.#moduleLoaded ) {
      // User code called update() in initialization or an event handler, before the dynamic import has completed
      debug && console.log( 'loadFromText ignored; module not loaded' );
      return;
    }
    this.#viewerClient .resetScenes();
    this.#viewerClient .openText( name, contents );
  }

  connectedCallback()
  {
    debug && console.log( 'custom element connected' );
    import( '../viewer/index.jsx' )
      .then( module => {
        debug && console.log( 'dynamic module loaded' );
        module.renderViewer( this.#container, this.#config, this.#cameraStore, viewer => this.#viewerClient = viewer );
        this.#moduleLoaded = true;
        
        this.#viewerClient.subscribeFor( 'ALERT_RAISED', () => {
          this .dispatchEvent( new CustomEvent( 'vzome-design-failed' ) );
        } ); 
        this.#viewerClient.subscribeFor( 'SCENE_RENDERED', () => {
          let scene = {};
          if ( this.#indexed && !! this.#sceneTitles )
            scene = { index: this.#sceneIndex, title: this.#sceneTitles[ this.#sceneIndex ] };
          this .dispatchEvent( new CustomEvent( 'vzome-design-rendered', { detail: scene } ) );
        } );
        this.#viewerClient.subscribeFor( 'SCENES_DISCOVERED', ( payload ) => {
          this.#sceneIndices = payload .map( (scene,i) => `#${i}` ) .slice( 1 ); // strip the default scene
          this.#sceneTitles = payload .map( (scene,i) => scene.title ? decodeEntities( scene.title ) : `#${i}` );
          if ( this.#indexed )
            this.#sceneTitles = this.#sceneTitles .slice( 1 );
          this .dispatchEvent( new CustomEvent( 'vzome-scenes-discovered', { detail: this.#sceneTitles } ) );

          this .dispatchEvent( new CustomEvent( 'vzome-scenes', { detail: payload .slice( 1 ) } ) );
        } );
  

        // We used to do this in the constructor, after worker creation, for better responsiveness.
        // However, that causes a race condition on slow networks -- the model loads before the viewer
        //   is ready for the results, leaving a blank canvas.
        // User updates could also cause this, so now those are prevented before this moment.
        if ( this.#reactive || this.#updateCalled || this.#indexed )
          // #reactive means that there is no active control, so we need an initial #triggerWorker.
          // #updateCalled means that the user has called update() on a controlled component.
          this.#triggerWorker();
      });
  }

  static get observedAttributes()
  {
    return [ "src", "show-scenes", "scene", "load-camera", "reactive", "labels", "show-perspective", "tween-duration", "indexed", "download", "progress" ];
  }

  // This callback can happen *before* connectedCallback()!
  attributeChangedCallback( attributeName, _oldValue, _newValue )
  {
    debug && console.log( 'custom element attribute changed' );
    switch (attributeName) {

    case "src":
      const newUrl = new URL( _newValue, window.location ) .toString();
      if ( newUrl !== this.#config.url ) {
        this.#config.url = newUrl;
        this.#urlChanged = true;
        if ( this.#reactive )
          this.#triggerWorker();
      }
      break;
  
    case "scene":
      if ( this.#indexed )
        break;
      if ( _newValue !== this.#config.sceneTitle ) {
        this.#config = { ...this.#config, sceneTitle: _newValue };
        this.#sceneChanged = true;
        // TODO: control the config prop on the viewer component, so the scenes menu behaves right
        if ( this.#reactive )
          this.#triggerWorker();
      }
      break;
  
    case "show-scenes":
      if ( this.#indexed )
        break;
      const showScenes = _newValue;
      this.#config = { ...this.#config, showScenes };
      break;
  
    case "labels":
      const labels = _newValue === 'true';
      this.#config = { ...this.#config, labels };
      break;

    case "download":
      const download = _newValue === 'true';
      this.#config = { ...this.#config, download };
      break;

    case "progress":
      const useSpinner = _newValue === 'true';
      this.#config = { ...this.#config, useSpinner };
      break;
  
    case "show-perspective":
      const showPerspective = _newValue === 'true';
      this.#config = { ...this.#config, showPerspective };
      break;
  
    case "tween-duration":
      const duration = _newValue;
      const [ state, setState ] = this.#cameraStore;
      setState( 'tweening', 'duration', duration );
    break;
    
    case "reactive":
      if ( this.#indexed )
        break;
      this.#reactive = _newValue === 'true';
      break;
  
    case "indexed":
      if ( _newValue !== 'true' )
        break;
      this.#indexed = true;
      this.#reactive = false;
      this.#sceneIndex = 0;
      this.#config = { ...this.#config, showScenes: false, sceneTitle: '#1' };
      this.#sceneChanged = true;
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

  set labels( newValue )
  {
    if ( newValue === null ) {
      this.removeAttribute( "labels" );
    } else {
      this.setAttribute( "labels", newValue );
    }
  }

  get labels()
  {
    return this.getAttribute( "labels" );
  }

  set download( newValue )
  {
    if ( newValue === null ) {
      this.removeAttribute( "download" );
    } else {
      this.setAttribute( "download", newValue );
    }
  }

  get download()
  {
    return this.getAttribute( "download" );
  }

  set progress( newValue )
  {
    if ( newValue === null ) {
      this.removeAttribute( "progress" );
    } else {
      this.setAttribute( "progress", newValue );
    }
  }

  get progress()
  {
    return this.getAttribute( "progress" );
  }

  set showPerspective( newValue )
  {
    if ( newValue === null ) {
      this.removeAttribute( "show-perspective" );
    } else {
      this.setAttribute( "show-perspective", newValue );
    }
  }

  get showPerspective()
  {
    return this.getAttribute( "show-perspective" );
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

customElements .define( "vzome-viewer-end",      VZomeViewerLastButton );
customElements .define( "vzome-viewer-next",     VZomeViewerNextButton );
customElements .define( "vzome-viewer-previous", VZomeViewerPrevButton );
customElements .define( "vzome-viewer-start",    VZomeViewerFirstButton );
