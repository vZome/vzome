
import { createSignal, createEffect, onCleanup } from "solid-js";

import IconButton from '@suid/material/IconButton';
import MoreHorizIcon from '@suid/icons-material/MoreHoriz';
import CloseIcon from '@suid/icons-material/Close';

import {
  DragDropProvider,
  DragDropSensors,
  SortableProvider,
  createSortable,
  createDroppable,
  mostIntersecting,
} from "@thisbeyond/solid-dnd";

import { controllerProperty, subController, useEditor } from '../../framework/context/editor.jsx';
import { resumeMenuKeyEvents, suspendMenuKeyEvents } from '../context/commands.jsx';
import { useSymmetry } from "../context/symmetry.jsx";
import { ToolConfig } from "../dialogs/toolconfig.jsx";
import { resourceUrl } from "./length.jsx";

const ToolbarSpacer = () => ( <div style={{ 'min-width': '10px', 'min-height': '10px' }}></div> )

// Capitalize the first word (i.e. the first letter) of a label, leaving the rest untouched.
const capitalizeFirst = s => s? s.charAt(0).toUpperCase() + s.slice(1) : s;

const ToolbarButton = props =>
(
  // The tooltip (title) always shows the full label; the visible caption uses displayLabel when
  // provided (e.g. factory buttons strip a redundant "Create a/an " prefix), else the full label.
  <button aria-label={props.label} title={props.label} class='toolbar-button' onClick={props.onClick} onContextMenu={props.onContextMenu} disabled={props.disabled}>
    <img draggable={false} src={ resourceUrl( `icons/tools/${props.image}.png` ) } class='toolbar-image'/>
    <span class='toolbar-label'>{capitalizeFirst( props.displayLabel ?? props.label )}</span>
  </button>
)

const ToolFactoryButton = props =>
{
  const { controllerAction } = useEditor();
  const { symmetryController } = useSymmetry();
  const controller = () => subController( symmetryController(), props.factoryName );
  const enabled = () =>
  {
    const enabled = controllerProperty( controller(), 'enabled' );
    return enabled && (enabled === 'true');
  }
  const label = () => controllerProperty( controller(), 'title' );
  // Factory titles all read "Create a/an <thing> tool"; the "Create a/an " is noise on the button
  //  (though kept in the tooltip). Strip it for the caption only.
  const displayLabel = () => ( label() || '' ).replace( /^create an? /i, '' );
  const handleClick = () => controllerAction( controller(), 'createTool' );
  return (
    <ToolbarButton label={label()} displayLabel={displayLabel()} image={`newTool/${props.factoryName}`} onClick={handleClick} disabled={!enabled()} />
  )
}

const CommandButton = props =>
{
  const { controllerAction } = useEditor();
  const handleClick = () => controllerAction( props.ctrlr, props.cmdName );
  return (
    <ToolbarButton label={props.hoverText} image={`small/${props.cmdName}`} onClick={handleClick} />
  );
}

const SetColorButton = props =>
{
  const { controllerAction } = useEditor();
  let colorInputElement;
  const handleClick = () =>
  {
    colorInputElement.click();
  }
  const setColor = color =>
  {
    controllerAction( props.ctrlr, `ColorManifestations/${color}ff` );
  }
  createEffect( () => {
    // skip the leading "#"
    colorInputElement.addEventListener( "change", e => setColor( e.target.value.substring(1) ), false );
  });
  return ( <>
    <ToolbarButton label={props.hoverText} image={`small/setItemColor`} onClick={handleClick} />
    <input ref={colorInputElement} type="color" name="color-picker" class='hidden-color-input' />
  </>);
}

