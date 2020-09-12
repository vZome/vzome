
import { createInstance } from './mesh'

// import { NewCentroid } from '../jsweet/com/vzome/core/edits/NewCentroid'

// I can't use the ES6 module approach until I figure out how to use J4TS that way.
//  For now, I can load the bundles in index.html, and access the packages through window.
//
// I should be able to use require() and a commonjs module, but it has the same J4TS build problem.

var vzome = undefined

export const init = ( window, store ) =>
{
  vzome = window.com.vzome
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
    const { id } = createInstance( vectors )
    this.shown.delete( id )
    this.selected.delete( id )
  }

  select( vectors )
  {
    const { id } = createInstance( vectors )
    const instance = this.shown.get( id )
    this.shown.delete( id )
    this.selected.set( id, instance )
  }

  unselect( vectors )
  {
    const { id } = createInstance( vectors )
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
    const { id } = createInstance( vectors )
    return this.selected.has( id )
  }

  findOrAddManifestation( vectors )
  {
    const created = createInstance( vectors )
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

  get( key )
  {
    return this.config[ key ]
  }
}

export const legacyCommand = ( editName, config ) => state =>
{
  const shown = new Map( state.shown )
  const hidden = new Map( state.hidden )
  const selected = new Map( state.selected )
  const adapter = new Adapter( shown, hidden, selected )

  const field = new vzome.jsweet.JsAlgebraicField( state.field )
  const realizedModel = new vzome.jsweet.JsRealizedModel( field, adapter )
  const selection = new vzome.jsweet.JsSelection( field, adapter )
  const editor = new vzome.jsweet.JsEditorModel( realizedModel, selection )

  const editClass = vzome.core.edits[ editName ]
  if ( editClass ) {
    const edit = new editClass( editor )

    edit.configure( new Properties( config ) )
  
    edit.perform()  // side-effects will appear in shown, hidden, and selected maps

    return {
      ...state, shown, selected, hidden
    }
  }
  else
    return state
}