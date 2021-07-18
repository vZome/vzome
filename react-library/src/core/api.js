
import * as vZomeJava from './legacyjava.js'

export const parse = async text =>
{
  const { parser } = await vZomeJava.coreState
  return parser( text )
}

export const getDefaultShaper = async () =>
{
  const { shapeRenderer } = await vZomeJava.coreState
  return shapeRenderer
}

export const cloneMesh = ( { shown, selected, hidden, groups=[] } ) =>
  ({ shown: new Map( shown ), selected: new Map( selected ), hidden: new Map( hidden ), groups: [ ...groups ] })

export const Step = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

export const interpret = ( action, mesh, edit, stack=[], recordSnapshot ) =>
{
  const step = () =>
  {
    if ( ! edit )
      return Step.DONE
    if ( edit.isBranch() ) {
      const branchMesh = cloneMesh( mesh )
      stack.push( { branch: edit, mesh } )
      recordSnapshot && recordSnapshot( branchMesh, edit.id(), edit.firstChild(), stack )
      edit = edit.firstChild() // this assumes there are no empty branches
      mesh = branchMesh
      return Step.IN
    } else {
      mesh = cloneMesh( mesh )  // each command builds on the last
      edit.perform( mesh )
      if ( edit.nextSibling() ) {
        recordSnapshot && recordSnapshot( mesh, edit.id(), edit.nextSibling() )
        edit = edit.nextSibling()
        return Step.OVER
      } else {
        let top
        do {
          top = stack.pop()
        } while ( top && ! top.branch.nextSibling() )
        if ( top ) {
          mesh = cloneMesh( top.mesh )  // overwrite and discard the prior value
          edit = top.branch.nextSibling()
          recordSnapshot && recordSnapshot( mesh, edit.id(), edit, stack )
          return Step.OUT
        } else {
          // at the end of the editHistory
          recordSnapshot && recordSnapshot( mesh, edit.id(), null )
          return Step.DONE
        }
      }
    }
  }

  const conTinue = () =>
  {
    let stepped
    do {
      stepped = stepOut()
    } while ( stepped !== Step.DONE );
  }

  const stepOver = () =>
  {
    const stepped = step()
    switch ( stepped ) {

      case Step.IN:
        stepOut()
        return Step.OVER
    
      default:
        return stepped
    }
  }

  const stepOut = () =>
  {
    let stepped
    do {
      stepped = stepOver()
    } while ( stepped !== Step.OUT && stepped !== Step.DONE )
    return stepped
  }

  switch ( action ) {

    case Step.IN:
      step()
      break;
  
    case Step.OVER:
      stepOver()
      break;
  
    case Step.OUT:
      stepOut()
      break;
  
    case Step.DONE:
    default:
      conTinue()
      break;
  }
}
