
import * as mesh from './mesh'
import { commandsDefined } from '../commands'
import * as planes from './planes'
import goldenField from '../fields/golden'
import { startProgress, stopProgress } from './progress'
import { parseViewXml } from './camera'
import { showAlert } from './alerts'
import { fetchModel } from './files'
import * as designs from './models'
import { meshChanged } from './mesh'
import * as shapers from './shapers'
import { ActionCreators } from 'redux-undo';
import Adapter from './adapter'


// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

const PARSER_READY = 'PARSER_READY'
const WORK_STARTED = 'WORK_STARTED'
const WORK_FINISHED = 'WORK_FINISHED'

const initialState = {
  readOnly: false,
  parser: undefined,       // ANTI-PATTERN, not a plain Object
  working: false,
}

export const reducer = ( state = initialState, action ) =>
{
  switch ( action.type ) {

    case PARSER_READY: {
      return { ...state, parser: action.payload }
    }

    case WORK_STARTED: {
      return { ...state, working: true }
    }

    case WORK_FINISHED: {
      return { ...state, working: false }
    }

    default:
      return state
  }
}

// Copied from core/src/main/resources/com/vzome/core/editor/defaultPrefs.properties
const defaults = {

  "color.red": "175,0,0",
  "color.yellow": "240,160,0",
  "color.blue": "0,118,149",
  "color.green": "0,141,54",
  "color.orange": "220,76,0",
  "color.purple": "108,0,198",
  "color.black": "30,30,30",
  "color.white": "225,225,225",
  "color.olive": "100,113,0",
  "color.lavender": "175,135,255",
  "color.maroon": "117,0,50",
  "color.rose": "255,51,143",
  "color.navy": "0,0,153",
  "color.brown": "107,53,26",
  "color.apple": "116,195,0",
  "color.sand": "154,117,74",
  "color.turquoise": "18,205,148",
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

const knownOrbitNames = [
  "apple",
  "black",
  "blue",
  "brown",
  "cinnamon",
  "connector",
  "coral",
  "green",
  "lavender",
  "maroon",
  "navy",
  "olive",
  "orange",
  "purple",
  "red",
  "rose",
  "sand",
  "snubDiagonal",
  "snubPentagon",
  "snubTriangle",
  "spruce",
  "sulfur",
  "turquoise",
  "yellow",
]

// Initialization

const makeQuaternions = ( matrices ) =>
{
  return matrices.map( am => {
    let m = [[],[],[]]
    for ( let i = 0; i < 3; i++) {
      for ( let j = 0; j < 3; j++) {
        const an = am.getElement( i, j );
        m[ i ][ j ] = an.evaluate()
      }
    }
    return matrix2quat( m )
  });
}

// Due to Mike Day, Insomniac Games
//   https://d3cw3dd2w32x2b.cloudfront.net/wp-content/uploads/2015/01/matrix-to-quat.pdf
const matrix2quat = ( m ) =>
{
  const [ [ m00, m10, m20 ],
          [ m01, m11, m21 ],
          [ m02, m12, m22 ],
        ] = m
  let t
  let q
  if (m22 < 0) {
    if (m00 > m11) {
      t = 1 + m00 - m11 - m22
      q = [ t, m01 + m10, m20 + m02, m12 - m21 ]
    }
    else {
      t = 1 - m00 + m11 - m22
      q = [ m01 + m10, t, m12 + m21, m20 - m02 ]
    }
  }
  else {
    if (m00 < -m11) {
      t = 1 - m00 - m11 + m22
      q = [ m20 + m02, m12 + m21, t, m01 - m10 ]
    }
    else {
      t = 1 + m00 + m11 + m22
      q = [ m12 - m21, m20 - m02, m01 - m10, t ]
    }
  }
  return q.map( x => x * 0.5 / Math.sqrt(t) )
}

export const init = async ( window, store ) =>
{
  const vzomePkg = window.com.vzome
  const util = window.java.util

  class ImportColoredMeshJson extends vzomePkg.core.edits.ImportMesh
  {
    parseMeshData( offset, events, registry )
    {
      const coloredMesh = JSON.parse( this.meshData )
      const field = registry.getField( coloredMesh.field )
      const vertices = coloredMesh.vertices.map( nums => {
        let vertex = field.createIntegerVectorFromTDs( nums )
        if ( vertex.dimension() > 3 )
            vertex = this.projection.projectImage( vertex, false )
        if ( offset != null )
            vertex = offset.plus( vertex )
        return vertex
      } )
      // TODO: handle legacy format; see ColoredMeshJson.java
      coloredMesh.balls.forEach( ball => {
        const vertex = vertices[ ball.vertex ]
        const color = ball.color && vzomePkg.core.construction.Color.parseWebColor( ball.color )
        events.constructionAdded( new vzomePkg.core.construction.FreePoint( vertex ), color );
      });
      coloredMesh.struts.forEach( strut => {
        const point1 = new vzomePkg.core.construction.FreePoint( vertices[ strut.vertices[ 0 ] ] )
        const point2 = new vzomePkg.core.construction.FreePoint( vertices[ strut.vertices[ 1 ] ] )
        const color = strut.color && vzomePkg.core.construction.Color.parseWebColor( strut.color )
        events.constructionAdded( new vzomePkg.core.construction.SegmentJoiningPoints( point1, point2 ), color );
      });
      // TODO: handle panels
    }
  }

  const xmlToEditClass = editName =>
  {
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
    return vzomePkg.core.edits[ editName ] || vzomePkg.core.editor[ editName ]
  }

  const editFactory = ( editor, toolFactories, toolsModel ) => xmlElement =>
  {
    let editName = xmlElement.getLocalName()
    if ( editName === "Snapshot" )
      return null

    if ( editName === "ImportColoredMeshJson" )
      return new ImportColoredMeshJson( editor )

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
    if ( editClass )
      return new editClass( editor )
    else
      return new vzomePkg.core.editor.CommandEdit( null, editor )
  }

  const injectResource = async ( path ) =>
  {
    const fullPath = `/app/resources/${path}`
    const response = await fetch( fullPath )
    if ( ! response.ok ) {
      console.log( `No resource for ${fullPath}` )
      return
    }
    const text = await response.text()
    // Inject the VEF into a static map on ExportedVEFShapes
    if ( text[ 0 ] === "<" ) {
      console.log( `No resource for ${fullPath}` )
      return
    }
    vzomePkg.xml.ResourceLoader.injectResource( path, text )
    console.log( `injected resource ${path}` )
  }

  // Initialize the field application
  await Promise.all( [
    injectResource( 'com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef' ),
    injectResource( 'com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef' ),
    injectResource( 'com/vzome/core/math/symmetry/H4roots.vef' ),
  ] )
  const properties = new Properties( defaults )
  const colors = new vzomePkg.core.render.Colors( properties )
  const gfield = new vzomePkg.jsweet.JsAlgebraicField( goldenField )
  const fieldApps = { golden: new vzomePkg.core.kinds.GoldenFieldApplication( gfield ) }

  // This object implements the UndoableEdit.Context interface
  const editContext = {
    // Since we are not creating Branch edits, this should never be used
    createEdit: () => { throw new Error( "createEdit should never be called" ) },

    createLegacyCommand: name => {
      const constructor = vzomePkg.core.commands[ name ]
      if ( constructor )
        return new constructor()
      else
        throw new Error( `${name} command is not available yet`)
    },
    
    // We will do our own edit recording, so this is just perform
    performAndRecord: edit => edit.perform()
  }

  const documentFactory = ( fieldName, namespace, xml ) =>
  {
    // This reproduces the DocumentModel constructor pretty faithfully

    const fieldApp = fieldApps[ fieldName ]
    if ( ! fieldApp )
      throw new Error( `Field "${fieldName}" is not supported yet.` )
    const field = fieldApp.getField()

    const originPoint = new vzomePkg.core.construction.FreePoint( field.origin( 3 ) )

    const systemXml = xml && xml.getChildElement( "SymmetrySystem" )
    const symmName = systemXml && systemXml.getAttribute( "name" )
    const symmPer = ( symmName && fieldApp.getSymmetryPerspective( symmName ) ) || fieldApp.getDefaultSymmetryPerspective()

    const toolsModel = new vzomePkg.core.editor.ToolsModel( editContext, originPoint )

    // Initialize the default SymmetrySystems from the FieldApplication
    const symmetrySystems = {}
    const symmPerspectives = fieldApp.getSymmetryPerspectives().iterator()
    while ( symmPerspectives.hasNext() ) {
      const perspective = symmPerspectives.next()
      const osm = new vzomePkg.core.editor.SymmetrySystem( null, perspective, editContext, colors, true )
      symmetrySystems[ osm.getName() ] = osm
    }

    // Now overwrite some or all of those default SymmetrySystems with those stored in the file.
    //   This is important mostly for the automatic orbits, but can also carry color overrides.
    const orbitSource = new vzomePkg.core.editor.SymmetrySystem( systemXml, symmPer, editContext, colors, true )
    symmetrySystems[ symmPer.getName() ] = orbitSource
    // if ( this .mXML != null ) {
    //     NodeList nl = this .mXML .getElementsByTagName( "OtherSymmetries" );
    //     if ( nl .getLength() != 0 ) {
    //         xml = (Element) nl .item( 0 );
    //         NodeList nodes = xml .getElementsByTagName( "SymmetrySystem" );
    //         for ( int i = 0; i < nodes .getLength(); i++ ) {
    //             Node node = nodes .item( i );
    //             if ( node instanceof Element ) {
    //                 Element symmElem = (Element) node;
    //                 String symmName = symmElem .getAttribute( "name" );  
    //                 symmPerspective = kind .getSymmetryPerspective( symmName );
    //                 SymmetrySystem otherSymmetrySystem = new SymmetrySystem( symmElem, symmPerspective, this, app .getColors(), true );
    //                 this .symmetrySystems .put( symmName, otherSymmetrySystem );
    //             }
    //         }
    //     }
    //     }

    orbitSource.quaternions = makeQuaternions( orbitSource.getSymmetry().getMatrices() )
    const orbitSetField = {
      __interfaces: [ "com.vzome.core.math.symmetry.OrbitSet.Field" ],
      getGroup: name => symmetrySystems[ name ].getOrbits(),
      getQuaternionSet: name => fieldApp.getQuaternionSymmetry( name )
    }

    const format = namespace && vzomePkg.core.commands.XmlSymmetryFormat.getFormat( namespace )
    format && format.initialize( field, orbitSetField, 0, "vZome Online", new util.Properties() )

    const toolFactories = new util.HashMap()
    fieldApp.registerToolFactories( toolFactories, toolsModel )

    const realizedModel = new vzomePkg.jsweet.JsRealizedModel( field )
    const selection = new vzomePkg.jsweet.JsSelection( field )
    const editor = new vzomePkg.jsweet.JsEditorModel( realizedModel, selection, fieldApp, orbitSource, symmetrySystems )
    
    toolsModel.setEditorModel( editor )
    orbitSource.createToolFactories( toolsModel ) // needed to register built-in tools
    const bookmarkFactory = new vzomePkg.core.tools.BookmarkTool.Factory( toolsModel );
    bookmarkFactory.createPredefinedTool( "ball at origin" );

    const toolsXml = xml && xml.getChildElement( "Tools" )
    toolsXml && toolsModel.loadFromXml( toolsXml )

    const shaper = shaperFactory( vzomePkg, orbitSource )
    shaper.shapesName = orbitSource.getShapes().getName()

    const createEditFromXml = xmlElement =>
    {
      const edit = editFactory( editor, toolFactories, toolsModel )( xmlElement )
      if ( ! edit )
        return undefined
      edit.deserializeAndPerform = ( adapter ) => {
        editor.setAdapter( adapter )
        edit.loadAndPerform( xmlElement, format, editContext )
      }
      return edit
    }

    const configureAndPerformEdit = ( className, config, adapter ) =>
    {
      const edit = editFactory( editor, toolFactories, toolsModel )( new JavaDomElement( { localName: className } ) )
      if ( ! edit )
        return
      editor.setAdapter( adapter )
      edit.configure( new Properties( config ) )
      edit.perform()
    }

    return { shaper, createEditFromXml, configureAndPerformEdit }
  }

  // Discover all the legacy edit classes and register as commands
  const commands = {}
  for ( const name of Object.keys( vzomePkg.core.edits ) )
    commands[ name ] = legacyCommandFactory( documentFactory, name )
  store.dispatch( commandsDefined( commands ) )

  // Prepare the orbitSource for resolveShapes
  const symmPer = fieldApps.golden.getDefaultSymmetryPerspective()
  const orbitSource = new vzomePkg.core.editor.SymmetrySystem( null, symmPer, editContext, colors, true )
  orbitSource.quaternions = makeQuaternions( orbitSource.getSymmetry().getMatrices() )
  const shaper = shaperFactory( vzomePkg, orbitSource )
  const shaperName = orbitSource.getShapes().getName()    

  const blue = [ [0,0,1], [0,0,1], [1,0,1] ]
  const yellow = [ [0,0,1], [1,0,1], [1,1,1] ]
  const red = [ [1,0,1], [0,0,1], [0,1,1] ]
  const green = [ [1,0,1], [1,0,1], [0,0,1] ]
  const gridPoints = vzomePkg.jsweet.JsAdapter.getZoneGrid( orbitSource, blue )
  store.dispatch( planes.doSetWorkingPlaneGrid( gridPoints ) )

  const parser = createParser( documentFactory )

  // TODO: fetch all shape VEFs in a ZIP, then inject each
  await Promise.all( knownOrbitNames.map( name => injectResource( `com/vzome/core/parts/octahedral/${name}.vef` ) ) )
  await Promise.all( knownOrbitNames.map( name => injectResource( `com/vzome/core/parts/default/${name}.vef` ) ) )
  // now we are finally ready to shape instances
  store.dispatch( shapers.shaperDefined( shaperName, shaper ) )
  store.dispatch( { type: PARSER_READY, payload: parser } )
  // if ( ! store.getState().workingPlane )
  //   store.dispatch( fetchModel( "/app/models/vZomeLogo.vZome" ) )
}

const embedShape = ( shape ) =>
{
  const vertices = shape.getVertexList().toArray().map( av => {
    const { x, y, z } = av.toRealVector()
    return { x, y, z }
  })
  const faces = shape.getTriangleFaces().toArray()
  const id = shape.getGuid().toString()
  return { id, vertices, faces }
}

const shaperFactory = ( vzomePkg, orbitSource ) => shapes => instance =>
{
  const { id, vectors, color } = instance
  const jsAF = orbitSource.getSymmetry().getField()

  const shown = new Map()
  shown.set( id, instance )
  const adapter = new Adapter( shown, new Map(), new Map() )

  const man = vzomePkg.jsweet.JsManifestation.manifest( vectors, jsAF, adapter )
  const rm = new vzomePkg.core.render.RenderedManifestation( man, orbitSource )
  rm.resetAttributes( false, true )

  // may be a zero-length strut, no shape
  if ( !rm.getShape() )
    return undefined
  
  // is the shape new?
  const shapeId = rm.getShapeId().toString()
  if ( ! shapes[ shapeId ] ) {
    shapes[ shapeId ] = embedShape( rm.getShape() )
  }

  // get shape, orientation, color, and position from rm
  const positionAV = rm.getLocationAV() || jsAF.origin( 3 )
  const { x, y, z } = orbitSource.getSymmetry().embedInR3( positionAV )
  const quatIndex = rm.getStrutZone()
  const rotation = ( quatIndex && (quatIndex >= 0) && orbitSource.quaternions[ quatIndex ] ) || [0,0,0,1]
  const finalColor = rm.getColor().getRGB()

  return { id, position: [ x, y, z ], rotation, color: finalColor, shapeId }
}

class Properties
{
  constructor( config )
  {
    this.config = config
  }

  getProperty( key )
  {
    return this.config[ key ]
  }

  get( key )
  {
    return this.config[ key ]
  }
}

export const legacyCommandFactory = ( createEditor, className ) => ( config ) => ( dispatch, getState ) =>
{
  const field = designs.selectCurrentField( getState() )
  // TODO const symmetry = designs.selectCurrentSymmetry( getState() )
  // TODO const shapes = designs.selectCurrentShapes( getState() )
  let { shown, hidden, selected } = designs.selectCurrentMesh( getState() )
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )
  const adapter = new Adapter( shown, selected, hidden )
  // side-effects will appear in shown, hidden, and selected maps

  createEditor( field.name ).configureAndPerformEdit( className, config, adapter )

  dispatch( { type: WORK_STARTED } )
  dispatch( meshChanged( shown, selected, hidden ) )
  dispatch( { type: WORK_FINISHED } )
}

