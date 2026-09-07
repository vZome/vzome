
import goldenField from '../fields/golden.js'
import root2Field from '../fields/root2.js'
import root3Field from '../fields/root3.js'
import heptagonField from '../fields/heptagon.js'
import { algebraicNumberFactory, JsProperties } from './jsweet2js.js';
import { JavaDomDocument, JavaDomElement } from './dom.js';
import { configureLogging } from './logging.js'

import allShapes from './resources/com/vzome/core/parts/index.js'
import shapeColors from './resources/com/vzome/core/parts/colors.js'
import groupResources from './resources/com/vzome/core/math/symmetry/index.js'

import { BookmarkToolFactory } from './from-java/com/vzome/core/tools/BookmarkToolFactory.js';
import { Color } from './from-java/com/vzome/core/construction/Color.js';
import { Colors } from './from-java/com/vzome/core/render/Colors.js';
import { CommandEdit } from './from-java/com/vzome/core/editor/CommandEdit.js';
import { DefaultFieldApplication } from './from-java/com/vzome/core/kinds/DefaultFieldApplication.js';
import { EdPeggField } from './from-java/com/vzome/core/algebra/EdPeggField.js';
import { EditHistory } from './from-java/com/vzome/core/editor/EditHistory.js';
import { FreePoint } from './from-java/com/vzome/core/construction/FreePoint.js';
import { GoldenFieldApplication } from './from-java/com/vzome/core/kinds/GoldenFieldApplication.js';
import { HeptagonFieldApplication } from './from-java/com/vzome/core/kinds/HeptagonFieldApplication.js';
import { ImportMesh } from './from-java/com/vzome/core/edits/ImportMesh.js';
import { JsAlgebraicField } from './from-java/com/vzome/jsweet/JsAlgebraicField.js';
import { JsEditorModel } from './from-java/com/vzome/jsweet/JsEditorModel.js';
import { PlaneOrbitSet } from './from-java/com/vzome/core/math/symmetry/PlaneOrbitSet.js';
import { PlasticNumberField } from './from-java/com/vzome/core/algebra/PlasticNumberField.js';
import { PlasticPhiField } from './from-java/com/vzome/core/algebra/PlasticPhiField.js';
import { PlasticPhiFieldApplication } from './from-java/com/vzome/core/kinds/PlasticPhiFieldApplication.js';
import { PolygonField } from './from-java/com/vzome/core/algebra/PolygonField.js';
import { PolygonFieldApplication } from './from-java/com/vzome/core/kinds/PolygonFieldApplication.js';
import { PolygonFromVertices } from './from-java/com/vzome/core/construction/PolygonFromVertices.js';
import { Projection } from './from-java/com/vzome/core/math/Projection.js';
import { RealizedModelImpl } from './from-java/com/vzome/core/model/RealizedModelImpl.js';
import { RenderedModel } from './from-java/com/vzome/core/render/RenderedModel.js';
import { ResourceLoader } from './from-java/com/vzome/xml/ResourceLoader.js';
import { RootThreeFieldApplication } from './from-java/com/vzome/core/kinds/RootThreeFieldApplication.js';
import { RootTwoFieldApplication } from './from-java/com/vzome/core/kinds/RootTwoFieldApplication.js';
import { SegmentJoiningPoints } from './from-java/com/vzome/core/construction/SegmentJoiningPoints.js';
import { SelectionImpl } from './from-java/com/vzome/core/editor/SelectionImpl.js';
import { SnubCubeField } from './from-java/com/vzome/core/algebra/SnubCubeField.js';
import { SnubCubeFieldApplication } from './from-java/com/vzome/core/kinds/SnubCubeFieldApplication.js';
import { SnubDodecField } from './from-java/com/vzome/core/algebra/SnubDodecField.js';
import { SnubDodecFieldApplication } from './from-java/com/vzome/core/kinds/SnubDodecFieldApplication.js';
import { SqrtPhiField } from './from-java/com/vzome/fields/sqrtphi/SqrtPhiField.js';
import { SqrtPhiFieldApplication } from './from-java/com/vzome/fields/sqrtphi/SqrtPhiFieldApplication.js';
import { SuperGoldenField } from './from-java/com/vzome/core/algebra/SuperGoldenField.js';
import { SymmetrySystem } from './from-java/com/vzome/core/editor/SymmetrySystem.js';
import { ToolsModel } from './from-java/com/vzome/core/editor/ToolsModel.js';
import { XmlSymmetryFormat } from './from-java/com/vzome/core/commands/XmlSymmetryFormat.js';
import { Properties } from './from-java/java/util/Properties.js';
import { AlgebraicVector } from './from-java/com/vzome/core/algebra/AlgebraicVector.js';
import { AlgebraicVectors } from './from-java/com/vzome/core/algebra/AlgebraicVectors.js';
import { AlgebraicMatrix } from './from-java/com/vzome/core/algebra/AlgebraicMatrix.js';
import { GrahamScan2D } from './from-java/com/vzome/core/math/convexhull/GrahamScan2D.js';
import { GitHubShare } from './from-java/com/vzome/core/exporters/GitHubShare.js';
import { SideEffects } from './from-java/com/vzome/core/editor/api/SideEffects.js';
import { editClasses, commandClasses, editorClasses } from './registry.js';
import { java } from './candies/j4ts-2.1.0-SNAPSHOT/bundle.js'
import { createParser } from './parser.js'
import { enhanceTopologicalMesh, } from './meshes.js';

