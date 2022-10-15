
// TODO: put this in a module that both worker and main context can use.
//  Right now this is duplicated!
const Step = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

const interpret = ( action, state, stack=[] ) =>
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

export const realizeShape = ( shape ) =>
{
  const vertices = shape.getVertexList().toArray().map( av => {
    const { x, y, z } = av.toRealVector();  // this is too early to do embedding, which is done later, globally
    return { x, y, z };
  })
  const faces = shape.getTriangleFaces().toArray().map( ({ vertices }) => ({ vertices }) );  // not a no-op, converts to POJS
  const id = 's' + shape.getGuid().toString();
  return { id, vertices, faces, instances: [] };
}

export const normalizeRenderedManifestation = rm =>
{
  const id = 'i' + rm.getGuid().toString();
  const shapeId = 's' + rm.getShapeId().toString();
  const positionAV = rm.getLocationAV();
  const { x, y, z } = ( positionAV && positionAV.toRealVector() ) || { x:0, y:0, z:0 };
  const rotation = rm .getOrientation() .getRowMajorRealElements();
  const selected = rm .getGlow() > 0.001;
  const componentToHex = c => {
    let hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
  }
  let color = "#ffffff";
  const rmc = rm.getColor();
  if ( rmc )
    color = "#" + componentToHex(rmc.getRed()) + componentToHex(rmc.getGreen()) + componentToHex(rmc.getBlue());

  return { id, position: [ x, y, z ], rotation, color, selected, shapeId };
}

export class RenderHistory
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
    this.recordSnapshot( '--START--', firstEdit? firstEdit.id() : '--END--' );
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