// A toolbar tool icon. Click applies the tool; right-click opens the shared ToolConfig dialog
// (which offers Pin/Unpin based on the tool's own hidden state).
//   - By default the button renders only when the tool is PINNED (visible), so it drops off the
//     bar when unpinned.
//   - With showWhenHidden, it renders only when the tool is UNPINNED (hidden); this is how the
//     open custom-tools panel presents the unpinned row using the identical icon presentation.
const ToolButton = props =>
{
  const { controllerAction } = useEditor();
  const kind = () => controllerProperty( props.controller, 'kind', 'kind', false );
  const label = () => controllerProperty( props.controller, 'label', 'label', false );
  const hidden = () => controllerProperty( props.controller, 'hidden', 'hidden', false ) === 'true';
  const visible = () => !!kind() && ( props.showWhenHidden? hidden() : !hidden() );
  const handleClick = () => controllerAction( props.controller, 'apply' );
  const [anchorEl, setAnchorEl] = createSignal(null);
  const handleOpen = (e) =>
  {
    suspendMenuKeyEvents();
    setAnchorEl( e.currentTarget );
    e.preventDefault(); e.stopPropagation();
  }
  const handleClose = () => {
    resumeMenuKeyEvents();
    setAnchorEl( null );
  }
  return (
    <Show when={visible()}>
      <ToolbarButton label={label()} image={`small/${kind()}`} onClick={handleClick} onContextMenu={handleOpen} />
      <ToolConfig predefined={props.predefined} image={`small/${kind()}`} controller={props.controller} label={label()}
        anchor={anchorEl()} onClose={handleClose} onClick={handleClick} />
    </Show>
  )
}

// Shared pin/unpin + reorder behavior for a customizable area (tools or bookmarks). Given the
// worker list names and reorder action, it exposes the ordered pinned/unpinned id lists, a
// collision detector, and an onDragEnd. Layout (rows vs columns) is left to each caller.
//   controller     — an ACCESSOR (`() => toolsController`) for the tools controller, so the reads
//                     always resolve the LIVE controller from props rather than a value snapshotted
//                     during body execution (which could be undefined before props resolve, leaving
//                     the accessors bound to a stale/empty object that never becomes reactive).
//   allListName    — worker list of ALL custom items in order (e.g. 'allCustomTools')
//   pinnedListName — worker list of just the PINNED items (e.g. 'customTools')
//   reorderAction  — controller action to persist a new order (e.g. 'reorderTools')
const createPinnableArea = ( controller, allListName, pinnedListName, reorderAction ) =>
{
  const { controllerAction, setEdited } = useEditor();
  const allNames = () => controllerProperty( controller(), allListName, allListName, true );
  const pinnedNames = () => controllerProperty( controller(), pinnedListName, pinnedListName, true );

  const isPinned = id => pinnedNames() .includes( id );
  const order = () => allNames();
  const pinnedOrder = () => order() .filter( isPinned );
  const unpinnedOrder = () => order() .filter( id => ! isPinned( id ) );

  // Which segment a droppable belongs to: a zone id encodes it; an item id is read from pin state.
  const droppableIsPinned = id =>
    ( typeof id === 'string' && id .startsWith( ZONE_PREFIX ) )
      ? id .slice( ZONE_PREFIX .length ) === 'pinned'
      : isPinned( id );

  // SAME segment → normal intersection (drop onto an item → reorder). CROSS segment → force the
  //  destination ZONE (never an item), so the whole segment stays highlighted, the sortable
  //  "move out of the way" preview is suppressed, and the drop appends to the end (pin/unpin).
  const collisionDetector = ( draggable, droppables, context ) => {
    const target = mostIntersecting( draggable, droppables, context );
    if ( ! target )
      return null;
    if ( isPinned( draggable.id ) === droppableIsPinned( target.id ) )
      return target;
    const zoneId = ZONE_PREFIX + ( droppableIsPinned( target.id )? 'pinned' : 'unpinned' );
    return droppables .find( d => d.id === zoneId ) || target;
  };

  const onDragEnd = ( { draggable, droppable } ) => {
    if ( ! draggable || ! droppable )
      return;
    const fromId = draggable.id;
    const dropId = droppable.id;
    const current = order();
    if ( current .indexOf( fromId ) < 0 )
      return;

    let targetPinned, next;
    const withoutDragged = current .filter( id => id !== fromId );

    if ( typeof dropId === 'string' && dropId .startsWith( ZONE_PREFIX ) ) {
      // Zone drop: append to the END of that segment.
      targetPinned = dropId .slice( ZONE_PREFIX .length ) === 'pinned';
      const zoneIds = withoutDragged .filter( id => isPinned( id ) === targetPinned );
      const lastZoneId = zoneIds[ zoneIds .length - 1 ];
      const insertAt = lastZoneId === undefined
          ? ( targetPinned ? 0 : withoutDragged .length )        // empty zone
          : withoutDragged .indexOf( lastZoneId ) + 1;
      next = [ ...withoutDragged ];
      next .splice( insertAt, 0, fromId );
    }
    else {
      // Item drop (same segment only): insert immediately before it.
      if ( dropId === fromId )
        return;
      targetPinned = isPinned( dropId );
      const insertAt = withoutDragged .indexOf( dropId );
      if ( insertAt < 0 )
        return;
      next = [ ...withoutDragged ];
      next .splice( insertAt, 0, fromId );
    }

    // If the item changed segments, flip its hidden flag first so the worker's re-emitted lists
    //  reflect the new pinned status; then persist the new order.
    if ( isPinned( fromId ) !== targetPinned )
      controllerAction( subController( controller(), fromId ),
          targetPinned? 'unhideTool' : 'hideTool' );
    controllerAction( controller(), reorderAction, { order: next .join( ',' ) } );
    setEdited( true ); // reorder/pin persists with the design, so mark it dirty
  };

  return { order, pinnedNames, pinnedOrder, unpinnedOrder, collisionDetector, onDragEnd };
}

