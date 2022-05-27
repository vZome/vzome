// babel workaround
import "regenerator-runtime/runtime";

import { vZomeViewerCSS } from "./vzome-viewer.css";

import { muiCSS } from "./mui-styles.css";

import { createWorkerStore } from '../ui/viewer/store.js';

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

    this.#store = createWorkerStore( this );

    this.#config = { preview: true };

    //  This part seems to be redundant, since the attributeChangedCallback() happens anyway.
    //
    // if ( this.hasAttribute( 'src' ) ) {
    //   const url = this.getAttribute( 'src' );
    //   if ( ! url.endsWith( ".vZome" ) ) {
    //     // This is the only case in which we don't resolve the promise with text,
    //     //  since there is no point in allowing download of non-vZome text.
    //     alert( `Unrecognized file name: ${url}` );
    //   }
    //   else
    //     this.#url = new URL( url, window.location ) .toString();
    //     // Get the fetch started by the worker before we load the dynamic module below,
    //     //  which is pretty big.  I really should encapsulate the message in a function!
    //     this.#store.dispatch( { type: 'URL_PROVIDED', payload: { url: this.#url, config: { preview: true } } } );
    // }
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
      this.#url = new URL( _newValue, window.location ) .toString();
      break;

    case "show-scenes":
      // "preview" means show a preview if you find one.  When "show-scenes" is true, the
      //   XML will have to be parsed, so a preview JSON is not desirable.
      this.#config = { preview: ( _newValue === 'false' ), ...this.#config };
      console.log( JSON.stringify( this.#config, null, 2 ) );
      break;
    }
    // TODO: this should be encapsulated in an API on the store
    this.#store.dispatch( { type: 'URL_PROVIDED', payload: { url: this.#url, config: this.#config } } );
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
