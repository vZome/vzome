import { StringWriter } from './from-java/java/io/StringWriter.js';

import { Color } from './from-java/com/vzome/core/construction/Color.js';
import { Lights } from './from-java/com/vzome/core/viewing/Lights.js';
import { RealVector } from './from-java/com/vzome/core/math/RealVector.js';
import { RealMatrix4 } from './from-java/com/vzome/core/math/RealMatrix4.js';

import { Java2dExporter } from './from-java/com/vzome/core/exporters2d/Java2dExporter.js';
import { PDFExporter } from './from-java/com/vzome/core/exporters2d/PDFExporter.js';
import { PostScriptExporter } from './from-java/com/vzome/core/exporters2d/PostScriptExporter.js';
import { SVGExporter } from './from-java/com/vzome/core/exporters2d/SVGExporter.js';

import { StlExporter } from './from-java/com/vzome/core/exporters/StlExporter.js';
import { STEPExporter } from './from-java/com/vzome/core/exporters/STEPExporter.js';
import { OpenScadMeshExporter } from './from-java/com/vzome/core/exporters/OpenScadMeshExporter.js';
import { PythonBuild123dExporter } from './from-java/com/vzome/core/exporters/PythonBuild123dExporter.js';
import { DxfExporter } from './from-java/com/vzome/core/exporters/DxfExporter.js';
import { OffExporter } from './from-java/com/vzome/core/exporters/OffExporter.js';
import { PlyExporter } from './from-java/com/vzome/core/exporters/PlyExporter.js';
import { VRMLExporter } from './from-java/com/vzome/core/exporters/VRMLExporter.js';
import { POVRayExporter } from './from-java/com/vzome/core/exporters/POVRayExporter.js';
import { PartGeometryExporter } from './from-java/com/vzome/core/exporters/PartGeometryExporter.js';
import { OpenScadExporter } from './from-java/com/vzome/core/exporters/OpenScadExporter.js';
import { MathTableExporter } from './from-java/com/vzome/core/exporters/MathTableExporter.js';

//  Mapping the format name straight to the class lets esbuild see which
//  exporters are reachable.  The old form indexed the `com.vzome.core.exporters`
//  namespace object by a runtime string, which no bundler can follow, so every
//  exporter had to be retained.
const exporters2d = {
  'pdf'      : PDFExporter,
  'ps'       : PostScriptExporter,
  'svg'      : SVGExporter,
}

const exporters3d = {
  'stl'      : StlExporter,
  'step'     : STEPExporter,
  'scad'     : OpenScadMeshExporter,
  'build123d': PythonBuild123dExporter,
  'dxf'      : DxfExporter,
  'off'      : OffExporter,
  'ply'      : PlyExporter,
  'vrml'     : VRMLExporter,
  'pov'      : POVRayExporter,
  'partgeom' : PartGeometryExporter,
  'openscad' : OpenScadExporter,
  'math'     : MathTableExporter,
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
      return new Color(
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
  const lights = new Lights();
  lights .setBackgroundColor( parseColor( backgroundColor ) );
  lights .setAmbientColor( parseColor( ambientColor ) );
  for ( const light of directionalLights ) {
    const { worldDirection, direction, color } = light;
    //  Apparently, POVRayExporter is the only 3D exporter that uses directional lights, and it wants them in world coordinates.
    //  For 2D export, we need the directions in view coordinates.
    const [ x, y, z ] = useWorldDirection ? worldDirection : direction;
    lights .addDirectionLight( parseColor( color ), new RealVector( x, y, z ) );
  }
  return lights;
}

const createViewMatrix = camera =>
{
  const { lookAt, lookDir, up, distance } = camera;
  const lookAtRV = new RealVector( ...lookAt );
  const lookDirRV = new RealVector( ...lookDir );
  const upRV = new RealVector( ...up );
  const position = lookAtRV .minus( lookDirRV .scale( distance ) );
  return RealMatrix4.lookAt( position, lookAtRV, upRV );
}

const createProjectionMatrix = ( camera, aspectRatio ) =>
{
  // TODO: support orthographic
  const { near, far, distance, width } = camera;
  const fovX = 2 * Math .atan( (width/2) / distance );
  return RealMatrix4.perspective( fovX, aspectRatio, near, far );
}

const createCamera = ( camera ) =>
{
  const { distance, width, perspective, lookAt, up, lookDir, magnification } = camera; // This camera always comes from the client context
  const halfX = width / 2;
  const fov = 2 * Math.atan( halfX / distance );
  let [ x, y, z ] = lookAt;
  const lookAtRV = new RealVector( x, y, z );
  [ x, y, z ] = up;
  const upRV = new RealVector( x, y, z );
  [ x, y, z ] = lookDir;
  const lookDirRV = new RealVector( x, y, z );
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
  const snapshotter = new Java2dExporter();
  const lights = createLights( lighting );
  const snapshot = snapshotter .render2d( renderedModel, viewTransform, projection, lights, height, width, !useShapes, useLighting );
  const exporter = new exporters2d[ format ]();
  const out = new StringWriter();
  exporter .export( snapshot, out, drawOutlines, monochrome, showBackground );
  return out.toString();
}

export const export3d = ( scene, configuration ) =>
  {
    const { format, height, width } = configuration;
    const { renderedModel } = scene;
    const exporter = new exporters3d[ format ]();
    const out = new StringWriter();
    exporter .exportGeometry( renderedModel, null, out, height, width );
    return out.toString();
  }
  
export const export3dDocument = ( legacyDesign, camera, lighting, configuration ) =>
  {
    const { format, height, width } = configuration;
    const exporter = new exporters3d[ format ]();
    const out = new StringWriter();
    // Satisfy the DocumentIntf contract required by DocumentExporter
    const document = createDocument( legacyDesign, camera, lighting );
    exporter .exportDocument( document, null, out, height, width );
    return out.toString();
  }
  