// A draggable/sortable wrapper for a custom item (tool or bookmark) in the open panel. The item
// id is the sortable id; children are the actual button (ToolButton / BookmarkButton).
const SortableItem = props =>
{
  const sortable = createSortable( props.itemId );
  return (
    <div use:sortable class='sortable-tool'
        classList={{ 'sortable-tool-dragging': sortable.isActiveDraggable }}>
      {props.children}
    </div>
  );
}

// The prefix distinguishing a zone droppable id from a tool id, so onDragEnd can tell whether a
// drop landed on a tool (fine ordering) or in the empty area of a row (append to that zone).
const ZONE_PREFIX = 'zone:';

// A droppable ROW (pinned or unpinned). Being a droppable in its own right, it is a valid drop
// target even when empty — which is what makes "drag the only tool into the unpinned row" work,
// and gives the two rows distinct, predictable pin/unpin drop regions.
const DropZone = props =>
{
  const droppable = createDroppable( ZONE_PREFIX + props.zone );
  return (
    <div use:droppable class={props.class}
        classList={{ 'drop-zone-active': droppable.isActiveDroppable }}>
      {props.children}
    </div>
  );
}

// The custom-tools area — a single container with collapsed and expanded states (NOT a portalled
// popover). Collapsed, it sits in the toolbar flow showing the pinned custom tools and a "⋯"
// button. Expanded, a panel floats ABOVE the toolbar (absolutely positioned, so it does not
// reflow the bar) anchored at the same origin, so its top "Pinned" row overlays the in-flow row
// and the panel simply grows downward into the unpinned tools and the tool factories.
// Both tool rows use the identical ToolButton presentation and the same right-click ToolConfig
// gesture; the only difference is Pin vs. Unpin, which ToolConfig derives from each tool's state.
const CustomToolsArea = props =>
{
  const { symmetryController, symmetryDefined } = useSymmetry();
  const { order, pinnedNames, pinnedOrder, unpinnedOrder, collisionDetector, onDragEnd } =
    createPinnableArea( () => props.toolsController, 'allCustomTools', 'customTools', 'reorderTools' );

  // Tool factories, relocated here from the removed ToolFactoryBar.
  const symmFactoryNames = () => controllerProperty( symmetryController(), 'symmetryToolFactories', 'symmetryToolFactories', true );
  const transFactoryNames = () => controllerProperty( symmetryController(), 'transformToolFactories', 'transformToolFactories', true );
  const mapFactoryNames = () => controllerProperty( symmetryController(), 'linearMapToolFactories', 'linearMapToolFactories', true );

  // A factory is enabled when the current selection is valid input for it. Aggregate across all
  //  factories so the "Create a tool" section can prompt the user when none can fire (reactive,
  //  since each factory's 'enabled' updates with the selection).
  const factoryEnabled = factoryName =>
    controllerProperty( subController( symmetryController(), factoryName ), 'enabled' ) === 'true';
  const anyFactoryEnabled = () =>
    [ ...symmFactoryNames(), ...transFactoryNames(), ...mapFactoryNames() ].some( factoryEnabled );

  const [ open, setOpen ] = createSignal( false );
  // Viewport position of the collapsed row, so the floating (fixed) panel can overlay it at the
  //  same origin while escaping the toolbar's overflow/height clipping.
  const [ anchorRect, setAnchorRect ] = createSignal( null );
  let collapsedRef;
  const captureAnchor = () => collapsedRef && setAnchorRect( collapsedRef.getBoundingClientRect() );
  const handleOpen = () => { suspendMenuKeyEvents(); captureAnchor(); setOpen( true ); }
  const handleClose = () => { resumeMenuKeyEvents(); setOpen( false ); }

  // Keep the panel aligned if the window resizes while it is open.
  createEffect( () => {
    if ( ! open() ) return;
    const onResize = () => captureAnchor();
    window.addEventListener( 'resize', onResize );
    onCleanup( () => window.removeEventListener( 'resize', onResize ) );
  });

  const panelStyle = () => {
    const r = anchorRect();
    return r? { position: 'fixed', top: `${r.top}px`, left: `${r.left}px` } : {};
  };

  // The pinned custom tools, in user order — plain (non-draggable) buttons, used in the collapsed
  //  in-flow row. (The expanded panel uses the sortable variants below.)
  const PinnedRow = () => (
    <For each={pinnedOrder()}>{ toolId =>
      <ToolButton controller={subController( props.toolsController, toolId )}/>
    }</For>
  );

  return (
    <Show when={symmetryDefined()}>
      <div class='custom-tools-area'>

        {/* Collapsed, in-flow content: the pinned tools list, then the "more" toggle as a
            first-class sibling (not part of the tool list). Stays in the toolbar flow in both
            states, reserving the bar space beneath the floating panel. */}
        <div class='custom-tools-collapsed' ref={collapsedRef}>
          <div class='custom-tools-row'>
            <Show when={pinnedNames().length > 0} fallback={<span class='custom-tools-empty-label'>Custom Tools</span>}>
              <PinnedRow/>
            </Show>
          </div>
          <IconButton class='custom-tools-more' aria-label='More tools' title='More tools'
              size='small' onClick={handleOpen}>
            <MoreHorizIcon fontSize='small'/>
          </IconButton>
        </div>

        {/* Expanded, floating panel. Rendered position:fixed and anchored to the collapsed row's
            viewport rect, so it floats above BOTH the toolbar and the canvas (like a dialog) and
            does not participate in the toolbar's scroll/overflow. A backdrop catches outside clicks. */}
        <Show when={open()}>
          <div class='custom-tools-backdrop' onClick={handleClose} />
          {/* L-shaped panel (option 2): a narrow top arm (the pinned tools + close, matching the
              collapsed footprint) fused to a wider body below. The wrapper carries only the
              drop-shadow so the shadow traces the whole L silhouette; each box draws its own
              border, and the arm overlaps the body's top edge by 1px to hide the seam. */}
          <div class='custom-tools-panel' style={panelStyle()}>

           {/* One DragDropProvider spans BOTH the pinned (arm) and unpinned (body) rows, so a drag
               can reorder within a row and also carry a tool across the pin boundary. SortableProvider
               ids = the whole shared order. */}
           <DragDropProvider onDragEnd={onDragEnd} collisionDetector={collisionDetector}>
            <DragDropSensors/>
            <SortableProvider ids={order()}>

            {/* Top arm — sized to its content (not stretched to body width), so the body is wider
                and the notch appears at the top-right. */}
            <div class='custom-tools-arm'>
              <DropZone zone='pinned' class='custom-tools-row custom-tools-dropzone'>
                <Show when={pinnedOrder().length > 0} fallback={<span class='custom-tools-empty-label'>Custom Tools</span>}>
                  <For each={pinnedOrder()}>{ toolId =>
                    <SortableItem itemId={toolId}>
                      <ToolButton controller={subController( props.toolsController, toolId )}/>
                    </SortableItem>
                  }</For>
                </Show>
              </DropZone>
              <IconButton class='custom-tools-close' aria-label='Close custom tools' title='Close'
                  size='small' onClick={handleClose}>
                <CloseIcon fontSize='small'/>
              </IconButton>
            </div>

            {/* Body — the larger rectangle. */}
            <div class='custom-tools-body'>
              <div class='custom-tools-section-label'>Unpinned tools</div>
              <DropZone zone='unpinned' class='custom-tools-row custom-tools-unpinned custom-tools-dropzone'>
                <Show when={unpinnedOrder().length > 0}
                    fallback={<span class='custom-tools-drop-hint'>Drag tools here to unpin</span>}>
                  <For each={unpinnedOrder()}>{ toolId =>
                    <SortableItem itemId={toolId}>
                      <ToolButton showWhenHidden controller={subController( props.toolsController, toolId )}/>
                    </SortableItem>
                  }</For>
                </Show>
              </DropZone>

              <Show when={anyFactoryEnabled()}
                  fallback={<div class='custom-tools-section-label custom-tools-hint'><i>Select objects to enable tool creation</i></div>}>
                <div class='custom-tools-section-label'>Create a tool</div>
              </Show>
              {/* First factory row: the symmetry and transform sections. */}
              <div class='custom-tools-row'>
                <For each={symmFactoryNames()}>{ factoryName =>
                  <ToolFactoryButton factoryName={factoryName}/>
                }</For>
                <ToolbarSpacer/>
                <For each={transFactoryNames()}>{ factoryName =>
                  <ToolFactoryButton factoryName={factoryName}/>
                }</For>
              </div>
              {/* Second factory row: the linear-map section. */}
              <div class='custom-tools-row'>
                <For each={mapFactoryNames()}>{ factoryName =>
                  <ToolFactoryButton factoryName={factoryName}/>
                }</For>
              </div>
            </div>

            </SortableProvider>
           </DragDropProvider>
          </div>
        </Show>
      </div>
    </Show>
  )
}

