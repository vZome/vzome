
import { java } from "./candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { com } from './core-java.js';

const exporterClasses = {
  'stl'      : 'StlExporter',
  'scad'     : 'OpenScadMeshExporter',
  'build123d': 'PythonBuild123dExporter',
  'dxf'      : 'DxfExporter',
  'off'      : 'OffExporter',
  'ply'      : 'PlyExporter',
  'vrml'     : 'VRMLExporter',
  'pov'      : 'POVRayExporter',
  'partgeom' : 'PartGeometryExporter',
  'openscad' : 'OpenScadExporter',
  'math'     : 'MathTableExporter',

  'pdf'      : 'PDFExporter',
  'ps'       : 'PostScriptExporter',
  'svg'      : 'SVGExporter',
//
//   TODO: These may not all be ResourceLoader-enabled (using GeometryExporter.getBoilerplate), or otherwise web-ready,
//     but they all successfully transpiled with JSweet
//
// 'FORMAT'  : 'VefVectorExporter',
// 'FORMAT'  : 'PdbExporter',
// 'FORMAT'  : 'VefExporter',
// 'FORMAT'  : 'SegExporter',
// 'FORMAT'  : 'VefModelExporter',
}

const parseColor = input =>
{
  const m = input .match( /^#([0-9a-f]{6})$/i )[1];
  if( m ) {
      return new com.vzome.core.construction.Color(
          parseInt(m.substr(0,2),16),
          parseInt(m.substr(2,2),16),
          parseInt(m.substr(4,2),16)
      );
  }
  return null;
}

const createLights = lighting =>
{
  const { backgroundColor, ambientColor, directionalLights, useWorldDirection } = lighting;
  const lights = new com.vzome.core.viewing.Lights();
  lights .setBackgroundColor( parseColor( backgroundColor ) );
  lights .setAmbientColor( parseColor( ambientColor ) );
  for ( const light of directionalLights ) {
    const { worldDirection, direction, color } = light;
    //  Apparently, POVRayExporter is the only 3D exporter that uses directional lights, and it wants them in world coordinates.
    //  For 2D export, we need the directions in view coordinates.
    const [ x, y, z ] = useWorldDirection ? worldDirection : direction;
    lights .addDirectionLight( parseColor( color ), new com.vzome.core.math.RealVector( x, y, z ) );
  }
  return lights;
}

const createViewMatrix = camera =>
{
  const { lookAt, lookDir, up, distance } = camera;
  const lookAtRV = new com.vzome.core.math.RealVector( ...lookAt );
  const lookDirRV = new com.vzome.core.math.RealVector( ...lookDir );
  const upRV = new com.vzome.core.math.RealVector( ...up );
  const position = lookAtRV .minus( lookDirRV .scale( distance ) );
  return com.vzome.core.math.RealMatrix4.lookAt( position, lookAtRV, upRV );
}

const createProjectionMatrix = ( camera, aspectRatio ) =>
{
  // TODO: support orthographic
  const { near, far, distance, width } = camera;
  const fovX = 2 * Math .atan( (width/2) / distance );
  return com.vzome.core.math.RealMatrix4.perspective( fovX, aspectRatio, near, far );
}

const createCamera = ( camera ) =>
{
  const { distance, width, perspective, lookAt, up, lookDir, magnification } = camera; // This camera always comes from the client context
  const halfX = width / 2;
  const fov = 2 * Math.atan( halfX / distance );
  let [ x, y, z ] = lookAt;
  const lookAtRV = new com.vzome.core.math.RealVector( x, y, z );
  [ x, y, z ] = up;
  const upRV = new com.vzome.core.math.RealVector( x, y, z );
  [ x, y, z ] = lookDir;
  const lookDirRV = new com.vzome.core.math.RealVector( x, y, z );
  return {
    isPerspective:      () => perspective,
    getFieldOfView:     () => fov,
    getViewDistance:    () => distance,
    getMagnification:   () => magnification,
    getLookAtPointRV:   () => lookAtRV,
    getLookDirectionRV: () => lookDirRV,
    getUpDirectionRV:   () => upRV,

    // POVRayExporter will call this to map light directions to world coordinates, in the Java code,
    //   but here in Javascript our light directions are *already* in world coordinates.
    mapViewToWorld:     rv => rv,
  }
}

const createDocument = ( legacyDesign, camera, lighting ) =>
  {
    const { renderedModel, editor, toolsModel } = legacyDesign;
    const lights = createLights( lighting );
    const cameraModel = createCamera( camera );
    return {
      getCameraModel:   () => cameraModel,
      getSceneLighting: () => lights,
      getRenderedModel: () => renderedModel,
      getToolsModel:    () => toolsModel,
      getEditorModel:   () => editor,
      getDetailsXml:    ( dom, deep ) => null, // TODO: implement this so more exporters work  
    }
  }
  
////////////////////////////////////////////// main entry points:

export const export2d = ( scene, configuration ) =>
{
  const { format, height, width, useShapes, drawOutlines, monochrome, showBackground, useLighting } = configuration;
  const { renderedModel, camera, lighting } = scene;
  const viewTransform = createViewMatrix( camera );
  const projection = createProjectionMatrix( camera, 1.0 ); // TODO why can aspectRatio = width/height?
  const snapshotter = new com.vzome.core.exporters2d.Java2dExporter();
  const lights = createLights( lighting );
  const snapshot = snapshotter .render2d( renderedModel, viewTransform, projection, lights, height, width, !useShapes, useLighting );
  const exporter = new com.vzome.core.exporters2d[ exporterClasses[ format ] ]();
  const out = new java.io.StringWriter();
  exporter .export( snapshot, out, drawOutlines, monochrome, showBackground );
  return out.toString();
}

export const export3d = ( scene, configuration ) =>
  {
    const { format, height, width } = configuration;
    const { renderedModel } = scene;
    const exporter = new com.vzome.core.exporters[ exporterClasses[ format ] ]();
    const out = new java.io.StringWriter();
    exporter .exportGeometry( renderedModel, null, out, height, width );
    return out.toString();
  }
  
export const export3dDocument = ( legacyDesign, camera, lighting, configuration ) =>
  {
    const { format, height, width } = configuration;
    const exporter = new com.vzome.core.exporters[ exporterClasses[ format ] ]();
    const out = new java.io.StringWriter();
    // Satisfy the DocumentIntf contract required by DocumentExporter
    const document = createDocument( legacyDesign, camera, lighting );
    exporter .exportDocument( document, null, out, height, width );
    return out.toString();
  }
  