// monkey-patch
SideEffects.logBugAccommodation = function(message) {
  console.log("SideEffects logBugAccommodation: " + message);
  throw new Error(message);
}

// Copied from core/src/main/resources/com/vzome/core/editor/defaultPrefs.properties
const defaults = {

  // Standard Zometool colors
// color.blue      = 0,142,194    #008ec2
// color.yellow    = 255,179,26   #ffb31a
// color.red       = 217,18,24    #d91218
// color.green     = 0,153,63     #00993f
// color.turquoise = 0,179,161    #00b3a1
// color.white     = 242,242,242  #f2f2f2
// color.black     = 50,50,50     #323232
// color.orange    = 235,82,0     #eb5200
// color.purple    = 125,54,211   #7d36d3
// color.gray      = 133,133,133  #858585

  "color.blue"     : "0,142,194",
  "color.yellow"   : "255,179,26",
  "color.red"      : "217,18,24",
  "color.green"    : "0,153,63",
  "color.turquoise": "0,179,161",
  "color.white"    : "242,242,242",
  "color.black"    : "50,50,50",
  "color.orange"   : "235,82,0",
  "color.purple"   : "125,54,211",
  "color.gray"     : "133,133,133",


  "color.olive": "100,113,0",
  "color.lavender": "175,135,255",
  "color.maroon": "117,0,50",
  "color.rose": "255,51,143",
  "color.navy": "0,0,153",
  "color.brown": "107,53,26",
  "color.apple": "116,195,0",
  "color.sand": "154,117,74",
  "color.coral": "255,126,106",
  "color.sulfur": "230,245,62",
  "color.cinnamon": "136,37,0",
  "color.spruce": "18,73,48",
  "color.magenta": "255,41,183",
  "color.snubPentagon": "187,57,48",
  "color.snubTriangle": "87,188,48",
  "color.snubDiagonal": "228,225,199",
  "color.snubFaceNormal": "216,216,208",
  "color.snubVertex": "204,204,0",
  "color.slate": "108, 126, 142",
  "color.mauve": "137, 104, 112",
  "color.ivory": "228,225,199",
  "color.panels": "225,225,225",
  "color.background": "175,200,220",
  "color.highlight": "195,195,195",
  "color.highlight.mac ": "153,255,0",
  "color.light.directional.1": "235, 235, 228",
  "color.light.directional.2": "228, 228, 235",
  "color.light.directional.3": "30, 30, 30",
  "color.light.ambient": "41, 41, 41",

  // direction values are given as "x,y,z", a vector of real numbers.
  // The X axis points to the right of your screen, the Y axis points to
  // the top of the screen, and the Z axis points out of the screen toward you.
  "direction.light.1": "1.0,-1.0,-1.0",
  "direction.light.2": "-1.0,0.0,0.0",
  "direction.light.3": "0.0,0.0,-1.0",
}

// We are using matrices, not quaternions, because these matrices
//  may not be orthogonal, in the case of a symmetry that has a
//  non-trivial embedding, e.g. heptagonal antiprism.
// The embedding must be applied after the "rotation".
const makeFloatMatrices = ( matrices ) =>
{
  return matrices.map( am => {
    let m = []
    for ( let i = 0; i < 3; i++) {
      for ( let j = 0; j < 3; j++) {
        const an = am.getElement( i, j );
        m[ i*4 + j ] = an.evaluate()
      }
    }
    m[ 3 ] = m[ 7 ] = m[ 11 ] = m[ 12 ] = m[ 13 ] = m[ 14 ] = 0
    m[ 15 ] = 1
    return m
  });
}

//  Classes needed by name outside this module.  These replace the old
//  whole-namespace `vzomePkg` export: GitHubShare for the worker's sharing
//  action, and the algebra/hull classes for the standalone experiment pages
//  under serve/app/test/cases/.
export { GitHubShare };
export { AlgebraicVector, AlgebraicVectors, AlgebraicMatrix, GrahamScan2D };
export const util = java.util;

// This is a bit of a hack, but how else would you configure system props for JSweet?
java.lang.System.propertyMap_$LI$().put( "gwt.logging.enabled", "TRUE" );
configureLogging( util.logging );

const verticesBigIntReviver = ( key, value, context ) =>
{
  // Leaves are revived first, so we have to convert large numbers to BigInt using source string.
  if ( typeof value === 'number' && context && context.source ) {
    if ( context.source !== value.toString() ) {
      // precision loss in value, so ignore it
      return context.source; // Return as String instead; will be converted to BigInt later
    }
    return value; // Safe to return as Number
  }
  return value;
}

class ImportColoredMeshJson extends ImportMesh
{
    getXmlElementName() { return "ImportColoredMeshJson"; }

