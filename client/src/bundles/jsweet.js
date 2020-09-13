
import * as mesh from './mesh'
import goldenField from '../fields/golden'

// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

export const init = ( window, store ) =>
{
  const vzome = window.com.vzome
  const commands = {}
  for ( const [ name, editClass ] of Object.entries( vzome.core.edits ) )
    commands[ name ] = legacyCommand( vzome.jsweet, editClass )
  store.dispatch( mesh.commandsDefined( commands ) )

  const context = new vzome.jsweet.JsEditContext()
  const field = new vzome.jsweet.JsAlgebraicField( goldenField )
  const symmPer = new vzome.core.kinds.IcosahedralSymmetryPerspective( field )
  const colors = new vzome.core.render.Colors( new Properties( {} ) )
  const orbitSource = new vzome.core.editor.SymmetrySystem( null, symmPer, context, colors, true )
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

export const legacyCommand = ( pkg, editClass ) => ( state, config ) =>
{
  const shown = new Map( state.shown )
  const hidden = new Map( state.hidden )
  const selected = new Map( state.selected )
  const adapter = new Adapter( shown, hidden, selected )

  const field = new pkg.JsAlgebraicField( state.field )
  const realizedModel = new pkg.JsRealizedModel( field, adapter )
  const selection = new pkg.JsSelection( field, adapter )
  const editor = new pkg.JsEditorModel( realizedModel, selection )

  const edit = new editClass( editor )

  edit.configure( new Properties( config ) )

  edit.perform()  // side-effects will appear in shown, hidden, and selected maps

  return {
    ...state, shown, selected, hidden
  }
}

const resolveShape = ( instance ) =>
{
  if ( instance.shapeId )
    return;
  // TODO: make this work for more than balls
  instance.shapeId = "unknown"
  instance.color = "#0088aa"
}

const renderableInstance = ( instance, selected, field ) =>
{
  resolveShape( instance ) // not pure
  const result = {
    ...instance,
    position: field.embedv( instance.vectors[0] ),
    selected
  }
  delete result.vectors
  if ( selected )
    result.color = "#ff4400"
  return result
}

export const supportsEdits = true

export const instanceSelector = ( { mesh } ) =>
{
  const instances = []
  Array.from( mesh.shown    ).map( ( [id, instance] ) => { instances.push( renderableInstance( instance, false, mesh.field ) ) } )
  Array.from( mesh.selected ).map( ( [id, instance] ) => { instances.push( renderableInstance( instance, true,  mesh.field ) ) } )
  return { shapes: [], instances }
}