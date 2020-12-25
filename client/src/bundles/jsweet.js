
import * as mesh from './mesh'
import { commandsDefined } from '../commands'
import * as planes from './planes'
import goldenField from '../fields/golden'
import { startProgress, stopProgress } from './progress'
import { parseViewXml } from './camera'
import { showAlert } from './alerts'
import { fetchModel } from './files'
import * as models from './models'
import { meshChanged } from './mesh'


// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

const PARSER_READY = 'PARSER_READY'
const RESOLVER_READY = 'RESOLVER_READY'
const WORK_STARTED = 'WORK_STARTED'
const WORK_FINISHED = 'WORK_FINISHED'

const initialState = {
  readOnly: false,
  resolver: undefined,       // ANTI-PATTERN, not a plain Object
  parser: undefined,       // ANTI-PATTERN, not a plain Object
  working: false,
}

export const reducer = ( state = initialState, action ) =>
{
  switch ( action.type ) {

    case PARSER_READY: {
      return { ...state, parser: action.payload }
    }

    case RESOLVER_READY: {
      return { ...state, resolver: action.payload }
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
    let editClass = vzomePkg.core.edits[ editName ]
    if ( ! editClass )
      editClass = vzomePkg.core.editor[ editName ]
    return editClass
  }

  const createEdit = ( xmlElement, editor ) =>
  {
    let editName = xmlElement.getLocalName()
    if ( editName === "Snapshot" )
      return null
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
  const context = new vzomePkg.jsweet.JsEditContext()
  const gfield = new vzomePkg.jsweet.JsAlgebraicField( goldenField )
  const fieldApps = { golden: new vzomePkg.core.kinds.GoldenFieldApplication( gfield ) }

  const formatFactory = ( namespace, fieldName, systemXml ) =>
  {
    const fieldApp = fieldApps[ fieldName ]
    if ( ! fieldApp )
      throw new Error( `Field "${fieldName}" is not supported yet.` )
    const field = fieldApp.getField()
    const symmName = systemXml? systemXml.getAttribute( "name" ) : "icosahedral"
    const format = vzomePkg.core.commands.XmlSymmetryFormat.getFormat( namespace )
    const symmPer = fieldApp.getSymmetryPerspective( symmName )
    if ( ! symmPer )
      throw new Error( `Symmetry "${symmName}" is not supported yet.` )
    const orbitSource = new vzomePkg.core.editor.SymmetrySystem( systemXml, symmPer, context, colors, true )
    orbitSource.quaternions = makeQuaternions( orbitSource.getSymmetry().getMatrices() )
    const orbitSetField = new vzomePkg.jsweet.JsOrbitSetField( orbitSource )
    format.initialize( field, orbitSetField, 0, "vZome Online", new util.Properties() )
    format.orbitSource = orbitSource
    return format
  }

  const createEditor = ( adapter, fieldName ) =>
  {
    const fieldApp = fieldApps[ fieldName ]
    const field = fieldApp.getField()
    const realizedModel = new vzomePkg.jsweet.JsRealizedModel( field, adapter )
    const selection = new vzomePkg.jsweet.JsSelection( field, adapter )
    return new vzomePkg.jsweet.JsEditorModel( realizedModel, selection, fieldApp )
  }

  // This object implements the UndoableEdit.Context interface
  const editContext = {
    // Since we are not creating Branch edits, this should never be used
    createEdit: () => { throw new Error( "createEdit should never be called" ) },

    createLegacyCommand: name => new vzomePkg.core.commands[ name ](),
    
    // We will do our own edit recording, so this is just perform
    performAndRecord: edit => edit.perform()
  }

  // Discover all the legacy edit classes and register as commands
  const commands = {}
  for ( const [ name, editClass ] of Object.entries( vzomePkg.core.edits ) )
    commands[ name ] = legacyCommand( createEdit, createEditor, name )
  store.dispatch( commandsDefined( commands ) )

  // Prepare the orbitSource for resolveShapes
  const symmPer = fieldApps.golden.getDefaultSymmetryPerspective()
  const orbitSource = new vzomePkg.core.editor.SymmetrySystem( null, symmPer, context, colors, true )
  orbitSource.quaternions = makeQuaternions( orbitSource.getSymmetry().getMatrices() )
  const resolver = resolverFactory( vzomePkg )( orbitSource )

  const blue = [ [0,0,1], [0,0,1], [1,0,1] ]
  const yellow = [ [0,0,1], [1,0,1], [1,1,1] ]
  const red = [ [1,0,1], [0,0,1], [0,1,1] ]
  const green = [ [1,0,1], [1,0,1], [0,0,1] ]
  const gridPoints = vzomePkg.jsweet.JsAdapter.getZoneGrid( orbitSource, blue )
  store.dispatch( planes.doSetWorkingPlaneGrid( gridPoints ) )

  const parser = createParser( editContext, createEditor, createEdit, formatFactory, resolverFactory( vzomePkg ) )

  // TODO: fetch all shape VEFs in a ZIP, then inject each
  Promise.all( knownOrbitNames.map( name => injectResource( `com/vzome/core/parts/octahedral/${name}.vef` ) ) )
    .then( () => {
  Promise.all( knownOrbitNames.map( name => injectResource( `com/vzome/core/parts/default/${name}.vef` ) ) )
    .then( () => {
      // now we are finally ready to resolve instance shapes
      store.dispatch( { type: RESOLVER_READY, payload: resolver } )
      store.dispatch( { type: PARSER_READY, payload: parser } )
      if ( ! store.getState().workingPlane )
        store.dispatch( fetchModel( "/app/models/vZomeLogo.vZome" ) )
    })
  })
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

const resolverFactory = vzomePkg => orbitSource => shapes => instance =>
{
  const { id, vectors, color } = instance
  const jsAF = orbitSource.getSymmetry().getField()

  const shown = new Map()
  shown.set( id, instance )
  const adapter = new Adapter( shown, new Map(), new Map() )

  const man = vzomePkg.jsweet.JsManifestation.manifest( vectors, jsAF, adapter )
  const rm = new vzomePkg.core.render.RenderedManifestation( man, orbitSource )
  rm.resetAttributes( orbitSource, orbitSource.getShapes(), false, true )

  // may be a zero-length strut, no shape
  if ( !rm.getShape() )
    return undefined
  
  // is the shape new?
  const shapeId = rm.getShapeId().toString()
  if ( ! shapes[ shapeId ] ) {
    shapes[ shapeId ] = embedShape( rm.getShape() )
  }

  // get shape, orientation, color from rm
  const quatIndex = rm.getStrutZone()
  const rotation = ( quatIndex && (quatIndex >= 0) && orbitSource.quaternions[ quatIndex ] ) || [0,0,0,1]
  const finalColor = rm.getColor().getRGB()

  return { id, rotation, color: finalColor, shapeId }
}

class Adapter
{
  constructor( shown, selected, hidden )
  {
    this.shown = shown
    this.hidden = hidden
    this.selected = selected
  }

  clone()
  {
    return new Adapter( new Map( this.shown ), new Map( this.selected ), new Map( this.hidden ) )
  }

  selectAll()
  {
    const temp = this.selected
    this.selected = this.shown
    this.shown = temp
  }

  clearSelection()
  {
    const temp = this.shown
    this.shown = this.selected
    this.selected = temp
  }

  delete( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    this.shown.delete( id )
    this.selected.delete( id )
    this.hidden.delete( id )
  }

  select( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    const instance = this.shown.get( id )
    if ( ! instance )
      throw new Error( `No shown instance to select at ${id}`)
    this.shown.delete( id )
    this.selected.set( id, instance )
  }

  unselect( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    const instance = this.selected.get( id )
    this.selected.delete( id )
    this.shown.set( id, instance )
  }

  selectionSize()
  {
    return this.selected.size
  }

  selectedIterator()
  {
    return Array.from( this.selected.values() ).map( instance => instance.vectors ).values()
  }

  manifestationRendered( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    return this.shown.has( id ) || this.selected.has( id )
  }

  manifestationSelected( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    return this.selected.has( id )
  }

  manifestationHasColor( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    const existing = this.shown.get( id ) || this.selected.get( id )
    return !!existing.color
  }

  manifestationColor( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    const existing = this.shown.get( id ) || this.selected.get( id )
    return existing.color
  }

  setManifestationColor( vectors, color )
  {
    const instance = mesh.createInstance( vectors )
    instance.color = color
    if ( this.shown.has( instance.id ) ) {
      this.shown.set( instance.id, instance )
    } else if ( this.selected.has( instance.id ) ) {
      this.selected.set( instance.id, instance )
    }
  }

  findOrCreateManifestation( vectors )
  {
    const created = mesh.createInstance( vectors )
    const { id } = created
    const existing = this.shown.get( id ) || this.hidden.get( id ) || this.selected.get( id )
    if ( existing )
      return existing.vectors;
    // TODO avoid creating zero-length struts, to match Java semantics of RMI.findConstruction
    return vectors
  }

  showManifestation( vectors )
  {
    let instance = mesh.createInstance( vectors )
    instance = this.shown.get( instance.id ) || this.selected.get( instance.id ) || this.hidden.get( instance.id ) || instance
    this.selected.delete( instance.id ) || this.hidden.delete( instance.id )
    this.shown.set( instance.id, instance )
  }

  hideManifestation( vectors )
  {
    let instance = mesh.createInstance( vectors )
    instance = this.shown.get( instance.id ) || this.selected.get( instance.id ) || this.hidden.get( instance.id ) || instance
    this.selected.delete( instance.id ) || this.shown.delete( instance.id )
    this.hidden.set( instance.id, instance )
  }

  allIterator()
  {
    const shownArray = Array.from( this.shown.values() )
    const selectedArray = Array.from( this.selected.values() )
    const hiddenArray = Array.from( this.hidden.values() )
    const all = [ ...shownArray, ...selectedArray, ...hiddenArray ]
    return all.map( instance => instance.vectors ).values()
  }
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

export const legacyCommand = ( createEdit, createEditor, className ) => ( config ) => ( dispatch, getState ) =>
{
  let { mesh, fieldName } = models.selectCurrentModel( getState() )
  let { shown, hidden, selected } = mesh
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )
  const adapter = new Adapter( shown, selected, hidden )
  const editor = createEditor( adapter, fieldName )
  const edit = createEdit( new JavaDomElement( { localName: className } ), editor )

  edit.configure( new Properties( config ) )

  edit.perform()  // side-effects will appear in shown, hidden, and selected maps

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
    return this.nativeElement.getAttribute( name )
  }

  getLocalName()
  {
    return this.nativeElement.localName
  }

  getChildNodes()
  {
    return new JavaDomNodeList( this.nativeElement.childNodes )
  }
}

export const createParser = ( editContext, createEditor, createEdit, formatFactory, resolverFactory ) => ( name, xmlText, dispatch, getState ) =>
{
  // I tried using JXON here, but wants to make the EditHistory into an object not an array.
  // I could also just roll my own, ala https://developer.mozilla.org/en-US/docs/Archive/JXON,
  //   but I think there would be enough special cases that I might as well just use the DOM.

  dispatch( startProgress( "Parsing vZome file..." ) )

  const fields = getState().fields

  const getChildElement = ( parent, name ) =>
  {
    let target = parent.firstElementChild
    while ( target && name.toLowerCase() !== target.nodeName.toLowerCase() )
      target = target.nextElementSibling
    return target
  }

  const parser = new DOMParser();
  const domDoc = parser.parseFromString( xmlText, "application/xml" );
  let vZomeRoot = domDoc.firstElementChild
  console.log( vZomeRoot )

  try {
    const namespace = vZomeRoot.getAttribute( "xmlns:vzome" )
    const fieldName = vZomeRoot.getAttribute( "field" )

    dispatch( models.startLoadingModel( name, fields[ fieldName ] ) )

    const origin = goldenField.origin( 3 ) // TODO use the field name
    const originBall = mesh.createInstance( [ origin ] )
    const shown = new Map().set( originBall.id, originBall )
    const hidden = new Map()
    const selected = new Map()  

    const systemXml = getChildElement( vZomeRoot, "SymmetrySystem" )
    const system = systemXml && new JavaDomElement( systemXml )
    const format = formatFactory( namespace, fieldName, system )

    const resolver = resolverFactory( format.orbitSource )
    dispatch( { type: RESOLVER_READY, payload: resolver } )

    const history = getChildElement( vZomeRoot, "EditHistory" )
    const editNumber = history.getAttribute( "editNumber" )
    let editElement = history.firstElementChild

    const performEdits = ( editElement, adapter, publishMesh ) =>
    {
      do {
        console.log( editElement.outerHTML )
        if ( editElement.nodeName === "Branch" ) {
          const sandbox = adapter.clone()
          performEdits( editElement.firstElementChild, sandbox, () => {} ) // nothing published
          // we discard the cloned sandbox adapter
        } else {
          const wrappedElement = new JavaDomElement( editElement )
          adapter = adapter.clone()  // each command builds on the last
          const editor = createEditor( adapter, fieldName )
          const edit = createEdit( wrappedElement, editor )
          // null edit only happens for expected cases (e.g. "Shapshot"); others become CommandEdit
          if ( edit ) {
            edit.loadAndPerform( wrappedElement, format, editContext ) // a fixed editContext is sufficient for us
            publishMesh( adapter )
          }
        }
        editElement = editElement.nextElementSibling
      }
      while ( editElement );
    }

    const publishMesh = ( { shown, selected, hidden } ) => dispatch( mesh.meshChanged( shown, selected, hidden ) )

    const adapter = new Adapter( shown, selected, hidden )
    publishMesh( adapter ) // so the user can undo back to the initial state?
    performEdits( editElement, adapter, publishMesh )
    
    const viewing = getChildElement( vZomeRoot, "Viewing" )
    if ( viewing ) {
      dispatch( parseViewXml( viewing, getChildElement ) )
    }
    dispatch( models.finishLoadingModel() )
  } catch (error) {
    console.log( error )
    dispatch( showAlert( `Unable to parse model file: ${name};\n ${error.message}` ) )
  }

  dispatch( stopProgress() )
}
