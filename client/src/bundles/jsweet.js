
import * as mesh from './mesh'
import goldenField from '../fields/golden'

// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

const PACKAGE_INJECTED = 'PACKAGE_INJECTED'
const ORBITS_INITIALIZED = 'ORBITS_INITIALIZED'
const SHAPE_LOADED = 'SHAPE_LOADED'

const initialState = {
  vzomePkg: undefined,            // ANTI-PATTERN, not a plain Object
  orbitSource: undefined,    // ANTI-PATTERN, not a plain Object
  shapes: new Map(),
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

    case SHAPE_LOADED: {
      const shape = action.payload
      const { id } = shape
      return {
        ...state,
        shapes: new Map( state.shapes ).set( id, shape ),
      }
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
  const { vzomePkg, orbitSource } = getState().jsweet

  // Inject the VEF into a static map on ExportedVEFShapes
  vzomePkg.core.viewing.ExportedVEFShapes.injectShapeVEF( `${shapePkg}-${shapeName}`, text )

  // For connectors, we can actually parse the shape
  if ( shapeName === "connector" )
  {
    const shape = orbitSource.getShapes().getConnectorShape()  
    dispatch( { type: SHAPE_LOADED, payload: makeShape( shape ) } )  
  }
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

  shownIterator()
  {
    return this.shown.values()
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

export const legacyCommand = ( pkg, editClass ) => ( mesh, config ) =>
{
  const shown = new Map( mesh.shown )
  const hidden = new Map( mesh.hidden )
  const selected = new Map( mesh.selected )
  const adapter = new Adapter( shown, hidden, selected )

  const field = new pkg.JsAlgebraicField( mesh.field )
  const realizedModel = new pkg.JsRealizedModel( field, adapter )
  const selection = new pkg.JsSelection( field, adapter )
  const editor = new pkg.JsEditorModel( realizedModel, selection )

  const edit = new editClass( editor )

  edit.configure( new Properties( config ) )

  edit.perform()  // side-effects will appear in shown, hidden, and selected maps

  return {
    ...mesh, shown, selected, hidden
  }
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

const resolveShape = ( instance, field, state, shapes ) =>
{
  if ( instance.shapeId )
    return;

  const { vzomePkg, orbitSource } = state
  const jsAF = new vzomePkg.jsweet.JsAlgebraicField( field )
  const man = vzomePkg.jsweet.JsManifestation.manifest( instance.vectors, jsAF )
  const rm = new vzomePkg.core.render.RenderedManifestation( man, orbitSource )
  rm.resetAttributes( orbitSource, orbitSource.getShapes(), false, true )
  // get shape, orientation, color from rm
  const quatIndex = rm.getStrutZone()
  instance.rotation = field.vZomeIcosahedralQuaternions[ quatIndex ]
  const shapeId = rm.getShapeId().toString()
  instance.shapeId = shapeId   // ANTI-PATTERN: mutating a state object
  instance.color = rm.getColor().getRGB()   // ANTI-PATTERN: mutating a state object
  if ( ! shapes[ shapeId ] )
    shapes[ shapeId ] = makeShape( rm.getShape() )
}

const renderableInstance = ( instance, selected, field, state, shapes ) =>
{
  resolveShape( instance, field, state, shapes ) // not pure
  const result = {
    ...instance,
    position: field.embedv( instance.vectors[0] ),
    selected
  }
  delete result.vectors
  return result
}

export const supportsEdits = true

export const instanceSelector = ( { mesh, jsweet } ) =>
{
  const instances = []
  const shapes = {}
  Array.from( mesh.shown    ).map( ( [id, instance] ) => { instances.push( renderableInstance( instance, false, mesh.field, jsweet, shapes ) ) } )
  Array.from( mesh.selected ).map( ( [id, instance] ) => { instances.push( renderableInstance( instance, true,  mesh.field, jsweet, shapes ) ) } )
  return { shapes: Object.values( shapes ), instances }
}