    parseMeshData( offset, events, registry )
    {
      // TODO: handle projection and scale

      const coloredMesh = JSON.parse( this.meshData, verticesBigIntReviver ); // No loss of integer precision!
      const field = registry.getField( coloredMesh.field )
      const vertices = coloredMesh.vertices.map( nums => {
        // This does not care whether nums has Strings or Numbers or BigInts.
        //   For JsAlgebraicField, all turned into BigInts.
        //   For legacy fields (JavaAlgebraicField), all turned into JavaBigRationals (pairs of BigInts).
        let vertex = field.createVectorFromTDs( nums )
        if ( vertex.dimension() > 3 )
            vertex = this.projection.projectImage( vertex, false )
        if ( offset != null )
            vertex = offset.plus( vertex )
        return vertex
      } )
      // TODO: handle legacy format; see ColoredMeshJson.java
      coloredMesh.balls.forEach( ball => {
        const vertex = vertices[ ball.vertex ]
        const color = ball.color && Color.parseWebColor( ball.color )
        events.constructionAdded( new FreePoint( vertex ), color );
      });
      coloredMesh.struts.forEach( strut => {
        const point1 = new FreePoint( vertices[ strut.vertices[ 0 ] ] )
        const point2 = new FreePoint( vertices[ strut.vertices[ 1 ] ] )
        const color = strut.color && Color.parseWebColor( strut.color )
        events.constructionAdded( new SegmentJoiningPoints( point1, point2 ), color );
      });
      coloredMesh.panels.forEach( panel => {
        const points = []
        panel.vertices.forEach( i => {
          points.push( new FreePoint( vertices[ i ] ) )
        } )
        const color = panel.color && Color.parseWebColor( panel.color )
        events.constructionAdded( new PolygonFromVertices( points ), color );
      });
      // TODO: handle panels
    }
}

class ImportSimpleMeshJson extends ImportMesh
{
    scaleAndProject = true;

    getXmlElementName() { return "ImportSimpleMeshJson"; }

    getXmlAttributes( element )
    {
      if ( this.scaleAndProject )
        element .setAttribute( "scaleAndProject", "true" );

      super.getXmlAttributes( element );
    }
    
    setXmlAttributes( xml, format )
    {
      const boolStr = xml .getAttribute( "scaleAndProject" );
      // Default is true, but legacy files must behave as if false
      if ( ! boolStr )
          this.scaleAndProject = false;

      super.setXmlAttributes( xml, format );
    }

    parseMeshData( offset, events, registry )
    {
      // Legacy code dropped the 4th coordinate from the projection, implicitly because it never used
      //   the 4th coordinate, always making a 3D vector.  New code drops the 1st coordinate.
      let wFirst = false;

      if ( ! this.scaleAndProject ) {
          const field = this.mManifestations .getField();
          this.scale = field .one();
          this.projection = new Projection.Default( field );
      }

      const simpleMesh = JSON.parse( this.meshData, verticesBigIntReviver ); // No loss of integer precision!
      const field = registry.getField( simpleMesh.field || 'golden' )
      const vertices = simpleMesh.vertices.map( nums => {
        // This does not care whether nums has Strings or Numbers or BigInts.
        //   For JsAlgebraicField, all turned into BigInts.
        //   For legacy fields (JavaAlgebraicField), all turned into JavaBigRationals (pairs of BigInts).
        let vertex = field.createVectorFromTDs( nums );
        vertex = vertex .scale( this.scale );
        if ( vertex.dimension() > 3 )
            vertex = this.projection .projectImage( vertex, wFirst );
        if ( offset != null )
            vertex = offset .plus( vertex );
        return vertex
      } )
      simpleMesh.edges.forEach( strut => {
        const point1 = new FreePoint( vertices[ strut[ 0 ] ] )
        const point2 = new FreePoint( vertices[ strut[ 1 ] ] )
        events.constructionAdded( point1 )
        events.constructionAdded( point2 )
        events.constructionAdded( new SegmentJoiningPoints( point1, point2 ) );
      });
      simpleMesh.faces.forEach( panel => {
        const points = []
        panel.forEach( i => {
          points.push( new FreePoint( vertices[ i ] ) )
        } )
        events.constructionAdded( new PolygonFromVertices( points ) );
      });
    }
}

const xmlToEditClass = editName =>
{
    if ( editName === 'CommandEdit' ) {
      // The constructor pattern is wrong
      return undefined
    }
    const legacyNames = {
      setItemColor: "ColorManifestations",
      BnPolyope: "B4Polytope",
      DeselectByClass: "AdjustSelectionByClass",
      realizeMetaParts: "RealizeMetaParts",
      SelectSimilarSize: "AdjustSelectionByOrbitLength",
      zomic: "RunZomicScript",
      py: "RunPythonScript",
      apiProxy: "ApiEdit",
    }
    editName = legacyNames[ editName ] || editName
    return editClasses[ editName ] || editorClasses[ editName ]
}

