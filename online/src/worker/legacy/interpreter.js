
import { Branch } from './from-java/com/vzome/core/editor/Branch.js';
import { realizeShape, normalizeRenderedManifestation } from './scenes.js';

//  Right now this is duplicated!
export const Step = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

// TODO: record snapshots only for Snapshots, unless debugging
export class RenderHistory
{
  constructor( design, polygons )
  {
    const { firstEdit } = design;
    this.shapes = {};
    this.snapshotsAfter = {};
    this.shapshotsBefore = {};
    this.design = design;
    this.currentSnapshot = [];
    this.polygons = polygons;
    this.recordSnapshot( '--START--', firstEdit? firstEdit.id() : '--END--' );
  }

  // partial implementation of legacy RenderListener
  // TODO replace this with scenes.js renderedModelTransducer? Only if we want incremental events.
  manifestationAdded( rm )
  {
    const shapeId = 's' + rm.getShapeId().toString();
    let shape = this.shapes[ shapeId ];
    if ( ! shape ) {
      shape = realizeShape( rm .getShape(), this.polygons );
      this.shapes[ shapeId ] = shape;
    }
    let instance = normalizeRenderedManifestation( rm );
    // Record this instance for the current edit
    this.currentSnapshot .push( instance );
  }

  recordSnapshot( afterId, beforeId )
  {
    this.currentSnapshot = []; // prior value overwritten, but it was already captured in snapshotsAfter and snapshotsBefore
    this.snapshotsAfter[ afterId ] = this.currentSnapshot;
    this.shapshotsBefore[ beforeId ] = this.currentSnapshot;
    this.design .batchRender( this ); // will make many callbacks to manifestationAdded()
  }

  getSnapshot( editId, before=false )
  {
    let snapshot = before? this.shapshotsBefore[ editId ] : this.snapshotsAfter[ editId ];
    if ( !snapshot ) {
      editId = before? '--END--' : this.lastEdit;
      snapshot = before? this.shapshotsBefore[ editId ] : this.snapshotsAfter[ editId ];
    }
    return snapshot;
  }

  getShapes()
  {
    return this.shapes;
  }
}

export class Interpreter
{
  constructor( design, effects )
  {
    this.design = design;
    this.effects = effects;

    this.cursor = new EditCursor( design.history, design.firstEdit );
    this.stack = [];
    this.editContext = {
      performAndRecord: edit => {
        edit .perform();
        if ( edit .isNoOp() )
          // TODO: get stricter for more modern designs, where this should never happen
          console.log( 'WARNING: No-op edit:', edit.constructor.name );
        this.cursor.history .addEdit( edit ); // must use the current cursor!
      },
      createLegacyCommand: name => this.design.editContext .createLegacyCommand( name ),
    };

    this.lastEdit = '--START--';
    this.breakpoint = null;
    this.error = null; // TODO: catch errors from edit.perform()

    //  The <EditHistory editNumber="N"> in the file counts edits AS WRITTEN there, but
    //  migrating an old-format file expands one recorded edit into several internal ones
    //  (and snapshots get inserted), so N is not an index into history.mEdits.  Passing it
    //  straight to goToEdit() rewinds far too far: one design with editNumber="40" has 248
    //  internal edits, and goToEdit(40) discards ~208 edits' worth of geometry.
    //
    //  So record, for each top-level edit id we replay, the internal edit number reached
    //  once it is done.  Java does the same translation in EditHistory.synchronize():
    //  "lastDoneEdit is in terms of the edits in the file, and we need to translate it to
    //  match the actual edit numbers, after migration and snapshot creation."
    this.internalEditNumbers = new Map();
  }

  /**
   * Translates a file-level edit number to the internal history edit number that replaying
   * it produced.  Returns undefined when the file number was never reached, in which case
   * the caller should leave the history where interpretation left it.
   */
  getInternalEditNumber( fileEditNumber )
  {
    return this.internalEditNumbers .get( `:${ fileEditNumber }:` );
  }

