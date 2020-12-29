
import { Group } from '@material-ui/icons'
import * as mesh from './mesh'

export default class Adapter
{
  constructor( shown, selected, hidden, groups=[] )
  {
    this.shown = shown
    this.hidden = hidden
    this.selected = selected
    this.groups = groups
  }

  // Selection methods

  clone()
  {
    return new Adapter( new Map( this.shown ), new Map( this.selected ), new Map( this.hidden ), [ ...this.groups ] )
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

  select( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    if ( this.selected.has( id ) )
      return // idempotent
    const instance = this.shown.get( id )
    if ( ! instance ) {
      throw new Error( `No shown instance to select at ${id}`)
    }
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

  createGroup()
  {
    if ( this.selectionIsGroup() )
      return
    const group = Array.from( this.selected.keys() )
    this.groups.push( group )
  }

  disbandGroup()
  {
    this.groups = this.groups.filter( group =>
      ( group.length !== this.selected.size ) || ( group.some( id => ! this.selected.has( id ) ) ) )
  }

  createLegacyGroup()
  {
    throw new Error( "createLegacyGroup is not yet implemented" )
  }

  disbandLegacyGroup()
  {
    throw new Error( "disbandLegacyGroup is not yet implemented" )
  }

  selectWithGrouping( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    if ( this.selected.has( id ) )
      return
    const group = this.getLargestGroup( vectors )
    if ( group ) {
      group.forEach( id => {
        const instance = this.shown.get( id ) || this.selected.get( id )
        this.shown.delete( id )
        this.selected.set( id, instance )
      });
    } else {
      const instance = this.shown.get( id )
      if ( ! instance ) {
        throw new Error( `No shown instance to select at ${id}`)
      }
      this.shown.delete( id )
      this.selected.set( id, instance )
    }
  }

  unselectWithGrouping( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    if ( ! this.selected.has( id ) )
      return
    const group = this.getLargestGroup( vectors )
    if ( group ) {
      group.forEach( id => {
        const instance = this.shown.get( id ) || this.selected.get( id )
        this.selected.delete( id )
        this.shown.set( id, instance )
      });
    } else {
      const instance = this.selected.get( id )
      this.selected.delete( id )
      this.shown.set( id, instance )
    }
  }

  selectionIsGroup()
  {
    return !! this.getSelectionGroup()
  }

  getSelectionGroup()
  {
    const selectionSize = this.selected.size
    if ( selectionSize === 0 )
      return false;
    return this.groups.find( group => ( group.length === selectionSize ) && ( group.every( id => this.selected.has( id ) ) ) )
  }

  // RealizedModel calls

  getLargestGroup( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    const matchingGroups = this.groups.filter( group => group.includes( id ) )
    if ( matchingGroups.length === 0 )
      return null

    let largestGroup
    let largestSize = 0
    matchingGroups.map( group => {
      if ( group.length > largestSize ) {
        largestGroup = group
        largestSize = group.length
      }
    })
    return largestGroup && largestGroup.map( id => ( this.selected.get( id ) || this.shown.get( id ) ).vectors )
  }

  delete( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    this.shown.delete( id )
    this.selected.delete( id )
    this.hidden.delete( id )
  }

  manifestationRendered( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    return this.shown.has( id ) || this.selected.has( id )
  }

  manifestationHidden( vectors )
  {
    const { id } = mesh.createInstance( vectors )
    return this.hidden.has( id )
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
    return existing && !!existing.color
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
    if ( this.shown.has( instance.id ) || this.selected.has( instance.id ) )
      return // idempotent
    this.hidden.delete( instance.id )
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