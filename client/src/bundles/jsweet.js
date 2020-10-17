
import * as mesh from './mesh'
import { field as goldenField } from '../fields/golden'

// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

const PACKAGE_INJECTED = 'PACKAGE_INJECTED'
const ORBITS_INITIALIZED = 'ORBITS_INITIALIZED'
const SHAPE_DEFINED = 'SHAPE_DEFINED'
const INSTANCE_SHAPED = 'INSTANCE_SHAPED'
const WORK_STARTED = 'WORK_STARTED'
const WORK_FINISHED = 'WORK_FINISHED'

const initialState = {
  vzomePkg: undefined,       // ANTI-PATTERN, not a plain Object
  orbitSource: undefined,    // ANTI-PATTERN, not a plain Object
  fieldApp: undefined,       // ANTI-PATTERN, not a plain Object
  shapes: {},
  shapedInstances : {},  // where we store shape and orientation, outside of the mesh
  working: false,
}

export const reducer = ( state = initialState, action ) =>
{
  switch ( action.type ) {

    case PACKAGE_INJECTED: {
      const vzomePkg = action.payload
      return { ...state, vzomePkg }
    }

    case ORBITS_INITIALIZED: {
      const { fieldApp, orbitSource } = action.payload
      return { ...state, orbitSource, fieldApp }
    }

    case INSTANCE_SHAPED: {
      const { id } = action.payload
      const shapedInstances = { ...state.shapedInstances, [id]: action.payload }
      return { ...state, shapedInstances }
    }

    case SHAPE_DEFINED: {
      const shape = action.payload
      const shapes = { ...state.shapes, [shape.id]: shape }
      return { ...state, shapes }
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
  store.dispatch( { type: PACKAGE_INJECTED, payload: vzomePkg } )

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

  // Discover all the legacy edit classes and register as commands
  const commands = {}
  for ( const [ name, editClass ] of Object.entries( vzomePkg.core.edits ) )
    commands[ name ] = legacyCommand( vzomePkg.jsweet, editClass )
  store.dispatch( mesh.commandsDefined( commands ) )

  // Initialize the field application
  await Promise.all( [
    injectResource( 'com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef' ),
    injectResource( 'com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef' ),
    injectResource( 'com/vzome/core/math/symmetry/H4roots.vef' ),
  ] )
  const context = new vzomePkg.jsweet.JsEditContext()
  const field = new vzomePkg.jsweet.JsAlgebraicField( goldenField )
  const fieldApp = new vzomePkg.core.kinds.GoldenFieldApplication( field )

  // Prepare the orbitSource for resolveShapes
  const symmPer = fieldApp.getSymmetryPerspective( "icosahedral" )
  const colors = new vzomePkg.core.render.Colors( new Properties( defaults ) )
  const orbitSource = new vzomePkg.core.editor.SymmetrySystem( null, symmPer, context, colors, true )

  store.dispatch( { type: ORBITS_INITIALIZED, payload: { fieldApp, orbitSource } } )
  store.dispatch( mesh.resolverDefined( { resolve } ) )

  // TODO: fetch all shape VEFs in a ZIP, then inject each
  knownOrbitNames.map( name => injectResource( `com/vzome/core/parts/default/${name}.vef` ) )
}

const makeShape = ( shape ) =>
{
  const vertices = shape.getVertexList().toArray().map( av => {
    const { x, y, z } = av.toRealVector()
    return { x, y, z }
  })
  const faces = shape.getTriangleFaces().toArray()
  const id = shape.getGuid().toString()
  return { id, vertices, faces }
}

// This thunk gets hooked up as the resolver for the mesh
const resolve = ( instances ) => ( dispatch, getState ) =>
{
  const { vzomePkg, orbitSource, shapes, shapedInstances } = getState().jsweet
  const { field } = getState().mesh
  const jsAF = new vzomePkg.jsweet.JsAlgebraicField( field )
  instances.map( ({ id, vectors }) =>
  {
    // first, is the instance already resolved?
    if ( shapedInstances[ id ] )
      return

    // Not resolved, so use the orbitSource
    const man = vzomePkg.jsweet.JsManifestation.manifest( vectors, jsAF )
    const rm = new vzomePkg.core.render.RenderedManifestation( man, orbitSource )
    rm.resetAttributes( orbitSource, orbitSource.getShapes(), false, true )

    // may be a zero-length strut, no shape
    if ( !rm.getShape() )
      return
    
    // is the shape new?
    const shapeId = rm.getShapeId().toString()
    if ( ! shapes[ shapeId ] ) {
      const shape = makeShape( rm.getShape() )
      dispatch( { type: SHAPE_DEFINED, payload: shape } )
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
  
    dispatch( { type: INSTANCE_SHAPED, payload: { id, shapeId, rotation, color } } )
  } )
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

export const legacyCommand = ( pkg, editClass ) => ( config ) => ( dispatch, getState ) =>
{
  let { shown, hidden, selected, resolver } = getState().mesh
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )
  const adapter = new Adapter( shown, hidden, selected )

  const { fieldApp } = getState().jsweet
  const field = fieldApp.getField()
  const realizedModel = new pkg.JsRealizedModel( field, adapter )
  const selection = new pkg.JsSelection( field, adapter )
  const editor = new pkg.JsEditorModel( realizedModel, selection, fieldApp )

  const edit = new editClass( editor )

  edit.configure( new Properties( config ) )

  edit.perform()  // side-effects will appear in shown, hidden, and selected maps

  dispatch( { type: WORK_STARTED } )
  dispatch( mesh.meshChanged( shown, selected, hidden ) )
  // shape any new mesh objects
  dispatch( resolver.resolve( Array.from( shown.values() ) ) )     // overkill, but hard to optimize
  dispatch( resolver.resolve( Array.from( selected.values() ) ) )  // overkill, but hard to optimize
  dispatch( { type: WORK_FINISHED } )
}

export const supportsEdits = true

const renderableInstance = ( instance, selected, field, shapedInstances ) =>
{
  const { id, vectors } = instance
  const position = field.embedv( vectors[0] )
  return { ...shapedInstances[ id ], position, selected }
}

const filterInstances = ( shape, instances ) =>
{
  return instances.filter( instance => instance.shapeId === shape.id )
}

// This is a selector, called when updating the ModelCanvas component
export const sortedShapes = ( { mesh, jsweet } ) =>
{
  if ( jsweet.working )
    return []
  
  const instances = []
  mesh.shown.forEach( ( instance, id ) => {
    instances.push( renderableInstance( instance, false, mesh.field, jsweet.shapedInstances ) )
  })
  mesh.selected.forEach( ( instance, id ) => {
    instances.push( renderableInstance( instance, true, mesh.field, jsweet.shapedInstances ) )
  })
  return Object.values( jsweet.shapes ).map( shape => ( { shape, instances: filterInstances( shape, instances ) } ) )
}