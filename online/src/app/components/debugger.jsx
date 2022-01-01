import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { withStyles, makeStyles } from '@material-ui/core/styles'
import Toolbar from '@material-ui/core/Toolbar'
import Grid from '@material-ui/core/Grid'
import TreeView from '@material-ui/lab/TreeView'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import ChevronRightIcon from '@material-ui/icons/ChevronRight'
import TreeItem from '@material-ui/lab/TreeItem'
import IconButton from '@material-ui/core/IconButton'
import Tooltip from '@material-ui/core/Tooltip'
import GetAppRoundedIcon from '@material-ui/icons/GetAppRounded'
import PublishRoundedIcon from '@material-ui/icons/PublishRounded'
import SkipNextRoundedIcon from '@material-ui/icons/SkipNextRounded'
import SkipPreviousRoundedIcon from '@material-ui/icons/SkipPreviousRounded'
import UndoRoundedIcon from '@material-ui/icons/UndoRounded'
import RedoRoundedIcon from '@material-ui/icons/RedoRounded'
import Table from '@material-ui/core/Table'
import TableBody from '@material-ui/core/TableBody'
import TableCell from '@material-ui/core/TableCell'
import TableContainer from '@material-ui/core/TableContainer'
import TableHead from '@material-ui/core/TableHead'
import TableRow from '@material-ui/core/TableRow'
import Paper from '@material-ui/core/Paper'
import Typography from '@material-ui/core/Typography'

const StyledTableCell = withStyles((theme) => ({
  head: {
    backgroundColor: theme.palette.common.black,
    color: theme.palette.common.white,
  },
  body: {
    fontSize: 14,
  },
}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
  root: {
    '&:nth-of-type(odd)': {
      backgroundColor: theme.palette.action.hover,
    },
  },
}))(TableRow);

const useTreeItemStyles = makeStyles((theme) => ({
  root: {
    color: theme.palette.text.secondary,
    '&:hover > $content': {
      backgroundColor: theme.palette.action.hover,
    },
    '&:focus > $content': {
      backgroundColor: `var(--tree-view-bg-color, ${theme.palette.grey[400]})`,
      color: 'var(--tree-view-color)',
    },
    '&$selected > $content': {
      backgroundColor: '#ab003c',
      color: theme.palette.common.white,
    },
    '&:focus > $content $label, &:hover > $content $label, &$selected > $content $label': {
      backgroundColor: 'transparent',
    },
  },
  content: {
    color: theme.palette.text.secondary,
    borderTopRightRadius: theme.spacing(2),
    borderBottomRightRadius: theme.spacing(2),
    paddingRight: theme.spacing(1),
    fontWeight: theme.typography.fontWeightMedium,
    '$expanded > &': {
      fontWeight: theme.typography.fontWeightRegular,
    },
  },
  expanded: {},
  selected: {},
  label: {
    fontWeight: 'inherit',
    color: 'inherit',
  },
  labelRoot: {
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing(0.5, 0),
  },
  labelIcon: {
    marginRight: theme.spacing(1),
  },
  labelText: {
    fontWeight: 'inherit',
    flexGrow: 1,
  },
}));

const StyledTreeItem = props => {
  const classes = useTreeItemStyles();
  const { labelText, labelInfo, color, bgColor, ...other } = props;

  return (
    <TreeItem
      label={
        <div className={classes.labelRoot}>
          <Typography variant="body2" className={classes.labelText}>
            {labelText}
          </Typography>
          <Typography variant="caption" color="inherit">
            {labelInfo}
          </Typography>
        </div>
      }
      style={{
        '--tree-view-color': color,
        '--tree-view-bg-color': bgColor,
      }}
      classes={{
        root: classes.root,
        content: classes.content,
        expanded: classes.expanded,
        selected: classes.selected,
        group: classes.group,
        label: classes.label,
      }}
      {...other}
    />
  );
}

// TODO: put this in a module that both worker and main context can use.
//  Right now this is duplicated!
export const Step = { IN: 0, OVER: 1, OUT: 2, DONE: 3 }

