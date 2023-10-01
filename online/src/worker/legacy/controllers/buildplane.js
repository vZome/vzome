import { com } from '../core-java.js';
import { JsProperties } from '../jsweet2js.js';
import { normalizeRenderedManifestation } from '../scenes.js';

export class BuildPlaneController extends com.vzome.desktop.controller.DefaultController
{
  constructor( renderedModel, orbitSource, clientEvents )
  {
    super();
    this.clientEvents = clientEvents;
    this.renderedModel = renderedModel;
    this.buildPlanes = orbitSource.buildPlanes;

    renderedModel .addListener( {
      shapesChanged: () => false, // important so RenderedModel.setShapes() will not fail and re-render all the parts
      manifestationAdded: ( rm ) => {
        if ( rm .getManifestation() ?.constructor["__interfaces"] ?.indexOf("com.vzome.core.model.Connector") >= 0 )
          this.latestBall = normalizeRenderedManifestation( rm );
      },
      manifestationRemoved: () => {},
      colorChanged: () => {},
      glowChanged: () => {},
    } );
  }

  setOrbitSource( orbitSource )
  {
    this.buildPlanes = orbitSource.buildPlanes;

    // by this time, the initial origin ball has already been rendered
    this.clientEvents .latestBallAdded( this.latestBall );
  }

  // There are no implementors of doParamAction() in the Java code, except for
  //   DefaultController, which just propagates the calls up to the next controller.
  doParamAction(action, params)
  {
    const config = params.getConfig();
    switch (action) {

      case 'STRUT_CREATION_TRIGGERED': {
        const { id, plane, zone, index, orientation } = config;
        if (id) {
          const rm = this.renderedModel.getRenderedManifestation(id);
          const anchor = rm.getManifestation().toConstruction();

          const buildPlane = this.buildPlanes[plane];
          const buildZone = buildPlane.zones[zone];
          let axis = buildZone.zone; // the Axis object
          const orbit = axis.getOrbit();
          const symmetry = orbit.getSymmetry();
          const permutation = symmetry.getPermutation(orientation);
          axis = permutation.permute(axis, 0); // TODO: is PLUS always right?
          const length = buildZone.vectors[index].scale;

          super.doParamAction('StrutCreation', new JsProperties({ anchor, zone: axis, length }));

          this.clientEvents .latestBallAdded( this.latestBall );
          return;
        } // else fall through
      }

      case 'JOIN_BALLS_TRIGGERED': {
        const { id1, id2 } = config;
        if (id1 && id2) {
          const rm1 = this.renderedModel.getRenderedManifestation(id1);
          const rm2 = this.renderedModel.getRenderedManifestation(id2);
          const start = rm1.getManifestation().toConstruction();
          const end = rm2.getManifestation().toConstruction();
          super.doParamAction('JoinPointPair', new JsProperties({ start, end }));
          return;
        } // else fall through
      }

      default:
        super.doParamAction(action, params);
    }
  }
}
