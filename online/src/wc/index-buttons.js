
const debug = true;

class VZomeViewerIndexButton extends HTMLButtonElement
{
  #next;
  #viewerId;
  #viewer;

  constructor( next=true )
  {
    self = super();
    this.#next = next;
  }

  connectedCallback()
  {
    if ( !! this.#viewerId ) {
      this.#viewer = document .querySelector( `#${this.#viewerId}` );
    }
    if ( ! this.#viewer ) {
      this.#viewer = document .querySelector( 'vzome-viewer' );
    }
    if ( !! this.#viewer ) {
      const loadParams = { camera: false };
      self .addEventListener( "click", () => this.#next? this.#viewer .nextScene( loadParams ) : this.#viewer .previousScene( loadParams ) );
    }
  }

  static get observedAttributes()
  {
    return [ "viewer" ];
  }

  attributeChangedCallback( attributeName, _oldValue, _newValue )
  {
    debug && console.log( 'VZomeViewerIndexButton attribute changed' );
    switch (attributeName) {

    case "viewer":
      this.#viewerId = _newValue;
      break;
    }
  }
}

export class VZomeViewerNextButton extends VZomeViewerIndexButton
{
  constructor()
  {
    super( true );
  }
}

export class VZomeViewerPrevButton extends VZomeViewerIndexButton
{
  constructor()
  {
    super( false );
  }
}