export const Debugger = ( {} )  =>
{
  const report = useDispatch();
  const reportAction = action => report( { type: 'ACTION_TRIGGERED', payload: action } );

  const root = useSelector( state => state.xmlTree );
  const current = useSelector( state => state.edit );
  
  const goToStart = () => reportAction( 'start' ); // TODO these all need to be rethought
  const stepBack  = () => reportAction( 'back' );
  const stepIn    = () => reportAction( Step.IN );
  const stepOver  = () => reportAction( Step.OVER );
  const stepOut   = () => reportAction( Step.OUT );
  const goToEnd   = () => reportAction( Step.DONE );

  const onNodeSelect = ( event, value ) =>
  {
    report( { type: 'EDIT_SELECTED', payload: value } );
  }

  const renderTree = ( edit ) =>
  {
    let subtrees = edit.children.length > 0 ? edit.children : null;
    if ( subtrees && subtrees.length === 1 && subtrees[ 0 ].tagName === 'Boolean' ) {
      subtrees = null;
    }
    return (
      <StyledTreeItem key={edit.id} nodeId={edit.id} labelText={edit.tagName}>
        { subtrees && subtrees.map( child => renderTree( child ) ) }
      </StyledTreeItem>
    )
  }

  if ( !root )
    return null

  // I don't know why this styling works to fill the vertical space, without letting
  //  the TreeView force everything to overflow, but it works.  I found it in
  //  this JSFiddle:  https://jsfiddle.net/sgxpqc6b/9/
  //  from a comment on this post:  https://www.whitebyte.info/programming/css/how-to-make-a-div-take-the-remaining-height
  //  Note that I had to add an extra div around the TreeView.

  return (
    <Grid container direction='column' style={{ display: 'table', height: '100%' }}>
      <Grid item style={{ display: 'table-row' }}>
        <Toolbar id="debugger-tools" variant='dense'>
          <Tooltip title="Go to start" aria-label="go-to-start">
            <IconButton color="secondary" aria-label="go-to-start" onClick={goToStart}>
              <SkipPreviousRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step back" aria-label="step-back">
            <IconButton color="secondary" aria-label="step-back" onClick={stepBack}>
              <UndoRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step in" aria-label="step-in">
            <IconButton color="secondary" aria-label="step-in" onClick={stepIn}>
              <GetAppRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step over" aria-label="step-over">
            <IconButton color="secondary" aria-label="step-over" onClick={stepOver}>
              <RedoRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step out" aria-label="step-out">
            <IconButton color="secondary" aria-label="step-out" onClick={stepOut}>
              <PublishRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Go to end" aria-label="go-to-end">
            <IconButton color="secondary" aria-label="go-to-end" onClick={goToEnd}>
              <SkipNextRoundedIcon/>
            </IconButton>
          </Tooltip>
        </Toolbar>
      </Grid>
      <Grid item id="debugger-source" style={{ display: 'table-row', height: '100%' }}>
        <div style={{ width: '100%', height: '100%', position: 'relative' }}>
          <TreeView style={{ overflow: 'auto', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }}
            // selected={current} expanded={expanded}
            onNodeSelect={onNodeSelect}
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpanded={ [ ':' ] }
            defaultExpandIcon={<ChevronRightIcon />}
          >
            {renderTree( root, '' ) }
          </TreeView>
        </div>
      </Grid>
      <Grid item style={{ display: 'table-row' }}>
        <TableContainer component={Paper}>
          <Table size="small" aria-label="command attributes">
            <TableHead>
              <TableRow>
                <StyledTableCell>Name</StyledTableCell>
                <StyledTableCell>Value</StyledTableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {current && current.attributes && Object.keys( current.attributes ).map( name => (
                ( name !== 'id' ) &&
                <StyledTableRow key={name}>
                  <StyledTableCell component="th" scope="row">{name}</StyledTableCell>
                  <StyledTableCell>{current.attributes[ name ]}</StyledTableCell>
                </StyledTableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Grid>
    </Grid>
  )
}


// const select = ( state ) =>
// {
//   const dbugger = state.designs && designFns.selectDebugger( state )
//   if ( ! dbugger ) {
//     return {} // document had an unknown field, or couldn't parse
//   }
//   return {
//     data: dbugger.source,
//     current: dbugger && dbugger.nextEdit && dbugger.nextEdit.id(),
//     branches: dbugger && dbugger.branchStack && dbugger.branchStack.map( ({ branch }) => branch.id() ),
//     designName: dbugger && designFns.selectDesignName( state )
//   }
// }

// const boundEventActions = {
//   stepIn : dbugger.stepper.in,
//   stepOver: dbugger.stepper.over,
//   stepOut: dbugger.stepper.out,
//   run: dbugger.stepper.done,
// }