class JavaDomNodeList
{
  constructor( nodeList )
  {
    this.nativeNodeList = nodeList
    this.__interfaces = [ "org.w3c.dom.NodeList" ]
  }

  getLength()
  {
    return this.nativeNodeList.length
  }

  item( i )
  {
    const node = this.nativeNodeList.item( i )
    if ( node.nodeType === 1 )
      return new JavaDomElement( node )
    else
      return node
  }
}

class JavaDomElement
{
  constructor( element )
  {
    this.nativeElement = element
    this.__interfaces = [ "org.w3c.dom.Element" ]
  }

  getAttribute( name )
  {
    return this.nativeElement.getAttribute && this.nativeElement.getAttribute( name )
  }

  getLocalName()
  {
    return this.nativeElement.localName
  }

  getTextContent()
  {
    return this.nativeElement.textContent
  }

  getChildNodes()
  {
    return new JavaDomNodeList( this.nativeElement.childNodes )
  }

  getChildElement( name )
  {
    let target = this.nativeElement.firstElementChild
    while ( target && name.toLowerCase() !== target.nodeName.toLowerCase() )
      target = target.nextElementSibling
    return target && new JavaDomElement( target )
  }

  getElementsByTagName( name )
  {
    let target = this.nativeElement.firstElementChild
    const results = []
    while ( target ) {
      if ( name.toLowerCase() === target.nodeName.toLowerCase() ) {
        results.push( new JavaDomElement( target ) )
      }
      target = target.nextElementSibling
    }
    return { getLength: () => results.length, item: i => results[ i ] }
  }
}