const editFactory = ( editor, toolFactories, toolsModel ) => xmlElement =>
{
    editor.setAdapter( null ) // This should trigger NPEs in any edits that have side-effects in their constructor

    let editName = xmlElement.getLocalName()

    if ( editName === "ImportColoredMeshJson" )
      return new ImportColoredMeshJson( editor )

    if ( editName === "ImportSimpleMeshJson" )
      return new ImportSimpleMeshJson( editor )

    if ( toolsModel ) {
      const toolEdit = toolsModel.createEdit( editName );
      if ( toolEdit )
          return toolEdit;
    }

    const toolId = xmlElement.getAttribute( "name" );
    if ( toolId ) {
      const factory = toolFactories.get( editName )
      if ( factory ) {
        const edit = factory.deserializeTool( toolId );
        if ( edit )
          return edit
      }
    }

    const editClass = xmlToEditClass( editName )
    if ( editClass ) {
      const edit = new editClass( editor );

      if ( editName === 'Snapshot' ) {
        edit .recorder = { recordSnapshot: () => {} }; // safe no-op
      }

      // HACK! This works only when the .vZome file has been hand-edited to add label
      //   attributes to GroupSelection commands.
      if ( editName === 'GroupSelection' ) {
        const label = xmlElement .getAttribute( "label" );
        if ( label ) {
          edit .label = label;
        }
      }

      return edit;
    }
    else
      return new CommandEdit( null, editor )
}

const resources = {}

