
import React from 'react';
import { IconButton } from '@material-ui/core';

import { useControllerProperty, useControllerAction } from '../controller-hooks.js';

const ToolbarSpacer = () => ( <div style={{ minWidth: '10px' }}></div> )

const ToolFactoryButton = ( { factoryName } ) =>
(
  <button aria-label={factoryName} style={{ padding: '0px', borderWidth: '2px', borderColor: 'darkgray' }}>
    <img key={factoryName} src={`./icons/tools/newTool/${factoryName}.png`}/>
  </button>
)

export const ToolFactoryBar = ( { controller }) =>
{
  const symmFactoryNames = useControllerProperty( controller, 'symmetryToolFactories', 'symmetryToolFactories', true );
  const transFactoryNames = useControllerProperty( controller, 'transformToolFactories', 'transformToolFactories', true );
  const mapFactoryNames = useControllerProperty( controller, 'linearMapToolFactories', 'linearMapToolFactories', true );

  return (
    <div id='factory-bar' style={{ display: 'flex', minHeight: '34px' }}>
      { symmFactoryNames && symmFactoryNames.map && symmFactoryNames.map( factoryName =>
        <ToolFactoryButton factoryName={factoryName}/>
      ) }
      <ToolbarSpacer/>
      { transFactoryNames && transFactoryNames.map && transFactoryNames.map( factoryName =>
        <ToolFactoryButton factoryName={factoryName}/>
      ) }
      <ToolbarSpacer/>
      { mapFactoryNames && mapFactoryNames.map && mapFactoryNames.map( factoryName =>
        <ToolFactoryButton factoryName={factoryName}/>
      ) }
    </div>
  )
}

const CommandButton = ( { cmdName } ) =>
{
  return (
    <button aria-label={cmdName} style={{ padding: '0px', borderWidth: '2px', borderColor: 'darkgray' }}>
      <img key={cmdName} src={`./icons/tools/small/${cmdName}.png`}/>
    </button>
  )
}

const ToolButton = ( { controller } ) =>
{
  const kind = useControllerProperty( controller, 'kind', 'kind', false );
  const label = useControllerProperty( controller, 'label', 'label', false );
  return (
    <button aria-label={label} style={{ padding: '0px', borderWidth: '2px', borderColor: 'darkgray' }}>
      <img key={label} src={`./icons/tools/small/${kind}.png`}/>
    </button>
  )
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
        <ToolButton controller={`${toolsController}:${toolName}`}/>
      ) }
      <ToolbarSpacer/>
      { transToolNames && transToolNames.map && transToolNames.map( toolName =>
        <ToolButton controller={`${toolsController}:${toolName}`}/>
      ) }
    </div>
  )
}