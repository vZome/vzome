
import React, { useState } from 'react';

import { useControllerProperty, useControllerAction } from '../controller-hooks.js';
import { useEffect } from 'react';

const ToolbarSpacer = () => ( <div style={{ minWidth: '10px', minHeight: '10px' }}></div> )

const ToolbarButton = ( { label, image } ) =>
(
  <button aria-label={label} className='toolbar-button'>
    <img src={ `./icons/tools/${image}.png`}/>
  </button>
)

const ToolFactoryButton = ( { factoryName } ) => <ToolbarButton label={factoryName} image={`newTool/${factoryName}`} />
const CommandButton = ( { cmdName } ) => <ToolbarButton label={cmdName} image={`small/${cmdName}`} />

export const ToolFactoryBar = ( { controller }) =>
{
  const symmFactoryNames = useControllerProperty( controller, 'symmetryToolFactories', 'symmetryToolFactories', true );
  const transFactoryNames = useControllerProperty( controller, 'transformToolFactories', 'transformToolFactories', true );
  const mapFactoryNames = useControllerProperty( controller, 'linearMapToolFactories', 'linearMapToolFactories', true );

  return (
    <div id='factory-bar' style={{ display: 'flex', minHeight: '34px' }}>
      { symmFactoryNames && symmFactoryNames.map && symmFactoryNames.map( factoryName =>
        <ToolFactoryButton key={factoryName} factoryName={factoryName}/>
      ) }
      <ToolbarSpacer key='sp1' />
      { transFactoryNames && transFactoryNames.map && transFactoryNames.map( factoryName =>
        <ToolFactoryButton key={factoryName} factoryName={factoryName}/>
      ) }
      <ToolbarSpacer key='sp2' />
      { mapFactoryNames && mapFactoryNames.map && mapFactoryNames.map( factoryName =>
        <ToolFactoryButton key={factoryName} factoryName={factoryName}/>
      ) }
    </div>
  )
}

const ToolButton = ( { controller } ) =>
{
  const kind = useControllerProperty( controller, 'kind', 'kind', false );
  const label = useControllerProperty( controller, 'label', 'label', false );
  return ( <ToolbarButton label={label} image={`small/${kind}`} /> )
}

export const ToolBar = ( { symmetryController, toolsController }) =>
{
  const symmToolNames = useControllerProperty( symmetryController, 'builtInSymmetryTools', 'builtInSymmetryTools', true );
  const transToolNames = useControllerProperty( symmetryController, 'builtInTransformTools', 'builtInTransformTools', true );

  return (
    <div id='tools-bar' style={{ display: 'flex' }}>
      <CommandButton cmdName='Delete'/>
      <CommandButton cmdName='hideball'/>
      <CommandButton cmdName='setItemColor'/>
      <ToolbarSpacer/>
      <CommandButton cmdName='JoinPoints/CLOSED_LOOP' hoverText='Connect balls in a loop'/>
      <CommandButton cmdName='JoinPoints/CHAIN_BALLS' hoverText='Connect balls in a chain'/>
      <CommandButton cmdName='JoinPoints/ALL_TO_LAST' hoverText='Connect all balls to last selected'/>
      <CommandButton cmdName='JoinPoints/ALL_POSSIBLE' hoverText='Connect balls in all possible ways'/>
      <CommandButton cmdName='panel' hoverText='Make a panel polygon'/>
      <CommandButton cmdName='NewCentroid' hoverText='Construct centroid of points'/>
      <ToolbarSpacer/>
      { symmToolNames && symmToolNames.map && symmToolNames.map( toolName =>
        <ToolButton key={toolName} controller={`${toolsController}:${toolName}`}/>
      ) }
      <ToolbarSpacer/>
      { transToolNames && transToolNames.map && transToolNames.map( toolName =>
        <ToolButton key={toolName} controller={`${toolsController}:${toolName}`}/>
      ) }
    </div>
  )
}

let nextBookmarkIcon = 0;

const BookmarkButton = ( { controller } ) =>
{
  const label = useControllerProperty( controller, 'label', 'label', false );
  const [ iconName, setIconName ] = useState( null );
  useEffect( () => {
    setIconName( `bookmark_${nextBookmarkIcon}`);
    nextBookmarkIcon = ( nextBookmarkIcon + 1 ) % 4;
  }, [] );
  return ( <ToolbarButton label={label} image={`small/${iconName}`} /> )
}

export const BookmarkBar = ( { bookmarkController, toolsController }) =>
{
  // const bookmarkNames = useControllerProperty( bookmarkController, 'builtInSymmetryTools', 'builtInSymmetryTools', true );

  return (
    <div id='tools-bar' style={{ display: 'flex', flexDirection: 'column' }}>
      <ToolFactoryButton factoryName='bookmark' />
      <ToolbarSpacer/>
      <BookmarkButton controller={`${toolsController}:bookmark.builtin/ball at origin`}/>
      <BookmarkButton controller={`${toolsController}:bookmark.builtin/ball at origin`}/>
      <BookmarkButton controller={`${toolsController}:bookmark.builtin/ball at origin`}/>
      <BookmarkButton controller={`${toolsController}:bookmark.builtin/ball at origin`}/>
      <BookmarkButton controller={`${toolsController}:bookmark.builtin/ball at origin`}/>
      {/* { bookmarkNames && bookmarkNames.map && bookmarkNames.map( toolName =>
        <BookmarkButton controller={`${toolsController}:${toolName}`}/>
      ) } */}
    </div>
  )
}
