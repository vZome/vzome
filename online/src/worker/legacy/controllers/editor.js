import { java } from "../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { com } from '../core-java.js';
import { JsProperties } from '../jsweet2js.js';

export class EditorController extends com.vzome.desktop.controller.DefaultController {
  constructor(design, clientEvents) {
    super();
    this.design = design;
    this.clientEvents = clientEvents;
  }

  getProperty(name) {
    switch (name) {

      case "symmetry":
        return this.design.getSymmetrySystem().getName();

      default:
        console.log("EditorController getProperty fall through: ", name);
        return super.getProperty(name);
    }
  }

  getCommandList(name) {
    switch (name) {

      case "symmetryPerspectives":
        const { symmetrySystems } = this.design;
        return Object.keys(symmetrySystems);

      default:
        console.log("EditorController getCommandList fall through: ", name);
        return super.getCommandList(name);
    }
  }

  // There are no implementors of doParamAction() in the Java code, except for
  //   DefaultController, which just propagates the calls up to the next controller.
  doParamAction(action, params) {
    switch (action) {

      case "exportText":
        const format = params.getConfig().format; // TODO remove the STL hardcoding!

        switch (format) {

          case 'stl': {
            const exporter = new com.vzome.core.exporters.StlExporter();
            const out = new java.io.StringWriter();
            exporter.exportGeometry(this.design.renderedModel, null, out, 500, 800);
            this.clientEvents.textExported(action, out.toString()); // returning the action for Promise correlation on the client
            return;
          }

          default: // vZome
            const text = this.design.serializeToDom().toIndentedString("");
            this.clientEvents.textExported(action, text);
            return;
        }
        break;

      default:
        if (action.startsWith("setSymmetry.")) {
          const name = action.replace(/^setSymmetry\./, '');
          this.design.setSymmetrySystem(name);

          // TODO: test this change with buildPlane
          const orbitSource = this.design.getSymmetrySystem();
          this.getSubController('buildPlane').setOrbitSource(orbitSource); // Does this fire sufficient properties?


          // TODO: update the PartsController
          this.firePropertyChange('symmetry', '', name);
        }

        else
          this.design.configureAndPerformEdit(action, params && params.getConfig());

        // For the classic client app, this is redundant, since it can use the exportText action,
        //   but I still need it for React-based clients.
        const text = this.design.serializeToDom().toIndentedString("");
        this.clientEvents.designSerialized(text);
        break;
    }
  }

  doAction(action) {
    this.doParamAction(action, new JsProperties());
  }
}