export const createParser = ( createDocument ) => ( name, xmlText, dispatch, getState ) =>
{
  dispatch( startProgress( "Parsing vZome file..." ) )

  const fields = getState().fields

  try {
    const parser = new DOMParser();
    const domDoc = parser.parseFromString( xmlText, "application/xml" );
    let vZomeRoot = new JavaDomElement( domDoc.firstElementChild )

    const namespace = vZomeRoot.getAttribute( "xmlns:vzome" )
    const fieldName = vZomeRoot.getAttribute( "field" )

    const origin = goldenField.origin( 3 ) // TODO use the field name
    const originBall = mesh.createInstance( [ origin ] )
    const shown = new Map().set( originBall.id, originBall )
    const hidden = new Map()
    const selected = new Map()  

    const { shaper, createEditFromXml } = createDocument( fieldName, namespace, vZomeRoot )

    // We don't want to dispatch all the edits, which can trigger tons of
    //  overhead and re-rendering.  Instead, we'll build up a design
    //  by calling the designReducer manually.
    let design = designs.initializeDesign( fields[ fieldName ], shaper.name )

    // const shaper = resolverFactory( orbitSource )
    dispatch( shapers.shaperDefined( shaper.shapesName, shaper ) )

    const history = vZomeRoot.getChildElement( "EditHistory" ).nativeElement
    // TODO: use editNumber
    const targetEdit = parseInt( history.getAttribute( "editNumber" ) )
    let currentEdit = 0
    let editElement = history.firstElementChild

    const performEdits = ( editElement, adapter, recordEdit, increment ) =>
    {
      do {
        console.log( editElement.outerHTML )
        if ( editElement.nodeName === "Branch" ) {
          const sandbox = adapter.clone()
          performEdits( editElement.firstElementChild, sandbox, () => ({}), 0 ) // don't record mesh changes for branches, don't increment currentEdit
          // we discard the cloned sandbox adapter
        } else {
          const wrappedElement = new JavaDomElement( editElement )
          adapter = adapter.clone()  // each command builds on the last
          // editor.setAdapter( adapter )
          const edit = createEditFromXml( wrappedElement )
          // null edit only happens for expected cases (e.g. "Shapshot"); others become CommandEdit
          if ( edit ) {
            edit.deserializeAndPerform( adapter )
            design = designs.designReducer( design, recordEdit( adapter ) )
          }
        }
        currentEdit += increment
        editElement = editElement.nextElementSibling
      }
      while ( editElement );
    }

    // TODO: move this to Adapter itself?
    const recordEdit = ( { shown, selected, hidden } ) => mesh.meshChanged( shown, selected, hidden )

    const adapter = new Adapter( shown, selected, hidden )
    performEdits( editElement, adapter, recordEdit, 1 )

    console.log( `target is ${targetEdit}, currentEdit is ${currentEdit}` )
    while ( currentEdit > targetEdit ) {
      design = designs.designReducer( design, ActionCreators.undo() )
      --currentEdit
    }
    console.log( `target is ${targetEdit}, currentEdit is ${currentEdit}` )
    
    const viewing = vZomeRoot.getChildElement( "Viewing" )
    if ( viewing ) {
      design = designs.designReducer( design, parseViewXml( viewing ) )
    }
    
    const extIndex = name.lastIndexOf( '.' )
    if ( extIndex > 0 ) {
      name = name.substring( 0, extIndex )
    }
    dispatch( designs.loadedDesign( name, design ) )
  } catch (error) {
    console.log( error )
    dispatch( showAlert( `Unable to parse model file: ${name};\n ${error.message}` ) )
  }

  dispatch( stopProgress() )
}
