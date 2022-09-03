
import { normalizeRenderedManifestation, parserPromise, realizeShape } from './js2jsweet.js'
import { defaultNew } from './resources/com/vzome/core/parts/index.js'

export { realizeShape, normalizeRenderedManifestation } from './js2jsweet.js'

export const parse = async text =>
{
  const { parser } = await parserPromise
  return parser( text )
}

export const getField = async name =>
{
  const { getLegacyField } = await parserPromise;
  return getLegacyField( name );
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

const loadBallShape = ( fieldName, shapesModel ) =>
{
  const { connectorShape, name } = shapesModel
  const { faces } = connectorShape
  const id = `${fieldName}/${name}/connector`
  const vertices = connectorShape.vertices.map( ([x,y,z]) => ({x,y,z}) )
  return { id, vertices, faces }
}

export const getDefaultRenderer = field =>
{
  const defaultShaper = shapes => ( { id, vectors } ) =>
  {
    if ( ! shapes.octahedron ) {
      shapes.octahedron = octahedronShape
    }
    const [ v0 ] = vectors
    const [ x, y, z ] = field.embedv( v0 )
    return { id, position: [ x, y, z ], rotation: IDENTITY_MATRIX, color: "#ffffff", shapeId: "octahedron" }
  }
  const goldenShaper = shapes => ( { id, vectors } ) =>
  {
    if ( ! shapes.goldenBall ) {
      shapes.goldenBall = loadBallShape( "golden", defaultNew )
    }
    const [ v0 ] = vectors
    const [ x, y, z ] = field.embedv( v0 )
    return { id, position: [ x, y, z ], rotation: IDENTITY_MATRIX, color: "#ffffff", shapeId: shapes.goldenBall.id }
  }
  return {
    name: 'default',
    embedding: IDENTITY_MATRIX,
    shaper: ( field.name === "golden" )? goldenShaper : defaultShaper
  }
}

export const cloneMesh = ( { shown, selected, hidden, groups=[] } ) =>
  ({ shown: new Map( shown ), selected: new Map( selected ), hidden: new Map( hidden ), groups: [ ...groups ] })

// TODO: put this in a module that both worker and main context can use.
//  Right now this is duplicated!
export const Step = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

export const interpret = ( action, state, stack=[] ) =>
{
  let edit = state .getNextEdit();

  const step = () =>
  {
    if ( ! edit )
      return Step.DONE;
    if ( edit.isBranch() ) {
      const branchState = state.clone();
      stack.push( { branch: edit, state } );
      branchState .recordSnapshot( edit.id(), edit.firstChild().id(), stack );
      if ( edit.nextSibling() ) {
        branchState .recordSnapshot( edit.id(), edit.nextSibling().id() );
      }
      edit = edit.firstChild(); // this assumes there are no empty branches
      state .setNextEdit( edit );
      state = branchState;
      return Step.IN;
    } else {
      state = state.clone();  // each command builds on the last
      edit.perform( state ); // here we may create a legacy edit object
      const breakpointHit = state .atBreakpoint( edit );
      if ( edit.nextSibling() ) {
        state .recordSnapshot( edit.id(), edit.nextSibling().id() );
        edit = edit.nextSibling();
        state .setNextEdit( edit );
        return breakpointHit? Step.DONE : Step.OVER;
      } else {
        state .recordSnapshot( edit.id(), '--END--' ); // last one will be the real before-end
        let top;
        do {
          top = stack.pop();
        } while ( top && ! top.branch.nextSibling() )
        if ( top ) {
          state = top.state.clone();  // overwrite and discard the prior value
          top.branch.undoChildren();
          edit = top.branch.nextSibling();
          state .setNextEdit( edit );
          return breakpointHit? Step.DONE : Step.OUT;
        } else {
          // at the end of the editHistory
          return Step.DONE;
        }
      }
    }
  }

  const conTinue = () =>
  {
    let stepped;
    do {
      stepped = stepOut();
    } while ( stepped !== Step.DONE );
  }

  const stepOver = () =>
  {
    const stepped = step();
    switch ( stepped ) {

      case Step.IN:
        stepOut();
        return Step.OVER;
    
      default:
        return stepped;
    }
  }

  const stepOut = () =>
  {
    let stepped;
    do {
      stepped = stepOver();
    } while ( stepped !== Step.OUT && stepped !== Step.DONE );
    return stepped;
  }

  switch ( action ) {

    case Step.IN:
      step();
      break;
  
    case Step.OVER:
      stepOver();
      break;
  
    case Step.OUT:
      stepOut();
      break;
  
    case Step.DONE:
    default:
      conTinue();
      break;
  }
}

class RenderHistory
{
  constructor( design )
  {
    const { firstEdit, batchRender } = design;
    this.shapes = {};
    this.snapshotsAfter = {};
    this.shapshotsBefore = {};
    this.batchRender = batchRender;
    this.currentSnapshot = [];
    this.nextEdit = firstEdit;
    this.lastEdit = '--START--';
    this.recordSnapshot( '--START--', firstEdit.id() );
  }

  getNextEdit()
  {
    return this.nextEdit;
  }

  setNextEdit( edit )
  {
    this.nextEdit = edit;
  }

  atBreakpoint( edit )
  {
    return ( edit.id() === this.breakpoint );
  }

  clone()
  {
    // Because we're not actually recording RealizedModel state, but recording
    //   RenderedModel snapshots, we don't need to do anything here.  This
    //   just satisfies the requirements of interpret().
    return this;
  }

  // partial implementation of legacy RenderListener
  manifestationAdded( rm )
  {
    const shapeId = 's' + rm.getShapeId().toString();
    let shape = this.shapes[ shapeId ];
    if ( ! shape ) {
      shape = realizeShape( rm .getShape() );
      this.shapes[ shapeId ] = shape;
    }
    let instance = normalizeRenderedManifestation( rm );
    // Record this instance for the current edit
    this.currentSnapshot .push( instance );
  }

  recordSnapshot( afterId, beforeId, stack ) // stack is unused here, but part of the `state` contract for interpret()
  {
    this.lastEdit = afterId;
    this.currentSnapshot = []; // prior value overwritten, but it was already captured in snapshotsAfter and snapshotsBefore
    this.snapshotsAfter[ afterId ] = this.currentSnapshot;
    this.shapshotsBefore[ beforeId ] = this.currentSnapshot;
    this.batchRender( this ); // will make many callbacks to manifestationAdded()
  }

  getScene( editId, before=false )
  {
    this .setError( null );
    const shapes = {};
    let snapshot = before? this.shapshotsBefore[ editId ] : this.snapshotsAfter[ editId ];
    if ( !snapshot ) {

      this.breakpoint = editId;
      try {
        interpret( Step.DONE, this, [] );
      } catch (error) {
        this .setError( error );
      }
      this.breakpoint = null;

      editId = before? '--END--' : this.lastEdit;
      snapshot = before? this.shapshotsBefore[ editId ] : this.snapshotsAfter[ editId ];
    }
    for ( const instance of snapshot ) {
      const shapeId = instance.shapeId;
      if ( ! shapes[ shapeId ] ) {
        shapes[ shapeId ] = { ...this.shapes[ shapeId ], instances: [] };
      }
      shapes[ shapeId ] .instances .push( instance );
    }
    return { shapes, edit: editId };
  }

  setError( error )
  {
    this.error = error;
  }

  getError()
  {
    return this.error;
  }
}

export const interpretAndRender = ( design, debug ) =>
{
  const renderHistory = new RenderHistory( design );
  if ( ! debug ) // in debug mode, we'll interpret incrementally
    try {
      interpret( Step.DONE, renderHistory, [] );
    } catch (error) {
      renderHistory .setError( error );
    }
  return renderHistory;
}