import { createSignal } from "solid-js";
import { render } from "solid-js/web";

import { Link } from "@kobalte/core/link";

import { instructionsCSS } from "./zometool.css.js";

const debug = false;

const product_catalog_url = 'https://zometool.github.io/vzome-sharing/metadata/zometool-products.json';

const fetchJSON = url => fetch( url ) .then( response => response.text() ) .then( text => JSON.parse( text ) );
const productsPromise = fetchJSON( product_catalog_url );

const findProducts = ( bom, products ) =>
{
  const productCoversBom = ( product, bom ) =>
    bom .reduce( (covers, part) => covers && product.bom[ part.partNum ] >= part.count, true );

  const result = [];
  Object.entries( products ) .map( ([ code, product ]) => {
    if ( product.url && productCoversBom( product, bom ) ) // ignore products without a URL
      result .push( { name: product.label, url: product.url } );
  } );
  return result;
}

const productUrl = url => new URL( url, 'https://zometool.com' );

const ZometoolProducts = props =>
{
  const [ matchingProducts, setMatchingProducts ] = createSignal( [] );

  productsPromise
    .then( ( products ) => {
      setMatchingProducts( findProducts( props.bom, products ) );
    } );

  return (
    <div class='zometool-products-table'>
      <ul>
        <For each={matchingProducts()} >
          { ( { name, url } ) =>
            <li class='matched-product'>
              <Link class="link" href={productUrl(url)} target="_blank" rel="noopener">{name}</Link>
            </li>
          }
        </For>
      </ul>
    </div>
  );
}

const renderComponent = ( container, bom ) =>
  {
    const bindComponent = () =>
    {
      return (
        <ZometoolProducts bom={bom}>
        </ZometoolProducts>
      );
    }
  
    // Apply external override styles to the shadow dom
    // const linkElem = document.createElement("link");
    // linkElem .setAttribute("rel", "stylesheet");
    // linkElem .setAttribute("href", "./zometool-styles.css");
    // container .appendChild( linkElem );
  
    render( bindComponent, container );
  }

// TODO: this is almost identical to ZometoolPartsElement... make a superclass
export class ZometoolProductsElement extends HTMLElement
{
  #instructionsId;
  #instructions;
  #bomLoaded;

  constructor()
  {
    super();

    debug && console.log( 'ZometoolProductsElement constructed' );
  }

  connectedCallback()
  {
    if ( !! this.#instructionsId ) {
      this.#instructions = document .querySelector( `#${this.#instructionsId}` );
      if ( ! this.#instructions ) {
        console.error( `No zometool-instructions with id "${this.#instructionsId}" found.` );
      } else if ( this.#instructions .getParts === undefined ) {
        console.error( `Element with id "${this.#instructionsId}" is not a zometool-instructions.` );
        return;
      }
    }
    if ( ! this.#instructions ) {
      this.#instructions = document .querySelector( 'zometool-instructions' );
    }
    if ( ! this.#instructions ) {
      console.error( `No zometool-instructions found.` );
      return;
    }

    this.#instructions .addEventListener( "zometool-instructions-loaded", (e) => {
      if ( this.#bomLoaded )
        return;

      this.#bomLoaded = true;
      debug && console.log( JSON.stringify( e.detail ) );

      this .appendChild( document.createElement("style") ).textContent = instructionsCSS;
      const container = document.createElement("div");
      container .classList .add( 'zometool-products-container' );
      this .appendChild( container );
  
      renderComponent( container, e.detail )
    } );

    debug && console.log( 'ZometoolProductsElement connected' );
  }

  static get observedAttributes()
  {
    return [ "instructions" ];
  }

  attributeChangedCallback( attributeName, _oldValue, _newValue )
  {
    debug && console.log( 'ZometoolProductsElement attribute changed' );
    switch (attributeName) {

    case "instructions":
      this.#instructionsId = _newValue;
      break;
    }
  }
}

