import { com } from '../core-java.js';
import { java } from '../candies/j4ts-2.1.0-SNAPSHOT/bundle.js';
import { JsProperties } from '../jsweet2js.js';
import { normalizeRenderedManifestation } from '../scenes.js';

export class BuildPlaneController extends com.vzome.desktop.controller.DefaultController
{
  constructor( design, clientEvents )
  {
    super();
    this.design = design;
    this.clientEvents = clientEvents;
    this.renderedModel = design.renderedModel;
    this.orbitSource = design .getOrbitSource();
    this.buildPlanes = this.orbitSource .buildPlanes;

    this.renderedModel .addListener( {
      shapesChanged: () => false, // important so RenderedModel.setShapes() will not fail and re-render all the parts
      manifestationAdded: ( rm ) => {
        if ( rm .getManifestation() ?.constructor["__interfaces"] ?.indexOf("com.vzome.core.model.Connector") >= 0 )
          this.latestBall = normalizeRenderedManifestation( rm );
      },
      manifestationRemoved: () => {},
      labelChanged: () => {},
      colorChanged: () => {},
      glowChanged: () => {},
    } );
  }

  setOrbitSource( orbitSource )
  {
    this.orbitSource = orbitSource;
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

      case 'HINGE_STRUT_SELECTED': {
        const { strutId, centerId, hingeZone: oldHingeZone } = config;
        const vectorAlgebra = com.vzome.core.algebra.AlgebraicVectors;

        const oldCenterRm = this.renderedModel .getRenderedManifestation( centerId );
        if ( !oldCenterRm ) return;
        const id = oldCenterRm .getGuid() .toString();
        const oldCenter = oldCenterRm .getLocationAV();
        const { x, y, z } = oldCenter .toRealVector();
        let center = { id, position: [ x, y, z ] };

        const strutRm = this.renderedModel .getRenderedManifestation( strutId );
        if ( !strutRm ) return;
        const strut = strutRm .getManifestation();
        const start = strut .getLocation();
        const end = strut .getEnd();
        // In all cases, we want the hingeZone to reflect the selected strut
        const hingeZone = { orbit: strutRm .getStrutOrbit() .getName(), orientation: strutRm .getStrutZone() };

        const normal = vectorAlgebra .getNormal( oldCenter, start, end );
        if ( normal .isOrigin() ) {
          // old center is collinear with strut
          const strutEnds = new java.util.ArrayList();
          strutEnds .add( start ); strutEnds .add( end );
          let { diskZone } = config;
          let { orbit, orientation } = diskZone;
          const diskNormal = this.orbitSource .getZone( orbit, orientation ) .normal();
          if ( vectorAlgebra .areOrthogonalTo( diskNormal, strutEnds ) ) {
            // strut lies in the existing plane, so just set the new hingeZone
            this.clientEvents .buildPlaneSelected( center, diskZone, hingeZone );
            return;
          } else {
            // strut lies out of the existing plane
            // Set the diskZone to include the old hinge
            const { orbit: hingeOrbit, orientation: hingeOrientation } = oldHingeZone;
            const hingeNormal = this.orbitSource .getZone( hingeOrbit, hingeOrientation ) .normal();
            const planeNormal = hingeNormal .cross( strut .getOffset() );
            const diskZone = this .getZone( planeNormal );
            if ( !diskZone ) return;
            this.clientEvents .buildPlaneSelected( center, diskZone, hingeZone );
            return;
          }
        } else {
          // strut is not collinear with the current center
          // new plane defined, so set all new plane, hinge, and center if the plane is acceptable
          const ball = this.design .getBall( start );
          if ( !ball ) return;
          const newCenter = ball .getLocation();
          const newCenterRm = ball .getRenderedObject();
          const id = newCenterRm .getGuid() .toString();
          const { x, y, z } = newCenter .toRealVector();
          let center = { id, position: [ x, y, z ] };

          const planeNormal = newCenter .minus( oldCenter ) .cross( strut .getOffset() );
          const diskZone = this .getZone( planeNormal );
          if ( !diskZone ) return;

          this.clientEvents .buildPlaneSelected( center, diskZone, hingeZone );
          return;
        }
      }

      default:
        super.doParamAction(action, params);
    }
  }

  getZone( normal )
  {
    const planeAxis = this.orbitSource .getAxis( normal );
    if ( ! planeAxis ) return null;
    const planeDir = planeAxis .getOrbit();
    if ( ! planeDir .isStandard() ) return null; // TODO: support all planes
    const orbit = planeDir .getName();
    const orientation = planeAxis .getOrientation();
    return { orbit, orientation };
}
}