export const loadAndInjectResource = async ( path, url ) =>
{
    const response = await fetch( url )
    if ( ! response.ok ) {
      // use console.debug() instead of console.log() 
      // because these messages occur every time the src changes
      // and can get pretty verbose in the js console
      console.debug( `No resource for ${path}` )
      return
    }
    const text = await response.text()
    // Inject the VEF into a static map on ExportedVEFShapes
    if ( text[ 0 ] === "<" ) {
      // HTML from a 404, just skip it
      return
    }
    resources[ path ] = text
}

  // Now we can setup the ResourceLoader; we must do this before initializing the fieldApps,
  //  since they need the colors.properties to create the ExportedVEFShapes.
  ResourceLoader.setResourceLoader( {
    loadTextResource: path => resources[ path ]
  } )

  // Initialize the field application
  const groupResourcesReady = Promise.all( Object.entries( groupResources ).map( ([ key, value ]) => loadAndInjectResource( `com/vzome/core/math/symmetry/${key}.vef`, value ) ) )
  const properties = new JsProperties( defaults )
  const colors = new Colors( properties )

  const shapesReady = Promise.all( Object.entries( allShapes ).map(
    async ([ family, shapes ]) => {
      await Promise.all( Object.entries( shapes ).map( ([ key, value ]) => loadAndInjectResource( `com/vzome/core/parts/${family}/${key}.vef`, value ) ) )
    }
  ) )

  //  A few shape packages recolour an orbit -- in bigzome, blue struts are white, and
  //  connectors are silver.  ExportedVEFShapes reads colors.properties in its constructor,
  //  so these must be injected before any field app is constructed, alongside the shapes.
  //  'vienne' ships one with no .vef files at all, so this is keyed separately from
  //  allShapes rather than derived from it.
  const shapeColorsReady = Promise.all( Object.entries( shapeColors )
    .map( ([ pkg, url ]) => loadAndInjectResource( `com/vzome/core/parts/${pkg}/colors.properties`, url ) ) )

  // Static registry of every registrable field: NAMES and LABELS only, with no
  // FieldApplication construction.  A FieldApplication is heavy (it registers
  // symmetry perspectives, orbit sets, tool factories, etc.), and a given
  // worker typically needs only one or two fields, so we construct each app
  // lazily on first getFieldApp() and cache it in `fieldApps`.
  //
  //   kind 'new'    -> new JsAlgebraicField( jsField )        (golden, rootTwo, ...)
  //   kind 'legacy' -> new FieldClass( algebraicNumberFactory )
  //   polygonN      -> dynamic, not registered here (see constructFieldApp)
  //
  // `label` is exactly what the corresponding Java *FieldApplication.getLabel()
  // returns; null means "not shown in the New Design menu" (see filemenu.jsx).
  // Storing it statically lets the picker enumerate/filter fields without
  // forcing construction.  The class/field references are thunks so that merely
  // defining this table does not dereference vzomePkg.* eagerly.
  //
  // The registry key MUST equal the field's getName() -- design XML records
  // field.name and the client passes that name back to getField/createDesign.
  const fieldRegistry = {
    golden:        { kind: 'new',    label: 'Zome (Golden)',     jsField: goldenField,   appClass: () => GoldenFieldApplication },
    rootTwo:       { kind: 'new',    label: '√2',           jsField: root2Field,    appClass: () => RootTwoFieldApplication },
    rootThree:     { kind: 'new',    label: '√3',           jsField: root3Field,    appClass: () => RootThreeFieldApplication },
    heptagon:      { kind: 'new',    label: 'Heptagon',          jsField: heptagonField, appClass: () => HeptagonFieldApplication },

    sqrtPhi:       { kind: 'legacy', label: '√φ',      fieldClass: () => SqrtPhiField,      appClass: () => SqrtPhiFieldApplication },
    snubCube:      { kind: 'legacy', label: 'Snub Cube',         fieldClass: () => SnubCubeField,      appClass: () => SnubCubeFieldApplication },
    snubDodec:     { kind: 'legacy', label: 'Snub Dodecahedron', fieldClass: () => SnubDodecField,     appClass: () => SnubDodecFieldApplication },
    superGolden:   { kind: 'legacy', label: null,                fieldClass: () => SuperGoldenField,   appClass: () => DefaultFieldApplication },
    plasticNumber: { kind: 'legacy', label: null,                fieldClass: () => PlasticNumberField, appClass: () => DefaultFieldApplication },
    plasticPhi:    { kind: 'legacy', label: null,                fieldClass: () => PlasticPhiField,    appClass: () => PlasticPhiFieldApplication },
    edPegg:        { kind: 'legacy', label: null,                fieldClass: () => EdPeggField,        appClass: () => DefaultFieldApplication },
  };

  const fieldApps = {}   // cache of CONSTRUCTED apps, keyed by field name
  const wrapLegacyField = ( legacyField ) => ({
    name: legacyField.getName(),
  })

  // Build (and cache) the FieldApplication for one name; undefined if unknown.
  const constructFieldApp = ( name ) =>
  {
    if ( name.startsWith( "polygon" ) ) {
      const nsides = parseInt( name.replace( /^polygon/, '' ) )
      const legacyField = new PolygonField( "polygon"+nsides, nsides, algebraicNumberFactory )
      legacyField.delegate = wrapLegacyField( legacyField )
      const fieldApp = new PolygonFieldApplication( legacyField )
      fieldApps[ legacyField.getName() ] = fieldApp
      return fieldApp
    }
    const entry = fieldRegistry[ name ]
    if ( ! entry )
      return undefined
    let fieldApp
    if ( entry.kind === 'new' ) {
      const legacyField = new JsAlgebraicField( entry.jsField )
      fieldApp = new (entry.appClass())( legacyField )
    }
    else { // 'legacy'
      const legacyField = new (entry.fieldClass())( algebraicNumberFactory )
      legacyField.delegate = wrapLegacyField( legacyField )
      fieldApp = new (entry.appClass())( legacyField )
    }
    fieldApps[ name ] = fieldApp
    return fieldApp
  }

  const getFieldApp = ( name='golden' ) => fieldApps[ name ] || constructFieldApp( name )

  // All registrable field names (does NOT force construction).  polygonN fields
  // are dynamic and not enumerated here, as before.
  const getFieldNames = () => Object .keys( fieldRegistry )

  const getField = fieldName =>
  {
    const fieldApp = getFieldApp( fieldName )
    if ( !fieldApp )
      return { name: fieldName, unknown: true };
    return fieldApp.getField();
  }

  // Label WITHOUT construction (null => hidden in the New Design picker).
  const getFieldLabel = ( fieldName ) =>
  {
    const entry = fieldRegistry[ fieldName ]
    if ( entry )
      return entry.label;
    // polygonN or unknown: fall back to constructing to read getLabel(),
    // preserving prior behavior for names not in the static table.
    const fieldApp = getFieldApp( fieldName )
    if ( !fieldApp )
      return { error: `No such field name: ${fieldName}` };
    return fieldApp .getLabel();
  }

  const getSymmetry = ( fieldName, symmName ) =>
  {
    const fieldApp = getFieldApp( fieldName )
    if ( !fieldApp )
      return { error: `No such field name: ${fieldName}` };
    const symmPersp = fieldApp .getSymmetryPerspective( symmName );
    if ( !symmPersp )
      return { error: `No such symmetry name: ${symmName}` };
    return symmPersp .getSymmetry();
  }

  const documentFactory = ( fieldName, namespace, xml ) =>
  {
    // This reproduces the DocumentModel constructor pretty faithfully

    const fieldApp = getFieldApp( fieldName )
    if ( !fieldApp )
      return { field: { name: fieldName, unknown: true } };
    const legacyField = fieldApp.getField();
    const field = legacyField.delegate

    const originPoint = new FreePoint( legacyField.origin( 3 ) )

    const systemXml = xml && xml.getChildElement( "SymmetrySystem" )
    const symmName = systemXml && systemXml.getAttribute( "name" )
    const symmPer = ( symmName && fieldApp.getSymmetryPerspective( symmName ) ) || fieldApp.getDefaultSymmetryPerspective()

    const history = new EditHistory();
    history .setSerializer( { serialize: element => element .serialize( "" ) } );

    let changeCount = 0;
    const getChangeCount = () => changeCount;

    // This object implements the UndoableEdit.Context interface
    const editContext = {
      // Since we are not creating Branch edits, this should never be used
      createEdit: () => { throw new Error( "createEdit should never be called" ) },

      createLegacyCommand: name => {
        const constructor = commandClasses[ name ]
        if ( constructor )
          return new constructor()
        else
          throw new Error( `${name} command is not available yet`);
      },
      
      performAndRecord: edit => {
        edit.perform();
        if ( edit .isNoOp() )
          return;
        history .mergeSelectionChanges();
        history .addEdit( edit, editContext );
        editor .notifyListeners();
        ++changeCount;
      },

      doEdit: ( className, props ) => {
        if ( editor .selection .isEmpty() && className === "hideball" ) {
          className = "ShowHidden";
        }
  
        const command = fieldApp .getLegacyCommand( className );
        if ( command )
        {
          const edit = new CommandEdit( command, editor );
          editContext .performAndRecord( edit );
          return;
        }
  
        const [ action, mode ] = className .split( '/' );
        if ( mode )
          props .put( "mode", mode );

        const edit = editFactory( editor, toolFactories, toolsModel )( new JavaDomElement( { tagName: action } ) )
        if ( ! edit )
          return
        // editor.setAdapter( adapter );
        edit.configure( props );
        editContext .performAndRecord( edit );
      }
    }

    const configureAndPerformEdit = ( className, config, adapter ) =>
    {
      const props = new JsProperties( config );
      editContext .doEdit( className, props );
    }

    const toolsModel = new ToolsModel( editContext, originPoint )

    // Initialize the default SymmetrySystems from the FieldApplication
    const symmetrySystems = {}
    const symmPerspectives = fieldApp.getSymmetryPerspectives().iterator()
    while ( symmPerspectives.hasNext() ) {
      const perspective = symmPerspectives.next()
      const osm = new SymmetrySystem( null, perspective, editContext, colors, true )
      symmetrySystems[ osm.getName() ] = osm
    }

    // Now overwrite some or all of those default SymmetrySystems with those stored in the file.
    //   This is important mostly for the automatic orbits, but can also carry color overrides.
    symmetrySystems[ symmPer.getName() ] = new SymmetrySystem( systemXml, symmPer, editContext, colors, true )
    if ( xml ) {
      const symms = xml.getChildElement( "OtherSymmetries" )
      if ( symms ) {
        const nodes = symms.getElementsByTagName( "SymmetrySystem" )
        for ( let i = 0; i < nodes.getLength(); i++ ) {
          const symmElem = nodes.item( i )
          const symmName = symmElem.getAttribute( "name" )
          const symmPerspective = fieldApp.getSymmetryPerspective( symmName )
          const otherSymmetrySystem = new SymmetrySystem( symmElem, symmPerspective, this, colors, true )
          symmetrySystems[ symmName ] = otherSymmetrySystem
        }
      }
    }

    class OSField {
      constructor(){}
      getGroup( name ) {
        return symmetrySystems[ name ].getOrbits();
      }
      getQuaternionSet( name ) {
        return fieldApp.getQuaternionSymmetry( name );
      }
    }
    OSField.__interfaces = [ "com.vzome.core.math.symmetry.OrbitSet.Field" ];
    const orbitSetField = new OSField();

    const projection = new Projection.Default( legacyField );
    const realizedModel = new RealizedModelImpl( legacyField, projection );

    let orbitSource = symmetrySystems[ symmPer.getName() ];
    const renderedModel = new RenderedModel( legacyField, orbitSource );
    realizedModel .addListener( renderedModel );

    const originBall = realizedModel .manifest( originPoint );
    originBall .addConstruction( originPoint );
    realizedModel .add( originBall );

    realizedModel .show( originBall );

    const getBall = location => realizedModel .getManifestation( new FreePoint( location ) );

    const selection = new SelectionImpl();
    const editor = new JsEditorModel( realizedModel, selection, fieldApp, orbitSource, symmetrySystems );
    for ( const symmetrySystem of Object.values( symmetrySystems ) ) {
      symmetrySystem .setEditorModel( editor );
    }
    toolsModel .setEditorModel( editor )
    history .setListener( { publishChanges: () => {
      editor .notifyListeners();
    } } );

    selection .addListener( {
      manifestationAdded: m => renderedModel .setManifestationGlow( m, true ),
      manifestationRemoved: m => renderedModel .setManifestationGlow( m, false ),
    } );

    const setSymmetrySystem = symmName =>
    {
      // TODO: special case for antiprism

      orbitSource = symmetrySystems[ symmName ];

      if ( !orbitSource.orientations ) {
        // This has no analogue in Java DocumentModel
        orbitSource.orientations = makeFloatMatrices( orbitSource.getSymmetry().getMatrices() );
        orbitSource.permutations = orbitSource .getSymmetry() .getPermutations() .map( p => p .getJsonValue() );
        collectBuildPlanes( orbitSource );
      }

      editor .symmetries = orbitSource; // violating encapsulation, sorry
      renderedModel .setOrbitSource( orbitSource );
    }
    setSymmetrySystem( symmPer.getName() ); // updates orbitSource in editor and renderedModel

    const getSymmetrySystem = (name) =>
    {
      return editor .getSymmetrySystem( name? name : undefined );
    }

    const format = namespace && XmlSymmetryFormat.getFormat( namespace )
    format && format.initialize( legacyField, orbitSetField, 0, "vZome Online", new Properties() )

    const toolFactories = new util.HashMap()
    for ( const symmetrySystem of Object.values( symmetrySystems ) ) {
      symmetrySystem.createToolFactories( toolsModel ) // needed to register built-in tools
      for ( let toolkind of [ 0, 1, 2 ] ) {
        for (let index = symmetrySystem .getToolFactories( toolkind ) .iterator(); index.hasNext(); ) {
          let factory = index.next();  
          editor .addSelectionSummaryListener( factory );
        }
      }
    }

    fieldApp.registerToolFactories( toolFactories, toolsModel )
    
    const bookmarkFactory = new BookmarkToolFactory( toolsModel );
    editor .addSelectionSummaryListener( bookmarkFactory );
    bookmarkFactory.createPredefinedTool( "ball at origin" );

    const toolsXml = xml && xml.getChildElement( "Tools" )
    toolsXml && toolsModel.loadFromXml( toolsXml )
    // xml && console.log( xml .serialize( "" ) );

    const interpretEdit = ( xmlElement, context ) =>
    {
      const wrappedElement = new JavaDomElement( xmlElement )
      const edit = editFactory( editor, toolFactories, toolsModel )( wrappedElement )
      if ( ! edit )   // Null edit only happens for expected cases (e.g. "Shapshot"); others become CommandEdit.
        return null  //  Not indicating failure, just indicating nothing to record in history

      // HACK! This works only when the .vZome file has been hand-edited to add label
      //   attributes to GroupSelection commands.
      if ( xmlElement.tagName === 'GroupSelection' && !!edit.label ) {
        console.log( `Performed GroupSelection with label ${edit.label}`);
        const selection = edit.mSelection; // HACK! violating encapsulation
        for (let index = selection.mManifestations.iterator(); index.hasNext(); ) {
          let m = index.next();
          m .mRendered .label = edit.label;
        }
      }
      
      try {
        edit.loadAndPerform( wrappedElement, format, context )
      } catch (error) {
        if ( error .message .endsWith( 'command is not available yet' ) )
          throw new Error( `${xmlElement.tagName} command is not available`);
        else
          throw new Error( `${xmlElement.tagName} failure: ${error.message}` );
      }
      checkSideEffects( edit, wrappedElement );

      return edit;
    }

    const checkSideEffects = ( edit, element ) =>
    {
      const expectedEffects = element.nativeElement.children .filter( kid => kid.tagName === 'effects' )[ 0 ];
      if ( expectedEffects ) {
        const actualEffects = edit .getDetailXml( element .getOwnerDocument() ) .getChildElement( 'effects' ) .nativeElement;
        const expectedText = JSON.stringify( expectedEffects, null, 2 );
        const actualText = JSON.stringify( actualEffects, null, 2 );
        if ( actualText !== expectedText ) {
          const { id, index, tagName } = element.nativeElement
          const editNumber = element.getAttribute('editNumber')
          const nExpected = expectedEffects.children.length
          const nActual = actualEffects.children.length
          console.groupCollapsed(`${tagName} effects do not match recorded history!`)
          console.log({ editNumber, id, index, nExpected, nActual, tagName })
          console.log( 'EXPECTED: ', expectedText );
          console.log( 'ACTUAL  : ', actualText );
          console.groupEnd()
          // DJH: This method originally threw an error at this point which meant that subsequent parsing wouldn't occur,
          // but in some cases, the side effect lists are equivalent but just listed in a different order so the 
          // string comparison fails.
          // I think in some cases, it is helpful to allow the parsing to continue just to see if the reordering matters.
          // With that in mind, I am always logging any differences (above), but only throwing an error if the counts are different.
          // TODO: We could make more extensive comparisons as the basis for failing, but since this is just a seldom used
          // diagnostic tool, this is the only change I'm going to make for now, except to include tagName and editNumber
          // in the Error message when an error is actually thrown.
          if(nExpected != nActual) {
            throw new Error(`Side effects from edit number ${editNumber} (${tagName}) do not match recorded history!`)
          }
        }
      }
    }

    /*
      STATUS:
        (This was written before converting from DOM parsing to txml in the worker!)

          This approach is working OK, as far as producing valid JSON goes.  Obviously,
          The branch JSON is wrong.  More problematic is that the branch is already
          unpacked in the history.  This needs more thought.

          Another thing to consider is whether to simply convert the DOM elements to
          Javascript objects during the parse, effectively normalizing the edits.
          As an optimization, the DOM element could be retained.  When the converted
          JSON file is loaded a second time, the DOM elements would have to be reconstructed.

          Perhaps I should focus on "native" (non-legacy) edits first, so the interpreter
          is not biased towards DOM as it is today.  (See the createEdit API below.)
    */
    const serializeLegacyEdit = txmlElement =>
    {
      const editType = txmlElement.nodeName
      const result = { editType }
      if ( editType === "Branch" ) {
        result.edits = []
        return result
      }
      // not a branch, if we got here
      txmlElement.getAttributeNames().forEach( name => {
        if ( name !== 'id' ) result[ name ] = txmlElement.getAttribute( name )
      })
      if ( txmlElement.textContent ) {
        const text = txmlElement.textContent.trim()
        if ( !! text )
          result.textContent = text
      }
      if ( txmlElement.firstElementChild ) {
        console.log( `Nested XML: ${txmlElement.nodeName} ${txmlElement.firstElementChild.outerHTML}` );
      }
      return result
    }

    const batchRender = renderingListener => {
      const RM = RenderedModel;
      RM.renderChange( new RM( null, null ), renderedModel, renderingListener );
    }

    const serializeToDom = ( doc, root ) =>
    {
      root .setAttribute( "field", field.name );

      let childElement;
      {
        childElement = history .getXml( doc );
        let edits = 0, lastStickyEdit=-1;
        const undoables = history .iterator();
        while ( undoables .hasNext() ) {
          const undoable = undoables .next()
          childElement .appendChild( undoable .getXml( doc ) );
          ++ edits;
          if ( undoable .isSticky() )
            lastStickyEdit = edits;
        }
        childElement .setAttribute( "lastStickyEdit", '' + lastStickyEdit );
      }
      root .appendChild( childElement );

      // childElement = lesson .getXml( doc );
      // root .appendChild( childElement );

      childElement = orbitSource .getXml( doc );
      root .appendChild( childElement );

      // We have to store all symmetries, not just the current one, due to sequences like this:
      //   1. drag an icosahedral olive strut
      //   2. switch to octahedral symmetry
      //   3. do "build with this" on the olive strut
      //   4. drag out a strut
      //   5. switch back to icosahedral symmetry
      // The end result is that the command in step 4 records the name of an automatic orbit.
      // That automatic orbit must be captured in the file.
      childElement = doc .createElement( "OtherSymmetries" );
      for ( const symmetrySystem of Object.values( symmetrySystems ) ) {
          if ( symmetrySystem === orbitSource )
              continue; // already serialized above
          const symmElement = symmetrySystem .getXml( doc );
          childElement .appendChild( symmElement );
      }
      root .appendChild( childElement );

      childElement = toolsModel .getXml( doc );
      root .appendChild( childElement );
    }

    const getOrbitSource = () => orbitSource;

    return { interpretEdit, configureAndPerformEdit, batchRender, serializeToDom, setSymmetrySystem, getSymmetrySystem, getChangeCount,
      getOrbitSource, getBall,
      snapshotNodes: [ '--END--' ],
      scenes: [],
      editor,
      field, legacyField, fieldApp,
      renderedModel, symmetrySystems, toolsModel, bookmarkFactory, history, editContext };
  }

