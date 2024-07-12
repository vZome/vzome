
import { com } from './core-java.js';
import { realizeShape, normalizeRenderedManifestation } from './scenes.js';

//  Right now this is duplicated!
export const Step = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

// TODO: move this to 4 methods of EditCursor
export const interpret = ( action, cursor, effects, stack=[] ) =>
{
  const step = () =>
  {
    let edit = cursor .getNextEdit();
    if ( ! edit )
      return Step.DONE;
    if ( edit .isBranch() ) {
      stack .push( { branch: edit, cursor } );
      cursor = cursor .startBranch( edit );
      effects .recordSnapshot( edit.id(), edit .firstChild().id() );
      if ( edit .nextSibling() ) {
        effects .recordSnapshot( edit.id(), edit .nextSibling().id() );
      }
      return Step.IN;
    } else {
      edit.perform( cursor.editContext ); // here we may create a legacy edit object
      const breakpointHit = cursor .atBreakpoint( edit );
      if ( edit .nextSibling() ) {
        effects .recordSnapshot( edit.id(), edit .nextSibling() .id() );
        cursor .setNextEdit( edit .nextSibling() );
        return breakpointHit? Step.DONE : Step.OVER;
      } else {
        effects .recordSnapshot( edit.id(), '--END--' ); // last one will be the real before-end
        let top;
        do {
          top = stack .pop();
        } while ( top && ! top.branch .nextSibling() )
        if ( top ) {
          cursor = top.cursor;  // overwrite and discard the prior value
          cursor .endBranch( top.branch );
          cursor .setNextEdit( top.branch .nextSibling() );
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

// TODO: record snapshots only for Snapshots, unless debugging
export class RenderHistory
{
  constructor( design )
  {
    const { firstEdit } = design;
    this.shapes = {};
    this.snapshotsAfter = {};
    this.shapshotsBefore = {};
    this.design = design;
    this.currentSnapshot = [];
    this.recordSnapshot( '--START--', firstEdit? firstEdit.id() : '--END--' );
  }

  // partial implementation of legacy RenderListener
  // TODO replace this with scenes.js renderedModelTransducer? Only if we want incremental events.
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

  recordSnapshot( afterId, beforeId )
  {
    this.lastEdit = afterId;
    this.currentSnapshot = []; // prior value overwritten, but it was already captured in snapshotsAfter and snapshotsBefore
    this.snapshotsAfter[ afterId ] = this.currentSnapshot;
    this.shapshotsBefore[ beforeId ] = this.currentSnapshot;
    this.design .batchRender( this ); // will make many callbacks to manifestationAdded()
  }

  getSnapshot( editId, before=false )
  {
    this .setError( null );
    const shapes = {};
    let snapshot = before? this.shapshotsBefore[ editId ] : this.snapshotsAfter[ editId ];
    if ( !snapshot ) {

      // TODO: disabled until we have a debugger again.
      //  NOTE: we will need to pass in or link to the editCursor somehow
      // this.breakpoint = editId;
      // try {
      //   interpret( Step.DONE, editCursor, this, [] );
      // } catch (error) {
      //   this .setError( error );
      // }
      // this.breakpoint = null;

      editId = before? '--END--' : this.lastEdit;
      snapshot = before? this.shapshotsBefore[ editId ] : this.snapshotsAfter[ editId ];
    }
    // for ( const instance of snapshot ) {
    //   const shapeId = instance.shapeId;
    //   if ( ! shapes[ shapeId ] ) {
    //     shapes[ shapeId ] = { ...this.shapes[ shapeId ], instances: [] };
    //   }
    //   shapes[ shapeId ] .instances .push( instance );
    // }
    return snapshot;
  }

  getShapes()
  {
    return this.shapes;
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

// TODO: rename Interpreter, and incorporate 4 methods from interpret
export class EditCursor
{
  constructor( design, history=design.history, firstEdit=design.firstEdit )
  {
    this.design = design;
    this.history = history;
    this.editContext = {
      performAndRecord: edit => {
        edit .perform();
        history .addEdit( edit );
      },
      createLegacyCommand: name => this.design.editContext .createLegacyCommand( name ),
    };
    this.nextEdit = firstEdit;
    this.context
  }

  toString()
  {
    return this.nextEdit?.name() || 'nameless';
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
    // Unused until we have a debugger again
    return ( edit.id() === this.breakpoint );
  }

  startBranch( parsedEdit )
  {
    const branch = new com.vzome.core.editor.Branch( this.design.editContext );
    this.history .addEdit( branch, this.design.editContext );
    parsedEdit .legacyEdit = branch; // oops, violating encapsulation
    return new EditCursor( this.design, branch, parsedEdit.firstChild() );
  }

  endBranch( parsedEdit )
  {
    const reversed = parsedEdit.children .toReversed();
    for (const edit of reversed) {
      const trueEdit = edit .legacyEdit;
      if ( trueEdit ) {
        trueEdit .undo();
      } else
        console.log( 'No true edit?' );
    }
  }
}
