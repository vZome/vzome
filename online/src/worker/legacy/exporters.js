
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

export const export3d = ( scene, configuration ) =>
{
  const { format, height, width } = configuration;
  const { renderedModel } = scene;
  const exporter = new com.vzome.core.exporters[ exporterClasses[ format ] ]();
  const out = new java.io.StringWriter();
  exporter .exportGeometry( renderedModel, null, out, height, width );
  return out.toString();
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
  const { backgroundColor, ambientColor, directionalLights } = lighting;
  const lights = new com.vzome.core.viewing.Lights();
  lights .setBackgroundColor( parseColor( backgroundColor ) );
  lights .setAmbientColor( parseColor( ambientColor ) );
  for ( const { direction: [x,y,z], color } of directionalLights ) {
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

export const export2d = ( scene, configuration ) =>
{
  const { format, height, width, useShapes, drawOutlines, monochrome, showBackground } = configuration;
  const { renderedModel, camera, lighting } = scene;
  const viewTransform = createViewMatrix( camera );
  const projection = createProjectionMatrix( camera, 1.0 ); // TODO why can aspectRatio = width/height?
  const snapshotter = new com.vzome.core.exporters2d.Java2dExporter();
  const lights = createLights( lighting );
  const snapshot = snapshotter .render2d( renderedModel, viewTransform, projection, lights, height, width, !useShapes, !!lighting );
  const exporter = new com.vzome.core.exporters2d[ exporterClasses[ format ] ]();
  const out = new java.io.StringWriter();
  exporter .export( snapshot, out, drawOutlines, monochrome, showBackground );
  return out.toString();
}