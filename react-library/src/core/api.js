
import * as vZomeJava from './legacyjava.js'

export const parse = async text =>
{
  const { parser } = await vZomeJava.coreState
  return parser( text )
}

const R = 2
const octahedronShape =
{
  id: "octahedron",
  vertices: [
    { x: R, y: 0, z: 0 },
    { x: 0, y: R, z: 0 },
    { x: 0, y: 0, z: R },
    { x: -R, y: 0, z: 0 },
    { x: 0, y: -R, z: 0 },
    { x: 0, y: 0, z: -R },
  ],
  faces: [
    { vertices: [ 0, 1, 2 ] },
    { vertices: [ 0, 5, 1 ] },
    { vertices: [ 0, 4, 5 ] },
    { vertices: [ 0, 2, 4 ] },
    { vertices: [ 3, 2, 1 ] },
    { vertices: [ 3, 4, 2 ] },
    { vertices: [ 3, 5, 4 ] },
    { vertices: [ 3, 1, 5 ] },
  ]
}
const IDENTITY_MATRIX = [1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1]

export const getDefaultRenderer = field =>
{
  return {
    name: 'default',
    embedding: IDENTITY_MATRIX,
    shaper: shapes => ( { id, vectors } ) =>
    {
      if ( ! shapes.octahedron ) {
        shapes.octahedron = octahedronShape
      }
      const [ v0 ] = vectors
      const [ x, y, z ] = field.embedv( v0 )
      return { id, position: [ x, y, z ], rotation: IDENTITY_MATRIX, color: "#ffffff", shapeId: "octahedron" }
    }
  }
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
