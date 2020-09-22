
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

const initialState = {
  vzomePkg: undefined,       // ANTI-PATTERN, not a plain Object
  orbitSource: undefined,    // ANTI-PATTERN, not a plain Object
  shapes: {},
  shapedInstances : {},  // where we store shape and orientation, outside of the mesh
}

export const reducer = ( state = initialState, action ) =>
{
  switch ( action.type ) {

    case PACKAGE_INJECTED: {
      const vzomePkg = action.payload
      return { ...state, vzomePkg }
    }

    case ORBITS_INITIALIZED: {
      const orbitSource = action.payload
      return { ...state, orbitSource }
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

    default:
      return state
  }
}

// Async actions

const indexLegacyEdits = () => ( dispatch, getState ) =>
{
  // Discover all the legacy edit classes and register as commands
  const { vzomePkg } = getState().jsweet
  const commands = {}
  for ( const [ name, editClass ] of Object.entries( vzomePkg.core.edits ) )
    commands[ name ] = legacyCommand( vzomePkg.jsweet, editClass )

  dispatch( mesh.commandsDefined( commands ) )
}

const setupIcosahedralSymmetry = () => ( dispatch, getState ) =>
{
  // Prepare the orbitSource for resolveShapes
  const { vzomePkg } = getState().jsweet
  const context = new vzomePkg.jsweet.JsEditContext()
  const field = new vzomePkg.jsweet.JsAlgebraicField( goldenField )
  const symmPer = new vzomePkg.core.kinds.IcosahedralSymmetryPerspective( field )
  const colors = new vzomePkg.core.render.Colors( new Properties( { "color.red": "255,0,0" } ) )
  const orbitSource = new vzomePkg.core.editor.SymmetrySystem( null, symmPer, context, colors, true )

  dispatch( { type: ORBITS_INITIALIZED, payload: orbitSource } )
  dispatch( mesh.resolverDefined( { resolve } ) )
}

const fetchShape = ( shapePkg, shapeName ) => ( dispatch ) =>
{
  const path = `/app/shapes/${shapePkg}/${shapeName}.vef`
  fetch( path )
    .then( response =>
    {
      if ( ! response.ok ) {
        throw new Error( 'Network response was not ok' );
      }
      return response.text()
    })
    .then( (text) => {
      dispatch( loadShape( shapePkg, shapeName, text ) )
    })
    .catch( error =>
    {
      console.error( `Unable to fetch ${path}: ${error}` );
    });
}

const loadShape = ( shapePkg, shapeName, text ) => ( dispatch, getState ) =>
{
  const { vzomePkg } = getState().jsweet
  // Inject the VEF into a static map on ExportedVEFShapes
  vzomePkg.core.viewing.ExportedVEFShapes.injectShapeVEF( `${shapePkg}-${shapeName}`, text )
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
      return;

    // Not resolved, so use the orbitSource
    const man = vzomePkg.jsweet.JsManifestation.manifest( vectors, jsAF )
    const rm = new vzomePkg.core.render.RenderedManifestation( man, orbitSource )
    rm.resetAttributes( orbitSource, orbitSource.getShapes(), false, true )

    // is the shape new?
    const shapeId = rm.getShapeId().toString()
    if ( ! shapes[ shapeId ] ) {
      const shape = makeShape( rm.getShape() )
      dispatch( { type: SHAPE_DEFINED, payload: shape } )
    }

    // get shape, orientation, color from rm
    const quatIndex = rm.getStrutZone()
    const rotation = ( quatIndex && (quatIndex >= 0) && field.embedv( field.vZomeIcosahedralQuaternions[ quatIndex ] ) ) || [1,0,0,0]
    const color = rm.getColor().getRGB()
  
    dispatch( { type: INSTANCE_SHAPED, payload: { id, shapeId, rotation, color } } )
  } )
}

// Initialization

export const init = ( window, store ) =>
{
  store.dispatch( { type: PACKAGE_INJECTED, payload: window.com.vzome } )

  store.dispatch( indexLegacyEdits() )

  store.dispatch( setupIcosahedralSymmetry() )

  // TODO: fetch all shape VEFs in a ZIP, then loadShape for each
  store.dispatch( fetchShape( "default", "connector" ) )
  store.dispatch( fetchShape( "default", "red" ) )
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
  let { shown, hidden, selected, field, resolver } = getState().mesh
  shown = new Map( shown )
  hidden = new Map( hidden )
  selected = new Map( selected )
  const adapter = new Adapter( shown, hidden, selected )

  field = new pkg.JsAlgebraicField( field )
  const realizedModel = new pkg.JsRealizedModel( field, adapter )
  const selection = new pkg.JsSelection( field, adapter )
  const editor = new pkg.JsEditorModel( realizedModel, selection )

  const edit = new editClass( editor )

  edit.configure( new Properties( config ) )

  edit.perform()  // side-effects will appear in shown, hidden, and selected maps

  dispatch( mesh.meshChanged( shown, selected, hidden ) )
  // shape any new mesh objects
  dispatch( resolver.resolve( Array.from( shown.values() ) ) )     // overkill, but hard to optimize
  dispatch( resolver.resolve( Array.from( selected.values() ) ) )  // overkill, but hard to optimize
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
  const shown = Array.from( mesh.shown ).map( ( [id, instance] ) => renderableInstance( instance, false, mesh.field, jsweet.shapedInstances ) )
  const slctd = Array.from( mesh.selected ).map( ( [id, instance] ) => renderableInstance( instance, true,  mesh.field, jsweet.shapedInstances ) )
  const instances = [ ...shown, ...slctd ]
  return Object.values( jsweet.shapes ).map( shape => ( { shape, instances: filterInstances( shape, instances ) } ) )
}