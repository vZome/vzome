
const canonicalizedId = ( vectors ) =>
{
  const strings = vectors.map( n => JSON.stringify( n ) )
  let min = strings[ 0 ]
  let minIndex = 0
  for ( let i = 1; i < strings.length; i++ ) {
    if ( strings[ i ].localeCompare( min ) < 0 ) {
      minIndex = i
      min = strings[ i ]
    }
  }
  // Since % does not actually perform a consistent sawtooth modulus function for negative numbers,
  //  we add strings.length to force a positive number.
  const get = i => strings[ ( strings.length + minIndex + i ) % strings.length ]

  // We have the minimum (get(0)), now figure out which way to go
  const incr = ( get( 1 ).localeCompare( get( -1 ) ) < 0 )? 1 : -1

  let sorted = ""
  for ( let j = 0; j < strings.length; j++ ) {
    const next = get( j * incr )
    sorted += next
  }
  return sorted
}

export const createInstance = ( vectors ) =>
{
  const id = canonicalizedId( vectors )
  // We cannot rearrange vectors, as that would break strut semantics for some legacy commands
  return { id, vectors }
}

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

  debugBottleneck( id )
  {
    const setConditionalBreakpointHere = id
  }

  select( vectors )
  {
    const { id } = createInstance( vectors )
    if ( this.selected.has( id ) )
      return // idempotent
    const instance = this.shown.get( id )
    if ( ! instance ) {
      throw new Error( `No shown instance to select at ${id}`)
    }
    this.shown.delete( id )
    this.selected.set( id, instance )
    this.debugBottleneck( id )
  }

  unselect( vectors )
  {
    const { id } = createInstance( vectors )
    const instance = this.selected.get( id )
    this.selected.delete( id )
    this.shown.set( id, instance )
    this.debugBottleneck( id )
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
    const group = new Set( this.selected.keys() )
    this.groups.push( group )
  }

  disbandGroup()
  {
    this.groups = this.groups.filter( group =>
      ( group.size !== this.selected.size ) || ( Array.from(group).some( id => ! this.selected.has( id ) ) ) )
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
    const { id } = createInstance( vectors )
    if ( this.selected.has( id ) )
      return
    const group = this.getLargestGroup( vectors )
    if ( group ) {
      group.forEach( id => {
        const instance = this.shown.get( id ) || this.selected.get( id )
        this.shown.delete( id )
        this.selected.set( id, instance )
        this.debugBottleneck( id )
      });
    } else {
      const instance = this.shown.get( id )
      if ( ! instance ) {
        throw new Error( `No shown instance to select at ${id}`)
      }
      this.shown.delete( id )
      this.selected.set( id, instance )
      this.debugBottleneck( id )
    }
  }

  unselectWithGrouping( vectors )
  {
    const { id } = createInstance( vectors )
    if ( ! this.selected.has( id ) )
      return
    const group = this.getLargestGroup( vectors )
    if ( group ) {
      group.forEach( id => {
        const instance = this.shown.get( id ) || this.selected.get( id )
        this.selected.delete( id )
        this.shown.set( id, instance )
        this.debugBottleneck( id )
      });
    } else {
      const instance = this.selected.get( id )
      this.selected.delete( id )
      this.shown.set( id, instance )
      this.debugBottleneck( id )
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
    return this.groups.find( group => ( group.size === selectionSize ) && ( Array.from( group ).every( id => this.selected.has( id ) ) ) )
  }

  // RealizedModel calls

  getLargestGroup( vectors )
  {
    const { id } = createInstance( vectors )
    const matchingGroups = this.groups.filter( group => group.has( id ) )
    if ( matchingGroups.length === 0 )
      return null

    let largestGroup
    let largestSize = 0
    matchingGroups.map( group => {
      if ( group.size > largestSize ) {
        largestGroup = group
        largestSize = group.size
      }
    })
    return largestGroup && Array.from( largestGroup ).map( id => {
      const instance = this.selected.get( id ) || this.shown.get( id )
      return instance.vectors
     } )
  }

  delete( vectors )
  {
    const { id } = createInstance( vectors )
    this.debugBottleneck( id )
    this.shown.delete( id )
    this.selected.delete( id )
    this.hidden.delete( id )
    this.groups.forEach( group => group.delete( id ) )
  }

  manifestationRendered( vectors )
  {
    const { id } = createInstance( vectors )
    return this.shown.has( id ) || this.selected.has( id )
  }

  manifestationHidden( vectors )
  {
    const { id } = createInstance( vectors )
    return this.hidden.has( id )
  }

  manifestationSelected( vectors )
  {
    const { id } = createInstance( vectors )
    return this.selected.has( id )
  }

  manifestationHasColor( vectors )
  {
    const { id } = createInstance( vectors )
    const existing = this.shown.get( id ) || this.selected.get( id )
    return existing && !!existing.color
  }

  manifestationColor( vectors )
  {
    const { id } = createInstance( vectors )
    const existing = this.shown.get( id ) || this.selected.get( id )
    return existing.color
  }

  setManifestationColor( vectors, color )
  {
    const instance = createInstance( vectors )
    instance.color = color
    if ( this.shown.has( instance.id ) ) {
      this.shown.set( instance.id, instance )
    } else if ( this.selected.has( instance.id ) ) {
      this.selected.set( instance.id, instance )
    }
  }

  findOrCreateManifestation( vectors )
  {
    const created = createInstance( vectors )
    const { id } = created
    const existing = this.shown.get( id ) || this.hidden.get( id ) || this.selected.get( id )
    if ( existing )
      return existing.vectors;
    // TODO avoid creating zero-length struts, to match Java semantics of RMI.findConstruction
    return vectors
  }

  showManifestation( vectors )
  {
    console.log( 'showManifestation' );
    let instance = createInstance( vectors )
    if ( this.shown.has( instance.id ) || this.selected.has( instance.id ) )
      return // idempotent
    this.hidden.delete( instance.id )
    this.shown.set( instance.id, instance )
    this.debugBottleneck( instance.id )
  }

  hideManifestation( vectors )
  {
    console.log( 'hideManifestation' );
    let instance = createInstance( vectors )
    instance = this.shown.get( instance.id ) || this.selected.get( instance.id ) || this.hidden.get( instance.id ) || instance
    this.selected.delete( instance.id ) || this.shown.delete( instance.id )
    this.hidden.set( instance.id, instance )
    this.debugBottleneck( instance.id )
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