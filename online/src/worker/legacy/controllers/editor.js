import { java } from "../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { com } from '../core-java.js';
import { getFieldNames, getFieldLabel } from "../core.js";
import { JsProperties } from '../jsweet2js.js';
import { PickingController } from './picking.js';
import { BuildPlaneController } from './buildplane.js';

export class EditorController extends com.vzome.desktop.controller.DefaultController
{
  constructor(design, clientEvents) {
    super();
    this.design = design;
    this.clientEvents = clientEvents;
    this.symmetries = {};
    this.symmController = null;
  }

  initialize( renderingChanges )
  {
    const { orbitSource, renderedModel, toolsModel, bookmarkFactory, history, symmetrySystems,
      fieldApp, legacyField, editor, editContext } = this.design;

    this.setErrorChannel({
      reportError: (message, args) => {
        console.log('controller error:', message, args);
        if (message === com.vzome.desktop.api.Controller.UNKNOWN_ERROR_CODE) {
          const ex = args[0];
          clientEvents.errorReported(ex.message);
        }
        else
          clientEvents.errorReported(message);
      },
    });
    
    // This has similar function to the Java equivalent, but a very different mechanism
    const pickingController = new PickingController(renderedModel);
    this.addSubController('picking', pickingController);

    // This has no desktop equivalent
    const buildPlaneController = new BuildPlaneController(renderedModel, orbitSource);
    this.addSubController('buildPlane', buildPlaneController);

    const polytopesController = new com.vzome.desktop.controller.PolytopesController( editor, editContext );
    this.addSubController('polytopes', polytopesController);

    const undoRedoController = new com.vzome.desktop.controller.UndoRedoController(history);
    this.addSubController('undoRedo', undoRedoController);

    const bookmarkController = new com.vzome.desktop.controller.ToolFactoryController(bookmarkFactory);
    this.addSubController('bookmark', bookmarkController);

    const quaternionController = new com.vzome.desktop.controller.VectorController( legacyField .basisVector( 4, com.vzome.core.algebra.AlgebraicVector.W4 ) );
    this .addSubController( "quaternion", quaternionController );

    const strutBuilder = new com.vzome.desktop.controller.StrutBuilderController( editContext, legacyField )
      .withGraphicalViews( true )   // TODO use preset
      .withShowStrutScales( true ); // TODO use preset
      this.addSubController('strutBuilder', strutBuilder);

    const docSymmetrySystem = editor .getSymmetrySystem();
      
    // In case we fall through without matching (due to a legacy system in the doc, no longer UI-exposed)
    let docLabel = fieldApp .getDefaultSymmetryPerspective() .getLabel();

    const symmPerspectives = fieldApp.getSymmetryPerspectives().iterator();
    while ( symmPerspectives.hasNext() ) {
      const symper = symmPerspectives.next();
      const label = symper .getLabel(); // this can be different from name
      if ( !!label ) { // null label means this symmetry is not to expose in the UI
        let name = symper .getSymmetry() .getName();
        const system = this.design .getSymmetrySystem( name );
        if ( system.getName() === docSymmetrySystem.getName() ) {
          docLabel = label; // for setSymmetryController(), later
        }
        const symmController = new com.vzome.desktop.controller.SymmetryController( label, strutBuilder, system, renderedModel );
        strutBuilder .addSubController( `symmetry.${label}`, symmController );
        this.symmetries[ label ] = symmController;
      }
    }
  
    const toolsController = new com.vzome.desktop.controller.ToolsController(toolsModel);
    toolsController.addTool(toolsModel.get("bookmark.builtin/ball at origin"));
    strutBuilder.addSubController('tools', toolsController);

    // enable shape changes
    renderedModel .addListener( {
      shapesChanged: () => false, // this allows RenderedModel.setShapes() to not fail and re-render all the parts
      manifestationAdded: () => {},  // We don't need these incremental changes, since we'll batch render after
      manifestationRemoved: () => {},
      colorChanged: () => {},
      glowChanged: () => {},
    } );

    return docLabel;
  }

  getProperty(name) {
    switch (name) {

      case "symmetry":
        return this.symmController.getProperty( 'label' );

      case "single.orbit":
      case "disable.known.directions":
        return super.getProperty(name);

      default:
        if ( name.startsWith( "field.label." ) ) {
          return getFieldLabel( name .substring( "field.label.".length ) );
        }
        if ( ! name.startsWith( "defaultShapes." ) )
          console.log("EditorController getProperty fall through: ", name);
        return super.getProperty(name);
    }
  }

  getCommandList(name) {
    const { symmetrySystems, legacyField } = this.design;
    switch (name) {

      case "symmetryPerspectives":
        return Object.keys( this.symmetries );

      case "field.irrationals":
        return com.vzome.core.algebra.AlgebraicField .getIrrationals( legacyField );

      case "field.multipliers":
        return com.vzome.core.algebra.AlgebraicField .getMultipliers( legacyField );

      case "fields":
        return getFieldNames();

      default:
        console.log("EditorController getCommandList fall through: ", name);
        return super.getCommandList(name);
    }
  }

  setSymmetryController( label )
  {
    this.symmController = this.symmetries[ label ];

    let symmetrySystem = this.symmController .getOrbitSource();
    this.design .setSymmetrySystem( symmetrySystem.getName() );

    // TODO: test this change with buildPlane
    this.getSubController( 'buildPlane' ) .setOrbitSource( symmetrySystem );

    this .getSubController( 'strutBuilder' ) .setSymmetryController( this.symmController );

    // TODO: update the PartsController
    this.firePropertyChange( 'symmetry', '', label );
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
          this.setSymmetryController( name );
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
