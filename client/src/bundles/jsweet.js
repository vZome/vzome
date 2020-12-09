
import * as mesh from './mesh'
import * as planes from './planes'
import { field as goldenField } from '../fields/golden'
import { FILE_LOADED, fetchModel } from './files'
import { startProgress, stopProgress } from './progress'

// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

const ORBITS_INITIALIZED = 'ORBITS_INITIALIZED'
const WORK_STARTED = 'WORK_STARTED'
const WORK_FINISHED = 'WORK_FINISHED'

const initialState = {
  editFactory: undefined,       // ANTI-PATTERN, not a plain Object
  resolver: undefined,       // ANTI-PATTERN, not a plain Object
  working: false,
}

export const reducer = ( state = initialState, action ) =>
{
  switch ( action.type ) {

    case ORBITS_INITIALIZED: {
      const { editFactory, resolver } = action.payload
      return { ...state, editFactory, resolver }
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

export const init = async ( window, store ) =>
{
  const vzomePkg = window.com.vzome
  const shimClass = vzomePkg.jsweet.JsAdapter

  const injectResource = async ( path ) =>
  {
    const fullPath = `/app/resources/${path}`
    const response = await fetch( fullPath )
    if ( ! response.ok ) {
      throw new Error( 'Network response was not ok' );
    }
    const text = await response.text()
    // Inject the VEF into a static map on ExportedVEFShapes
    vzomePkg.xml.ResourceLoader.injectResource( path, text )
    console.log( `injected resource ${path}` )
  }

  // Initialize the field application
  await Promise.all( [
    injectResource( 'com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef' ),
    injectResource( 'com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef' ),
    injectResource( 'com/vzome/core/math/symmetry/H4roots.vef' ),
  ] )
  const context = new vzomePkg.jsweet.JsEditContext()
  const field = new vzomePkg.jsweet.JsAlgebraicField( goldenField )
  const fieldApp = new vzomePkg.core.kinds.GoldenFieldApplication( field )

  const editFactory = ( className, adapter ) =>
  {
    const field = fieldApp.getField()
    const realizedModel = new vzomePkg.jsweet.JsRealizedModel( field, adapter )
    const selection = new vzomePkg.jsweet.JsSelection( field, adapter )
    const editor = new vzomePkg.jsweet.JsEditorModel( realizedModel, selection, fieldApp )
    const classes = vzomePkg.core.edits
    return new classes[ className ]( editor )
  }

  // Discover all the legacy edit classes and register as commands
  const commands = {}
  for ( const [ name, editClass ] of Object.entries( vzomePkg.core.edits ) )
    commands[ name ] = legacyCommand( editFactory, name )
  store.dispatch( mesh.commandsDefined( commands ) )

  // Prepare the orbitSource for resolveShapes
  const symmPer = fieldApp.getSymmetryPerspective( "icosahedral" )
  const colors = new vzomePkg.core.render.Colors( new Properties( defaults ) )
  const orbitSource = new vzomePkg.core.editor.SymmetrySystem( null, symmPer, context, colors, true )

  const origin = goldenField.origin( 3 )
  const originBall = mesh.createInstance( [ origin ] )
  store.dispatch( mesh.meshChanged( new Map().set( originBall.id, originBall ), new Map(), new Map() ) )

  const blue = [ [0,0,1], [0,0,1], [1,0,1] ]
  const yellow = [ [0,0,1], [1,0,1], [1,1,1] ]
  const red = [ [1,0,1], [0,0,1], [0,1,1] ]
  const green = [ [1,0,1], [1,0,1], [0,0,1] ]
  const gridPoints = shimClass.getZoneGrid( orbitSource, blue )
  store.dispatch( planes.doSetWorkingPlaneGrid( gridPoints ) )

  const state = {
    editFactory,
    resolver: createResolver( goldenField, vzomePkg, orbitSource ),
  }

  // TODO: fetch all shape VEFs in a ZIP, then inject each
  Promise.all( knownOrbitNames.map( name => injectResource( `com/vzome/core/parts/default/${name}.vef` ) ) )
    .then( () => {
      // now we are finally ready to resolve instance shapes
      store.dispatch( { type: ORBITS_INITIALIZED, payload: state } )
      store.dispatch( fetchModel( "/app/models/120-cell.vZome" ) )
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

const createResolver = ( field, vzomePkg, orbitSource ) => shapes => instance =>
{
  const { id, vectors } = instance
  const jsAF = new vzomePkg.jsweet.JsAlgebraicField( field )
  {
    const man = vzomePkg.jsweet.JsManifestation.manifest( vectors, jsAF )
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

    const wlast = q =>
    {
      const [ w, x, y, z ] = q
      return [ x, y, z, w ]
    }
      // get shape, orientation, color from rm
    const quatIndex = rm.getStrutZone()
    const rotation = ( quatIndex && (quatIndex >= 0) && wlast( field.embedv( field.quaternions[ quatIndex ] ) ) ) || [0,0,0,1]
    const color = rm.getColor().getRGB()
  
    return { id, rotation, color, shapeId }
  }
}

class Adapter
{
  constructor( shown, hidden, selected )
  {
    this.shown = shown
    this.hidden = hidden
    this.selected = selected
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
  }

  select( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    const instance = this.shown.get( id )
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

  findOrAddManifestation( vectors )
  {
    const created = mesh.createInstance( vectors )
    const { id } = created
    const existing = this.shown.get( id ) || this.hidden.get( id ) || this.selected.get( id )
    if ( existing )
      return existing;
    // TODO avoid creating zero-length struts, to match Java semantics of RMI.findConstruction
    this.shown.set( id, created )
    return vectors
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

export const legacyCommand = ( editFactory, className ) => ( config ) => ( dispatch, getState ) =>
{
  let { shown, hidden, selected } = getState().mesh
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )
  const adapter = new Adapter( shown, hidden, selected )

  const edit = editFactory( className, adapter )

  edit.configure( new Properties( config ) )

  edit.perform()  // side-effects will appear in shown, hidden, and selected maps

  dispatch( { type: WORK_STARTED } )
  dispatch( mesh.meshChanged( shown, selected, hidden ) )
  dispatch( { type: WORK_FINISHED } )
}

export const middleware = store => next => async action => 
{
  if ( action.type === FILE_LOADED ) {
    store.dispatch( startProgress( "Parsing vZome model..." ) )
    const path = "/str/" + action.payload.name

    // I tried using JXON here, but wants to make the EditHistory into an object not an array.
    // I could also just roll my own, ala https://developer.mozilla.org/en-US/docs/Archive/JXON,
    //   but I think there would be enough special cases that I might as well just use the DOM.

    const parser = new DOMParser();
    const domDoc = parser.parseFromString( action.payload.text, "application/xml" );
    console.log( domDoc )
    const history = domDoc.firstElementChild.firstElementChild // TODO: fragile! may not get EditHistory first
    const editNumber = history.getAttribute( "editNumber" )
    const firstEdit = history.firstElementChild
    const editName = firstEdit.nodeName

    const editFactory = store.getState().jsweet.editFactory

    const shown = new Map()
    const hidden = new Map()
    const selected = new Map()
    const adapter = new Adapter( shown, selected, hidden )
    const edit = editFactory( editName, adapter )
    edit.loadAndPerform( firstEdit, null, { performAndRecord: edit => edit.perform() } )

    store.dispatch( mesh.meshChanged( shown, selected, hidden ) )

    store.dispatch( stopProgress() )
  }
  
  return next( action )
}