export const ToolBar = props =>
{
  const { symmetryController, symmetryDefined } = useSymmetry();
  const symmToolNames = () => controllerProperty( symmetryController(), 'builtInSymmetryTools', 'builtInSymmetryTools', true );
  const transToolNames = () => controllerProperty( symmetryController(), 'builtInTransformTools', 'builtInTransformTools', true );

  return (
    // Single grid item wrapping both rows, so ToolBar occupies one min-content track in the
    // #editor-canvas grid (a bare fragment would spill its two rows into separate grid tracks).
    <div class="toolbar-rows">
    {/* Top row: the command sections (edit/color and connect/panel/centroid). */}
    <div class="toolbar-wrapper">
    <div class="absolute-0">
    <div class='toolbar centered-scroller'>
      <CommandButton ctrlr={props.editorController} cmdName='Delete' hoverText='Delete'/>
      <CommandButton ctrlr={props.editorController} cmdName='hideball' hoverText='Hide'/>
      <SetColorButton ctrlr={props.editorController} hoverText='Set color'/>
      <ToolbarSpacer/>
      <CommandButton ctrlr={props.editorController} cmdName='JoinPoints/CLOSED_LOOP' hoverText='Connect balls in a loop'/>
      <CommandButton ctrlr={props.editorController} cmdName='JoinPoints/CHAIN_BALLS' hoverText='Connect balls in a chain'/>
      <CommandButton ctrlr={props.editorController} cmdName='JoinPoints/ALL_TO_LAST' hoverText='Connect all balls to last selected'/>
      <CommandButton ctrlr={props.editorController} cmdName='JoinPoints/ALL_POSSIBLE' hoverText='Connect balls in all possible ways'/>
      <CommandButton ctrlr={props.editorController} cmdName='panel' hoverText='Make a panel polygon'/>
      <CommandButton ctrlr={props.editorController} cmdName='NewCentroid' hoverText='Construct centroid of points'/>
    </div>
    </div>
    </div>

    {/* Second row: the predefined symmetry & transform tools, then the custom-tools area. */}
    <div class="toolbar-wrapper">
    <div class="absolute-0">
    <div class='toolbar centered-scroller'>
      <Show when={symmetryDefined()}>
        <For each={symmToolNames()}>{ toolName =>
          <ToolButton predefined controller={subController( props.toolsController, toolName )}/>
        }</For>
        <ToolbarSpacer/>
        <For each={transToolNames()}>{ toolName =>
          <ToolButton predefined controller={subController( props.toolsController, toolName )}/>
        }</For>
        <ToolbarSpacer/>
        <CustomToolsArea toolsController={props.toolsController}/>
      </Show>
    </div>
    </div>
    </div>
    </div>
  )
}

