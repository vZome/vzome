
import { createSignal, createEffect } from "solid-js";

import { controllerAction, controllerProperty, subController } from '../../../workerClient/controllers-solid.js';
import { useSymmetry } from "../classic.jsx";

const ToolbarSpacer = () => ( <div style={{ 'min-width': '10px', 'min-height': '10px' }}></div> )

const ToolbarButton = props =>
(
  <button aria-label={props.label} class='toolbar-button' onClick={props.onClick} disabled={props.disabled}>
    <img src={ `./icons/tools/${props.image}.png`} class='toolbar-image'/>
  </button>
)

const ToolFactoryButton = props =>
{
  const { symmetryController } = useSymmetry();
  const controller = () => subController( symmetryController(), props.factoryName );
  const enabled = () =>
  {
    const enabled = controllerProperty( controller(), 'enabled' );
    return enabled && (enabled === 'true');
  }
  const handleClick = () =>
    controllerAction( controller(), 'createTool' );
  return (
    <ToolbarButton label={props.factoryName} image={`newTool/${props.factoryName}`} onClick={handleClick} disabled={!enabled()} />
  )
}

export const ToolFactoryBar = props =>
{
  const { symmetryController, symmetryDefined } = useSymmetry();
  const symmFactoryNames = () => controllerProperty( symmetryController(), 'symmetryToolFactories', 'symmetryToolFactories', true );
  const transFactoryNames = () => controllerProperty( symmetryController(), 'transformToolFactories', 'transformToolFactories', true );
  const mapFactoryNames = () => controllerProperty( symmetryController(), 'linearMapToolFactories', 'linearMapToolFactories', true );

  return (
    <div id='factory-bar' class='toolbar'>
      <Show when={symmetryDefined()}>
      <For each={symmFactoryNames()}>{ factoryName =>
        <ToolFactoryButton factoryName={factoryName}/>
      }</For>
      <ToolbarSpacer key='sp1' />
      <For each={transFactoryNames()}>{ factoryName =>
        <ToolFactoryButton factoryName={factoryName}/>
      }</For>
      <ToolbarSpacer key='sp2' />
      <For each={mapFactoryNames()}>{ factoryName =>
        <ToolFactoryButton factoryName={factoryName}/>
      }</For>
      </Show>
    </div>
  )
}

const CommandButton = props =>
{
  const handleClick = () => controllerAction( props.ctrlr, props.cmdName );
  return (
    <ToolbarButton label={props.cmdName} image={`small/${props.cmdName}`} onClick={handleClick} />
  );
}

const ToolButton = props =>
{
  const kind = () => controllerProperty( props.controller, 'kind', 'kind', false );
  const label = () => controllerProperty( props.controller, 'label', 'label', false );
  const handleClick = () => controllerAction( props.controller, 'apply' );
  return ( <ToolbarButton label={label()} image={`small/${kind()}`} onClick={handleClick} /> )
}

export const ToolBar = props =>
{
  const { symmetryController, symmetryDefined } = useSymmetry();
  const symmToolNames = () => controllerProperty( symmetryController(), 'builtInSymmetryTools', 'builtInSymmetryTools', true );
  const transToolNames = () => controllerProperty( symmetryController(), 'builtInTransformTools', 'builtInTransformTools', true );
  const customToolNames = () => controllerProperty( props.toolsController, 'customTools', 'customTools', true );

  return (
    <div id='tools-bar' class='toolbar'>
      <CommandButton ctrlr={props.editorController} cmdName='Delete'/>
      <CommandButton ctrlr={props.editorController} cmdName='hideball'/>
      <CommandButton ctrlr={props.editorController} cmdName='setItemColor'/>
      <ToolbarSpacer/>
      <CommandButton ctrlr={props.editorController} cmdName='JoinPoints/CLOSED_LOOP' hoverText='Connect balls in a loop'/>
      <CommandButton ctrlr={props.editorController} cmdName='JoinPoints/CHAIN_BALLS' hoverText='Connect balls in a chain'/>
      <CommandButton ctrlr={props.editorController} cmdName='JoinPoints/ALL_TO_LAST' hoverText='Connect all balls to last selected'/>
      <CommandButton ctrlr={props.editorController} cmdName='JoinPoints/ALL_POSSIBLE' hoverText='Connect balls in all possible ways'/>
      <CommandButton ctrlr={props.editorController} cmdName='panel' hoverText='Make a panel polygon'/>
      <CommandButton ctrlr={props.editorController} cmdName='NewCentroid' hoverText='Construct centroid of points'/>
      <ToolbarSpacer/>
      <Show when={symmetryDefined()}>
        <For each={symmToolNames()}>{ toolName =>
          <ToolButton controller={subController( props.toolsController, toolName )}/>
        }</For>
        <ToolbarSpacer/>
        <For each={transToolNames()}>{ toolName =>
          <ToolButton controller={subController( props.toolsController, toolName )}/>
        }</For>
        <ToolbarSpacer/>
        <For each={customToolNames()}>{ toolName =>
          <ToolButton controller={subController( props.toolsController, toolName )}/>
        }</For>
      </Show>
    </div>
  )
}

let nextBookmarkIcon = 0;

const BookmarkButton = props =>
{
  const label = () => controllerProperty( props.controller, 'label', 'label', false );
  const [ iconName, setIconName ] = createSignal( null );
  createEffect( () => {
    setIconName( `bookmark_${nextBookmarkIcon}` );
    nextBookmarkIcon = ( nextBookmarkIcon + 1 ) % 4;
  }, [] );
  const handleClick = () => controllerAction( props.controller, 'apply' );
  return ( <ToolbarButton label={label()} image={`small/${iconName()}`} onClick={handleClick} /> )
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
      <BookmarkButton controller={subController( props.toolsController, 'bookmark.builtin/ball at origin' )}/>
      <For each={bookmarkNames()}>{ toolName =>
        <BookmarkButton controller={subController( props.toolsController, toolName )}/>
      }</For>
    </div>
  )
}
