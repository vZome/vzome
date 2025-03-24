
import { com } from '../core-java.js';
import { java } from '../candies/j4ts-2.1.0-SNAPSHOT/bundle.js';
import { JsProperties } from '../jsweet2js.js';
import { PickingController } from './picking.js';
import { BuildPlaneController } from './buildplane.js';
import { modelToJS } from "../json.js";
import { export2d, export3d, export3dDocument } from "../exporters.js";
import { resolveBuildPlanes } from '../scenes.js';


export class EditorController extends com.vzome.desktop.controller.DefaultController
{
  constructor( legacyDesign, core, clientEvents ) {
    super();
    this.legacyDesign = legacyDesign;
    this.core = core;
    this.clientEvents = clientEvents;
    this.symmetries = {};
    this.symmController = null;
    this.changeCount = 0;
  }

  reportError( message, args ) {
    console.log('controller error:', message, args);
    if (message === com.vzome.desktop.api.Controller.UNKNOWN_ERROR_CODE) {
      const ex = args[0];
      this.clientEvents .errorReported(ex.message);
    }
    else
      this.clientEvents .errorReported(message);
  }

  initialize()
  {
    const { renderedModel, toolsModel, bookmarkFactory, history,
      fieldApp, legacyField, editor, editContext } = this.legacyDesign;

    this.changeCount = this.legacyDesign.getChangeCount();

    this.setErrorChannel({
      reportError: ( message, args ) => this.reportError( message, args ),
    });
    
    // This has similar function to the Java equivalent, but a very different mechanism
    const pickingController = new PickingController(renderedModel);
    this.addSubController('picking', pickingController);

    // This has no desktop equivalent
    const buildPlaneController = new BuildPlaneController( this.legacyDesign, this.clientEvents );
    this.addSubController('buildPlane', buildPlaneController);

    const polytopesController = new com.vzome.desktop.controller.PolytopesController( editor, editContext );
    this.addSubController('polytopes', polytopesController);

    const undoRedoController = new com.vzome.desktop.controller.UndoRedoController(history);
    this.addSubController('undoRedo', undoRedoController);

    const bookmarkController = new com.vzome.desktop.controller.ToolFactoryController(bookmarkFactory);
    this.addSubController('bookmark', bookmarkController);

    const quaternionController = new com.vzome.desktop.controller.VectorController( legacyField .basisVector( 4, com.vzome.core.algebra.AlgebraicVector.W4 ) );
    this .addSubController( "quaternion", quaternionController );

    const measureController = new com.vzome.desktop.controller.MeasureController( editor, renderedModel );
    this .addSubController( "measure", measureController );

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
        const system = this.legacyDesign .getSymmetrySystem( name );
        if ( system.getName() === docSymmetrySystem.getName() ) {
          docLabel = label; // for setSymmetryController(), later
        }
        const symmController = new com.vzome.desktop.controller.SymmetryController( label, strutBuilder, system, renderedModel );
        strutBuilder .addSubController( `symmetry.${label}`, symmController );
        this .addSubController( `symmetry.${label}`, symmController );
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
      labelChanged: () => {},
      colorChanged: () => {},
      glowChanged: () => {},
    } );

    return docLabel;
  }

  getProperty(name) {
    switch (name) {

      case "edited": {
        return new Boolean( this.changeCount !== this.legacyDesign.getChangeCount() ) .toString();
      }

      case "symmetry":
        return this.symmController.getProperty( 'label' );
  
      case "single.orbit":
      case "disable.known.directions":
        return super.getProperty(name);

      default:
        if ( name.startsWith( "field.label." ) ) {
          return this.core.getFieldLabel( name .substring( "field.label.".length ) );
        }
        if ( ! name.startsWith( "defaultShapes." ) )
          console.log("EditorController getProperty fall through: ", name);
        return super.getProperty(name);
    }
  }

  getCommandList(name) {
    const { symmetrySystems, legacyField } = this.legacyDesign;
    switch (name) {

      case "symmetryPerspectives":
        return Object.keys( this.symmetries );

      case "field.irrationals":
        return com.vzome.core.algebra.AlgebraicField .getIrrationals( legacyField );

      case "field.multipliers":
        return com.vzome.core.algebra.AlgebraicField .getMultipliers( legacyField );

      case "fields":
        return this.core.getFieldNames();

      default:
        console.log("EditorController getCommandList fall through: ", name);
        return super.getCommandList(name);
    }
  }

  setSymmetryController( label )
  {
    this.symmController = this.symmetries[ label ];

    let symmetrySystem = this.symmController .getOrbitSource();
    this.legacyDesign .setSymmetrySystem( symmetrySystem.getName() );

    const planes = resolveBuildPlanes( symmetrySystem .buildPlanes );
    const { orientations, symmetry, permutations } = symmetrySystem;
    const scalars = [ symmetry .getField() .getAffineScalar() .evaluate() ]; //TODO get them all!
    const resourcePath = symmetrySystem .getModelResourcePath();
    this.clientEvents .symmetryChanged( { orientations, permutations, scalars, planes, resourcePath } );
    
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

      case "undoToManifestation": {
        const {  history, editor } = this.legacyDesign;
        const { picked } = params.getConfig();
        history .undoToManifestation( picked );
        editor .notifyListeners();
        break;
      }

      case "clearChanges": {
        this.changeCount = this.legacyDesign.getChangeCount();
        this.firePropertyChange( 'edited', '', 'false' );
        break;
      }

      case "usedOrbits": {
        const { renderedModel } = this.legacyDesign;
        const usedOrbits = new java.util.HashSet();
        const rMans = renderedModel .iterator();
        while ( rMans .hasNext() ) {
          const rm = rMans .next();
          const shape = rm .getShape();
          const orbit = shape .getOrbit();
          if ( orbit != null )
            usedOrbits .add( orbit );
        }
        this.symmController .availableController .doAction( "setNoDirections" );
        const orbits = usedOrbits .iterator();
        while ( orbits .hasNext() ) {
          const orbit = orbits .next();
          this.symmController .availableController .doAction( "enableDirection." + orbit .getName() );
        }
        break;
      }

      case "setBuildOrbitAndLength": {
        const { picked } = params.getConfig();
        const rm = picked .getRenderedObject();
        const length = rm .getStrutLength();
        const orbit = rm .getStrutOrbit();
        this.symmController .availableController .doAction( "enableDirection." + orbit .getName() );
        this.symmController .buildController .doAction( "setSingleDirection." + orbit .getName() );
        const lmodel = this.symmController .buildController .getSubController( "currentLength" );
        lmodel .setActualLength( length );
        break;
      }

      case "exportText":
        const { format, selection, camera, lighting, height=500, width=800,
                useShapes=true, drawOutlines=true, monochrome=false, showBackground=true, useLighting=true } = params.getConfig();
        let exported;

        switch (format) {

          case 'mesh':
          case 'cmesh': {
            const source = selection? this.legacyDesign .editor .selection : this.legacyDesign .editor .getRealizedModel();
            const mesh = modelToJS( source, format==='cmesh' );
            exported = JSON.stringify( mesh, null, 2 );
            break;
          }

          case 'pdf':
          case 'ps':
          case 'svg': {
            const { renderedModel } = this.legacyDesign;
            const config = { format, height, width, useShapes, drawOutlines, monochrome, showBackground, useLighting };
            exported = export2d( { renderedModel, camera, lighting }, config );
            break;
          }

          case 'pov': {
            lighting .useWorldDirection = true;
            exported = export3dDocument( this.legacyDesign, camera, lighting, { format, height, width } );
            break;
          }

          default: {
            const { renderedModel } = this.legacyDesign;
            exported = export3d( { renderedModel, camera, lighting }, { format, height, width } );
            break;
          }
        }
        this.clientEvents .textExported( action, exported ); // returning the action for Promise correlation on the client
        break;

      default:
        if (action.startsWith("setSymmetry.")) {
          const name = action.replace(/^setSymmetry\./, '');
          this.setSymmetryController( name );
        }

        else {
          this.legacyDesign.configureAndPerformEdit(action, params && params.getConfig());
          this.firePropertyChange( 'edited', '', 'true' ); // value really doesn't matter
        }
        break;
    }
  }

  doAction(action) {
    this.doParamAction(action, new JsProperties());
  }
}
