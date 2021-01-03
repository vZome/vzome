import React from 'react';
import { connect } from 'react-redux'

import { makeStyles } from '@material-ui/core/styles'
import Drawer from '@material-ui/core/Drawer'
import Toolbar from '@material-ui/core/Toolbar'
import TreeView from '@material-ui/lab/TreeView'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import ChevronRightIcon from '@material-ui/icons/ChevronRight'
import TreeItem from '@material-ui/lab/TreeItem'

import * as designFns from '../bundles/designs'

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

const Debugger = ( { data, current } )  =>
{
  const classes = useStyles();

  const selected = current && ':' + current.join( ':' )

  const renderTree = ( element, id ) => {
    const children = []
    let child = element.firstElementChild
    while ( child ) {
      children.push( child )
      child = child.nextElementSibling
    }
    return (
      <TreeItem key={id} nodeId={id} label={element.nodeName}>
        { children.length > 0 ? children.map( ( child, i ) => renderTree( child, `${id}:${i}` ) ) : null }
      </TreeItem>
    )
  }

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
      <div className={classes.drawerContainer}>
        <TreeView className={classes.treeview} selected={selected}
          defaultCollapseIcon={<ExpandMoreIcon />}
          defaultExpanded={['']}
          defaultExpandIcon={<ChevronRightIcon />}
        >
          {renderTree( data, '' ) }
        </TreeView>
      </div>
    </Drawer>
  )
}


const select = ( state ) =>
{
  const design = state.designs && designFns.selectDesign( state )
  const failed = design && ! design.success
  const dbugger = state.designs && designFns.selectDebugger( state )
  return {
    data: failed && state.dbuggerEnabled && dbugger.source,
    current: dbugger && dbugger.currentEditStack
  }
}

export default connect( select )( Debugger )