// Pick one of the 4 bookmark icons deterministically from the bookmark's id, so the SAME
// bookmark always shows the SAME icon everywhere it is rendered (collapsed column AND panel).
const bookmarkIconFor = id => {
  let h = 0;
  for ( let i = 0; i < (id||'') .length; i++ )
    h = ( h * 31 + id .charCodeAt( i ) ) & 0x7fffffff;
  return `bookmark_${ h % 4 }`;
}

const BookmarkButton = props =>
{
  const { controllerAction } = useEditor();
  const label = () => controllerProperty( props.controller, 'label', 'label', false ) || ''; // always defined, to control the ToolConfig
  const iconName = () => bookmarkIconFor( props.bookmarkId );
  const handleClick = () => controllerAction( props.controller, 'apply' );
  const [anchorEl, setAnchorEl] = createSignal(null);
  const handleOpen = (e) =>
  {
    suspendMenuKeyEvents();
    setAnchorEl( e.currentTarget );
    e.preventDefault(); e.stopPropagation();
  }
  const handleClose = () => {
    resumeMenuKeyEvents();
    setAnchorEl( null );
  }
  return ( <>
    <ToolbarButton label={label()} image={`small/${iconName()}`} onClick={handleClick} onContextMenu={handleOpen} />
    <ToolConfig bookmark predefined={props.predefined} image={`small/${iconName()}`} controller={props.controller} label={label()}
      anchor={anchorEl()} onClose={handleClose} onClick={handleClick} />
  </> )
}

