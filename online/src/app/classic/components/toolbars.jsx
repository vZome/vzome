
import { createSignal, createEffect, onCleanup } from "solid-js";

import IconButton from '@suid/material/IconButton';
import MoreHorizIcon from '@suid/icons-material/MoreHoriz';
import CloseIcon from '@suid/icons-material/Close';

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
    <img src={ resourceUrl( `icons/tools/${props.image}.png` ) } class='toolbar-image'/>
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
  const handleClick = () =>
    controllerAction( controller(), 'createTool' );
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
  // Full roster of user-created tools, including unpinned ones (customTools omits hidden).
  const allCustomNames = () => controllerProperty( props.toolsController, 'allCustomTools', 'allCustomTools', true );
  // Just the pinned tools (customTools omits hidden), so we can show a label when none are pinned.
  const pinnedNames = () => controllerProperty( props.toolsController, 'customTools', 'customTools', true );
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

  // The pinned custom tools row, reused for both the collapsed (in-flow) and expanded (floating) states.
  const PinnedRow = () => (
    <For each={allCustomNames()}>{ toolName =>
      <ToolButton controller={subController( props.toolsController, toolName )}/>
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

            {/* Top arm — sized to its content (not stretched to body width), so the body is wider
                and the notch appears at the top-right. */}
            <div class='custom-tools-arm'>
              <div class='custom-tools-row'>
                <Show when={pinnedNames().length > 0} fallback={<span class='custom-tools-empty-label'>Custom Tools</span>}>
                  <PinnedRow/>
                </Show>
              </div>
              <IconButton class='custom-tools-close' aria-label='Close custom tools' title='Close'
                  size='small' onClick={handleClose}>
                <CloseIcon fontSize='small'/>
              </IconButton>
            </div>

            {/* Body — the larger rectangle. */}
            <div class='custom-tools-body'>
              <div class='custom-tools-section-label'>Unpinned tools</div>
              <div class='custom-tools-row custom-tools-unpinned'>
                <For each={allCustomNames()}>{ toolName =>
                  <ToolButton showWhenHidden controller={subController( props.toolsController, toolName )}/>
                }</For>
              </div>

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

let nextBookmarkIcon = 0;

const BookmarkButton = props =>
{
  const { controllerAction } = useEditor();
  const label = () => controllerProperty( props.controller, 'label', 'label', false ) || ''; // always defined, to control the ToolConfig
  const [ iconName, setIconName ] = createSignal( null );
  createEffect( () => {
    setIconName( `bookmark_${nextBookmarkIcon}` );
    nextBookmarkIcon = ( nextBookmarkIcon + 1 ) % 4;
  }, [] );
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

export const BookmarkBar = props =>
{
  const { symmetryController, symmetryDefined } = useSymmetry();
  const bookmarkNames = () => controllerProperty( props.toolsController, 'customBookmarks', 'customBookmarks', true );

  return (
    <div id='tools-bar' class='toolbar-vert'>
      <Show when={symmetryDefined()}>
        <ToolbarSpacer/>
        <ToolFactoryButton factoryName='bookmark' controller={symmetryController()}/>
      </Show>
      <ToolbarSpacer/>
      <BookmarkButton predefined controller={subController( props.toolsController, 'bookmark.builtin/ball at origin' )}/>
      <For each={bookmarkNames()}>{ toolName =>
        <BookmarkButton controller={subController( props.toolsController, toolName )}/>
      }</For>
    </div>
  )
}
