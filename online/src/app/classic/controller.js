
import { doControllerAction, newDesign, requestControllerList, requestControllerProperty } from '../../ui/viewer/store.js';
import { com } from '../../ui/legacy/desktop-java.js'

// This is a fairly rough-and-ready implementation of a vZome desktop Controller
// on top of the Redux store.  I implemented it in Javascript, rather than Java,
// because I can iterate without running JSweet, and frankly I feel I have more
// control and power to interact with Redux as required.

// If I were going to use this in support of the transpiled legacy Swing UI, it would
// need to have support for subcontrollers, and we'd end up with a tree of these
// hooked up and running under the UI, in the web context, wrapping the Redux store.

// In the end, this becomes pretty complicated, probably fragile, and *still* does
// not support the Swing UI completely.  This is mainly because the Swing UI expects
// a lot of synchronous results to getProperty() and getCommandList(), and the
// worker simply cannot do that.  In the Java world, those sync methods execute
// directly in the EDT, driving straight to the model layer for answers; here, the
// model layer is completely insulated by an async mechanism.  This could actually
// reduce the value of having the real controllers running on the worker side,
// since they are not proactively pushing state out through async PCEs as much as they need to.

export class WorkerController extends com.vzome.desktop.controller.DefaultController
{
  constructor( store )
  {
    super();
    this .properties = {};
    this .store = store;

    // Subscribe to the Redux store change notifications
    store .subscribe( this.handleStoreChange.bind( this ) );

    // Create a promise that resolves when the main controller
    // is ready on the worker side.
    this .workerPromise = new Promise( (resolve,reject) => {
      store .dispatch( newDesign() );
      this .resolveWorkerPromise = resolve;
    });
  }

  handleStoreChange()
  {
    const select = state => state.controller

    const resolve = this .resolveWorkerPromise;
    resolve( this .store );
    
    const state = select( this .store .getState() );

    const oldState = { ...this .properties };
    this .properties = { ...this.properties, ...state };
    for ( const [key, value] of Object.entries( state ) ) {
      const oldValue = oldState[ key ];
      if ( !oldValue ) {
        console.log( `WorkerController new property: ${key}` );
        this .properggties() .firePropertyChange( key, null, value );
      }
      else if ( !( oldValue === value ) ) {
        console.log( `WorkerController changed property: ${key}` );
        this .properggties() .firePropertyChange( key, oldValue, value );
      }
    }
  }

  getCommandList( listName )
  {
    const list = this .properties[ listName ];
    if ( list )
      return list;
    else {
      this .workerPromise .then( store => {
        store .dispatch( requestControllerList( '', listName ) );
      });
      return [];
    }
  }

  getProperty( name )
  {
    const prop = this .properties[ name ];
    if ( prop )
      return prop;
    else {
      this .workerPromise .then( store => {
        store .dispatch( requestControllerProperty( '', name ) );
      });
      return null;
    }
  }

  doAction( action )
  {
  }
}
WorkerController["__interfaces"] = ["com.vzome.desktop.api.Controller"];
