import React, { useState } from 'react';
import { connect } from 'react-redux'

import { withStyles, makeStyles } from '@material-ui/core/styles'
import Toolbar from '@material-ui/core/Toolbar'
import Grid from '@material-ui/core/Grid'
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
import Table from '@material-ui/core/Table'
import TableBody from '@material-ui/core/TableBody'
import TableCell from '@material-ui/core/TableCell'
import TableContainer from '@material-ui/core/TableContainer'
import TableHead from '@material-ui/core/TableHead'
import TableRow from '@material-ui/core/TableRow'
import Paper from '@material-ui/core/Paper'
import Typography from '@material-ui/core/Typography'

import * as designFns from '../bundles/designs.js'
import * as dbugger from '../bundles/dbugger.js'

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
  group: {
    marginLeft: 0,
    '& $content': {
      paddingLeft: theme.spacing(2),
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

const Debugger = ( { data, current, branches, designName, stepIn, stepOut, stepOver, run } )  =>
{
  const [ edit, setEdit ] = useState( null )

  const onLabelClick = edit => event =>
  {
    event.preventDefault()
    setEdit( edit )
  }

  const renderTree = ( edit ) => {
    const id = edit.id()
    const children = []
    let child = edit.firstChild()
    while ( child ) {
      children.push( child )
      child = child.nextSibling()
    }
    return (
      <StyledTreeItem key={id} nodeId={id} labelText={edit.name()} onLabelClick={onLabelClick( edit )}>
        { children.length > 0 ? children.map( child => renderTree( child ) ) : null }
      </StyledTreeItem>
    )
  }

  const expanded = branches && [ ':', ...branches ]

  if ( !data )
    return null

  // I don't know why this styling works to fill the vertical space, without letting
  //  the TreeView force everything to overflow, but it works.  I found it in
  //  this JSFiddle:  https://jsfiddle.net/sgxpqc6b/9/
  //  from a comment on this post:  https://www.whitebyte.info/programming/css/how-to-make-a-div-take-the-remaining-height
  //  Note that I had to add an extra div around the TreeView.

  const root = {
    id: () => ':',
    name: () => 'EditHistory',
    firstChild: () => data,
    getAttributeNames: () => []
  }

  return (
    <Grid container direction='column' style={{ display: 'table', height: '100%' }}>
      <Grid item style={{ display: 'table-row' }}>
        <Toolbar id="debugger-tools" variant='dense'>
          <Tooltip title="Step in" aria-label="step-in">
            <IconButton color="secondary" aria-label="step-in" onClick={()=>stepIn( designName )}>
              <GetAppRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step over" aria-label="step-over">
            <IconButton color="secondary" aria-label="step-over" onClick={()=>stepOver( designName )}>
              <RedoRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Step out" aria-label="step-out">
            <IconButton color="secondary" aria-label="step-out" onClick={()=>stepOut( designName )}>
              <PublishRoundedIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title="Continue" aria-label="continue">
            <IconButton color="secondary" aria-label="continue" onClick={()=>run( designName )}>
              <FastForwardRoundedIcon/>
            </IconButton>
          </Tooltip>
        </Toolbar>
      </Grid>
      <Grid item id="debugger-source" style={{ display: 'table-row', height: '100%' }}>
        <div style={{ width: '100%', height: '100%', position: 'relative' }}>
          <TreeView style={{ overflow: 'auto', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }}
            selected={current} expanded={expanded}
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpanded={ expanded }
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
              {edit && edit.getAttributeNames().map( name => (
                ( name !== 'id' ) &&
                <StyledTableRow key={name}>
                  <StyledTableCell component="th" scope="row">
                    {name}
                  </StyledTableCell>
                  <StyledTableCell>{edit.getAttribute( name )}</StyledTableCell>
                </StyledTableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Grid>
    </Grid>
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
    current: dbugger && dbugger.nextEdit && dbugger.nextEdit.id(),
    branches: dbugger && dbugger.branchStack && dbugger.branchStack.map( ({ branch }) => branch.id() ),
    designName: dbugger && designFns.selectDesignName( state )
  }
}

const boundEventActions = {
  stepIn : dbugger.stepper.in,
  stepOver: dbugger.stepper.over,
  stepOut: dbugger.stepper.out,
  run: dbugger.stepper.done,
}

export default connect( select, boundEventActions )( Debugger )
