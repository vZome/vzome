
import goldenField from '../fields/golden.js'
import root2Field from '../fields/root2.js'
import root3Field from '../fields/root3.js'
import heptagonField from '../fields/heptagon.js'
import Adapter from './adapter.js'
import { algebraicNumberFactory, JavaDomElement, JsProperties } from './wrappers.js'

import allShapes from '../resources/com/vzome/core/parts/index.js'
import groupResources from '../resources/com/vzome/core/math/symmetry/index.js'

import { com } from '../jsweet/transpiled-java.js'
import { java } from '../jsweet/j4ts-2.0.0/bundle.js'

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

// Initialization

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

export const init = async () =>
{
  const vzomePkg = com.vzome
  const util = java.util

  class ImportColoredMeshJson extends vzomePkg.core.edits.ImportMesh
  {
    parseMeshData( offset, events, registry )
    {
      // TODO: handle projection and scale
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
      coloredMesh.panels.forEach( panel => {
        const points = []
        panel.vertices.forEach( i => {
          points.push( new vzomePkg.core.construction.FreePoint( vertices[ i ] ) )
        } )
        const color = panel.color && vzomePkg.core.construction.Color.parseWebColor( panel.color )
        events.constructionAdded( new vzomePkg.core.construction.PolygonFromVertices( points ), color );
      });
      // TODO: handle panels
    }
  }

  class ImportSimpleMeshJson extends vzomePkg.core.edits.ImportMesh
  {
    parseMeshData( offset, events, registry )
    {
      // TODO: handle projection and scale
      const simpleMesh = JSON.parse( this.meshData )
      const field = registry.getField( simpleMesh.field || 'golden' )
      const vertices = simpleMesh.vertices.map( nums => {
        let vertex = field.createIntegerVectorFromTDs( nums )
        if ( vertex.dimension() > 3 )
            vertex = this.projection.projectImage( vertex, false )
        if ( offset != null )
            vertex = offset.plus( vertex )
        return vertex
      } )
      simpleMesh.edges.forEach( strut => {
        const point1 = new vzomePkg.core.construction.FreePoint( vertices[ strut[ 0 ] ] )
        const point2 = new vzomePkg.core.construction.FreePoint( vertices[ strut[ 1 ] ] )
        events.constructionAdded( point1 )
        events.constructionAdded( point2 )
        events.constructionAdded( new vzomePkg.core.construction.SegmentJoiningPoints( point1, point2 ) );
      });
      simpleMesh.faces.forEach( panel => {
        const points = []
        panel.forEach( i => {
          points.push( new vzomePkg.core.construction.FreePoint( vertices[ i ] ) )
        } )
        events.constructionAdded( new vzomePkg.core.construction.PolygonFromVertices( points ) );
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
    return vzomePkg.core.edits[ editName ] || vzomePkg.core.editor[ editName ]
  }

  const editFactory = ( editor, toolFactories, toolsModel ) => xmlElement =>
  {
    editor.setAdapter( null ) // This should trigger NPEs in any edits that have side-effects in their constructor

    let editName = xmlElement.getLocalName()
    if ( editName === "Snapshot" )
      return null

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
    if ( editClass )
      return new editClass( editor )
    else
      return new vzomePkg.core.editor.CommandEdit( null, editor )
  }

  const resources = {}

  const loadAndInjectResource = async ( path, url ) =>
  {
    const response = await fetch( url )
    if ( ! response.ok ) {
      console.log( `No resource for ${path}` )
      return
    }
    const text = await response.text()
    // Inject the VEF into a static map on ExportedVEFShapes
    if ( text[ 0 ] === "<" ) {
      // HTML from a 404, just skip it
      return
    }
    resources[ path ] = text
    console.log( `injected resource ${path}` )
  }

  // Now we can setup the ResourceLoader; we must do this before initializing the fieldApps,
  //  since they need the colors.properties to create the ExportedVEFShapes.
  vzomePkg.xml.ResourceLoader.setResourceLoader( {
    loadTextResource: path => resources[ path ]
  } )

  // Initialize the field application
  await Promise.all( Object.entries( groupResources ).map( ([ key, value ]) => loadAndInjectResource( `com/vzome/core/math/symmetry/${key}.vef`, value ) ) )
  const properties = new JsProperties( defaults )
  const colors = new vzomePkg.core.render.Colors( properties )

  const fieldApps = {}
  const wrapLegacyField = ( legacyField ) => ({
    origin: () => legacyField.origin( 3 ).getComponents().map( an => an.toTrailingDivisor() )
  })
  const addLegacyField = ( fieldClass, appClass ) =>
  {
    const legacyField = new fieldClass( algebraicNumberFactory )
    legacyField.delegate = wrapLegacyField( legacyField )
    fieldApps[ legacyField.getName() ] = new appClass( legacyField )
  }
  const addNewField = ( field, appClass ) =>
  {
    const legacyField = new vzomePkg.jsweet.JsAlgebraicField( field )
    fieldApps[ field.name ] = new appClass( legacyField )
  }
  addNewField( goldenField, vzomePkg.core.kinds.GoldenFieldApplication )
  addNewField( root2Field, vzomePkg.core.kinds.RootTwoFieldApplication )
  addNewField( root3Field, vzomePkg.core.kinds.RootThreeFieldApplication )
  addNewField( heptagonField, vzomePkg.core.kinds.HeptagonFieldApplication )
  addLegacyField( vzomePkg.fields.sqrtphi.SqrtPhiField, vzomePkg.fields.sqrtphi.SqrtPhiFieldApplication )
  addLegacyField( vzomePkg.core.algebra.SnubCubeField, vzomePkg.core.kinds.SnubCubeFieldApplication )
  addLegacyField( vzomePkg.core.algebra.SuperGoldenField, vzomePkg.core.kinds.DefaultFieldApplication )
  addLegacyField( vzomePkg.core.algebra.PlasticNumberField, vzomePkg.core.kinds.DefaultFieldApplication )
  addLegacyField( vzomePkg.core.algebra.PlasticPhiField, vzomePkg.core.kinds.DefaultFieldApplication )
  addLegacyField( vzomePkg.core.algebra.EdPeggField, vzomePkg.core.kinds.DefaultFieldApplication )
  const getFieldApp = name =>
  {
    let fieldApp = fieldApps[ name ]
    if ( ! fieldApp ) {
      if ( name.startsWith( "polygon" ) ) {
        const nsides = parseInt( name.replace( /^polygon/, '' ) )
        const legacyField = new vzomePkg.core.algebra.PolygonField( "polygon"+nsides, nsides, algebraicNumberFactory )
        legacyField.delegate = wrapLegacyField( legacyField )
        fieldApp = new vzomePkg.core.kinds.PolygonFieldApplication( legacyField )
        fieldApps[ legacyField.getName() ] = fieldApp
      }
    }
    return fieldApp
  }

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
  
  const createRenderer = orbitSource => ({
    name: orbitSource.getShapes().getName(),
    shaper: shaperFactory( vzomePkg, orbitSource ),
    embedding: orbitSource.getEmbedding(),
  })

  const documentFactory = ( fieldName, namespace, xml ) =>
  {
    // This reproduces the DocumentModel constructor pretty faithfully

    const fieldApp = getFieldApp( fieldName )
    if ( !fieldApp )
      return { field: { name: fieldName, unknown: true } }
    const legacyField = fieldApp.getField()
    const field = legacyField.delegate

    const originPoint = new vzomePkg.core.construction.FreePoint( legacyField.origin( 3 ) )

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
    if ( xml ) {
      const symms = xml.getChildElement( "OtherSymmetries" )
      if ( symms ) {
        const nodes = symms.getElementsByTagName( "SymmetrySystem" )
        for ( let i = 0; i < nodes.getLength(); i++ ) {
          const symmElem = nodes.item( i )
          const symmName = symmElem.getAttribute( "name" )
          const symmPerspective = fieldApp.getSymmetryPerspective( symmName )
          const otherSymmetrySystem = new vzomePkg.core.editor.SymmetrySystem( symmElem, symmPerspective, this, colors, true )
          symmetrySystems[ symmName ] = otherSymmetrySystem
        }
      }
    }

    // This has no analogue in Java DocumentModel
    orbitSource.orientations = makeFloatMatrices( orbitSource.getSymmetry().getMatrices() )
    const orbitSetField = {
      __interfaces: [ "com.vzome.core.math.symmetry.OrbitSet.Field" ],
      getGroup: name => symmetrySystems[ name ].getOrbits(),
      getQuaternionSet: name => fieldApp.getQuaternionSymmetry( name )
    }

    const realizedModel = new vzomePkg.jsweet.JsRealizedModel( legacyField )
    const selection = new vzomePkg.jsweet.JsSelection( legacyField )
    const editor = new vzomePkg.jsweet.JsEditorModel( realizedModel, selection, fieldApp, orbitSource, symmetrySystems )
    toolsModel.setEditorModel( editor )

    const format = namespace && vzomePkg.core.commands.XmlSymmetryFormat.getFormat( namespace )
    format && format.initialize( legacyField, orbitSetField, 0, "vZome Online", new util.Properties() )

    const toolFactories = new util.HashMap()
    for ( const symmetrySystem of Object.values( symmetrySystems ) ) {
      symmetrySystem.createToolFactories( toolsModel ) // needed to register built-in tools
    }

    fieldApp.registerToolFactories( toolFactories, toolsModel )
    
    const bookmarkFactory = new vzomePkg.core.tools.BookmarkTool.Factory( toolsModel );
    bookmarkFactory.createPredefinedTool( "ball at origin" );

    const toolsXml = xml && xml.getChildElement( "Tools" )
    toolsXml && toolsModel.loadFromXml( toolsXml )

    const renderer = createRenderer( orbitSource )

    const parseAndPerformEdit = ( xmlElement, mesh ) =>
    {
      const wrappedElement = new JavaDomElement( xmlElement )
      // Note that we do not do editor.setAdapter() yet.  This means that we cannot
      //  deal with edits that have side-effects in their constructors!
      const edit = editFactory( editor, toolFactories, toolsModel )( wrappedElement )
      if ( ! edit )   // Null edit only happens for expected cases (e.g. "Shapshot"); others become CommandEdit.
        return false  //  Not indicating failure, just indicating nothing to record in history
      const { shown, selected, hidden, groups } = mesh
      editor.setAdapter( new Adapter( shown, selected, hidden, groups ) )
      edit.loadAndPerform( wrappedElement, format, editContext )
      return true
    }

    /*
      STATUS:
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
    const serializeLegacyEdit = domElement =>
    {
      const editType = domElement.nodeName
      const result = { editType }
      if ( editType === "Branch" ) {
        result.edits = []
        return result
      }
      // not a branch, if we got here
      domElement.getAttributeNames().forEach( name => {
        if ( name !== 'id' ) result[ name ] = domElement.getAttribute( name )
      })
      if ( domElement.textContent ) {
        const text = domElement.textContent.trim()
        if ( !! text )
          result.textContent = text
      }
      if ( domElement.firstElementChild ) {
        console.log( `Nested XML: ${domElement.nodeName} ${domElement.firstElementChild.outerHTML}` );
      }
      return result
    }

    const createEdit = ( domElement ) =>
    {
      const isBranch = () => domElement.nodeName === "Branch"
      const name = () => domElement.nodeName
      const id = () => domElement.id
      const nextSibling = () => domElement.nextElementSibling && createEdit( domElement.nextElementSibling )
      const firstChild = () => domElement.firstElementChild && createEdit( domElement.firstElementChild )
      const getAttributeNames = () => domElement.getAttributeNames()
      const getAttribute = name => domElement.getAttribute( name )
      const perform = mesh => parseAndPerformEdit( domElement, mesh )
      const serialize = () => serializeLegacyEdit( domElement )
      return {
        nextSibling, firstChild, isBranch, id, perform,   // these are for the interpreter
        name, getAttributeNames, getAttribute, serialize, // these are for the debugger UI
      }
    }

    const configureAndPerformEdit = ( className, config, adapter ) =>
    {
      const edit = editFactory( editor, toolFactories, toolsModel )( new JavaDomElement( { localName: className } ) )
      if ( ! edit )
        return
      editor.setAdapter( adapter )
      edit.configure( new JsProperties( config ) )
      edit.perform()
    }

    return { renderer, createEdit, configureAndPerformEdit, field }
  }

  // Discover all the legacy edit classes and register as commands
  const commands = {}
  for ( const name of Object.keys( vzomePkg.core.edits ) )
    commands[ name ] = legacyCommandFactory( documentFactory, name )

  // Prepare the gridPoints
  const symmPer = fieldApps.golden.getDefaultSymmetryPerspective()
  const orbitSource = new vzomePkg.core.editor.SymmetrySystem( null, symmPer, editContext, colors, true )
  // orbitSource.orientations = makeFloatMatrices( orbitSource.getSymmetry().getMatrices() )
  const blue = [ [0n,0n,1n], [0n,0n,1n], [1n,0n,1n] ]
  const yellow = [ [0n,0n,1n], [1n,0n,1n], [1n,1n,1n] ]
  const red = [ [1n,0n,1n], [0n,0n,1n], [0n,1n,1n] ]
  const green = [ [1n,0n,1n], [1n,0n,1n], [0n,0n,1n] ]
  const gridPoints = vzomePkg.jsweet.JsAdapter.getZoneGrid( orbitSource, blue )
  // store.dispatch( planes.doSetWorkingPlaneGrid( gridPoints ) )

  const parser = createParser( documentFactory )

  await Promise.all( Object.entries( allShapes ).map(
    async ([ family, shapes ]) => {
      await Promise.all( Object.entries( shapes ).map( ([ key, value ]) => loadAndInjectResource( `com/vzome/core/parts/${family}/${key}.vef`, value ) ) )
    }
  ) )

  return { parser, commands, gridPoints }
}

export const coreState = init()

const realizeShape = ( shape ) =>
{
  const vertices = shape.getVertexList().toArray().map( av => {
    const { x, y, z } = av.toRealVector()  // this is too early to do embedding, which is done later, globally
    return { x, y, z }
  })
  const faces = shape.getTriangleFaces().toArray()
  const id = shape.getGuid().toString()
  return { id, vertices, faces }
}

const shaperFactory = ( vzomePkg, orbitSource ) => shapes => instance =>
{
  const { id, vectors } = instance
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
    shapes[ shapeId ] = realizeShape( rm.getShape() )
  }

  // get shape, orientation, color, and position from rm
  const positionAV = rm.getLocationAV() || jsAF.origin( 3 )
  const { x, y, z } = positionAV.toRealVector() // orbitSource.getSymmetry().embedInR3( positionAV )
  const zoneIndex = rm.getStrutZone()
  const rotation = ( zoneIndex && (zoneIndex >= 0) && orbitSource.orientations[ zoneIndex ] ) || [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]
  let finalColor = rm.getColor().getRGB()

  return { id, position: [ x, y, z ], rotation, color: finalColor, shapeId }
}

export const legacyCommandFactory = ( createEditor, className ) => ( config ) => 
{
  const field = undefined // TODO designs.selectField( getState() )
  // TODO const symmetry = designs.selectCurrentSymmetry( getState() )
  // TODO const shapes = designs.selectCurrentShapes( getState() )
  let { shown, hidden, selected } = {} // TODO designs.selectMesh( getState() )
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )
  const adapter = new Adapter( shown, selected, hidden )
  // side-effects will appear in shown, hidden, and selected maps

  createEditor( field.name ).configureAndPerformEdit( className, config, adapter )

  // TODO
  // dispatch( { type: WORK_STARTED } )
  // dispatch( meshChanged( shown, selected, hidden ) )
  // dispatch( { type: WORK_FINISHED } )
}

const assignIds = ( element, id=':' ) => {
  element.id = id
  let child = element.firstElementChild
  let i = 0
  while ( child ) {
    assignIds( child, `${id}${i++}:` )
    child = child.nextElementSibling
  }
  return element
}

const parseVector = ( element, name ) =>
{
  const child = element.getChildElement( name )
  const x = parseFloat( child.getAttribute( "x" ) )
  const y = parseFloat( child.getAttribute( "y" ) )
  const z = parseFloat( child.getAttribute( "z" ) )
  return [ x, y, z ]
}

const parseColor = ( rgbCsv ) =>
{
  const [ r, g, b ] = rgbCsv.split( "," ).map( s => parseInt( s ) )
  const componentToHex = (c) =>
  {
    const hex = c.toString( 16 )
    return hex.length === 1 ? "0" + hex : hex
  }
  return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b)
}

export const parseLighting = ( sceneElement ) =>
{
  const backgroundColor = parseColor( sceneElement.getAttribute( "background" ) )
  return {
    backgroundColor,
    // TODO: parse these too, in case they change someday
    ambientColor: '#292929',
    directionalLights: [ // These are the vZome defaults, for consistency
      { direction: [ 1, -1, -1 ], color: '#EBEBE4' },
      { direction: [ -1, 0, 0 ], color: '#E4E4EB' },
      { direction: [ 0, 0, -1 ], color: '#1E1E1E' },
    ]
  }
}

export const parseViewXml = ( viewingElement ) =>
{
  const viewModel = viewingElement.getChildElement( "ViewModel" )
  const distance = parseFloat( viewModel.getAttribute( "distance" ) )
  const near = parseFloat( viewModel.getAttribute( "near" ) )
  const far = parseFloat( viewModel.getAttribute( "far" ) )
  const lookAt = parseVector( viewModel, "LookAtPoint" )
  const up = parseVector( viewModel, "UpDirection" )
  const lookDirection = parseVector( viewModel, "LookDirection" )
  const position = lookAt.map( (e,i) => e - distance * lookDirection[ i ] )
  return { position, lookAt, up, near, far }
}

export const createParser = ( createDocument ) => ( xmlText ) =>
{
  try {
    const domDoc = new DOMParser().parseFromString( xmlText, "application/xml" );
    let vZomeRoot = new JavaDomElement( domDoc.firstElementChild )

    const namespace = vZomeRoot.getAttribute( "xmlns:vzome" )
    const fieldName = vZomeRoot.getAttribute( "field" )

    const { renderer, createEdit, field } = createDocument( fieldName, namespace, vZomeRoot )

    const viewing = vZomeRoot.getChildElement( "Viewing" )
    const camera = viewing && parseViewXml( viewing )

    const scene = vZomeRoot.getChildElement( "sceneModel" )
    const lighting = scene && parseLighting( scene )

    const edits = assignIds( vZomeRoot.getChildElement( "EditHistory" ).nativeElement )
    // Note: I'm adding one so that this matches the assigned ID of the next edit to do
    const targetEdit = `:${edits.getAttribute( "editNumber" )}:`
    const firstEdit = createEdit && createEdit( edits.firstElementChild )

    return { firstEdit, camera, field, targetEdit, renderer, lighting }
  } catch (error) {
    console.log( `%%%%%%%%%%%%%%%%%%% legacyjava.js parser failed: ${error}` )
    return null
  }
}
