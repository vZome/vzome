import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { selectEditAfter } from '../../ui/viewer/store';

import { withStyles, makeStyles } from '@material-ui/core/styles'
import Grid from '@material-ui/core/Grid'
import TreeView from '@material-ui/lab/TreeView'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import ChevronRightIcon from '@material-ui/icons/ChevronRight'
import TreeItem from '@material-ui/lab/TreeItem'
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

export const HistoryInspector = props  =>
{
  const report = useDispatch();

  const root = useSelector( state => state.xmlTree );
  const allAttributes = useSelector( state => state.attributes );
  const current = useSelector( state => state.edit );

  const [expanded, setExpanded] = React.useState( [] );

  const handleToggle = ( event, nodeIds ) =>
  {
    setExpanded( nodeIds );
  };

  const handleSelect = ( event, value ) =>
  {
    report( selectEditAfter( value ) );
  };

  const renderTree = ( edit ) =>
  {
    if ( typeof edit === 'string' )
      return null;
    if ( edit.tagName === 'Boolean' || edit.tagName === 'polygonVertex' )
      return null;
    const kids = ( edit.children.length > 0 )? edit.children : null;
    let subtrees = kids && kids.filter( kid => kid.tagName !== 'effects' ) .map( child => renderTree( child ) );
    if ( subtrees && subtrees.length === 1 && subtrees[ 0 ] === null )
      subtrees = null;
    return (
      <StyledTreeItem key={edit.id} nodeId={edit.id} labelText={edit.tagName}>
        {subtrees}
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
      <Grid item id="debugger-source" style={{ display: 'table-row', height: '100%' }}>
        <div style={{ width: '100%', height: '100%', position: 'relative' }}>
          <TreeView style={{ overflow: 'auto', position: 'absolute', top: 0, bottom: 0, left: 0, right: 0 }}
            expanded={expanded}
            selected={current}
            onNodeToggle={handleToggle}
            onNodeSelect={handleSelect}
            defaultCollapseIcon={<ExpandMoreIcon />}
            defaultExpanded={ [ ':' ] }
            defaultExpandIcon={<ChevronRightIcon />}
          >
            <StyledTreeItem key={'--START--'} nodeId={'--START--'} labelText={'--START--'} />
            {root.children.map( edit => renderTree( edit ) ) }
          </TreeView>
        </div>
      </Grid>
      { allAttributes &&
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
              {current && allAttributes[ current ] && Object.keys( allAttributes[ current ] ).map( name => (
                <StyledTableRow key={name}>
                  <StyledTableCell component="th" scope="row">{name}</StyledTableCell>
                  <StyledTableCell>{allAttributes[ current ][ name ]}</StyledTableCell>
                </StyledTableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Grid>
      }
    </Grid>
  )
}
