import { JsProperties } from '../jsweet2js.js';

export class ControllerWrapper{
  
  constructor(path, name, controller, clientEvents) {
    this.__path = path;
    this.__name = name;
    this.controller = controller;
    this.__clientEvents = clientEvents;
    this.__changeTables = {};
    this.children = {};
    this.macro = [];
  }

  createChildWrapper(name, controller) {
    const controllerPath = this.__path ? this.__path + ':' + name : name;
    const child = new ControllerWrapper(controllerPath, name, controller, this.__clientEvents);
    this.children[name] = child;

    const thisController = this.controller;
    // thisController .addPropertyListener( {
    //   propertyChange( pce )
    //   {
    //     const propName = pce .getPropertyName();
    //     if ( name === propName ) {
    //       child .controllerChange( thisController );
    //     }
    //   }
    // } );
    return child;
  }

  getSubControllerByNames(names) {
    if (!names || names.length === 0) {
      return this;
    }
    else {
      const name = names[0];
      let child = this.children[name];
      if (!child) {
        const controller = this.controller.getSubController(name);
        if (!controller) {
          console.log(`No subcontroller for path ${this.__path} ${name}`);
          return undefined;
        }
        child = this.createChildWrapper(name, controller);
      }
      return child.getSubControllerByNames(names.slice(1));
    }
  }

  getControllerByPath( controllerPath ) {
    const controllerNames = controllerPath ? controllerPath.split(':') : [];
    return this.getSubControllerByNames(controllerNames);
  }

  fetchAndFirePropertyChange(propName, isList) {
    const value = isList ?
      this.controller.getCommandList(propName)
      : this.controller.getProperty(propName);
    this.__clientEvents.propertyChanged(this.__path, propName, value);
  }

  // This is only ever called on the root controller
  registerPropertyInterest(controllerPath, propName, changeName, isList) {
    const wrapper = this.getControllerByPath(controllerPath);

    if (!wrapper)
      return; // Happens regularly on startup, when some properties are still undefined

    wrapper.registerPropertyInterest2(propName, changeName, isList);

    // In case the initial value is never going to change
    wrapper.fetchAndFirePropertyChange(propName, isList);
  }

  registerPropertyInterest2(propName, changeName, isList) {
    if (Object.entries(this.__changeTables).length === 0) {
      // First registered interest in any property for this controller
      this.controller.addPropertyListener(this);
    }
    if (!this.__changeTables[changeName]) {
      // First registered interest in this change name
      this.__changeTables[changeName] = { [propName]: isList };
    }
    else {
      const change = this.__changeTables[changeName];
      if (change[propName] === undefined) {
        // First registered interest in this property
        this.__changeTables[changeName] = { ...change, [propName]: isList };
      }
    }
  }

  setProperty( controllerPath, name, value )
  {
    const wrapper = this.getControllerByPath(controllerPath);
    wrapper.controller.setProperty( name, value );
  }

  doAction( controllerPath, action, parameters = {} )
  {
    // Macro recording
    // if ( action === 'undoAll' ) {
    //   if ( !!this.macro.length )
    //     console.log( JSON.stringify( this.macro, null, 2 ) );
    //   this.macro = [];
    // } else {
    //   this.macro .push( { controllerPath, action, parameters } );
    // }

    const wrapper = this.getControllerByPath(controllerPath);
    if (parameters && Object.keys(parameters).length !== 0)
      wrapper.controller.paramActionPerformed(null, action, new JsProperties(parameters));

    else
      wrapper.controller.actionPerformed(null, action);

    // TODO: this is pretty heavy-handed, sending the whole scene after every edit.
    //  That said, it may perform better than the incremental approach.
    this.renderScene();
  }

  controllerChange(parent) {
    this.controller = parent.getSubController(this.__name);
    // refresh all properties
    for (const changes of Object.values(this.__changeTables)) {
      for (const [propName, isList] of Object.entries(changes)) {
        this.fetchAndFirePropertyChange(propName, isList);
      }
    }
    // force re-fetch of all subcontrollers
    this.children = {};
  }

  propertyChange(pce) {
    const name = pce.getPropertyName();

    for (const [cName, changes] of Object.entries(this.__changeTables)) {
      if (name === cName) {
        // This is the reason for this.__changeTables, to "fan out" property changes
        for (const [propName, isList] of Object.entries(changes)) {
          this.fetchAndFirePropertyChange(propName, isList);
        }
      }
    }
  }
}
