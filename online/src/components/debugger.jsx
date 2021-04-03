import React, { useState } from 'react';
import { connect } from 'react-redux'

import { makeStyles } from '@material-ui/core/styles'
import Drawer from '@material-ui/core/Drawer'
import Toolbar from '@material-ui/core/Toolbar'
import TreeView from '@material-ui/lab/TreeView'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import ChevronRightIcon from '@material-ui/icons/ChevronRight'
import TreeItem from '@material-ui/lab/TreeItem'
import IconButton from '@material-ui/core/IconButton'
import Tooltip from '@material-ui/core/Tooltip'
import RedoRoundedIcon from '@material-ui/icons/RedoRounded'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'
import PublishRoundedIcon from '@material-ui/icons/PublishRounded'
import FastForwardRoundedIcon from '@material-ui/icons/FastForwardRounded'

import * as designFns from '../bundles/designs.js'
import * as dbugger from '../bundles/dbugger.js'
import { vZomeJava } from '@vzome/react-vzome'

const drawerWidth = 500

const useStyles = makeStyles((theme) => ({
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
  },
  drawerContainer: {
    overflow: 'auto',
    flexGrow: 1,
  },
  treeview: {
    height: 110,
    flexGrow: 1,
  },
}))

const Debugger = ( { data, current, branches, designName, doDebug } )  =>
{
  const classes = useStyles();
  const [ xml, setXml ] = useState( '' )

  const onLabelClick = element => event =>
  {
    event.preventDefault()
    setXml( element.outerHTML )
  }

  const renderTree = ( element ) => {
    const children = []
    let child = element.firstElementChild
    while ( child ) {
      children.push( child )
      child = child.nextElementSibling
    }
    return (
      <TreeItem key={element.id} nodeId={element.id} label={element.nodeName} onLabelClick={onLabelClick(element)}>
        { children.length > 0 ? children.map( child => renderTree( child ) ) : null }
      </TreeItem>
    )
  }

  const expanded = branches && [ ':', ...branches ]

  if ( !data )
    return null

  return (
    <Drawer
      className={classes.drawer}
      variant="permanent"
      classes={{
        paper: classes.drawerPaper,
      }}
    >
      <Toolbar />
        <Toolbar variant='dense'>
          <Tooltip title="Step in" aria-label="step-in">
            <IconButton color="secondary" aria-label="step-in" onClick={()=>doDebug(designName, vZomeJava.Step.IN)}>
              <GetAppRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step over" aria-label="step-over">
            <IconButton color="secondary" aria-label="step-over" onClick={()=>doDebug(designName, vZomeJava.Step.OVER)}>
              <RedoRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step out" aria-label="step-out">
            <IconButton color="secondary" aria-label="step-out" onClick={()=>doDebug(designName, vZomeJava.Step.OUT)}>
              <PublishRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Continue" aria-label="continue">
            <IconButton color="secondary" aria-label="continue" onClick={()=>doDebug(designName, vZomeJava.Step.DONE)}>
              <FastForwardRoundedIcon/>
            </IconButton>
          </Tooltip>
        </Toolbar>
        <div className={classes.drawerContainer}>
          <TreeView className={classes.treeview} selected={current} expanded={expanded}
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpanded={ expanded }
            defaultExpandIcon={<ChevronRightIcon />}
          >
            {renderTree( data, '' ) }
          </TreeView>
        </div>
        <div style={{ flex: 0, minHeight: '70px' }}>{xml}</div>
    </Drawer>
  )
}


const select = ( state ) =>
{
  const dbugger = state.designs && designFns.selectDebugger( state )
  if ( ! dbugger ) {
    return {} // document had an unknown field, or couldn't parse
  }
  return {
    data: dbugger.source,
    current: dbugger && dbugger.currentElement && dbugger.currentElement.id,
    branches: dbugger && dbugger.branchStack && dbugger.branchStack.map( ({ branch }) => branch.id ),
    designName: dbugger && designFns.selectDesignName( state )
  }
}

const boundEventActions = {
  doDebug : dbugger.debug,
}

export default connect( select, boundEventActions )( Debugger )