// The ten official Zometool colors, in the order desktop vZome presents them in its
//  "Zometool Colors" chooser panel (see desktop ColorListPanel.ZOMETOOL_COLORS).
//  Only the names are listed here; the RGB values come from the `defaults` above, which
//  is the copy of core's defaultPrefs.properties, so there is one source for the values.
//  The remaining color.* entries in defaults are not Zometool colors -- they are lights,
//  backgrounds, highlights, and the extended palette -- so they cannot be discovered by
//  simply filtering for the "color." prefix.
const ZOMETOOL_COLOR_NAMES = [
  'blue', 'yellow', 'red', 'green', 'turquoise',
  'white', 'black', 'orange', 'purple', 'gray',
];

const capitalize = s => s .charAt(0) .toUpperCase() + s .slice(1);

// Each entry is "Name #rrggbb", so the client gets label and value in one list property,
//  which is all the controller list protocol can carry (an array of strings).
const getZometoolColors = () => ZOMETOOL_COLOR_NAMES .map( name => {
  const rgb = defaults[ `color.${name}` ] .split( ',' )
    .map( n => Number( n .trim() ) .toString( 16 ) .padStart( 2, '0' ) )
    .join( '' );
  return `${capitalize( name )} #${rgb}`;
} );

export const initialize = async () =>
{
  // Fields are now constructed lazily (see fieldRegistry / getFieldApp), so we
  // only wait for the group symmetry resources (needed before any field-app
  // construction) and the shapes.
  await Promise.all( [ groupResourcesReady, shapesReady, shapeColorsReady ] );
  const parse = createParser( documentFactory );
  return {
    getFieldNames, getField, getFieldLabel, getSymmetry,
    getZometoolColors,
    documentFactory, parse,
    enhanceTopologicalMesh: enhanceTopologicalMesh( getField ),
  };
}

  export const convertColor = color =>
  {
    if ( !color )
      return '#ffffff';

    const componentToHex = c => {
      let hex = c.toString(16);
      return hex.length == 1 ? "0" + hex : hex;
    }
    return "#" + componentToHex(color.getRed()) + componentToHex(color.getGreen()) + componentToHex(color.getBlue());
  }

  const collectBuildPlanes = ( orbitSource ) =>
  {
    const field = orbitSource .getSymmetry() .getField();
    orbitSource .buildPlanes = {};

    const orbitIterator = orbitSource .getOrbits() .getDirections() .iterator();
    while ( orbitIterator .hasNext() ) {
      const planeOrbit = orbitIterator .next();
      if ( ! planeOrbit .isStandard() )
        continue;
      const planeName = planeOrbit .getName();
      const color = convertColor( orbitSource .getColor( planeOrbit ) );
      const planeZone = planeOrbit .getAxis( 0, 0 );
      const orientation = planeZone .orientation;
      const normal = planeZone .normal();

      const zones = [];

      const planeOrbits = new PlaneOrbitSet( orbitSource.getOrbits(), normal );
      const iterator = planeOrbits .zones();
      while ( iterator .hasNext() ) {
        const zone = iterator .next()
        const orientation = zone .getOrientation();
        const orbit = zone .getDirection();
        if ( ! orbit .isStandard() )
          continue;
        const vectors = [];
        const zoneNormal = zone .normal();
        const zoneColor = convertColor( orbitSource .getVectorColor( zoneNormal ) );
        let scale = orbit .getUnitLength();
        for ( let i = 0; i < 5; i++ ) {
          scale = scale .times( field .createPower( 1 ) );
          const gridPoint = zoneNormal .scale( scale );
          vectors .push( { point: gridPoint, scale } );
        }
        
        zones .push( { name: orbit.getName(), zone, orientation, color: zoneColor, vectors } );
      }
      orbitSource .buildPlanes[ planeName ] = { color, normal, zones, orientation };
    }
  }

  // TODO: replace the legacyCommandFactory, which was for the old {shown,hidden,selected} model
  // Discover all the legacy edit classes and register as commands
  // const commands = {}
  // for ( const name of Object.keys( vzomePkg.core.edits ) )
  //   commands[ name ] = legacyCommandFactory( documentFactory, name )