  /** Notes where the history stands after finishing one top-level edit. */
  recordInternalEditNumber( editId )
  {
    //  Only top-level ids, of the form ":N:" -- nested edits inside a Branch carry longer
    //  ids and are not what editNumber counts.
    if ( /^:\d+:$/ .test( editId ) )
      this.internalEditNumbers .set( editId, this.design.history.mEditNumber );
  }

  getLastEdit()
  {
    return this.lastEdit;
  }
    
  step()
  {    
    let edit = this.cursor .getNextEdit();
    if ( ! edit )
      return Step.DONE;
    if ( edit .isBranch() ) {
      this.stack .push( { branch: edit, cursor: this.cursor } );
      this.cursor = this.cursor .startBranch( edit, this.editContext );
      this.effects .recordSnapshot( edit.id(), edit .firstChild().id() );
      this.lastEdit = edit.id();
      if ( edit .nextSibling() ) {
        this.effects .recordSnapshot( edit.id(), edit .nextSibling().id() );
        this.lastEdit = edit.id();
      }
      return Step.IN;
    } else {
      edit.perform( this.editContext ); // here we may create a legacy edit object
      const breakpointHit = edit.id() === this.breakpoint;
      if ( edit .nextSibling() ) {
        this.effects .recordSnapshot( edit.id(), edit .nextSibling() .id() );
        this.lastEdit = edit.id();
        this.recordInternalEditNumber( edit.id() );
        this.cursor .setNextEdit( edit .nextSibling() );
        return breakpointHit? Step.DONE : Step.OVER;
      } else {
        this.effects .recordSnapshot( edit.id(), '--END--' ); // last one will be the real before-end
        this.lastEdit = edit.id();
        this.recordInternalEditNumber( edit.id() );
        let top;
        do {
          top = this.stack .pop();
          if ( !! top ) {
            this.cursor .endBranch( top.branch );
            this.cursor = top.cursor;  // overwrite and discard the prior value
            this.recordInternalEditNumber( top.branch .id() );
            this.cursor .setNextEdit( top.branch .nextSibling() );
          }
        } while ( top && ! top.branch .nextSibling() )
        if ( top ) {
          return breakpointHit? Step.DONE : Step.OUT;  // at the end of a branch
        } else {
          return Step.DONE;                            // at the end of the editHistory
        }
      }
    }
  }
  
  conTinue()
  {
    let stepped;
    do {
      stepped = this.stepOut();
    } while ( stepped !== Step.DONE );
  }
  
  stepOver()
  {
    let stepped = this.step();
    switch ( stepped ) {

      case Step.IN:
        stepped = this.stepOut();
        return stepped;
    
      default:
        return stepped;
    }
  }
  
  stepOut()
  {
    let stepped;
    do {
      stepped = this.stepOver();
    } while ( stepped !== Step.OUT && stepped !== Step.DONE );
    return stepped;
  }
  
  interpret( action, breakpoint=null )
  {
    this.breakpoint = breakpoint;
    switch ( action ) {
  
      case Step.IN:
        this.step();
        break;
    
      case Step.OVER:
        this.stepOver();
        break;
    
      case Step.OUT:
        this.stepOut();
        break;
    
      case Step.DONE:
      default:
        this.conTinue();
        break;
    }
  }
}

export class EditCursor
{
  constructor( history, firstEdit )
  {
    this.history = history;
    this.nextEdit = firstEdit;
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

  startBranch( parsedEdit, editContext )
  {
    const branch = new Branch( editContext );
    this.history .addEdit( branch, editContext );
    parsedEdit .legacyEdit = branch; // oops, violating encapsulation
    return new EditCursor( branch, parsedEdit.firstChild() );
  }

  endBranch( parsedEdit )
  {
    const reversed = parsedEdit.children .toReversed();
    for (const edit of reversed) {
      const trueEdit = edit .legacyEdit;
      if ( trueEdit ) {
        // console.log( 'undo', edit.nativeElement.id, edit.nativeElement.tagName );
        trueEdit .undo();
      } else
        console.log( 'No true edit?' );
    }
  }
}