// The customizable bookmarks area — the column analog of CustomToolsArea. Collapsed, it is the
// in-flow vertical column of pinned bookmarks plus a "⋯" toggle. Expanded, a vertical L floats to
// the RIGHT: the arm (pinned column) sits at top-left over the collapsed column (notch at
// bottom-left), and the body extends rightward with the unpinned column beside it plus the single
// bookmark-factory button. Uses the same drag model as tools, with columns instead of rows.
const CustomBookmarksArea = props =>
{
  const { symmetryController, symmetryDefined } = useSymmetry();
  const { order, pinnedNames, pinnedOrder, unpinnedOrder, collisionDetector, onDragEnd } =
    createPinnableArea( () => props.toolsController, 'allCustomBookmarks', 'customBookmarks', 'reorderBookmarks' );

  const [ open, setOpen ] = createSignal( false );
  const [ anchorRect, setAnchorRect ] = createSignal( null );
  let collapsedRef;
  const captureAnchor = () => collapsedRef && setAnchorRect( collapsedRef.getBoundingClientRect() );
  const handleOpen = () => { suspendMenuKeyEvents(); captureAnchor(); setOpen( true ); }
  const handleClose = () => { resumeMenuKeyEvents(); setOpen( false ); }

  createEffect( () => {
    if ( ! open() ) return;
    const onResize = () => captureAnchor();
    window.addEventListener( 'resize', onResize );
    onCleanup( () => window.removeEventListener( 'resize', onResize ) );
  });

  // The floating panel is anchored to the collapsed column's viewport rect (top-left).
  const panelStyle = () => {
    const r = anchorRect();
    return r? { position: 'fixed', top: `${r.top}px`, left: `${r.left}px` } : {};
  };

  const bookmark = id => subController( props.toolsController, id );

  return (
    <Show when={symmetryDefined()}>
      <div class='custom-bookmarks-area'>

        {/* Collapsed, in-flow: the "more" toggle at the TOP, then the pinned bookmarks column below
            it. Placing the toggle above the column (where the panel's close button also sits) keeps
            the toggle stationary as the panel opens/closes, and the pinned bookmarks roughly so. */}
        <div class='custom-bookmarks-collapsed' ref={collapsedRef}>
          <IconButton class='custom-tools-more' aria-label='More bookmarks' title='More bookmarks'
              size='small' onClick={handleOpen}>
            <MoreHorizIcon fontSize='small'/>
          </IconButton>
          <div class='custom-bookmarks-column'>
            <For each={pinnedOrder()}>{ id =>
              <BookmarkButton bookmarkId={id} controller={bookmark( id )}/>
            }</For>
          </div>
        </div>

        <Show when={open()}>
          <div class='custom-tools-backdrop' onClick={handleClose} />
          <div class='custom-bookmarks-panel' style={panelStyle()}>
           <DragDropProvider onDragEnd={onDragEnd} collisionDetector={collisionDetector}>
            <DragDropSensors/>
            <SortableProvider ids={order()}>

            {/* Arm — the pinned bookmarks column (top-left), plus the close button. */}
            <div class='custom-bookmarks-arm'>
              <DropZone zone='pinned' class='custom-bookmarks-column custom-tools-dropzone'>
                <For each={pinnedOrder()}>{ id =>
                  <SortableItem itemId={id}>
                    <BookmarkButton bookmarkId={id} controller={bookmark( id )}/>
                  </SortableItem>
                }</For>
              </DropZone>
              <IconButton class='custom-tools-close' aria-label='Close bookmarks' title='Close'
                  size='small' onClick={handleClose}>
                <CloseIcon fontSize='small'/>
              </IconButton>
            </div>

            {/* Body — the unpinned column beside the arm, then the bookmark factory. */}
            <div class='custom-bookmarks-body'>
              <div class='custom-bookmarks-group'>
                <div class='custom-tools-section-label'>Unpinned</div>
                <DropZone zone='unpinned' class='custom-bookmarks-column custom-bookmarks-unpinned custom-tools-dropzone'>
                  <Show when={unpinnedOrder().length > 0}
                      fallback={<span class='custom-tools-drop-hint'>Drag bookmarks here to unpin</span>}>
                    <For each={unpinnedOrder()}>{ id =>
                      <SortableItem itemId={id}>
                        <BookmarkButton bookmarkId={id} controller={bookmark( id )}/>
                      </SortableItem>
                    }</For>
                  </Show>
                </DropZone>
              </div>

              <div class='custom-bookmarks-group'>
                <div class='custom-tools-section-label'>Create</div>
                <div class='custom-bookmarks-column'>
                  <ToolFactoryButton factoryName='bookmark'/>
                </div>
              </div>
            </div>

            </SortableProvider>
           </DragDropProvider>
          </div>
        </Show>
      </div>
    </Show>
  )
}

export const BookmarkBar = props =>
{
  const { symmetryController, symmetryDefined } = useSymmetry();

  return (
    <div id='tools-bar' class='toolbar-vert'>
      <Show when={symmetryDefined()}>
        <ToolbarSpacer/>
        {/* The bookmark factory stays here, above the column (also duplicated in the panel). */}
        <ToolFactoryButton factoryName='bookmark' controller={symmetryController()}/>
      </Show>
      <ToolbarSpacer/>
      <BookmarkButton predefined bookmarkId='bookmark.builtin/ball at origin'
          controller={subController( props.toolsController, 'bookmark.builtin/ball at origin' )}/>
      <CustomBookmarksArea toolsController={props.toolsController}/>
    </div>
  )
}
